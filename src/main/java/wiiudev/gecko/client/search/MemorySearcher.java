package wiiudev.gecko.client.search;

import wiiudev.gecko.client.gui.JGeckoUGUI;

import javax.swing.*;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MemorySearcher
{
	private int address;
	private int length;
	private List<SearchResult> searchResults;
	private Stack<List<SearchResult>> searchResultsStack;
	private boolean isFirstSearch;

	public MemorySearcher(int address, int length)
	{
		searchResults = new LinkedList<>();
		searchResultsStack = new Stack<>();
		this.address = address;
		this.length = length;
		isFirstSearch = true;
	}

	public List<SearchResult> search(SearchRefinement searchRefinement) throws IOException, ExecutionException, InterruptedException
	{
		SearchModes searchMode = searchRefinement.getSearchMode();
		boolean isUnknownValueSearch = searchMode == SearchModes.UNKNOWN;
		ValueSize valueSize = searchRefinement.getValueSize();
		SearchQueryOptimizer searchQueryOptimizer = new SearchQueryOptimizer(address, length);
		ByteBuffer byteBuffer = searchQueryOptimizer.dumpBytes(searchResults);
		Set<SearchResult> searchResultsSet = new HashSet<>(searchResults);
		List<SearchResult> updatedSearchResults = new LinkedList<>();

		JButton searchButton = JGeckoUGUI.getInstance().getSearchButton();

		if (!isFirstSearch)
		{
			searchButton.setText("Searching...");
		}

		while (byteBuffer.hasRemaining())
		{
			BigInteger currentValue = getValue(byteBuffer, valueSize);
			int searchResultAddress = address + byteBuffer.position() - valueSize.getBytesCount();
			SearchResult searchResult = new SearchResult(searchResultAddress, currentValue, currentValue, valueSize);

			// Add them all for the first unknown value search
			if (isUnknownValueSearch && isFirstSearch)
			{
				updatedSearchResults.add(searchResult);

				continue;
			}

			SearchCondition searchCondition = searchRefinement.getSearchCondition();
			boolean isSearchConditionTrue;

			// Not the first unknown value search
			if (isUnknownValueSearch)
			{
				if (searchResultsSet.contains(searchResult))
				{
					SearchResult retrievedSearchResult = getSearchResult(searchResultAddress);
					BigInteger previousValue = retrievedSearchResult.getCurrentValue();
					isSearchConditionTrue = searchCondition.isTrue(previousValue, currentValue);

					if (isSearchConditionTrue)
					{
						// Update the search result
						retrievedSearchResult.updateValue(currentValue);
						updatedSearchResults.add(retrievedSearchResult);
					}
				}

				continue;
			}

			BigInteger targetValue = searchRefinement.getValue();
			isSearchConditionTrue = searchCondition.isTrue(targetValue, currentValue);

			if (isFirstSearch)
			{
				if (isSearchConditionTrue)
				{
					// Add the result if this is the first search only
					updatedSearchResults.add(searchResult);
				}

				continue;
			}

			// Specific value search refining (2nd+ search)
			if (searchResultsSet.contains(searchResult))
			{
				if (isSearchConditionTrue)
				{
					// Update the search result
					SearchResult result = getSearchResult(searchResultAddress);
					result.updateValue(currentValue);
					updatedSearchResults.add(result);
				}
			}
		}

		searchResults = updatedSearchResults;
		isFirstSearch = false;
		pushResults();

		return searchResults;
	}

	private void pushResults()
	{
		List<SearchResult> clonedSearchResults = cloneList(searchResults);
		searchResultsStack.add(clonedSearchResults);
	}

	private SearchResult getSearchResult(int address)
	{
		SearchResult searchResult = new SearchResult(address);
		Comparator<SearchResult> comparator = (firstSearchResult, secondSearchResult) -> new Integer(firstSearchResult.getAddress()).compareTo(secondSearchResult.getAddress());
		int searchResultIndex = Collections.binarySearch(searchResults, searchResult, comparator);

		return searchResults.get(searchResultIndex);
	}

	private static List<SearchResult> cloneList(List<SearchResult> searchResults)
	{
		List<SearchResult> cloneSearchResults = new ArrayList<>(searchResults.size());
		cloneSearchResults.addAll(searchResults.stream().map(SearchResult::clone).collect(Collectors.toList()));

		return cloneSearchResults;
	}

	public List<SearchResult> undoSearchResults()
	{
		// Discard current search
		searchResultsStack.pop();

		// Return empty results
		if (searchResultsStack.size() == 0)
		{
			return new ArrayList<>();
		}

		// Use previous results
		searchResults = searchResultsStack.peek();

		return searchResults;
	}

	public boolean canUndoSearch()
	{
		return searchResultsStack.size() > 0;
	}

	public int getSearchIterations()
	{
		return searchResultsStack.size();
	}

	private BigInteger getValue(ByteBuffer byteBuffer, ValueSize valueSize)
	{
		byte[] retrieved = new byte[valueSize.getBytesCount()];
		byteBuffer.get(retrieved);

		return new BigInteger(retrieved);
	}

	public boolean isFirstSearch()
	{
		return isFirstSearch;
	}
}