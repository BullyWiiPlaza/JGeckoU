package wiiudev.gecko.client.codes;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CheatCodeFormatter;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;

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

	public CodeListEntry(int address, int value)
	{
		cheatCode = CheatCodeFormatter.format(CodeWizardDialog.generateNoOperationLine());
		comment = Conversions.toHexadecimal(address)
				+ " = "
				+ Conversions.toHexadecimal(value);
	}

	public CodeListEntry(String codeTitle, String cheatCode, String comment)
	{
		this.codeTitle = codeTitle;
		this.cheatCode = cheatCode;
		this.comment = comment;
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

	public byte[] getCheatCodeBytes()
	{
		CheatCodeFormatting cheatCodeFormatting = new CheatCodeFormatting(cheatCode);

		return cheatCodeFormatting.getBytes();
	}

	@Override
	public String toString()
	{
		String code = codeTitle + System.lineSeparator()
				+ cheatCode
				+ System.lineSeparator()
				+ comment;

		return code.trim();
	}
}