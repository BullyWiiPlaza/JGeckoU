package wiiudev.gecko.client.gui.input_filters;

import org.apache.commons.lang.StringUtils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class RegisterLimit extends PlainDocument
{
	public RegisterLimit()
	{
		super();
	}

	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
	{
		if (StringUtils.isNumeric(str))
		{
			int number = Integer.parseInt(str);

			if (number >= 0 && number <= 7 && getLength() == 0)
			{
				super.insertString(offset, str, attr);
			}
		}
	}
}