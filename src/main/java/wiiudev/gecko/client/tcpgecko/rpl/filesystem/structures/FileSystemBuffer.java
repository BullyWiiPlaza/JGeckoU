package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import java.io.IOException;

public class FileSystemBuffer extends FileSystemObject
{
	public FileSystemBuffer() throws IOException
	{
		super(0x200, 0x20);
	}
}