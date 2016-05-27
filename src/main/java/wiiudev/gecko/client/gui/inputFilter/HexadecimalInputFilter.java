package wiiudev.gecko.client.gui.inputFilter;

import wiiudev.gecko.client.conversion.Validation;

import javax.swing.text.*;
import java.awt.*;

/**
 * An input filter for the code text field to make sure only valid input is given. Also, lowercase letters will be converted to uppercase automatically.
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
		text = text.toUpperCase();
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
		text = text.toUpperCase();
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

	public static void addHexadecimalInputFilter(JTextComponent textComponent)
	{
		addHexadecimalInputFilter(textComponent, 8);
	}

	public static void addHexadecimalInputFilter(JTextComponent textComponent, int maximumInputLength)
	{
		PlainDocument plainDocument = (PlainDocument) textComponent.getDocument();
		plainDocument.setDocumentFilter(new HexadecimalInputFilter(maximumInputLength));
	}

	private boolean isValidInput(String text, int replaceLength)
	{
		boolean isLengthOkay = text.length() - replaceLength <= maximumLength;
		boolean isHexadecimal = Validation.isHexadecimal(text);

		return isLengthOkay && isHexadecimal;
	}
}