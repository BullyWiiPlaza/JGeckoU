package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.tcpgecko.main.utilities.memory.AddressRange;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.MemoryRange;

public enum MemoryRangeType
{
	CUSTOM("Custom", null),
	CODE_SECTION("Code Section", AddressRange.appExecutableLibraries),
	DATA_SECTION("Data Section", AddressRange.mem2Region);

	private String text;
	private MemoryRange memoryRange;

	MemoryRangeType(String text, MemoryRange memoryRange)
	{
		this.text = text;
		this.memoryRange = memoryRange;
	}

	public MemoryRange getMemoryRange()
	{
		return memoryRange;
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