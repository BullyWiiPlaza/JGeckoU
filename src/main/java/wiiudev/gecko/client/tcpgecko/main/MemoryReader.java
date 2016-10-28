package wiiudev.gecko.client.tcpgecko.main;

import wiiudev.gecko.client.tcpgecko.main.enumerations.Command;
import wiiudev.gecko.client.tcpgecko.main.enumerations.Status;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.DataConversions;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.AddressRange;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.MemoryAccessLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A class for reading data from the memory based on the {@link Connector} class
 */
public class MemoryReader extends TCPGecko
{
	/**
	 * Reads an 8-bit value from the memory at the given <code>address</code>
	 *
	 * @param address The address to read from
	 * @return The read byte
	 */
	public byte read(int address) throws IOException
	{
		byte[] readBytes = readBytes(address, 1);
		return readBytes[0];
	}

	/**
	 * Reads a 16-bit short value from the memory at the given <code>address</code>
	 *
	 * @param address The address to read from
	 * @return The read short
	 */
	public short readShort(int address) throws IOException
	{
		byte[] readBytes = readBytes(address, 2);
		return ByteBuffer.wrap(readBytes).getShort();
	}

	/**
	 * Reads a 32-bit integer value from the memory at the given <code>address</code>
	 *
	 * @param address The address to read from
	 * @return The read value
	 */
	public int readInt(int address) throws IOException
	{
		byte[] readBytes = readBytes(address, 4);
		return ByteBuffer.wrap(readBytes).getInt();
	}

	/**
	 * Reads a 32-bit floating point value from the memory at the given <code>address</code>
	 *
	 * @param address The address to read from
	 * @return The read float
	 */
	public float readFloat(int address) throws IOException
	{
		int readInt = readInt(address);
		return DataConversions.toFloat((long) readInt);
	}

	/**
	 * Reads a null-terminated String from the memory at the given <code>address</code>
	 *
	 * @param address The address the String starts at
	 * @return The retrieved String
	 */
	public String readString(int address) throws IOException
	{
		byte[] bytesRead;
		StringBuilder stringBuilder = new StringBuilder();
		int byteBufferSize = 100; // The amount of bytes to read and inspect at once

		while (true)
		{
			bytesRead = readBytes(address, byteBufferSize);

			for (byte byteRead : bytesRead)
			{
				if (byteRead == 0)
				{
					// The String has ended
					return stringBuilder.toString();
				}

				char letter = DataConversions.toCharacter(byteRead);
				stringBuilder.append(letter);
			}

			address += byteBufferSize;
		}
	}

	/**
	 * Reads a boolean value from the memory at the given <code>address</code>
	 *
	 * @param address The address to start reading from
	 * @return The read boolean
	 */
	public boolean readBoolean(int address) throws IOException
	{
		try (CloseableReentrantLock ignored = reentrantLock.acquire())
		{
			byte readByte = read(address);
			return readByte == 1;
		}
	}

	public int readFirmwareVersion() throws IOException
	{
		try (CloseableReentrantLock ignored = reentrantLock.acquire())
		{
			sendCommand(Command.GET_OS_VERSION);
			dataSender.flush();

			return dataReceiver.readInt();
		}
	}

	/**
	 * Searches the server's memory starting at <code>address</code> for <code>value</code> for <code>length</code> amount of bytes
	 *
	 * @param address The address to start searching at
	 * @param length  The maximum range of the search
	 * @param value   The value to search for
	 * @return The address where <code>value</code> occurred the first time or 0 if not found
	 */
	public int search(int address, int length, int value) throws IOException
	{
		try (CloseableReentrantLock ignored = reentrantLock.acquire())
		{
			AddressRange.assertValidAccess(address, length, MemoryAccessLevel.READ);

			sendCommand(Command.MEMORY_SEARCH_32);
			dataSender.writeInt(address);
			dataSender.writeInt(value);
			dataSender.writeInt(length);
			dataSender.flush();

			return dataReceiver.readInt();
		}
	}

