package wiiudev.gecko.client.gui.dialogs;

import wiiudev.gecko.client.conversions.Validation;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.IDAProFunctionsDumpParser;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;
import wiiudev.gecko.client.tcpgecko.rpl.ExportedSymbol;
import wiiudev.gecko.client.tcpgecko.rpl.RemoteProcedureCall;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;

public class RemoteProcedureCallDialog extends JDialog
{
	private JPanel contentPane;
	private JButton callFunctionButton;
	private JTextField rplNameField;
	private JTextField symbolNameField;
	private JTextArea parametersTextArea;
	private JTextField functionResultField;
	private JTextField functionAddressField;
	private JButton coreInitDocumentationButton;
	private IDAProFunctionsDumpParser coreInitFunctionsParser;

	public RemoteProcedureCallDialog()
	{
		setFrameProperties();
		addParametersInputFilter();
		addCallFunctionActionListener();

		coreInitFunctionsParser = new IDAProFunctionsDumpParser("coreinit.txt");
		rplNameField.setText("coreinit.rpl");

		symbolNameField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				setSymbolNameFieldColor();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				setSymbolNameFieldColor();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				setSymbolNameFieldColor();
			}
		});

		addWindowListener(new WindowAdapter()
		{
			public void windowOpened(WindowEvent windowEvent)
			{
				symbolNameField.requestFocus();
			}
		});

		coreInitDocumentationButton.addActionListener(actionEvent ->
				openURL("http://wiiubrew.org/wiki/Coreinit.rpl"));
	}

	private void setSymbolNameFieldColor()
	{
		if(rplNameField.getText().startsWith("coreinit"))
		{
			String symbolText = symbolNameField.getText();
			boolean containsSymbol = coreInitFunctionsParser.contains(symbolText);
			symbolNameField.setBackground(containsSymbol ? Color.GREEN : Color.RED);
			callFunctionButton.setEnabled(containsSymbol);
		}
		else
		{
			symbolNameField.setBackground(Color.WHITE);
			callFunctionButton.setEnabled(true);
		}
	}

	private void openURL(String link)
	{
		Desktop desktop = Desktop.getDesktop();
		try
		{
			desktop.browse(new URI(link));
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	private void addParametersInputFilter()
	{
		PlainDocument plainDocument = (PlainDocument) parametersTextArea.getDocument();
		plainDocument.setDocumentFilter(new RPCParametersFilter());
	}

	private void setFrameProperties()
	{
		setContentPane(contentPane);
		setModal(true);
		setTitle("Remote Procedure Call");
		getRootPane().setDefaultButton(callFunctionButton);
		WindowUtilities.setIconImage(this);
		setSize(400, 500);
	}

	private void addCallFunctionActionListener()
	{
		callFunctionButton.addActionListener(actionEvent ->
		{
			String rplName = rplNameField.getText();

			String forcedSuffix = ".rpl";
			if (!rplName.endsWith(forcedSuffix))
			{
				rplName += forcedSuffix;
			}

			String symbolName = symbolNameField.getText();
			String parametersString = parametersTextArea.getText();

			if (parametersString.equals(""))
			{
				call(rplName, symbolName);
			} else
			{
				String[] parametersArray = parametersString.split("\n");
				int[] parameters = new int[parametersArray.length];

				for (int parametersIndex = 0; parametersIndex < parameters.length; parametersIndex++)
				{
					parameters[parametersIndex] = Integer.parseInt(parametersArray[parametersIndex], 16);
				}

				call(rplName, symbolName, parameters);
			}
		});
	}

	private void call(String rplName, String symbolName, int... parameters)
	{
		try
		{
			RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
			ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(rplName, symbolName);
			functionAddressField.setText(new Hexadecimal(exportedSymbol.getAddress(), 8).toString());
			long returnValue = remoteProcedureCall.call64(exportedSymbol, parameters);
			functionResultField.setText(new Hexadecimal(returnValue, 16).toString());
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	public void setParameters(int[] parameters)
	{
		StringBuilder parametersBuilder = new StringBuilder();

		for(int parameter : parameters)
		{
			String hexadecimalParameter = new Hexadecimal(parameter, 8).toString();
			parametersBuilder.append(hexadecimalParameter);
			parametersBuilder.append(System.lineSeparator());
		}

		String parametersString = parametersBuilder.toString().trim();
		parametersTextArea.setText(parametersString);
	}

	private static class RPCParametersFilter extends DocumentFilter
	{
		@Override
		public void insertString(FilterBypass filterBypass, int offset, String string,
		                         AttributeSet attributeSet) throws BadLocationException
		{
			Document document = filterBypass.getDocument();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(document.getText(0, document.getLength()));
			stringBuilder.insert(offset, string);

			if (validate(stringBuilder.toString()))
			{
				super.insertString(filterBypass, offset, string, attributeSet);
			}
		}

		@Override
		public void replace(FilterBypass filterBypass, int offset, int length, String text,
		                    AttributeSet attributeSet) throws BadLocationException
		{

			Document document = filterBypass.getDocument();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(document.getText(0, document.getLength()));
			stringBuilder.replace(offset, offset + length, text);

			if (validate(stringBuilder.toString()))
			{
				super.replace(filterBypass, offset, length, text, attributeSet);
			}
		}

		@Override
		public void remove(FilterBypass filterBypass, int offset, int length)
				throws BadLocationException
		{
			Document document = filterBypass.getDocument();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(document.getText(0, document.getLength()));
			stringBuilder.delete(offset, offset + length);

			if (validate(stringBuilder.toString()))
			{
				super.remove(filterBypass, offset, length);
			}
		}

		private boolean validate(String text)
		{
			for (int textIndex = 0; textIndex < text.length(); textIndex++)
			{
				String character = text.charAt(textIndex) + "";

				if (!Validation.isHexadecimal(character) && !isLineBreak(character))
				{
					return false;
				}
			}

			return true;
		}

		private boolean isLineBreak(String input)
		{
			return input.matches("\n");
		}
	}
}
