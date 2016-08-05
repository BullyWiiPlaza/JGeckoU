package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import java.io.IOException;

public class FileSystemPath extends FileSystemObject
{
	public FileSystemPath() throws IOException
	{
		super(0x200, 0x20);
	}
}
