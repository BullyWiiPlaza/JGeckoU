package wiiudev.gecko.client.tcpgecko.main.threads;

public enum OSThreadStruct
{
	SUSPEND(0x328),
	NAME(0x5C0),
	LINK_ACTIVE(0x38C);

	private int offset;

	OSThreadStruct(int offset)
	{
		this.offset = offset;
	}

	public int getOffset()
	{
		return offset;
	}
}