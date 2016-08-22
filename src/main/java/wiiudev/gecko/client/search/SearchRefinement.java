package wiiudev.gecko.client.search;

import java.math.BigInteger;

public class SearchRefinement
{
	private SearchModes searchMode;
	private SearchConditions searchConditions;
	private ValueSize valueSize;
	private BigInteger value;

	/**
	 * Constructor for specific value searches
	 * @param searchConditions The search condition
	 * @param valueSize The value's size
	 * @param value The value to search
	 */
	public SearchRefinement(SearchConditions searchConditions, ValueSize valueSize, BigInteger value)
	{
		this.searchMode = SearchModes.SPECIFIC;
		this.searchConditions = searchConditions;
		this.valueSize = valueSize;
		this.value = value;
	}

	/**
	 * Constructor for the all unknown value searches but the first
	 * @param searchConditions The search condition
	 * @param valueSize The value's size
	 */
	public SearchRefinement(SearchConditions searchConditions, ValueSize valueSize)
	{
		searchMode = SearchModes.UNKNOWN;
		this.searchConditions = searchConditions;
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

	public SearchConditions getSearchCondition()
	{
		return searchConditions;
	}

	public ValueSize getValueSize()
	{
		return valueSize;
	}

	public BigInteger getValue()
	{
		return value;
	}
}