package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;

import java.io.IOException;

public class FileSystemHandle extends FileSystemObject
{
	public FileSystemHandle() throws IOException
	{
		super(4, 4);
	}

	public int dereference() throws IOException
	{
		int address = getAddress();
		return MemoryReader.deReference(address);
	}
}