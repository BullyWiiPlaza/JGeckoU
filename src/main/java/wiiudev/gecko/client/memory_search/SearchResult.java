package wiiudev.gecko.client.memory_search;

import org.apache.commons.io.output.ByteArrayOutputStream;
import wiiudev.gecko.client.memory_search.enumerations.ValueSize;

import java.io.IOException;
import java.math.BigInteger;

public class SearchResult implements Cloneable, Comparable
{
	private int address;
	private ValueSize valueSize;
	private BigInteger previousValue;
	private BigInteger currentValue;
	private BigInteger valueDifference;

	public SearchResult(int address, BigInteger previousValue, BigInteger currentValue, ValueSize valueSize)
	{
		this.address = address;
		this.valueSize = valueSize;
		this.previousValue = previousValue;
		this.currentValue = currentValue;

		setValueDifference();
	}

	private void setValueDifference()
	{
		BigInteger subtractionResult = previousValue.subtract(currentValue);
		valueDifference = subtractionResult.abs();
	}

	public BigInteger getCurrentValue()
	{
		return currentValue;
	}

	public BigInteger getPreviousValue()
	{
		return previousValue;
	}

	public byte[] getPreviousValueBytes() throws IOException
	{
		return getBytes(previousValue);
	}

	private byte[] getBytes(BigInteger bigInteger) throws IOException
	{
		byte[] previousValue = bigInteger.toByteArray();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int bytesCount = getValueSize().getBytesCount();
		int paddingBytes = bytesCount - previousValue.length;
		byteArrayOutputStream.write(new byte[paddingBytes]);
		byteArrayOutputStream.write(previousValue);

		return byteArrayOutputStream.toByteArray();
	}

	public byte[] getCurrentValueBytes() throws IOException
	{
		return getBytes(currentValue);
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

		return searchResult.getAddress() == getAddress();
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
	public int compareTo(Object object)
	{
		return new Integer(address).compareTo(((SearchResult) object).getAddress());
	}
}