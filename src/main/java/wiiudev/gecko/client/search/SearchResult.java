package wiiudev.gecko.client.search;

import java.math.BigInteger;

public class SearchResult implements Cloneable, Comparable
{
	private int address;
	private ValueSize valueSize;
	private BigInteger previousValue;
	private BigInteger currentValue;
	private BigInteger valueDifference;

	public SearchResult(int address)
	{
		this.address = address;
	}

	public SearchResult(int address, BigInteger previousValue, BigInteger currentValue, ValueSize valueSize)
	{
		this(address);
		this.valueSize = valueSize;
		this.previousValue = previousValue;
		this.currentValue = currentValue;

		setValueDifference();
	}

	private void setValueDifference()
	{
		if (previousValue == null || currentValue == null)
		{
			valueDifference = null;
		} else
		{
			valueDifference = previousValue.subtract(currentValue).abs();
		}
	}

	public BigInteger getCurrentValue()
	{
		return currentValue;
	}

	public BigInteger getPreviousValue()
	{
		return previousValue;
	}

	public int getAddress()
	{
		return address;
	}

	public BigInteger getValueDifference()
	{
		return valueDifference;
	}

	public void updateValue(BigInteger retrievedValue)
	{
		previousValue = currentValue;
		currentValue = retrievedValue;
		setValueDifference();
	}

	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof SearchResult))
		{
			return false;
		}

		SearchResult searchResult = (SearchResult) object;

		return searchResult.getAddress() == this.getAddress();
	}

	@Override
	public int hashCode()
	{
		return Integer.hashCode(address);
	}

	public ValueSize getValueSize()
	{
		return valueSize;
	}

	@Override
	public SearchResult clone()
	{
		return new SearchResult(address, previousValue, currentValue, valueSize);
	}

	@Override
	public int compareTo(Object o)
	{
		return new Integer(address).compareTo(((SearchResult) o).getAddress());
	}
}