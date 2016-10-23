package wiiudev.gecko.client.tcpgecko.main;

import wiiudev.gecko.client.tcpgecko.main.enumerations.Commands;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.DataConversions;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.AddressRange;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.MemoryAccessLevel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class for writing data to the memory
 */
public class MemoryWriter extends TCPGecko
{
	/**
	 * Sends <code>command</code> to the server followed by the <code>address</code> and <code>value</code> to ASSIGN
	 *
	 * @param address The address to write to
	 * @param value   The value to write
	 * @param command The {@link Commands} to execute
	 */
	private void sendWriteCommand(int address, int value, Commands command) throws IOException
	{
		int integerLength = 4;
		AddressRange.assertValidAccess(address, integerLength, MemoryAccessLevel.WRITE);

		try (CloseableReentrantLock ignored = TCPGecko.reentrantLock.acquire())
		{
			sendCommand(command);
			dataSender.writeInt(address);
			dataSender.writeInt(value);
			dataSender.flush();
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
		sendWriteCommand(address, value, Commands.MEMORY_POKE_8);
	}

	/**
	 * Writes a 16-bit <code>value</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to write to
	 * @param value   The value to write
	 */
	public void writeShort(int address, short value) throws IOException
	{
		sendWriteCommand(address, value, Commands.MEMORY_POKE_16);
	}

	/**
	 * Writes a 32-bit <code>value</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to write to
	 * @param value   The value to write
	 */
	public void writeInt(int address, int value) throws IOException
	{
		sendWriteCommand(address, value, Commands.MEMORY_POKE_32);
	}

	/**
	 * Writes a 32-bit <code>value</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to write to
	 * @param value   The value to write
	 */
	public void writeBoolean(int address, boolean value) throws IOException
	{
		byte booleanValue = (byte) (value ? 1 : 0);
		write(address, booleanValue);
	}

	/**
	 * Writes a 32-bit <code>value</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to write to
	 * @param value   The value to write
	 */
	public void writeFloat(int address, float value) throws IOException
	{
		int floatValue = DataConversions.toInteger(value);
		writeInt(address, floatValue);
	}

	/**
	 * Writes a null-terminated String <code>value</code> to the memory starting at <code>address</code>
	 *
	 * @param address The address to write to
	 * @param text    The value to write
	 */
	public void writeString(int address, String text) throws IOException
	{
		byte[] textBytes = getNullTerminatedBytes(text);

		writeBytes(address, textBytes);
	}

	private byte[] getNullTerminatedBytes(String text)
	{
		byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
		byte[] nullTerminatedTextBytes = new byte[textBytes.length + 1];
		System.arraycopy(textBytes, 0, nullTerminatedTextBytes, 0, textBytes.length);

		return nullTerminatedTextBytes;
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
			address = uploadBytes(address, bytesChunk, Commands.MEMORY_UPLOAD);
		}
	}

	private int uploadBytes(int address, byte[] bytes, Commands command) throws IOException
	{
		assertMemoryUploadCommand(command);
		AddressRange.assertValidAccess(address, bytes.length, MemoryAccessLevel.WRITE);
		int endAddress = address + bytes.length;

		try (CloseableReentrantLock ignored = TCPGecko.reentrantLock.acquire())
		{
			sendCommand(command);
			dataSender.writeInt(address);
			dataSender.writeInt(endAddress);
			dataSender.write(bytes);
			dataSender.flush();

			// No need to check the status but we need to read it at least
			readStatus();
		}

		return endAddress;
	}

	private void assertMemoryUploadCommand(Commands command)
	{
		if (!command.equals(Commands.MEMORY_UPLOAD))
				// && !command.equals(Commands.MEMORY_KERNEL_UPLOAD))
		{
			throw new IllegalArgumentException("Must be a memory upload command!");
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

	public void writeBytes(int address, int length, byte value) throws Exception
	{
		byte[] bytes = new byte[length];
		for (int bytesIndex = 0; bytesIndex < bytes.length; bytesIndex++)
		{
			bytes[bytesIndex] = value;
		}

		writeBytes(address, bytes);
	}

	/*public void kernelUploadInts(int address, int[] ints) throws IOException
	{
		byte[] bytes = Conversions.toByteArray(ints);
		List<byte[]> partitionedBytes = ByteUtilities.partition(bytes, MAXIMUM_MEMORY_CHUNK_SIZE);

		for (byte[] bytesChunk : partitionedBytes)
		{
			// The end address is the next starting address
			address = uploadBytes(address, bytesChunk, Commands.MEMORY_KERNEL_UPLOAD);
		}
	}*/

	public void kernelWriteInt(int address, int value) throws IOException
	{
		try (CloseableReentrantLock ignored = TCPGecko.reentrantLock.acquire())
		{
			sendCommand(Commands.MEMORY_KERNEL_WRITE);
			dataSender.writeInt(address);
			dataSender.writeInt(value);
			dataSender.flush();
		}
	}

	public void serialWrite(int address, int value, int writesCount) throws IOException
	{
		serialWrite(address, value, writesCount, 0);
	}

	public void serialWrite(int address, int value, int writesCount, int increment) throws IOException
	{
		byte[] bytes = getBytes(value, writesCount, increment);
		writeBytes(address, bytes);
	}

	private byte[] getBytes(int value, int writesCount, int increment)
	{
		ByteBuffer byteBuffer = ByteBuffer.allocate(4 * writesCount);

		for (int writesIndex = 0; writesIndex < writesCount; writesIndex++)
		{
			byteBuffer.putInt(value);
			value += increment;
		}

		return byteBuffer.array();
	}

	private static class ByteUtilities
	{
		static List<byte[]> readPartitionedBytes(Path sourcePath, int chunkSize) throws IOException
		{
			byte[] fileBytes = Files.readAllBytes(sourcePath);

			return partition(fileBytes, chunkSize);
		}

		static List<byte[]> partition(byte[] bytes, int chunkSize)
		{
			List<byte[]> byteArrayChunks = new ArrayList<>();
			int startingIndex = 0;

			while (startingIndex < bytes.length)
			{
				int end = Math.min(bytes.length, startingIndex + chunkSize);
				byteArrayChunks.add(Arrays.copyOfRange(bytes, startingIndex, end));
				startingIndex += chunkSize;
			}

			return byteArrayChunks;
		}
	}
}