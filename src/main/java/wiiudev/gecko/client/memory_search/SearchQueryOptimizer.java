package wiiudev.gecko.client.memory_search;

import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.tabs.search.GraphicalSearcher;

import java.io.IOException;
import java.nio.ByteBuffer;
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
		SearchBounds searchBounds = SearchBounds.getSearchBound(address, length, searchResults);

		int updatedAddress = searchBounds.getAddress();
		int updatedLength = searchBounds.getLength();

		JGeckoUGUI jGeckoUGUI = JGeckoUGUI.getInstance();
		GraphicalSearcher graphicalSearcher = new GraphicalSearcher(updatedAddress, updatedLength, jGeckoUGUI.getSearchProgressBar());
		byte[] dumpedBytes = graphicalSearcher.dumpMemory();
		byte[] dumpedMemory = new byte[length]; // Initialize with null bytes
		System.arraycopy(dumpedBytes, 0, dumpedMemory, updatedAddress - address, dumpedBytes.length);

		return ByteBuffer.wrap(dumpedMemory);
	}
}