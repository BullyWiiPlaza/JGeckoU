package wiiudev.gecko.client.connector.utilities;

/**
 * Class for defining the allowed default memory range to avoid crashing by trying to read invalid memory
 */
public class MemoryRange
{
	public static final int INVALID_ADDRESS = -1;

	private int startingAddress;
	private int endingAddress;

	public MemoryRange(int startingAddress, int endingAddress)
	{
		this.startingAddress = startingAddress;
		this.endingAddress = endingAddress;
	}

	public int getEndingAddress()
	{
		return endingAddress;
	}

	public int getStartingAddress()
	{
		return startingAddress;
	}
	/*public static final int STARTING_ADDRESS = 0x10000000;

	// Default memory range size
	private static int length = 0x3E000000;

	public static final int INVALID_ADDRESS = -1;

	public static boolean isValid(int address)
	{
		return (address >= STARTING_ADDRESS) && (address < getLastAllowedAddress());
	}

	public static int getLastAllowedAddress()
	{
		return STARTING_ADDRESS + length;
	}

	public static void setLastAllowedAddress(int address)
	{
		length = address - STARTING_ADDRESS;
	}
	*/
}