package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.conversion.ConversionType;
import wiiudev.gecko.client.conversion.Conversions;
import wiiudev.gecko.client.conversion.SystemClipboard;
import wiiudev.gecko.client.gui.utilities.DefaultContextMenu;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConversionDialog extends JDialog
{
	private JPanel contentPane;
	private JButton copyResultButton;
	private JButton buttonCancel;
	private JTextArea inputField;
	private JTextArea convertedResultField;
	private JLabel resultLabel;
	private String resultLabelName;
	private ConversionType conversionType;

	public ConversionDialog(JFrame frame, String dialogTitle, ConversionType conversionType)
	{
		resultLabelName = "Result: ";
		setContentPane(contentPane);
		setLocationRelativeTo(frame);
		setSize(450, 400);
		WindowUtilities.setIconImage(this);
		setTitle(dialogTitle);
		this.conversionType = conversionType;
		convertInput();

		addInputFieldDocumentListener();

		new DefaultContextMenu().attachTo(convertedResultField);
		new DefaultContextMenu().attachTo(inputField);

		copyResultButton.addActionListener(actionEvent -> onCopyResult());
		buttonCancel.addActionListener(actionEvent -> onCancel());

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent windowEvent)
			{
				onCancel();
			}
		});

		contentPane.registerKeyboardAction(actionEvent -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void addInputFieldDocumentListener()
	{
		inputField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				convertInput();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				convertInput();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				convertInput();
			}
		});
	}

	private void convertInput()
	{
		try
		{
			String input = inputField.getText();
			String converted;

			switch (conversionType)
			{
				case hexadecimalToDecimal:
					converted = Conversions.hexadecimalToDecimal(input);
					break;
				case decimalToHexadecimal:
					converted = Conversions.decimalToHexadecimal(input);
					break;

				case hexadecimalToUTF8:
					converted = Conversions.hexadecimalToASCII(input);
					break;
				case utf8ToHexadecimal:
					converted = Conversions.asciiToHexadecimal(input);
					break;

				case hexadecimalToFloatingPoint:
					converted = Conversions.hexadecimalToFloatingPoint(input);
					break;
				case floatingPointToHexadecimal:
					converted = Conversions.floatingPointToHexadecimal(input);
					break;

				case hexadecimalToUnicode:
					converted = Conversions.hexadecimalToUnicode(input);
					break;
				case unicodeToHexadecimal:
					converted = Conversions.unicodeToHexadecimal(input);
					break;

				default:
					converted = "";
			}

			convertedResultField.setText(converted);
			convertedResultField.setBackground(Color.GREEN);
			copyResultButton.setEnabled(true);
			resultLabel.setText(resultLabelName + " OK!");
		} catch (Exception exception)
		{
			convertedResultField.setText("");
			String exceptionMessage = exception.getMessage();

			int maximumMessageLength = 40;
			if (exceptionMessage.length() > maximumMessageLength)
			{
				// Do not break the graphical interface formatting by cutting a long message String
				exceptionMessage = exceptionMessage.substring(0, maximumMessageLength) + "...";
			}

			resultLabel.setText(resultLabelName + " Invalid input (" + exceptionMessage + ")");
			convertedResultField.setBackground(Color.RED);
			copyResultButton.setEnabled(false);
		}
	}

	private void onCopyResult()
	{
		String converted = convertedResultField.getText();
		SystemClipboard.copy(converted);
		dispose();
	}

	private void onCancel()
	{
		dispose();
	}

	public void display()
	{
		setVisible(true);
	}
}