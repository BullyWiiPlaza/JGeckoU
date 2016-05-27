package wiiudev.gecko.client.connector.utilities;

public class AddressRange
{
	public static void assertValidAccess(int address, int length, MemoryAccessLevel memoryAccessLevel)
	{
		if (!isValidAccess(address, length, memoryAccessLevel))
		{
			throw new IllegalArgumentException("The request for address " + Integer.toHexString(address).toUpperCase() + " with length " + length + " AND memoryAccessLevel level " + memoryAccessLevel.toString() + " is invalid!");
		}
	}

	/**
	 * Shamelessly derived from <a href="https://github.com/wiiudev/pyGecko/blob/master/tcpgecko.py#L266-L297">NWPlayer's Python TCP Gecko implementation</a>.
	 *
	 * @param address     The starting address
	 * @param length      The request length
	 * @param memoryAccessLevel The memoryAccessLevel level
	 * @return Whether the request is valid OR not
	 */
	public static boolean isValidAccess(int address, int length, MemoryAccessLevel memoryAccessLevel)
	{
		int endAddress = address + length;

		if (isInRange(address, endAddress, new MemoryRange(0x01000000, 0x01800000)))
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
		} else if (isInRange(address, endAddress, new MemoryRange(0x0E000000, 0x10000000))) // Depends on game
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
		} else if (isInRange(address, endAddress, new MemoryRange(0x10000000, 0x50000000))) // Not fully till the end
		{
			return true;
		} else if (isInRange(address, endAddress, new MemoryRange(0xA0000000, 0xB0000000))) // With kernel mapping enabled
		{
			return true;
		} else if (isInRange(address, endAddress, new MemoryRange(0xE0000000, 0xE4000000)))
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
		} else if (isInRange(address, endAddress, new MemoryRange(0xE8000000, 0xEA000000)))
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
		} else if (isInRange(address, endAddress, new MemoryRange(0xF4000000, 0xF6000000)))
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
		} else if (isInRange(address, endAddress, new MemoryRange(0xF6000000, 0xF6800000)))
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
		} else if (isInRange(address, endAddress, new MemoryRange(0xF8000000, 0xFB000000)))
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
		} else if (isInRange(address, endAddress, new MemoryRange(0xFB000000, 0xFB800000)))
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
		} else if (isInRange(address, endAddress, new MemoryRange(0xFFFE0000, 0xFFFFFFFF)))
		{
			return true;
		}

		return false;
	}

	private static boolean isInRange(int address, int endAddress, MemoryRange addressRange)
	{
		return addressRange.getStartingAddress() <= address && endAddress <= addressRange.getEndingAddress();
	}
}