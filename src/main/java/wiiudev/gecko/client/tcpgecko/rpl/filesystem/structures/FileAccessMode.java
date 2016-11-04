package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

public enum FileAccessMode
{
	READ("r"),
	WRITE("w"),
	APPEND("a"),
	UPDATE("r+"),
	WRITE_READ("w+"),
	APPEND_READ("a+");

	private String accessMode;

	FileAccessMode(String accessMode)
	{
		this.accessMode = accessMode;
	}

	@Override
	public String toString()
	{
		return accessMode;
	}
}