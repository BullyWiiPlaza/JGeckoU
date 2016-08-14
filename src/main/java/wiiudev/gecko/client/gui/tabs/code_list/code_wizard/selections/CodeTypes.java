package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections;

import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;

public enum CodeTypes
{
	RAM_WRITE(0x00, "RAM Write"),
	STRING_WRITE(0x01, "String Write"),
	SKIP_WRITE(0x02, "Skip Write"),
	IF_CONDITION(0x03, "Equal"),
	NOT_EQUAL(0x04, "Not Equal"),
	GREATER(0x05, "Greater"),
	LESS(0x06, "Less"),
	GREATER_OR_EQUAL(0x07, "Greater or Equal"),
	LESS_OR_EQUAL(0x08, "Less or Equal"),
	LOAD_INT(0x10, "Load Integer"),
	STORE_INT(0x11, "Store Integer"),
	LOAD_FLOAT(0x12, "Load Float"),
	STORE_FLOAT(0x13, "Store Float"),
	INTEGER_OPERATIONS(0x14, "Integer Operations"),
	FLOAT_OPERATIONS(0x15, "Float Operations"),
	FILL_MEMORY(0x20, "Fill Memory"),
	LOAD_POINTER(0x30, "Load Pointer"),
	ADD_POINTER_OFFSET(0x31, "Add Offset To Pointer"),
	TERMINATION(0xD0, "Termination"),
	NO_OPERATION(0xD1, "No Operation");

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