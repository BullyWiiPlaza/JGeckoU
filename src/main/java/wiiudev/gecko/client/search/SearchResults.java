package wiiudev.gecko.client.search;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class SearchResults
{
	private ByteBuffer memory;
	private boolean[] inActive;
	private int memoryPointer;

	public SearchResults(byte[] bytes)
	{
		memory = ByteBuffer.wrap(bytes);
		inActive = new boolean[memory.capacity()];
		resetMemoryPointer();
	}

	public void setInActive(int offset)
	{
		inActive[offset] = true;
	}

	private boolean isActive(int offset)
	{
		return !inActive[offset];
	}

	public int getValueSize()
	{
		return 4;
	}

	public void resetMemoryPointer()
	{
		memoryPointer = (-1) * getValueSize();
	}

	public boolean hasMore()
	{
		return memoryPointer < memory.capacity() - 4;
	}

	public int getNext()
	{
		while (hasMore())
		{
			memoryPointer += getValueSize();

			if (isActive(memoryPointer))
			{
				return memory.getInt(memoryPointer);
			}
		}

		throw new IllegalArgumentException("No more addresses");
	}

	public int getOffsetPointer()
	{
		return memoryPointer;
	}

	public List<Integer> getSearchResults()
	{
		List<Integer> searchResults = new LinkedList<>();
		resetMemoryPointer();

		try
		{
			while (hasMore())
			{
				int value = getNext();
				searchResults.add(value);
			}
		} catch (IllegalArgumentException ignored)
		{

		}

		return searchResults;
	}

	public int getUpdatedDumpLength(int addressIncrement)
	{
		int startingIndex = inActive.length - getValueSize();

		for (; startingIndex > 0; startingIndex -= getValueSize())
		{
			boolean active = isActive(startingIndex);

			if (active)
			{
				break;
			}
		}

		return startingIndex + getValueSize() - addressIncrement;
	}

	/**
	 * Updates the starting address by dropping all inactive addresses at the front
	 *
	 * @param startingAddress The address the search has started at
	 * @return The possibly increased starting address
	 */
	public int getUpdatedStartingAddress(int startingAddress)
	{
		int startingIndex = 0;

		for (; startingIndex < inActive.length; startingIndex += getValueSize())
		{
			boolean active = isActive(startingIndex);

			if (active)
			{
				break;
			}
		}

		return startingAddress + startingIndex;
	}
}