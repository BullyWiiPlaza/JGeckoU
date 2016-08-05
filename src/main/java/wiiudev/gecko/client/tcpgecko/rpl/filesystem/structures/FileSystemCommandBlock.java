package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import java.io.IOException;

public class FileSystemCommandBlock extends FileSystemObject
{
	public FileSystemCommandBlock() throws IOException
	{
		super(0xA80, 0x20);
	}
}