package wiiudev.gecko.client.search;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class WordSearch_old
{
	private ByteBuffer byteBuffer;
	private boolean[] activeMemory;
	private MemoryReader memoryReader;
	private int startingAddress;
	private int updatedStartingAddress;
	private int searchLength;
	private int updatedSearchLength;
	private static final Logger LOGGER = Logger.getLogger(WordSearch_old.class.getName());

	public WordSearch_old(int address, int length)
	{
		memoryReader = new MemoryReader();
		this.startingAddress = address;
		updatedStartingAddress = address;
		this.searchLength = length;
		updatedSearchLength = length;

		activeMemory = new boolean[length / 4];

		for (int activateMemoryIndex = 0; activateMemoryIndex < activeMemory.length; activateMemoryIndex++)
		{
			activeMemory[activateMemoryIndex] = true;
		}
	}

	private void dump() throws IOException
	{
		LOGGER.info("Dumping memory from " + Integer.toHexString(updatedStartingAddress).toUpperCase() + " to " + Integer.toHexString(updatedStartingAddress + updatedSearchLength).toUpperCase() + "...");

		byte[] dumpedMemory = memoryReader.readBytes(updatedStartingAddress, updatedSearchLength);
		System.out.println(dumpedMemory[0]);

		if (byteBuffer == null)
		{
			// First, dump everything
			byteBuffer = ByteBuffer.wrap(dumpedMemory);
		} else
		{
			// Now just update the byte buffer
			for (int index = 0; index < dumpedMemory.length; index++)
			{
				int targetIndex = updatedStartingAddress - startingAddress + index;
				LOGGER.info("Target index: " + Integer.toHexString(targetIndex).toUpperCase());
				byte b = byteBuffer.get(targetIndex);
				LOGGER.info("Currently: " + Integer.toHexString(b));
				byte dumped = dumpedMemory[index];
				LOGGER.info("Dumped: " + dumped);
				byteBuffer.put(targetIndex, dumped);
				b = byteBuffer.get(targetIndex);
				LOGGER.info("Now: " + Integer.toHexString(b));
			}
		}
	}

	public void refine(int value) throws IOException
	{
		dump();

		while (byteBuffer.hasRemaining())
		{
			int dumpedValue = byteBuffer.getInt();
			int matchedAddress = byteBuffer.position() - 4;

			if (dumpedValue == value && activeMemory[matchedAddress / 4])
			{
				LOGGER.info("Found match at " + Integer.toHexString(matchedAddress).toUpperCase());
			} else
			{
				activeMemory[matchedAddress / 4] = false;
			}
		}

		adjustDumpingRange();
	}

	private void adjustDumpingRange()
	{
		int updatedStartingIndex = getUpdatedStartingIndex();

		updatedStartingAddress = startingAddress + updatedStartingIndex * 4;
		LOGGER.info("Updated starting address: " + Integer.toHexString(updatedStartingAddress).toUpperCase());

		if (updatedStartingAddress == startingAddress + searchLength)
		{
			throw new NoSearchResultsException("No search results!");
		}

		int updatedLength = getUpdatedLength();
		updatedSearchLength = updatedLength * 4 + startingAddress - updatedStartingAddress + 4;
		LOGGER.info("Updated search length: " + Integer.toHexString(updatedSearchLength).toUpperCase());
	}

	private int getUpdatedLength()
	{
		int startingIndex = activeMemory.length - 1;

		for (; startingIndex > 0; startingIndex--)
		{
			boolean active = activeMemory[startingIndex];

			if (active)
			{
				break;
			}
		}

		return startingIndex;
	}

	private int getUpdatedStartingIndex()
	{
		int startingIndex = 0;

		for (; startingIndex < activeMemory.length; startingIndex++)
		{
			boolean active = activeMemory[startingIndex];

			if (active)
			{
				break;
			}
		}

		return startingIndex;
	}
}