package wiiudev.gecko.client.tcpgecko.rpl;

public enum RPL
{
	CORE_INIT("coreinit.rpl");

	private String fileName;

	RPL(String fileName)
	{
		this.fileName = fileName;
	}

	@Override
	public String toString()
	{
		return fileName;
	}
}