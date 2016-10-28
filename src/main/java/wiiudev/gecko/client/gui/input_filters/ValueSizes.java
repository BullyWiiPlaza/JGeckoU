package wiiudev.gecko.client.gui.input_filters;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;

import java.io.IOException;

public enum ValueSizes
{
	EIGHT_BIT("8-Bit", 2),
	SIXTEEN_BIT("16-Bit", 4),
	THIRTY_TWO_BIT("32-Bit", 8);

	private String text;
	private int size;

	ValueSizes(String text, int size)
	{
		this.text = text;
		this.size = size;
	}

	public int getSize()
	{
		return size;
	}

	@Override
	public String toString()
	{
		return text;
	}

	public int readValue(int targetAddress) throws IOException
	{
		int currentValue = -1;
		MemoryReader memoryReader = new MemoryReader();

		switch (this)
		{
			case EIGHT_BIT:
				currentValue = memoryReader.read(targetAddress);
				break;

			case SIXTEEN_BIT:
				currentValue = memoryReader.readShort(targetAddress);
				break;

			case THIRTY_TWO_BIT:
				currentValue = memoryReader.readInt(targetAddress);
				break;
		}

		return currentValue;
	}
}