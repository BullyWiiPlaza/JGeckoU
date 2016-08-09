package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections;

import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;

public enum Pointer
{
	no_pointer(0x00, "No Pointer"), pointer(0x01, "Pointer"), pointer_in_pointer(0x02, "Pointer in Pointer");

	private short value;
	private String text;

	Pointer(int value, String text)
	{
		this.value = (short) value;
		this.text = text;
	}

	public String getValue()
	{
		return new Hexadecimal(value, 1).toString();
	}

	@Override
	public String toString()
	{
		return text;
	}
}