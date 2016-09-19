package wiiudev.gecko.client.memory_search;

import java.util.List;

public class SearchBounds
{
	private int address;
	private int length;

	public SearchBounds(int address, int length)
	{
		this.address = address;
		this.length = length;
	}

	public int getLength()
	{
		return length;
	}

	public int getAddress()
	{
		return address;
	}

	public static SearchBounds getSearchBound(int address, int length, List<SearchResult> searchResults)
	{
		if (searchResults.isEmpty())
		{
			return new SearchBounds(address, length);
		}

		// Optimize by decreasing the search interval according to the search results
		SearchResult firstSearchResult = searchResults.get(0);
		int updatedAddress = firstSearchResult.getAddress();
		int bytesCount = firstSearchResult.getValueSize().getBytesCount();
		int lastAddress = searchResults.get(searchResults.size() - 1).getAddress();
		int updatedLength = lastAddress - updatedAddress + bytesCount;

		return new SearchBounds(updatedAddress, updatedLength);
	}
}