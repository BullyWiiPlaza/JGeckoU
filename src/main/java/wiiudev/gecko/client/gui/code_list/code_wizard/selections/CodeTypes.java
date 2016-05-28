package wiiudev.gecko.client.gui.code_list.code_wizard.selections;

import wiiudev.gecko.client.connector.utilities.Hexadecimal;

public enum CodeTypes
{
	ram_write(0x00, "RAM Write"),
	string_write(0x01, "String Write"),
	skip_write(0x02, "Skip Write"),
	if_condition(0x03, "Equal"),
	not_equal(0x04, "Not Equal"),
	greater(0x05, "Greater"),
	less(0x06, "Less"),
	greater_or_equal(0x07, "Greater or Equal"),
	less_or_equal(0x08, "Less or Equal"),
	load_int(0x10, "Load Integer"),
	store_int(0x11, "Store Integer"),
	load_float(0x12, "Load Float"),
	store_float(0x13, "Store Float"),
	integer_operations(0x14, "Integer Operations"),
	float_operations(0x15, "Float Operations"),
	termination(0xD0, "Termination"),
	no_operation(0xD1, "No Operation");

	private short value;
	private String text;

	CodeTypes(int value, String text)
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