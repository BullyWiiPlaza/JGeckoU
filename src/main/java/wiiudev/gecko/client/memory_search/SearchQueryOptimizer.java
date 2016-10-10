package wiiudev.gecko.client.memory_search;

import wiiudev.gecko.client.gui.tabs.memory_search.GraphicalRefiner;
import wiiudev.gecko.client.gui.tabs.memory_search.GraphicalSearcher;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchQueryOptimizer
{
	private int address;
	private int length;

	public SearchQueryOptimizer(int address, int length)
	{
		this.address = address;
		this.length = length;
	}

	public ByteBuffer dumpBytes(List<SearchResult> searchResults) throws Exception
	{
		SearchBounds searchBounds = SearchBounds.getSearchBound(address, length, searchResults);
		int updatedAddress = searchBounds.getAddress();
		int updatedLength = searchBounds.getLength();

		if (searchResults.isEmpty())
		{
			return dumpAllBytes(updatedAddress, updatedLength);
		}

		// Only dump the bytes that are still in the results
		long bytesToDump = 0;
		List<MemoryDumpingChunk> memoryDumpingChunks = new LinkedList<>();

		// For performance optimization, use this set
		Set<Integer> addressesMap = searchResults.stream().map(SearchResult::getAddress).collect(Collectors.toSet());

		int bytesCount = searchResults.get(0).getValueSize().getBytesCount();

		for (int searchResultAddress = updatedAddress;
		     searchResultAddress < updatedAddress + updatedLength;
		     searchResultAddress += bytesCount)
		{
			if (!addressesMap.contains(searchResultAddress))
			{
				// Not a search result
				continue;
			}

			int nextSearchResultAddress = searchResultAddress + bytesCount;
			int endAddress = nextSearchResultAddress;

			// Does this fit into one chunk?
			while (nextSearchResultAddress - searchResultAddress
					<= MemoryReader.MAXIMUM_MEMORY_CHUNK_SIZE)
			{
				nextSearchResultAddress += bytesCount;

				if (addressesMap.contains(nextSearchResultAddress - bytesCount))
				{
					endAddress = nextSearchResultAddress;
				}

				// We hit the end of the search results
				if (nextSearchResultAddress >= updatedAddress + updatedLength)
				{
					endAddress = nextSearchResultAddress;
					break;
				}
			}

			int chunkLength = endAddress - searchResultAddress - bytesCount;

			if (chunkLength == 0)
			{
				chunkLength += bytesCount;
			}

			MemoryDumpingChunk memoryDumpingChunk = new MemoryDumpingChunk(searchResultAddress, chunkLength);
			memoryDumpingChunks.add(memoryDumpingChunk);
			bytesToDump += chunkLength;
			searchResultAddress += chunkLength;
		}

		int memoryDumpingChunksSize = memoryDumpingChunks.size();
		int maximumChunksSize = (updatedLength / TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE) + 1;

		// Decide whether to use the "optimized" chunks or dump everything again
		if (memoryDumpingChunksSize / maximumChunksSize <= 0.1)
		{
			GraphicalRefiner graphicalRefiner = new GraphicalRefiner(updatedAddress, updatedLength, memoryDumpingChunks,
					bytesToDump);

			return ByteBuffer.wrap(graphicalRefiner.dumpMemory());
		} else
		{
			return dumpAllBytes(updatedAddress, updatedLength);
		}
	}

	private ByteBuffer dumpAllBytes(int updatedAddress, int updatedLength) throws Exception
	{
		// Dump all bytes for the first search
		GraphicalSearcher graphicalSearcher = new GraphicalSearcher(updatedAddress, updatedLength);
		byte[] dumpedBytes = graphicalSearcher.dumpMemory();
		byte[] dumpedMemory = new byte[length]; // Initialize with null bytes
		System.arraycopy(dumpedBytes, 0, dumpedMemory, updatedAddress - address, dumpedBytes.length);

		return ByteBuffer.wrap(dumpedMemory);
	}

	public static class MemoryDumpingChunk
	{
		private int address;
		private int length;

		MemoryDumpingChunk(int address, int length)
		{
			this.address = address;
			this.length = length;
		}

		public byte[] dump() throws IOException
		{
			MemoryReader memoryReader = new MemoryReader();
			return memoryReader.readBytes(address, length);
		}

		public int getAddress()
		{
			return address;
		}

		public int getLength()
		{
			return length;
		}
	}
}