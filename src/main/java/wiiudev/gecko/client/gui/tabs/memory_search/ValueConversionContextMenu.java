package wiiudev.gecko.client.gui.tabs.memory_search;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.debugging.StackTraceUtils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ValueConversionContextMenu extends JPopupMenu
{
	private JTextField textField;

	public ValueConversionContextMenu(JTextField textField)
	{
		this.textField = textField;

		addMenuItem("Decimal -> Hexadecimal", actionEvent -> convert(Conversions::decimalToHexadecimal));
		addMenuItem("Hexadecimal -> Decimal", actionEvent -> convert(Conversions::hexadecimalToDecimal));
		addMenuItem("Float -> Hexadecimal", actionEvent -> convert(Conversions::floatingPointToHexadecimal));
		addMenuItem("Hexadecimal -> Float", actionEvent -> convert(Conversions::hexadecimalToFloatingPoint));
		addMenuItem("ASCII -> Hexadecimal", actionEvent -> convert(Conversions::asciiToHexadecimal));
		addMenuItem("Hexadecimal -> ASCII", actionEvent -> convert(Conversions::hexadecimalToASCII));
		addMenuItem("Unicode -> Hexadecimal", actionEvent -> convert(Conversions::unicodeToHexadecimal));
		addMenuItem("Hexadecimal -> Unicode", actionEvent -> convert(Conversions::hexadecimalToUnicode));
		addMenuItem("Coordinates -> Hexadecimal", actionEvent -> convert(Conversions::coordinatesToHexadecimal));
		addMenuItem("Hexadecimal -> Coordinates", actionEvent -> convert(Conversions::hexadecimalToCoordinates));
	}

	private void addMenuItem(String text, ActionListener actionListener)
	{
		JMenuItem hexadecimalToDecimalOption = new JMenuItem(text);
		hexadecimalToDecimalOption.addActionListener(actionListener);
		add(hexadecimalToDecimalOption);
	}

	public void addContextMenu()
	{
		textField.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				if (mouseEvent.isPopupTrigger()
						&& textField.isEnabled())
				{
					show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
				}
			}
		});
	}

	private void convert(Converter converter)
	{
		try
		{
			String input = textField.getText();
			String output = converter.convert(input);
			textField.setText(output);
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(getRootPane(), exception);
		}
	}

	@FunctionalInterface
	interface Converter
	{
		String convert(String input);
	}
}