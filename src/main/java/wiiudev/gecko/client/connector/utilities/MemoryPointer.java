package wiiudev.gecko.client.connector.utilities;

public class MemoryPointer
{
	private int baseAddress;
	private int[] offsets;

	public MemoryPointer(int baseAddress, int[] offsets)
	{
		this.baseAddress = baseAddress;
		this.offsets = offsets;
	}

	public int[] getOffsets()
	{
		return offsets;
	}

	public int getBaseAddress()
	{
		return baseAddress;
	}
}