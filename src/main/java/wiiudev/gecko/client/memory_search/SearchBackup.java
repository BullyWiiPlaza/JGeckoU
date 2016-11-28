package wiiudev.gecko.client.memory_search;

import wiiudev.gecko.client.memory_search.enumerations.ValueSize;

import java.util.List;

public class SearchBackup
{
	private SearchBounds searchBounds;
	private ValueSize valueSize;
	private List<SearchResult> searchResults;

	public SearchBackup(SearchBounds searchBounds,
	                    ValueSize valueSize,
	                    List<SearchResult> searchResults)
	{
		this.searchBounds = searchBounds;
		this.valueSize = valueSize;
		this.searchResults = searchResults;
	}

	public SearchBounds getSearchBounds()
	{
		return searchBounds;
	}

	public ValueSize getValueSize()
	{
		return valueSize;
	}

	public List<SearchResult> getSearchResults()
	{
		return searchResults;
	}
}