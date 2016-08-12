package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import wiiudev.gecko.client.tcpgecko.rpl.structures.AllocatedMemory;

import java.io.IOException;

public abstract class FileSystemObject extends AllocatedMemory
{
	private boolean registered;

	FileSystemObject(int size, int alignment) throws IOException
	{
		super(size, alignment);
	}

	public boolean isRegistered()
	{
		return registered;
	}

	public void setRegistered(boolean registered)
	{
		this.registered = registered;
	}
}