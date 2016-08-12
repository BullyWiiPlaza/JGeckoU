package wiiudev.gecko.client.gui.tabs.watch_list;

import wiiudev.gecko.client.gui.MemoryPointerExpression;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.ValueSize;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;

import java.io.IOException;
import java.util.Vector;

public class WatchListElement
{
	private String name;
	private MemoryPointerExpression addressExpression;
	private ValueSize valueSize;

	public WatchListElement(String name, MemoryPointerExpression addressExpression, ValueSize valueSize)
	{
		this.name = name;
		this.addressExpression = addressExpression;
		this.valueSize = valueSize;
	}

	public WatchListElement(Vector element)
	{
		int vectorIndex = 0;
		name = (String) element.get(vectorIndex++);
		addressExpression = (MemoryPointerExpression) element.get(vectorIndex++);
		valueSize = (ValueSize) element.get(vectorIndex);
	}

	public ValueSize getValueSize()
	{
		return valueSize;
	}

	public MemoryPointerExpression getAddressExpression()
	{
		return addressExpression;
	}

	public String getName()
	{
		return name;
	}

	public String readValue() throws IOException
	{
		long destinationAddress = addressExpression.getDestinationAddress();

		if(destinationAddress == MemoryPointerExpression.INVALID_POINTER)
		{
			return "INVALID";
		}
		else
		{
			MemoryReader memoryReader = new MemoryReader();
			return memoryReader.readValue((int) destinationAddress, valueSize);
		}
	}
}