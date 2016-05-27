package wiiudev.gecko.client.gui.inputFilter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class InputCapitalizer extends PlainDocument
{
	public void insertString(int offset, String text, AttributeSet attributeSet) throws BadLocationException
	{
		text = text.toUpperCase();
		super.insertString(offset, text, attributeSet);
	}
}