package wiiudev.gecko.client.gui.code_list.code_wizard.selections;

import wiiudev.gecko.client.connector.utilities.Hexadecimal;

public enum IntegerOperations
{
	add_register(0x00, "Add Register"),
	subtract_register(0x01, "Subtract Register"),
	multiply_register(0x02, "Multiply Register"),
	divide_register(0x03, "Divide Register"),
	add_value(0x04, "Add Value"),
	subtract_value(0x05, "Subtract Value"),
	multiply_value(0x06, "Multiply Value"),
	divide_value(0x07, "Divide Value");

	private short value;
	private String text;

	IntegerOperations(int value, String text)
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