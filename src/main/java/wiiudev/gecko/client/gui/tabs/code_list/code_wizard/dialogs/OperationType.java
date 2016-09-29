package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

public enum OperationType
{
	LOAD("Load"),
	STORE("Store"),
	OPERATION("Operation");

	private String text;

	OperationType(String text)
	{
		this.text = text;
	}

	@Override
	public String toString()
	{
		return text;
	}
}