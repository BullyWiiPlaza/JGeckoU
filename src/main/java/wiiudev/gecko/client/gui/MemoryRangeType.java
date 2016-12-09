package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.tcpgecko.main.utilities.memory.AddressRange;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.MemoryRange;

public enum MemoryRangeType
{
	CUSTOM("Custom"),
	CODE_SECTION("Code Section"),
	DATA_SECTION("Data Section");

	private String text;

	MemoryRangeType(String text)
	{
		this.text = text;
	}

	public MemoryRange getMemoryRange()
	{
		switch (this)
		{
			case CODE_SECTION:
				return AddressRange.appExecutableLibraries;

			case DATA_SECTION:
				return AddressRange.mem2Region;

			default:
				return null;
		}
	}

	@Override
	public String toString()
	{
		return text;
	}

	public static MemoryRangeType parse(String dumpingMemoryRange)
	{
		MemoryRangeType[] memoryRangeTypes = values();
		for (MemoryRangeType memoryRangeType : memoryRangeTypes)
		{
			if (memoryRangeType.toString().equals(dumpingMemoryRange))
			{
				return memoryRangeType;
			}
		}

		return null;
	}
}