	public int search(int address, int length, byte[] bytes) throws IOException
	{
		try (CloseableReentrantLock ignored = reentrantLock.acquire())
		{
			AddressRange.assertValidAccess(address, length + bytes.length, MemoryAccessLevel.READ);

			sendCommand(Command.MEMORY_SEARCH);

			ByteBuffer byteBuffer = ByteBuffer.allocate(TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE);
			byteBuffer.putInt(address);
			byteBuffer.putInt(address + length);
			byteBuffer.putInt(bytes.length);
			byteBuffer.put(bytes);
			byte[] paddingBytes = new byte[TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE - byteBuffer.array().length];
			byteBuffer.put(paddingBytes);
			// System.out.println(Arrays.toString(byteBuffer.array()));
			dataSender.write(byteBuffer.array());
			dataSender.flush();

			return dataReceiver.readInt();
		}
	}

	public boolean isRunning() throws IOException
	{
		try (CloseableReentrantLock ignored = reentrantLock.acquire())
		{
			sendCommand(Command.GET_STATUS);
			dataSender.flush();

			Status receivedStatus = readStatus();
			return receivedStatus == Status.RUNNING;
		}
	}

	/* // Freezes
	public int kernelReadInt(int address) throws IOException
	{
		reentrantLock.lock();

		try
		{
			AddressRange.assertValidAccess(address, 4, MemoryAccessLevel.READ);

			sendCommand(Command.MEMORY_KERNEL_READ);
			dataSender.writeInt(address);
			dataSender.flush();

			Status status = readStatus();

			if (status == Status.OK)
			{
				return dataReceiver.readInt();
			}
			else
			{
				throw new IllegalStateException("Unexpected status: " + status);
			}
		} finally
		{
			reentrantLock.unlock();
		}
	}*/

	public static int dereference(int address) throws IOException
	{
		MemoryReader memoryReader = new MemoryReader();
		return memoryReader.readInt(address);
	}

	/**
	 * Reads a block of memory with length <code>bytes</code> starting at <code>address</code>
	 *
	 * @param address The address to start dumping memory at
	 * @param length  The amount of bytes to dump
	 */
	public byte[] readBytes(int address, int length) throws IOException
	{
		try (CloseableReentrantLock ignored = reentrantLock.acquire())
		{
			requestBytes(address, length);

			ByteArrayOutputStream receiverBuffer = new ByteArrayOutputStream();

			while (length > 0)
			{
				int chunkSize = length;

				if (chunkSize > MAXIMUM_MEMORY_CHUNK_SIZE)
				{
					chunkSize = MAXIMUM_MEMORY_CHUNK_SIZE;
				}

				byte[] readBytes = readBytes(chunkSize);
				receiverBuffer.write(readBytes);

				length -= chunkSize;
			}

			return receiverBuffer.toByteArray();
		} finally
		{
			TCPGecko.hasRequestedBytes = false;
		}
	}

	public void requestBytes(int address, int length) throws IOException
	{
		AddressRange.assertValidAccess(address, length, MemoryAccessLevel.READ);

		try
		{
			sendCommand(Command.MEMORY_READ);
			dataSender.writeInt(address);
			dataSender.writeInt(address + length);
			dataSender.flush();
		} finally
		{
			TCPGecko.hasRequestedBytes = true;
		}
	}

	public byte[] readBytes(int length) throws IOException
	{
		Status status = readStatus();
		byte[] received = new byte[length];

		if (status == Status.OK)
		{
			dataReceiver.readFully(received);
		}

		return received;
	}

	public int kernelReadInt(int address) throws IOException
	{
		try (CloseableReentrantLock ignored = reentrantLock.acquire())
		{
			sendCommand(Command.MEMORY_KERNEL_READ);
			dataSender.writeInt(address);
			dataSender.flush();

			return dataReceiver.readInt();
		}
	}

	public void disassembleRange(int address, int length) throws IOException
	{
		try (CloseableReentrantLock ignored = reentrantLock.acquire())
		{
			sendCommand(Command.MEMORY_DISASSEMBLE);
			dataSender.writeInt(address);
			dataSender.writeInt(address + length);
			dataSender.flush();
		}
	}
}