package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;

import java.io.IOException;

public enum ValueSize
{
	EIGHT_BIT(0x00, 2, "8-Bit"),
	SIXTEEN_BIT(0x01, 4, "16-Bit"),
	THIRTY_TWO_BIT(0x02, 8, "32-Bit");

	private short value;
	private int size;
	private String text;

	ValueSize(int value, int size, String text)
	{
		this.value = (short) value;
		this.size = size;
		this.text = text;
	}

	public String getValue()
	{
		return new Hexadecimal(value, 1).toString();
	}

	public int getSize()
	{
		return size;
	}

	public static ValueSize get(String valueSize)
	{
		for (ValueSize currentValueSize : values())
		{
			if (currentValueSize.toString().equals(valueSize))
			{
				return currentValueSize;
			}
		}

		return null;
	}

	@Override
	public String toString()
	{
		return text;
	}

	public String readValue(int address) throws IOException
	{
		long readValue = -1;
		MemoryReader memoryReader = new MemoryReader();

		switch (this)
		{
			case EIGHT_BIT:
				readValue = memoryReader.read(address);
				break;
			case SIXTEEN_BIT:
				readValue = memoryReader.readShort(address);
				break;

			case THIRTY_TWO_BIT:
				readValue = memoryReader.readInt(address);
				break;
		}

		return new Hexadecimal((int) readValue, 8).toString();
	}
}