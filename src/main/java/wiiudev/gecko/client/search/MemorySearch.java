package wiiudev.gecko.client.search;

import wiiudev.gecko.client.connector.MemoryReader;

import java.io.IOException;

public class MemorySearch
{
	private int startingAddress;
	private int updatedAddress;
	private int length;
	private boolean isFirstDump;
	private int updatedLength;
	private MemoryReader memoryReader;
	// private List<byte[]> dumpedBytesHistory;
	private SearchResults searchResults;

	/**
	 * Defines an object for a memory search
	 *
	 * @param startingAddress The address to start searching the memory at
	 * @param length          The length of the search
	 */
	public MemorySearch(int startingAddress, int length)
	{
		this.startingAddress = startingAddress;
		updatedAddress = startingAddress;
		this.length = length;
		updatedLength = length;
		memoryReader = new MemoryReader();
		isFirstDump = true;
		// dumpedBytesHistory = new ArrayList<>();
	}

	/**
	 * Dumps the memory AND updates the existing memory storage with new values if ran more than once
	 *
	 * @throws IOException
	 */
	public void dump() throws IOException
	{
		byte[] dumpedBytes = memoryReader.readBytes(updatedAddress, updatedLength);

		if (isFirstDump)
		{
			// dumpedBytesHistory.addListeners(dumpedBytes);
			searchResults = new SearchResults(dumpedBytes);
			isFirstDump = false;
		} else
		{
			/*System.out.println("Dumping " + Integer.toHexString(updatedLength) + " from " + Integer.toHexString(updatedAddress) + "...");
			dumpedBytes = memoryReader.readBytes(updatedAddress, updatedLength);
			System.out.println("Dumped: " + dumpedBytes[0]);
			System.out.println(dumpedBytes[1]);
			System.out.println(dumpedBytes[2]);
			System.out.println(dumpedBytes[3]);
			System.out.println(dumpedBytes[4]);*/
			int read = memoryReader.readInt(updatedAddress);
			System.out.println("Read: " + read);
			// TODO Merge dumped into existing results
		}
	}

	/**
	 * Refines the search by marking all values that do not fulfill the <code>comparison</code> conditon
	 *
	 * @param targetValue The value to compare against
	 * @param comparison  The specific search condition to use
	 */
	public void refine(int targetValue, SpecificValueComparison comparison)
	{
		// Start from the beginning
		searchResults.resetMemoryPointer();

		// Check every value
		while (searchResults.hasMore())
		{
			try
			{
				int offsetPointer = searchResults.getOffsetPointer();
				boolean notPassed = false;
				int nextValue = searchResults.getNext();

				switch (comparison)
				{
					case EQUAL:
						if (targetValue != nextValue)
						{
							notPassed = true;
						} else
						{
							System.out.println("Found: " + offsetPointer);
						}
						break;

					case NOT_EQUAL:
						if (targetValue == nextValue)
						{
							notPassed = true;
						}
						break;
					case GREATER_THAN:
						if (targetValue <= nextValue)
						{
							notPassed = true;
						}
						break;
					case LESS_THAN:
						if (targetValue >= nextValue)
						{
							notPassed = true;
						}
						break;
					case GREATER_OR_EQUAL:
						if (targetValue < nextValue)
						{
							notPassed = true;
						}
						break;
					case LESS_OR_EQUAL:
						if (targetValue > nextValue)
						{
							notPassed = true;
						}
						break;
				}

				// Did the condition pass? If not, the value is out of the search results
				if (notPassed)
				{
					searchResults.setInActive(offsetPointer);
					System.out.println(Integer.toHexString(offsetPointer) + " not passed");
				}
			} catch (IllegalArgumentException ignored)
			{

			}
		}


		updateSearchInterval();
	}

	/**
	 * Decreases the search interval by chopping off all addresses from the left AND right of the initial search interval
	 */
	private void updateSearchInterval()
	{
		updatedAddress = searchResults.getUpdatedStartingAddress(startingAddress);
		System.out.println(("Updated starting address: " + Integer.toHexString(updatedAddress).toUpperCase()));

		int addressIncrement = updatedAddress - startingAddress;
		updatedLength = searchResults.getUpdatedDumpLength(addressIncrement);
		System.out.println("Updated search length: " + Integer.toHexString(updatedLength).toUpperCase());
		System.out.println("Results size: " + searchResults.getSearchResults().size());
	}
}