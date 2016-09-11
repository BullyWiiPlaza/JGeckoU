package wiiudev.gecko.client.memory_search;

import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.memory_search.enumerations.SearchConditions;
import wiiudev.gecko.client.memory_search.enumerations.SearchMode;
import wiiudev.gecko.client.memory_search.enumerations.ValueSize;

import javax.swing.*;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
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

	public List<SearchResult> search(SearchRefinement searchRefinement) throws IOException, ExecutionException, InterruptedException
	{
		SearchMode searchMode = searchRefinement.getSearchMode();
		boolean isUnknownValueSearch = searchMode == SearchMode.UNKNOWN;
		ValueSize valueSize = searchRefinement.getValueSize();
		SearchQueryOptimizer searchQueryOptimizer = new SearchQueryOptimizer(address, length);
		ByteBuffer valuesReader = searchQueryOptimizer.dumpBytes(searchResults);
		List<SearchResult> updatedSearchResults = new LinkedList<>();

		JButton searchButton = JGeckoUGUI.getInstance().getSearchButton();
		JProgressBar progressBar = JGeckoUGUI.getInstance().getSearchProgressBar();

		int valueSizeBytesCount = valueSize.getBytesCount();
		int byteBufferLimit = valuesReader.limit();

		searchButton.setText("Searching...");
		progressBar.setValue(0);

		if (isFirstSearch)
		{
			while (valuesReader.position() + valueSizeBytesCount < byteBufferLimit)
			{
				BigInteger currentValue = getValue(valuesReader, valueSizeBytesCount);
				int searchResultAddress = address + valuesReader.position() - valueSize.getBytesCount();
				SearchResult searchResult = new SearchResult(searchResultAddress, currentValue, currentValue, valueSize);

				if (isUnknownValueSearch)
				{
					updatedSearchResults.add(searchResult);
				} else
				{
					SearchConditions searchCondition = searchRefinement.getSearchCondition();
					BigInteger targetValue = searchRefinement.getValue();
					boolean isSearchConditionTrue = searchCondition.isTrue(targetValue, currentValue);

					if (isSearchConditionTrue)
					{
						updatedSearchResults.add(searchResult);
					}
				}

				int progress = valuesReader.position() * 100 / byteBufferLimit;
				progressBar.setValue(progress);
			}
		} else
		{
			int searchResultsIndex = 0;
			int startingAddress = searchResults.get(0).getAddress();

			for (SearchResult searchResult : searchResults)
			{
				int currentAddress = searchResult.getAddress();
				int position = currentAddress - startingAddress;
				valuesReader.position(position);

				BigInteger currentValue = getValue(valuesReader, valueSizeBytesCount);
				SearchConditions searchCondition = searchRefinement.getSearchCondition();

				BigInteger targetValue = isUnknownValueSearch ? searchResult.getCurrentValue() : searchRefinement.getValue();
				boolean isSearchConditionTrue = searchCondition.isTrue(targetValue, currentValue);

				if (isSearchConditionTrue)
				{
					searchResult.updateValue(currentValue);
					updatedSearchResults.add(searchResult);
				}

				int progress = (searchResultsIndex + 1) * 100 / searchResults.size();
				progressBar.setValue(progress);

				searchResultsIndex++;
			}
		}

		searchResults = updatedSearchResults;
		isFirstSearch = false;

		// Do not smash the RAM
		if (searchResults.size() < 99999)
		{
			pushSearchResults();
		}

		return searchResults;
	}

	private void pushSearchResults()
	{
		List<SearchResult> clonedSearchResults = cloneList(searchResults);
		searchResultsStack.add(clonedSearchResults);
	}

	private static List<SearchResult> cloneList(List<SearchResult> searchResults)
	{
		List<SearchResult> cloneSearchResults = new ArrayList<>(searchResults.size());
		cloneSearchResults.addAll(searchResults.stream().map(SearchResult::clone).collect(Collectors.toList()));

		return cloneSearchResults;
	}

	public List<SearchResult> undoSearchResults()
	{
		// Discard current memory_search
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

	private BigInteger getValue(ByteBuffer byteBuffer, int bytesCount)
	{
		byte[] retrieved = new byte[bytesCount];

		try
		{
			byteBuffer.get(retrieved);
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}

		// For bigger value sizes still go in 32-bit steps
		int additionalBytes = bytesCount - ValueSize.THIRTY_TWO_BIT.getBytesCount();

		if (additionalBytes > 0)
		{
			// Scale the buffer position backwards
			int currentPosition = byteBuffer.position();
			byteBuffer.position(currentPosition - additionalBytes);
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

	public void setSetResults(List<SearchResult> searchResults)
	{
		this.searchResults = searchResults;
		pushSearchResults();
	}
}