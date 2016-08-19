package wiiudev.gecko.client.search;

import java.math.BigInteger;

public class SearchRefinement
{
	private SearchModes searchMode;
	private SearchCondition searchCondition;
	private ValueSize valueSize;
	private BigInteger value;

	/**
	 * Constructor for specific value searches
	 * @param searchCondition The search condition
	 * @param valueSize The value's size
	 * @param value The value to search
	 */
	public SearchRefinement(SearchCondition searchCondition, ValueSize valueSize, BigInteger value)
	{
		this.searchMode = SearchModes.SPECIFIC;
		this.searchCondition = searchCondition;
		this.valueSize = valueSize;
		this.value = value;
	}

	/**
	 * Constructor for the all unknown value searches but the first
	 * @param searchCondition The search condition
	 * @param valueSize The value's size
	 */
	public SearchRefinement(SearchCondition searchCondition, ValueSize valueSize)
	{
		searchMode = SearchModes.UNKNOWN;
		this.searchCondition = searchCondition;
		this.valueSize = valueSize;
	}

	/**
	 * Constructor for the first unknown value search
	 * @param valueSize The value's size
	 */
	public SearchRefinement(ValueSize valueSize)
	{
		searchMode = SearchModes.UNKNOWN;
		this.valueSize = valueSize;
	}

	public SearchModes getSearchMode()
	{
		return searchMode;
	}

	public SearchCondition getSearchCondition()
	{
		if (searchCondition == null)
		{
			throw new IllegalArgumentException("The search condition is undefined");
		}

		return searchCondition;
	}

	public ValueSize getValueSize()
	{
		return valueSize;
	}

	public BigInteger getValue()
	{
		if (value == null)
		{
			throw new IllegalArgumentException("The value is undefined");
		}

		return value;
	}
}