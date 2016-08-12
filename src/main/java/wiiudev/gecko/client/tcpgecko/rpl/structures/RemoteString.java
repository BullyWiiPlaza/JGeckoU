package wiiudev.gecko.client.tcpgecko.rpl.structures;

import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;

import java.io.Closeable;
import java.io.IOException;

public class RemoteString implements Closeable
{
	private String string;
	private int address;

	public RemoteString(String string) throws IOException
	{
		this.string = string;
		this.address = allocateString(string);
	}

	public int getAddress()
	{
		return address;
	}

	public String getString()
	{
		return string;
	}

	/**
	 * Allocates a String in the memory. The address is determined by the allocator
	 *
	 * @param string The String to allocate
	 * @return The address pointing to the beginning of the allocated String
	 */
	private int allocateString(String string) throws IOException
	{
		// Allocate space for the String
		int stringLength = string.length();
		int address = CoreInit.allocateSystemMemory(stringLength, 0x20);
		int size = getDataSize(stringLength);

		// Initialize the memory with zeros
		CoreInit.setMemory(address, 0x00, size);

		// Set the String
		MemoryWriter memoryWriter = new MemoryWriter();
		memoryWriter.writeString(address, string);

		return address;
	}

	private int getDataSize(int length)
	{
		return length + (32 - (length % 32));
	}

	@Override
	public void close() throws IOException
	{
		CoreInit.freeSystemMemory(address);
	}
}