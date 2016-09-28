package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeTypes;

public enum RegisterDataType
{
	FLOAT("Floating Point", CodeTypes.FLOAT_OPERATIONS),
	INTEGER("Integer", CodeTypes.INTEGER_OPERATIONS);

	private String text;
	private CodeTypes codeType;

	RegisterDataType(String text, CodeTypes codeType)
	{
		this.text = text;
		this.codeType = codeType;
	}

	@Override
	public String toString()
	{
		return text;
	}

	public String getCodeType()
	{
		return codeType.getValue();
	}
}