package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.connector.MemoryReader;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;
import javax.swing.text.*;

public class RemoteProcedureCallDialog extends JDialog
{
	private JPanel contentPane;
	private JButton callFunctionButton;
	private JTextField rplNameField;
	private JTextField symbolNameField;
	private JTextArea parametersTextArea;
	private JTextField resultTextField;

	public RemoteProcedureCallDialog()
	{
		setFrameProperties();
		addParametersInputFilter();
		addCallFunctionActionListener();
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
			MemoryReader memoryReader = new MemoryReader();
			long returnValue = memoryReader.call(rplName, symbolName, parameters);
			resultTextField.setText(Long.toHexString(returnValue).toUpperCase());
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
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

				if (!isHexadecimal(character) && !isLineBreak(character))
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

		private boolean isHexadecimal(String input)
		{
			return input.matches("-?[0-9a-fA-F]+");
		}
	}
}
