package wiiudev.gecko.client.tcpgecko.main;

import org.apache.commons.io.output.ByteArrayOutputStream;
import wiiudev.gecko.client.gui.input_filters.ValueSizes;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.ValueSize;
import wiiudev.gecko.client.tcpgecko.main.enumerations.Commands;
import wiiudev.gecko.client.tcpgecko.main.enumerations.Status;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.DataConversions;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.AddressRange;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.MemoryAccessLevel;

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
		reentrantLock.lock();

		try
		{
			byte[] readBytes = readBytes(address, 1);
			return readBytes[0];
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Reads a 16-bit short value from the memory at the given <code>address</code>
	 *
	 * @param address The address to read from
	 * @return The read short
	 */
	public short readShort(int address) throws IOException
	{
		reentrantLock.lock();

		try
		{
			byte[] readBytes = readBytes(address, 2);
			return ByteBuffer.wrap(readBytes).getShort();
		} finally
		{
			reentrantLock.unlock();
		}
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
		reentrantLock.lock();

		try
		{
			int readInt = readInt(address);
			return DataConversions.toFloat((long) readInt);
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Reads a null-terminated String from the memory at the given <code>address</code>
	 *
	 * @param address The address the String starts at
	 * @return The retrieved String
	 */
	public String readString(int address) throws IOException
	{
		reentrantLock.lock();

		try
		{
			byte[] bytesRead;
			StringBuilder stringBuilder = new StringBuilder();
			int byteBufferSize = 100; // The amount of bytes to read AND inspect at once

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
		} finally
		{
			reentrantLock.unlock();
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
		reentrantLock.lock();

		try
		{
			byte readByte = read(address);
			return readByte == 1;
		} finally
		{
			reentrantLock.unlock();
		}
	}

	public int readFirmwareVersion() throws IOException
	{
		reentrantLock.lock();

		try
		{
			sendCommand(Commands.GET_OS_VERSION);
			dataSender.flush();

			return dataReceiver.readInt();
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Searches the server's memory starting at <code>address</code> for <code>value</code> for <code>length</code> amount of bytes
	 *
	 * @param address The address to start searching at
	 * @param value   The value to search_old for
	 * @param length  The maximum range of the search_old
	 * @return The address where <code>value</code> occurred the first time OR 0 if not found
	 */
	public int search(int address, int value, int length) throws IOException
	{
		reentrantLock.lock();

		try
		{
			AddressRange.assertValidAccess(address, 4, MemoryAccessLevel.READ);

			sendCommand(Commands.MEMORY_SEARCH_32);
			dataSender.write(address);
			dataSender.write(value);
			dataSender.write(length);
			dataSender.flush();

			return dataReceiver.read();
		} finally
		{
			reentrantLock.unlock();
		}
	}

	public int readValue(int targetAddress, ValueSizes valueSize) throws IOException
	{
		reentrantLock.lock();

		try
		{
			int currentValue = -1;

			switch (valueSize)
			{
				case EIGHT_BIT:
					currentValue = read(targetAddress);
					break;

				case SIXTEEN_BIT:
					currentValue = readShort(targetAddress);
					break;

				case THIRTY_TWO_BIT:
					currentValue = readInt(targetAddress);
					break;
			}

			return currentValue;
		} finally
		{
			reentrantLock.unlock();
		}
	}

	public String readValue(int address, ValueSize valueSize) throws IOException
	{
		long readValue = -1;

		switch (valueSize)
		{
			case EIGHT_BIT:
				readValue = read(address);
				break;
			case SIXTEEN_BIT:
				readValue = readShort(address);
				break;

			case THIRTY_TWO_BIT:
				readValue = readInt(address);
				break;
		}

		return new Hexadecimal((int) readValue, 8).toString();
	}

	public boolean isRunning() throws IOException
	{
		reentrantLock.lock();

		try
		{
			sendCommand(Commands.GET_STATUS);
			dataSender.flush();

			Status receivedStatus = readStatus();

			return receivedStatus == Status.RUNNING;
		} finally
		{
			reentrantLock.unlock();
		}
	}

	public int readKernelInt(int address) throws IOException
	{
		reentrantLock.lock();

		try
		{
			sendCommand(Commands.MEMORY_KERNEL_READ);
			dataSender.writeInt(address);
			dataSender.flush();

			return dataReceiver.readInt();
		} finally
		{
			reentrantLock.unlock();
		}
	}

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
		reentrantLock.lock();

		try
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
			reentrantLock.unlock();
		}
	}

	public void requestBytes(int address, int length) throws IOException
	{
		reentrantLock.lock();

		try
		{
			AddressRange.assertValidAccess(address, length, MemoryAccessLevel.READ);

			sendCommand(Commands.MEMORY_READ);
			dataSender.writeInt(address);
			dataSender.writeInt(address + length);
			dataSender.flush();

			TCPGecko.hasRequestedBytes = true;
		} finally
		{
			reentrantLock.unlock();
		}
	}

	public byte[] readBytes(int length) throws IOException
	{
		reentrantLock.lock();

		try
		{
			Status status = readStatus();
			byte[] received = new byte[length];

			if (status == Status.OK)
			{
				dataReceiver.readFully(received);
			}

			return received;
		} finally
		{
			reentrantLock.unlock();
		}
	}
}