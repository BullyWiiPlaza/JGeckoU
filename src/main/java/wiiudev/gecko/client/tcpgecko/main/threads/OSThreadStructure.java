package wiiudev.gecko.client.tcpgecko.main.threads;

public enum OSThreadStructure
{
	AFFINITY(0x304),
	SUSPEND(0x328),
	PRIORITY(0x330),
	NAME(0x5C0),
	LINK_ACTIVE(0x38C);

	private int offset;

	OSThreadStructure(int offset)
	{
		this.offset = offset;
	}

	public int getOffset()
	{
		return offset;
	}
}