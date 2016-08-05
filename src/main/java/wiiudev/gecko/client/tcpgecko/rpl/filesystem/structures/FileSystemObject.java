package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;

import java.io.IOException;

public abstract class FileSystemObject
{
	protected int address;
	private boolean registered;

	FileSystemObject(int size, int alignment) throws IOException
	{
		address = CoreInit.allocateDefaultHeapMemory(size, alignment);
	}

	public int getAddress()
	{
		return address;
	}

	public void setAddress(int address)
	{
		this.address = address;
	}

	public boolean isRegistered()
	{
		return registered;
	}

	public void setRegistered(boolean registered)
	{
		this.registered = registered;
	}

	public void free() throws IOException
	{
		CoreInit.freeDefaultHeapMemory(address);
	}
}