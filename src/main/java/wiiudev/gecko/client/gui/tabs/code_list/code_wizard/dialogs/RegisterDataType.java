package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

public enum RegisterDataType
{
	FLOAT("Floating Point"),
	INTEGER("Integer");

	private String text;

	RegisterDataType(String text)
	{
		this.text = text;
	}

	@Override
	public String toString()
	{
		return text;
	}
}