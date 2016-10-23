package wiiudev.gecko.client.tcpgecko.main.utilities.memory;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import java.io.IOException;

public class AddressRange
{
	// Use default values at first
	public static MemoryRange appExecutableLibraries = new MemoryRange(0x01800000, 0x10000000 - 1);
	public static MemoryRange mem2Region = new MemoryRange(0x10000000, 0x50000000);
	public static MemoryRange kernelMapping = new MemoryRange(0xA0000000, 0xB0000000);

	public static void assertValidAccess(int address, int length, MemoryAccessLevel memoryAccessLevel)
	{
		if (!isValidAccess(address, length, memoryAccessLevel))
		{
			throw new IllegalArgumentException("The request for address " + Integer.toHexString(address).toUpperCase() + " with length " + Conversions.toHexadecimal(length) + " and memory access level " + memoryAccessLevel.toString() + " is invalid!");
		}
	}

	public static void setMEM2RegionEnd() throws IOException
	{
		appExecutableLibraries.updateMemoryRange(true);
	}

	public static void setAppExecutableLibrariesStart() throws IOException
	{
		mem2Region.updateMemoryRange(false);
	}

	/**
	 * Shamelessly derived from <a href="https://github.com/wiiudev/pyGecko/blob/master/tcpgecko.py#L266-L297">NWPlayer's Python TCP Gecko dialogs</a>.
	 *
	 * @param address           The starting address
	 * @param length            The request length
	 * @param memoryAccessLevel The memoryAccessLevel level
	 * @return Whether the request is valid or not
	 */
	public static boolean isValidAccess(int address, int length, MemoryAccessLevel memoryAccessLevel)
	{
		if (!TCPGecko.enforceMemoryAccessProtection)
		{
			return true;
		}

		int endAddress = address + length;

		if (isInRange(address, endAddress, new MemoryRange(0x01000000, 0x01800000)))
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
			// return true;
		} else if (isInRange(address, endAddress, appExecutableLibraries)) // Depends on game
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
		} else if (isInRange(address, endAddress, mem2Region)) // Not fully till the end
		{
			return true;
		} else if (isInRange(address, endAddress, kernelMapping)) // With kernel mapping enabled
		{
			return true;
			/*if (TCPGecko.isConnected())
			{
				try
				{
					// Let us know if it's mapped
					return CoreInit.isAddressMapped(address);
				} catch (Exception exception)
				{
					StackTraceUtils.handleException(null, exception);
				}
			}

			// We're defensive so not allowed
			return false;*/
		} else if (isInRange(address, endAddress, new MemoryRange(0xE0000000, 0xE4000000)))
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
		} else if (isInRange(address, endAddress, new MemoryRange(0xE8000000, 0xEA000000)))
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
		} else if (isInRange(address, endAddress, new MemoryRange(0xF4000000, 0xF6000000 - 1)))
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
		} else if (isInRange(address, endAddress, new MemoryRange(0xF6000000, 0xF6800000)))
		{
			return memoryAccessLevel == MemoryAccessLevel.READ;
		} else if (isInRange(address, endAddress, new MemoryRange(0xF8000000, 0xFB000000 - 1)))
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

	public static boolean isInRange(int address)
	{
		return isInRange(address, address + 4, new MemoryRange(0x10000000, 0x50000000));
	}

	private static boolean isInRange(int address, int endAddress, MemoryRange addressRange)
	{
		return addressRange.getStartingAddress() <= address && endAddress <= addressRange.getEndingAddress();
	}
}