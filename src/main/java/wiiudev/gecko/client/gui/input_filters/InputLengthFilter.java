package wiiudev.gecko.client.gui.input_filters;

import wiiudev.gecko.client.conversions.Validation;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;

public class InputLengthFilter extends PlainDocument
{
	private int maximumInputLength;

	public InputLengthFilter()
	{
		this(8);
	}

	public InputLengthFilter(int maximumInputLength)
	{
		super();
		this.maximumInputLength = maximumInputLength;
	}

	public void insertString(int offset, String text, AttributeSet attributeSet) throws BadLocationException
	{
		text = text.toUpperCase();
		boolean lengthOkay = (getLength() + text.length()) <= maximumInputLength;

		if (lengthOkay && Validation.isHexadecimal(text))
		{
			super.insertString(offset, text, attributeSet);
		} else
		{
			Toolkit.getDefaultToolkit().beep();
		}
	}
}