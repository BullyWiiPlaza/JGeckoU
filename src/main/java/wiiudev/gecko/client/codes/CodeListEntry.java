package wiiudev.gecko.client.codes;

import javax.swing.*;
import java.io.Serializable;

/**
 * A class for representing an entry in the cheat codes list
 */
public class CodeListEntry implements Serializable
{
	private String codeTitle;
	private String cheatCode;
	private String comment;

	public CodeListEntry(String codeTitle, String cheatCode, String comment)
	{
		this.codeTitle = codeTitle;
		this.cheatCode = cheatCode;
		this.comment = comment;
	}

	public CodeListEntry(CodeListEntry codeListEntry)
	{
		this.codeTitle = codeListEntry.getTitle();
		this.cheatCode = codeListEntry.getCode();
		this.comment = codeListEntry.getComment();
	}

	public String getTitle()
	{
		return codeTitle;
	}

	public JCheckBox getCodeListCheckBox()
	{
		return new JCheckBox(codeTitle);
	}

	public String getCode()
	{
		return cheatCode;
	}

	public String getComment()
	{
		return comment;
	}
}