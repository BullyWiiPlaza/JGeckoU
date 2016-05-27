package wiiudev.gecko.client.connector;

import org.apache.commons.io.FileUtils;
import wiiudev.gecko.client.connector.utilities.AddressRange;
import wiiudev.gecko.client.connector.utilities.DataConversions;
import wiiudev.gecko.client.connector.utilities.MemoryAccessLevel;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A class for writing data to the memory
 */
public class MemoryWriter extends SocketCommunication
{
	/**
	 * Sends <code>command</code> to the server followed by the <code>address</code> AND <code>value</code> to ASSIGN
	 *
	 * @param address The address to ASSIGN to
	 * @param value   The value to ASSIGN
	 * @param command The {@link Commands} to execute
	 * @throws IOException
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
	 * @param address The address to ASSIGN to
	 * @param value   The value to ASSIGN
	 * @throws IOException
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
	 * @param address The address to ASSIGN to
	 * @param value   The value to ASSIGN
	 * @throws IOException
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
	 * @param address The address to ASSIGN to
	 * @param value   The value to ASSIGN
	 * @throws IOException
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
	 * @param address The address to ASSIGN to
	 * @param value   The value to ASSIGN
	 * @throws IOException
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
	 * @param address The address to ASSIGN to
	 * @param value   The value to ASSIGN
	 * @throws IOException
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
	 * @param address The address to ASSIGN to
	 * @param value   The value to ASSIGN
	 * @throws IOException
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
	 * @param address       The address to ASSIGN to
	 * @param value         The value to ASSIGN
	 * @param maximumLength The value's maximum allowed amount of characters
	 * @throws IOException
	 */
	public void writeString(int address, String value, int maximumLength) throws IOException
	{
		reentrantLock.lock();

		try
		{
			int valueLength = value.length();

			if (valueLength > maximumLength)
			{
				throw new IllegalArgumentException("The text's length is " + valueLength + " but may not exceed " + maximumLength + " characters!");
			}

			writeString(address, value);
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Writes an entire <code>file</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to ASSIGN to
	 * @param file    The file to ASSIGN
	 * @throws IOException
	 */
	public void upload(int address, File file) throws IOException
	{
		reentrantLock.lock();

		try
		{
			byte[] bytes = FileUtils.readFileToByteArray(file);

			writeBytes(address, bytes);
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Writes <code>bytes</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to ASSIGN to
	 * @param bytes   The value to ASSIGN
	 * @throws IOException
	 */
	public void writeBytes(int address, byte[] bytes) throws IOException
	{
		reentrantLock.lock();

		try
		{
			ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
			int bufferPointer = 0;

			while (byteBuffer.hasRemaining())
			{
				// We can read an int
				if (byteBuffer.remaining() > 3)
				{
					int intValue = byteBuffer.getInt();
					writeInt(address + bufferPointer, intValue);
					bufferPointer += 4;

					continue;
				}

				// We can read a short
				if (byteBuffer.remaining() > 1)
				{
					short shortValue = byteBuffer.getShort();
					writeShort(address + bufferPointer, shortValue);
					bufferPointer += 2;

					continue;
				}

				// We only have a byte left
				byte byteValue = byteBuffer.get();
				write(address + bufferPointer, byteValue);
				bufferPointer++;
			}
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Pokes successive addresses with the same value
	 *
	 * @param startingAddress       The address to start poking memory at
	 * @param value                 The value to writeInt
	 * @param additionalWritesCount The amount of additional pokes to perform
	 * @throws IOException
	 */
	public void serialWrite(int startingAddress, int value, int additionalWritesCount) throws IOException
	{
		reentrantLock.lock();

		try
		{
			for (int writesIndex = 0; writesIndex < additionalWritesCount + 1; writesIndex++)
			{
				writeInt(startingAddress + writesIndex * 4, value);
			}
		} finally
		{
			reentrantLock.lock();
		}
	}
}