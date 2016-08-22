package wiiudev.gecko.client.memory_search;

import wiiudev.gecko.client.memory_search.enumerations.SearchConditions;
import wiiudev.gecko.client.memory_search.enumerations.SearchModes;
import wiiudev.gecko.client.memory_search.enumerations.ValueSize;

import java.math.BigInteger;

public class SearchRefinement
{
	private SearchModes searchMode;
	private SearchConditions searchConditions;
	private ValueSize valueSize;
	private BigInteger value;

	/**
	 * Constructor for specific value searches
	 * @param searchConditions The memory_search condition
	 * @param valueSize The value's size
	 * @param value The value to memory_search
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
	 * @param searchConditions The memory_search condition
	 * @param valueSize The value's size
	 */
	public SearchRefinement(SearchConditions searchConditions, ValueSize valueSize)
	{
		searchMode = SearchModes.UNKNOWN;
		this.searchConditions = searchConditions;
		this.valueSize = valueSize;
	}

	/**
	 * Constructor for the first unknown value memory_search
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