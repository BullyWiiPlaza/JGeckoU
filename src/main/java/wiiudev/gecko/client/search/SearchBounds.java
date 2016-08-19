package wiiudev.gecko.client.search;

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

		// Optimize by decreasing the search_old interval according to the search_old results
		SearchResult firstSearchResult = searchResults.get(0);
		int firstSearchResultAddress = firstSearchResult.getAddress();
		int updatedAddress = address + (firstSearchResultAddress - address);
		int updatedLength = length - (firstSearchResultAddress - address);
		SearchResult lastSearchResult = searchResults.get(searchResults.size() - 1);
		int lastSearchResultAddress = lastSearchResult.getAddress();
		updatedLength = updatedLength - (address + length - firstSearchResult.getValueSize().getBytesCount() - lastSearchResultAddress);

		return new SearchBounds(updatedAddress, updatedLength);
	}
}