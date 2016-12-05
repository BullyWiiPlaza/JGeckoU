package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;

import java.io.IOException;

public class FileSystemPath extends FileSystemObject
{
	private String path;

	public FileSystemPath(String targetPath) throws IOException
	{
		super(0x200, 0x20);

		if (isAllocated())
		{
			setPath(targetPath);
		}
	}

	public void setPath(String targetPath) throws IOException
	{
		this.path = targetPath;
		int pathAddress = getAddress();
		MemoryWriter memoryWriter = new MemoryWriter();
		memoryWriter.writeString(pathAddress, targetPath);
	}

	public String getPath()
	{
		return path;
	}

	public void append(String path) throws IOException
	{
		setPath(getPath() + "/" + path);
	}
}