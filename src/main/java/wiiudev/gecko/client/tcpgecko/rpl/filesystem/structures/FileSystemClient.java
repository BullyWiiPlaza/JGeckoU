package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import java.io.IOException;

public class FileSystemClient extends FileSystemObject
{
	public FileSystemClient() throws IOException
	{
		super(0x1700, 0x20);
	}
}