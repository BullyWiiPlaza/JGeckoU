package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;

import java.io.IOException;

public class FileSystemHandle extends FileSystemObject
{
	public FileSystemHandle() throws IOException
	{
		super(4, 4);

		int directoryHandleAddress = getAddress();
		CoreInit.setMemory(directoryHandleAddress, 0x00, 4);
	}

	public int dereference() throws IOException
	{
		return MemoryReader.dereference(getAddress());
	}
}