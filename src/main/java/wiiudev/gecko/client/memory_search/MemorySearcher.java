package wiiudev.gecko.client.memory_search;

import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.tabs.memory_search.ProgressVisualization;
import wiiudev.gecko.client.memory_search.enumerations.SearchConditions;
import wiiudev.gecko.client.memory_search.enumerations.SearchMode;
import wiiudev.gecko.client.memory_search.enumerations.ValueSize;

import javax.swing.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class MemorySearcher
{
	private int address;
	private int length;
	private List<SearchResult> searchResults;
	private Stack<List<SearchResult>> searchResultsStack;
	private boolean isFirstSearch;

	public MemorySearcher(SearchBounds searchBounds)
	{
		this(searchBounds.getAddress(), searchBounds.getLength());
	}

	public MemorySearcher(int address, int length)
	{
		searchResults = new ArrayList<>();
		searchResultsStack = new Stack<>();
		this.address = address;
		this.length = length;
		isFirstSearch = true;
	}

	public List<SearchResult> search(SearchRefinement searchRefinement,
	                                 boolean aligned) throws Exception
	{
		ValueSize valueSize = searchRefinement.getValueSize();
		int valueSizeBytesCount = valueSize.getBytesCount();

		if (!isFirstSearch)
		{
			// Update the address and length
			SearchResult firstSearchResult = searchResults.get(0);
			address = firstSearchResult.getAddress();
			length = searchResults.get(searchResults.size() - 1).getAddress() - address + valueSizeBytesCount;
		}

		SearchMode searchMode = searchRefinement.getSearchMode();
		boolean isUnknownValueSearch = searchMode == SearchMode.UNKNOWN;
		SearchQueryOptimizer searchQueryOptimizer = new SearchQueryOptimizer(address, length);
		ByteBuffer valuesBuffer = searchQueryOptimizer.dumpBytes(searchResults);
		List<SearchResult> updatedSearchResults = new ArrayList<>();
		JButton searchButton = JGeckoUGUI.getInstance().getSearchButton();
		int limit = valuesBuffer.limit();
		searchButton.setText("Searching...");
		ProgressVisualization.Optimizer progressOptimizer;

		if (isFirstSearch)
		{
			progressOptimizer = new ProgressVisualization.Optimizer(limit, 100000);

			// Iterate over all values
			while (valuesBuffer.position() + valueSizeBytesCount <= limit)
			{
				BigInteger currentValue = getValue(valuesBuffer, valueSizeBytesCount, aligned);
				int position = valuesBuffer.position();
				int searchResultAddress = address + position - Math.min(valueSize.getBytesCount(), 4);

				if (!aligned)
				{
					searchResultAddress += valueSize.getBytesCount() - 1;
				}

				SearchResult searchResult = new SearchResult(searchResultAddress, currentValue, currentValue, valueSize);

				if (isUnknownValueSearch)
				{
					updatedSearchResults.add(searchResult);
				} else
				{
					SearchConditions searchCondition = searchRefinement.getSearchCondition();
					BigInteger targetValue = searchRefinement.getValue();
					boolean isSearchConditionTrue = searchCondition.isTrue(targetValue, searchResult);

					if (isSearchConditionTrue)
					{
						updatedSearchResults.add(searchResult);
					}
				}

				progressOptimizer.considerUpdatingProgress("Evaluated Bytes", position);

				if (JGeckoUGUI.getInstance().isDumpingCanceled())
				{
					return null;
				}
			}

			ProgressVisualization.deleteUpdateLabel();
		} else
		{
			progressOptimizer = new ProgressVisualization.Optimizer(searchResults.size(), 1000);

			// Not the first search, refine the search results
			int searchResultsIndex = 0;

			// Go through the search results when not the first search
			for (SearchResult searchResult : searchResults)
			{
				int currentAddress = searchResult.getAddress();
				int position = currentAddress - address;
				valuesBuffer.position(position);

				BigInteger currentValue = getValue(valuesBuffer, valueSizeBytesCount, aligned);
				SearchConditions searchCondition = searchRefinement.getSearchCondition();

				BigInteger targetValue = isUnknownValueSearch ?
						searchResult.getCurrentValue() : searchRefinement.getValue();
				searchResult.updateValue(currentValue);
				boolean isSearchConditionTrue = searchCondition.isTrue(targetValue, searchResult);

				if (isSearchConditionTrue)
				{
					updatedSearchResults.add(searchResult);
				}

				searchResultsIndex++;

				progressOptimizer.considerUpdatingProgress("Evaluated Search Results",
						searchResultsIndex);

				if (JGeckoUGUI.getInstance().isDumpingCanceled())
				{
					return null;
				}
			}
		}

		JLabel addressProgressLabel = JGeckoUGUI.getInstance().getAddressProgressLabel();
		addressProgressLabel.setText("");

		searchResults = updatedSearchResults;
		isFirstSearch = false;

		// Do not smash the RAM
		if (searchResults.size() < SearchResult.SEARCH_RESULTS_THRESHOLD)
		{
			pushSearchResultsAsynchronously();
		}

		return searchResults;
	}

	private void pushSearchResultsAsynchronously()
	{
		Thread thread = new Thread(() ->
		{
			List<SearchResult> clonedSearchResults = cloneList(searchResults);
			searchResultsStack.add(clonedSearchResults);
		});

		thread.start();
	}

	private static List<SearchResult> cloneList(List<SearchResult> searchResults)
	{
		List<SearchResult> cloneSearchResults = new ArrayList<>(searchResults.size());
		cloneSearchResults.addAll(searchResults.stream().map(SearchResult::clone).collect(Collectors.toList()));

		return cloneSearchResults;
	}

	public List<SearchResult> undoSearchResults()
	{
		// Discard current memory search
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

	public int getSearchIterationsCount()
	{
		return searchResultsStack.size();
	}

	private BigInteger getValue(ByteBuffer byteBuffer, int bytesCount, boolean aligned)
	{
		byte[] retrieved = new byte[bytesCount];
		byteBuffer.get(retrieved);

		if (aligned)
		{
			// For bigger value sizes still go in 32-bit steps
			int additionalBytes = bytesCount - ValueSize.THIRTY_TWO_BIT.getBytesCount();

			if (additionalBytes > 0)
			{
				// Scale the buffer position backwards
				int currentPosition = byteBuffer.position();
				byteBuffer.position(currentPosition - additionalBytes);
			}
		} else
		{
			int currentPosition = byteBuffer.position();
			byteBuffer.position(currentPosition - bytesCount + 1);
		}

		return getUnsigned(retrieved);
	}

	private static BigInteger getUnsigned(byte[] bytes)
	{
		int byteSize = 8;
		BigInteger twosComplement = BigInteger.ONE.shiftLeft(bytes.length * byteSize);
		BigInteger bigInteger = new BigInteger(bytes);

		if (bigInteger.compareTo(BigInteger.ZERO) < 0)
		{
			bigInteger = bigInteger.add(twosComplement);
		}

		return bigInteger;
	}

	public boolean isFirstSearch()
	{
		return isFirstSearch;
	}

	public void loadSearchResults(List<SearchResult> searchResults)
	{
		this.searchResults = searchResults;
		pushSearchResultsAsynchronously();
	}
}