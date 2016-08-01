package wiiudev.gecko.client.connector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import wiiudev.gecko.client.connector.utilities.AddressRange;
import wiiudev.gecko.client.connector.utilities.DataConversions;
import wiiudev.gecko.client.connector.utilities.Hexadecimal;
import wiiudev.gecko.client.connector.utilities.MemoryAccessLevel;
import wiiudev.gecko.client.gui.code_list.code_wizard.selections.ValueSize;
import wiiudev.gecko.client.gui.inputFilter.ValueSizes;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class for reading data from the memory based on the {@link Connector} class
 */
public class MemoryReader extends SocketCommunication
{
	private static int SMALL_RPC_PARAMETERS_COUNT = 8;
	private static String coreInitRPL = "coreinit.rpl";

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
		reentrantLock.lock();

		try
		{
			byte[] readBytes = readBytes(address, 4);
			return ByteBuffer.wrap(readBytes).getInt();
		} finally
		{
			reentrantLock.unlock();
		}
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

	/**
	 * Dumps a block of memory with length <code>bytes</code> to a local <code>file</code> starting at <code>address</code>
	 *
	 * @param address    The address to start dumping memory at
	 * @param bytesCount The amount of bytes to dump
	 * @param targetFile The file to store the data to
	 */
	public void dump(int address, int bytesCount, File targetFile) throws IOException
	{
		reentrantLock.lock();

		int startingBytesCount = bytesCount;
		int bytesDumped = 0;

		try
		{
			// Delete the target file before dumping into it
			FileUtils.deleteQuietly(targetFile);

			// Read in chunks
			while (bytesCount > MAXIMUM_MEMORY_CHUNK_SIZE)
			{
				byte[] retrievedBytes = readBytes(address, MAXIMUM_MEMORY_CHUNK_SIZE);
				FileUtils.writeByteArrayToFile(targetFile, retrievedBytes, true);

				bytesCount -= MAXIMUM_MEMORY_CHUNK_SIZE;
				address += MAXIMUM_MEMORY_CHUNK_SIZE;
				bytesDumped += MAXIMUM_MEMORY_CHUNK_SIZE;
				System.out.println("Dumped: " + (double) bytesDumped / startingBytesCount);
			}

			if (bytesCount <= MAXIMUM_MEMORY_CHUNK_SIZE)
			{
				byte[] retrievedBytes = readBytes(address, bytesCount);
				FileUtils.writeByteArrayToFile(targetFile, retrievedBytes, true);
			}
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
	 * @param value   The value to search for
	 * @param length  The maximum range of the search
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

	/**
	 * Reads <code>length</code> bytes from the memory starting at <code>address</code>
	 *
	 * @param address The address to start reading memory from
	 * @param length  The amount of bytes to read
	 * @return A byte array containing all read bytes
	 */
	public byte[] readBytes(int address, int length) throws IOException
	{
		reentrantLock.lock();

		try
		{
			AddressRange.assertValidAccess(address, length, MemoryAccessLevel.READ);
			byte[] received = new byte[length];

			sendCommand(Commands.MEMORY_READ);
			dataSender.writeInt(address);
			dataSender.writeInt(address + length);
			dataSender.flush();

			Status status = readStatus();

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

	public static String getExpectedWaitingTime(int bytesCount)
	{
		int bytesPerMillisecond = 61; // The network bandwidth might differ
		return millisecondsToDateString(bytesCount / bytesPerMillisecond);
	}

	private static String millisecondsToDateString(long expectedWaitingTimeMilliseconds)
	{
		long seconds = (expectedWaitingTimeMilliseconds / 1000) % 60;
		long minutes = (expectedWaitingTimeMilliseconds / (1000 * 60)) % 60;
		long hours = (expectedWaitingTimeMilliseconds / (1000 * 60 * 60)) % 24;

		return String.format("%02d hours %02d minutes and %02d seconds", hours, minutes, seconds);
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
		reentrantLock.lock();

		try
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
		} finally
		{
			reentrantLock.unlock();
		}
	}

	public ExportedSymbol getSymbol(String rplName, String symbolName) throws IOException
	{
		return getSymbol(rplName, symbolName, false, false);
	}

	private ExportedSymbol getSymbol(String rplName, String symbolName, boolean isPointer, boolean isData) throws IOException
	{
		sendCommand(Commands.GET_SYMBOL);

		ByteArrayOutputStream symbolRequest = getSymbolRequest(rplName, symbolName);

		dataSender.writeByte(symbolRequest.size());
		dataSender.write(symbolRequest.toByteArray());
		dataSender.writeByte(isData ? 1 : 0);
		dataSender.flush();

		int address = dataReceiver.readInt();

		if (isPointer)
		{
			// Dereference pointer if we need to
			address = readInt(address);
		}

		return new ExportedSymbol(address, rplName, symbolName);
	}

	private ByteArrayOutputStream getSymbolRequest(String rplName, String symbolName) throws IOException
	{
		// We need to buffer the strings to get the length
		ByteArrayOutputStream request = new ByteArrayOutputStream();

		// This length is static at least
		request.write(toByteArray(8));

		int length = 8 + rplName.length() + 1;
		byte[] lengthBytes = toByteArray(length);
		request.write(lengthBytes);

		// Write the UTF-8 encoded null-terminated names
		request.write(rplName.getBytes(StandardCharsets.UTF_8));
		request.write(0);
		request.write(symbolName.getBytes(StandardCharsets.UTF_8));
		request.write(0);

		return request;
	}

	private byte[] toByteArray(int integer)
	{
		return ByteBuffer.allocate(4).putInt(integer).array();
	}

	public long call(String rplName, String symbolName, int... parameters) throws IOException
	{
		ExportedSymbol exportedSymbol = getSymbol(rplName, symbolName, false, false);
		return exportedSymbol.call(parameters);
	}

	/**
	 * Calls a remote method.
	 *
	 * @param exportedSymbol The symbol that defines the method
	 * @param parameters     The parameters for the method
	 * @throws IllegalArgumentException If there are to many parameters
	 */
	public long call(ExportedSymbol exportedSymbol, int... parameters) throws IOException
	{
		int paddingIntegersCount = getPaddingIntegersCount(parameters);

		List<Integer> parametersList = Arrays.stream(parameters).boxed().collect(Collectors.toList());
		for (int paddingCountIndex = 0; paddingCountIndex < paddingIntegersCount; paddingCountIndex++)
		{
			// Pad for the parameters count for the small or big RPC calls respectively
			parametersList.add(0);
		}

		Commands command = parametersList.size() == SMALL_RPC_PARAMETERS_COUNT
				? Commands.RPC : Commands.RPC_BIG;
		sendCommand(command);
		int functionAddress = exportedSymbol.getAddress();
		dataSender.writeInt(functionAddress);

		for (int parameter : parametersList)
		{
			dataSender.writeInt(parameter);
		}

		dataSender.flush();

		return dataReceiver.readLong();
	}

	private int getPaddingIntegersCount(int... parameters)
	{
		int parametersCount = parameters.length;

		if (parametersCount <= SMALL_RPC_PARAMETERS_COUNT)
		{
			return SMALL_RPC_PARAMETERS_COUNT - parametersCount;
		}

		int BIG_RPC_PARAMETERS_COUNT = 16;
		if (parametersCount <= BIG_RPC_PARAMETERS_COUNT)
		{
			return BIG_RPC_PARAMETERS_COUNT - parametersCount;
		}

		throw new IllegalArgumentException("Invalid parameters count: " + parametersCount);
	}

	/**
	 * Allocates <code>size</code> bytes of memory
 	 * @param size The amount of memory to allocate
	 * @return The starting address of the allocated memory
	 */
	public int allocateMemory(int size, boolean align) throws IOException
	{
		ExportedSymbol exportedSymbol = getSymbol(coreInitRPL, "OSAllocFromSystem");
		return (int) exportedSymbol.call(size, align ? 1 : 0);
	}

	public boolean isMapped(int address) throws IOException
	{
		ExportedSymbol exportedSymbol = getSymbol(coreInitRPL, "OSEffectiveToPhysical");
		return exportedSymbol.call(address) != 0;
	}

	/**
	 * Frees memory previous allocated
	 * @param address The <code>address</code> to free memory from
	 */
	public void freeMemory(int address) throws IOException
	{
		ExportedSymbol exportedSymbol = getSymbol(coreInitRPL, "OSFreeToSystem");
		exportedSymbol.call(address);
	}

	/**
	 * Allocates a String in the memory
	 * @param string The String to allocate
	 * @return The starting address of the String
	 */
	private int allocateString(String string) throws IOException
	{
		int length = string.length();
		int address = allocateMemory(length + 1, true);
		MemoryWriter memoryWriter = new MemoryWriter();
		memoryWriter.writeString(address, string);

		return address;
	}

	/**
	 * Print the message <code>message</code> to the screen then halts the system
	 * @param message The message to print
	 */
	public void displayFatalErrorMessage(String message) throws IOException
	{
		ExportedSymbol exportedSymbol = getSymbol(coreInitRPL, "OSFatal");
		int allocatedString = allocateString(message);
		exportedSymbol.call(allocatedString);
	}

	public int getEffectiveToPhysical(int address) throws IOException
	{
		ExportedSymbol exportedSymbol = getSymbol("coreinit.rpl", "OSEffectiveToPhysical");
		long physical = exportedSymbol.call(address);
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(physical);

		return buffer.getInt(0);
	}
}