package wiiudev.gecko.client.tcpgecko.main.utilities.memory;

/**
 * Class for defining the allowed default memory range to avoid crashing by trying to read invalid memory
 */
public class MemoryRange
{
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
}