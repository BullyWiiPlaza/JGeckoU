package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections;

import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;

public enum RegisterOperation
{
	ADD_REGISTER(0x00, "Add"),
	SUBTRACT_REGISTER(0x01, "Subtract"),
	MULTIPLY_REGISTER(0x02, "Multiply"),
	DIVIDE_REGISTER(0x03, "Divide"),
	ADD_VALUE(0x04, "Add"),
	SUBTRACT_VALUE(0x05, "Subtract"),
	DIVIDE_VALUE(0x06, "Divide"),
	MULTIPLY_VALUE(0x07, "Multiply"),
	FLOAT_TO_INT(0x08, "Float -> Int");

	private short value;
	private String text;

	RegisterOperation(int value, String text)
	{
		this.value = (short) value;
		this.text = text;
	}

	public String getValue()
	{
		return new Hexadecimal(value, 2).toString();
	}

	@Override
	public String toString()
	{
		return text;
	}
}