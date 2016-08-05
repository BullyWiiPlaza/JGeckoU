package wiiudev.gecko.client.connector.rpl;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;

import wiiudev.gecko.client.connector.rpl.filesystem.memory.IAllocatedBuffer;
import wiiudev.gecko.client.connector.rpl.filesystem.memory.IRemoteBuffer;

/**
 * Misc coreinit.rpl methods on the Wii U<br>
 * <br>
 * Be careful about the memory you allocate, the library does not track it for you!<br>
 * <br>
 * TODO Track memory usage
 *
 * @author gudenau
 */
public class CoreInit implements Closeable
{
	public CoreInit()
	{

	}

	/**
	 * Allocates memory on the default heap
	 *
	 * @param size      Size of memory block to allocate
	 * @param alignment Alignment to use when allocating
	 * @return A {@link IAllocatedBuffer } that represents the allocated memory, or NULL if MEMAllocFromDefaultHeapEx failed
	 * @throws IOException   When there is an error talking to the Wii U
	 */
	public IAllocatedBuffer mallocHeap(int size, int alignment, boolean fetch) throws IOException
	{
		/*ExportedSymbol symbol = gecko.getSymbol("coreinit.rpl", "MEMAllocFromDefaultHeapEx", true, true);
		long returnValue = symbol.call(size, alignment);

		if (returnValue == 0)
		{
			return null;
		}

		byte[] data = new byte[size];

		if (fetch)
		{
			gecko.readMemory(returnValue, data);
		}

		IAllocatedBuffer buffer = new SimpleAllocatedBuffer(this, true, gecko, (int) (returnValue & 0x000000000FFFFFFFFL), data);

		if (!fetch)
		{
			buffer.markDirty();
		}

		return buffer;*/
	}

	/**
	 * Frees memory allocated on the default heap
	 *
	 * @param address Address to free
	 * @throws IOException   When there is an error talking to the Wii U
	 * @deprecated Don't use this, this is used internally by the library
	 */
	public void freeHeap(int address) throws IOException
	{
		/*ExportedSymbol symbol = gecko.getSymbol("coreinit.rpl", "MEMFreeToDefaultHeap", true, true);
		symbol.call(address);*/
	}

	/**
	 * Allocates memory from the system
	 *
	 * @param size      Size of memory block to allocate
	 * @param alignment Alignment to use when allocating
	 * @return A {@link IAllocatedBuffer } that represents the allocated memory, or NULL if OSAllocFromSystem failed
	 * @throws IOException   When there is an error talking to the Wii U
	 */
	public IAllocatedBuffer malloc(int size, int alignment, boolean fetch) throws IOException
	{
		/*ExportedSymbol symbol = gecko.getSymbol("coreinit.rpl", "OSAllocFromSystem", false, false);
		long returnValue = symbol.call(size, alignment);

		if (returnValue == 0)
		{
			return null;
		}

		byte[] data = new byte[size];

		if (fetch)
		{
			gecko.readMemory(returnValue, data);
		}

		IAllocatedBuffer buffer = new SimpleAllocatedBuffer(this, false, gecko, (int) (returnValue & 0x000000000FFFFFFFFL), data);

		if (!fetch)
		{
			buffer.markDirty();
		}

		return buffer;*/
	}

	/**
	 * Frees memory allocated from the system
	 *
	 * @param address Address to free
	 * @throws IOException   When there is an error talking to the Wii U
	 * @deprecated Don't use this, this is used internally by the library
	 */
	public void free(int address) throws IOException
	{
		/*ExportedSymbol symbol = gecko.getSymbol("coreinit.rpl", "OSFreeToSystem", false, false);
		symbol.call(address);*/
	}

	/**
	 * Sets an area of memory to a given value
	 *
	 * @param address Address of the buffer to clear
	 * @param value   Value to set the buffer to
	 * @param size    Size of the buffer
	 * @throws IOException   When there is an error talking to the Wii U
	 */
	public void memset(int address, byte value, int size) throws IOException
	{
		/*ExportedSymbol symbol = gecko.getSymbol("coreinit.rpl", "memset", false, false);
		symbol.call(address, value, size);*/
	}

	/**
	 * Sets an area of memory to a given value
	 *
	 * @param buffer The buffer to clear
	 * @param offset Offset relative to the start of the buffer to clear
	 * @param value  Value to set the buffer to
	 * @param size   Size of the buffer
	 * @throws IOException   When there is an error talking to the Wii U
	 * @throws WiiUException When there is a protocol error while talking to the Wii U
	 */
	public void memset(IRemoteBuffer buffer, int offset, byte value, int size) throws IOException
	{
		/*byte[] data = new byte[size];
		buffer.setData(offset, data, 0, size);

		ExportedSymbol symbol = gecko.getSymbol("coreinit.rpl", "memset", false, false);
		symbol.call(buffer.getAddress() + offset, value, size);

		buffer.clearDirty();*/
	}

	/**
	 * Allocates a buffer for a string, then sets the data in the buffer
	 *
	 * @param string  The string to create
	 * @param charset The {@link Charset charset} to use
	 * @param flush   Should the created buffer be flushed to the Wii U?
	 * @return The {@link IAllocatedBuffer IAllocatedBuffer} that contains the string
	 * @throws IOException              If there was an error reading from the Wii U
	 * @throws IllegalArgumentException If the string is null
	 */
	public IAllocatedBuffer createString(String string, Charset charset, boolean flush) throws IOException
	{
		/*if (string == null)
		{
			throw new IllegalArgumentException("Path can not be null");
		}

		// Create the buffer
		IAllocatedBuffer buffer = malloc(string.length() + 1, 0x20, false);
		// Clear it
		memset(buffer, 0, (byte) 0, align(string.length() + 1, 0x20));
		// Fill it, don't need to worry about the null terminator; memset did it already
		buffer.setData(0, string.getBytes(charset), 0, string.length());

		if (flush)
		{
			buffer.flush();
		}

		return buffer;*/
	}

	/**
	 * Simple alignment method<br>
	 * Does not allocate memory, this is client only!
	 *
	 * @param value     The value to align
	 * @param alignment The alignment needed
	 * @return The aligned value
	 */
	public static int align(int value, int alignment)
	{
		return value + alignment - (value % alignment);
	}

	@Override
	public void close() throws IOException
	{

	}
}