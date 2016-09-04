package wiiudev.gecko.client.memory_search;

import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.tabs.memory_search.GraphicalRefiner;
import wiiudev.gecko.client.gui.tabs.memory_search.GraphicalSearcher;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;

import javax.swing.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchQueryOptimizer
{
	private int address;
	private int length;

	public SearchQueryOptimizer(int address, int length)
	{
		this.address = address;
		this.length = length;
	}

	public ByteBuffer dumpBytes(List<SearchResult> searchResults) throws IOException, ExecutionException, InterruptedException
	{
		JGeckoUGUI jGeckoUGUI = JGeckoUGUI.getInstance();
		JProgressBar searchProgressBar = jGeckoUGUI.getSearchProgressBar();

		SearchBounds searchBounds = SearchBounds.getSearchBound(address, length, searchResults);
		int updatedAddress = searchBounds.getAddress();
		int updatedLength = searchBounds.getLength();

		if (searchResults.isEmpty())
		{
			// Dump all bytes for the first search
			GraphicalSearcher graphicalSearcher = new GraphicalSearcher(updatedAddress, updatedLength, searchProgressBar);
			byte[] dumpedBytes = graphicalSearcher.dumpMemory();
			byte[] dumpedMemory = new byte[length]; // Initialize with null bytes
			System.arraycopy(dumpedBytes, 0, dumpedMemory, updatedAddress - address, dumpedBytes.length);

			return ByteBuffer.wrap(dumpedMemory);
		}

		// Only dump the bytes that are still in the results
		int bytesToDump = 0;
		List<MemoryDumpingChunk> memoryDumpingChunks = new LinkedList<>();

		for (int searchResultsIndex = 0; searchResultsIndex < searchResults.size(); searchResultsIndex++)
		{
			SearchResult searchResult = searchResults.get(searchResultsIndex);
			int address = searchResult.getAddress();

			if (searchResultsIndex < searchResults.size() - 1)
			{
				// Combine this into one chunk
				while (searchResults.get(searchResultsIndex + 1).getAddress() - address
						<= MemoryReader.MAXIMUM_MEMORY_CHUNK_SIZE)
				{
					searchResultsIndex++;

					if (searchResultsIndex == searchResults.size() - 1)
					{
						break;
					}
				}
			}

			int chunkLength = searchResults.get(searchResultsIndex).getAddress() - address + searchResults.get(searchResultsIndex).getValueSize().getBytesCount();

			MemoryDumpingChunk memoryDumpingChunk = new MemoryDumpingChunk(address, chunkLength);
			memoryDumpingChunks.add(memoryDumpingChunk);
			bytesToDump += chunkLength;
		}

		GraphicalRefiner graphicalRefiner = new GraphicalRefiner(updatedAddress, updatedLength, memoryDumpingChunks,
				bytesToDump, searchProgressBar);
		return ByteBuffer.wrap(graphicalRefiner.dumpMemory());
	}

	public static class MemoryDumpingChunk
	{
		private int address;
		private int length;

		public MemoryDumpingChunk(int address, int length)
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