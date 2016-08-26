package wiiudev.gecko.client.gui.input_filters;

import wiiudev.gecko.client.conversions.Validation;

import javax.swing.text.*;
import java.awt.*;

/**
 * An input filter for a text field to make sure only valid input is given.
 * Also, lowercase letters will be converted to uppercase automatically.
 */
public class HexadecimalInputFilter extends DocumentFilter
{
	private int maximumLength;

	public HexadecimalInputFilter(int maximumLength)
	{
		this.maximumLength = maximumLength;
	}

	@Override
	public void insertString(FilterBypass filterBypass, int offset, String text,
	                         AttributeSet attributeSet) throws BadLocationException
	{
		text = text.toUpperCase().trim();
		Document document = filterBypass.getDocument();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(document.getText(0, document.getLength()));
		stringBuilder.insert(offset, text);

		if (isValidInput(stringBuilder.toString(), 0))
		{
			super.insertString(filterBypass, offset, text, attributeSet);
		} else
		{
			Toolkit.getDefaultToolkit().beep();
		}
	}

	@Override
	public void replace(DocumentFilter.FilterBypass filterBypass, int offset, int length, String text,
	                    AttributeSet attributeSet) throws BadLocationException
	{
		text = text.toUpperCase().trim();
		Document document = filterBypass.getDocument();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(document.getText(0, document.getLength()));
		stringBuilder.insert(offset, text);

		if (isValidInput(stringBuilder.toString(), length))
		{
			super.replace(filterBypass, offset, length, text, attributeSet);
		} else
		{
			Toolkit.getDefaultToolkit().beep();
		}
	}

	public static void setHexadecimalInputFilter(JTextComponent textComponent)
	{
		setHexadecimalInputFilter(textComponent, 8);
	}

	public static void setHexadecimalInputFilter(JTextComponent textComponent, int maximumInputLength)
	{
		PlainDocument plainDocument = (PlainDocument) textComponent.getDocument();
		plainDocument.setDocumentFilter(new HexadecimalInputFilter(maximumInputLength));

		String text = textComponent.getText();

		if (text.length() > maximumInputLength)
		{
			textComponent.setText(text.substring(text.length() - maximumInputLength, text.length()));
		} else
		{
			while (text.length() < maximumInputLength)
			{
				text = "0" + text;
			}

			textComponent.setText(text);
		}
	}

	private boolean isValidInput(String text, int replaceLength)
	{
		boolean isLengthOkay = text.length() - replaceLength <= maximumLength;
		boolean isHexadecimal = Validation.isHexadecimal(text);

		return isLengthOkay && isHexadecimal;
	}
}