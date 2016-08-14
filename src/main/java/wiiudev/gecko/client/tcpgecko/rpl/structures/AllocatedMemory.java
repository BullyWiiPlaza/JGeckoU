package wiiudev.gecko.client.tcpgecko.rpl.structures;

import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;

import java.io.Closeable;
import java.io.IOException;

public class AllocatedMemory implements Closeable
{
	private int address;
	private int size;

	public AllocatedMemory(int size, int alignment) throws IOException
	{
		address = CoreInit.allocateDefaultHeapMemory(size, alignment);
		this.size = size;
	}

	public int getAddress()
	{
		return address;
	}

	@Override
	public void close() throws IOException
	{
		CoreInit.freeDefaultHeapMemory(address);
	}

	public int getSize()
	{
		return size;
	}
}