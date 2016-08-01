package wiiudev.gecko.client.connector;

import wiiudev.gecko.client.connector.utilities.AddressRange;
import wiiudev.gecko.client.connector.utilities.DataConversions;
import wiiudev.gecko.client.connector.utilities.MemoryAccessLevel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * A class for writing data to the memory
 */
public class MemoryWriter extends SocketCommunication
{
	/**
	 * Sends <code>command</code> to the server followed by the <code>address</code> AND <code>value</code> to ASSIGN
	 *
	 * @param address The address to write to
	 * @param value   The value to write
	 * @param command The {@link Commands} to execute
	 */
	private void sendWriteCommand(int address, int value, Commands command) throws IOException
	{
		reentrantLock.lock();

		try
		{
			int integerLength = 4;
			AddressRange.assertValidAccess(address, integerLength, MemoryAccessLevel.WRITE);

			sendCommand(command);
			dataSender.writeInt(address);
			dataSender.writeInt(value);
			dataSender.flush();
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Writes an 8-bit <code>value</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to write to
	 * @param value   The value to write
	 */
	public void write(int address, byte value) throws IOException
	{
		reentrantLock.lock();

		try
		{
			sendWriteCommand(address, value, Commands.MEMORY_POKE_8);
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Writes a 16-bit <code>value</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to write to
	 * @param value   The value to write
	 */
	public void writeShort(int address, short value) throws IOException
	{
		reentrantLock.lock();

		try
		{
			sendWriteCommand(address, value, Commands.MEMORY_POKE_16);
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Writes a 32-bit <code>value</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to write to
	 * @param value   The value to write
	 */
	public void writeInt(int address, int value) throws IOException
	{
		reentrantLock.lock();

		try
		{
			sendWriteCommand(address, value, Commands.MEMORY_POKE_32);
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Writes a 32-bit <code>value</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to write to
	 * @param value   The value to write
	 */
	public void writeBoolean(int address, boolean value) throws IOException
	{
		reentrantLock.lock();

		try
		{
			byte booleanValue = (byte) (value ? 1 : 0);
			write(address, booleanValue);
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Writes a 32-bit <code>value</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to write to
	 * @param value   The value to write
	 */
	public void writeFloat(int address, float value) throws IOException
	{
		reentrantLock.lock();

		try
		{
			int floatValue = DataConversions.toInteger(value);
			writeInt(address, floatValue);
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Writes a null-terminated String <code>value</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to write to
	 * @param value   The value to write
	 */
	public void writeString(int address, String value) throws IOException
	{
		reentrantLock.lock();

		try
		{
			byte[] valueBytes = value.getBytes();
			byte[] valueBytesNullTerminated = new byte[valueBytes.length + 1];

			System.arraycopy(valueBytes, 0, valueBytesNullTerminated, 0, valueBytes.length);
			valueBytesNullTerminated[valueBytesNullTerminated.length - 1] = 0;

			writeBytes(address, valueBytesNullTerminated);
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Writes a null-terminated String <code>value</code> to the memory starting at <code>address</code> with a maximum length of <code>maximumLength</code>
	 *
	 * @param address       The address to write to
	 * @param value         The value to write
	 * @param maximumLength The value's maximum allowed amount of characters
	 */
	public void writeString(int address, String value, int maximumLength) throws IOException
	{
		int valueLength = value.length();

		if (valueLength > maximumLength)
		{
			throw new IllegalArgumentException("The text's length is " + valueLength + " but may not exceed " + maximumLength + " characters!");
		}

		writeString(address, value);
	}

	/**
	 * Writes an entire local <code>sourcePath</code> to the memory starting at <code>address</code>
	 *
	 * @param address    The address to write to
	 * @param sourcePath The file to write
	 */
	public void upload(int address, Path sourcePath) throws IOException
	{
		List<byte[]> partitionedBytes = ByteUtilities.readPartitionedBytes(sourcePath, MAXIMUM_MEMORY_CHUNK_SIZE);
		writePartitionedBytes(address, partitionedBytes);
	}

	private void writePartitionedBytes(int address, List<byte[]> partitionedBytes) throws IOException
	{
		for (byte[] bytesChunk : partitionedBytes)
		{
			// The end address is the next starting address
			address = uploadBytes(address, bytesChunk);
		}
	}

	private int uploadBytes(int start, byte[] bytes) throws IOException
	{
		reentrantLock.lock();

		try
		{
			AddressRange.assertValidAccess(start, bytes.length, MemoryAccessLevel.WRITE);
			int end = start + bytes.length;
			sendCommand(Commands.MEMORY_UPLOAD);
			dataSender.writeInt(start);
			dataSender.writeInt(end);
			dataSender.write(bytes);
			dataSender.flush();

			// No need to check the status,but we need to read it at least
			readStatus();

			return end;
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Writes <code>bytes</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to write to
	 * @param bytes   The value to write
	 */
	public void writeBytes(int address, byte[] bytes) throws IOException
	{
		List<byte[]> partitionedBytes = ByteUtilities.partition(bytes, MAXIMUM_MEMORY_CHUNK_SIZE);
		writePartitionedBytes(address, partitionedBytes);
	}
}