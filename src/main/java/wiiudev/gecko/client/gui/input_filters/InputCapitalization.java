package wiiudev.gecko.client.gui.input_filters;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class InputCapitalization extends PlainDocument
{
	public void insertString(int offset, String text, AttributeSet attributeSet) throws BadLocationException
	{
		text = text.toUpperCase();
		super.insertString(offset, text, attributeSet);
	}
}