package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;

import java.io.IOException;

public class FileSystemPath extends FileSystemObject
{
	public FileSystemPath(String targetPath) throws IOException
	{
		super(0x200, 0x20);

		setPath(targetPath);
	}

	public void setPath(String targetPath) throws IOException
	{
		int pathAddress = getAddress();
		MemoryWriter memoryWriter = new MemoryWriter();
		memoryWriter.writeString(pathAddress, targetPath);
	}
}
