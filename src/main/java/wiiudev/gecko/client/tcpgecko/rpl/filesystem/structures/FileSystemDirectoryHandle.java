package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;

import java.io.IOException;

public class FileSystemDirectoryHandle extends FileSystemObject
{
	public FileSystemDirectoryHandle() throws IOException
	{
		super(4, 4);
	}

	public int dereference() throws IOException
	{
		return MemoryReader.dereference(getAddress());
	}
}