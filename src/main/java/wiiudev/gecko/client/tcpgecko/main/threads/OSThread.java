package wiiudev.gecko.client.tcpgecko.main.threads;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;
import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;

import java.io.IOException;

public class OSThread
{
	private int address;
	private int stateAddress;
	private int nameLocationPointer;
	private String name;
	private OSThreadState state;

	private MemoryReader memoryReader;

	public OSThread(int address) throws IOException
	{
		this.address = address;
		stateAddress = address + 0x328;
		nameLocationPointer = address + 0x5C0;
		memoryReader = new MemoryReader();

		setFields();
	}

	public void setState(OSThreadState state) throws IOException
	{
		CoreInit.setThreadState(this, state);
		this.state = state;
	}

	public void toggleState() throws IOException
	{
		if(state == OSThreadState.RUNNING)
		{
			setState(OSThreadState.PAUSED);
		}
		else
		{
			setState(OSThreadState.RUNNING);
		}
	}

	private void setFields() throws IOException
	{
		int nameLocation = memoryReader.readInt(nameLocationPointer);

		if (nameLocation == 0)
		{
			// No name, we're using the address instead
			name = new Hexadecimal(address, 8).toString();
		}
		else
		{
			// Read the name
			name = memoryReader.readString(nameLocation);
		}

		setInternalState();
	}

	private void setInternalState() throws IOException
	{
		int threadState = memoryReader.readInt(stateAddress);
		state = OSThreadState.parse(threadState);
	}

	public int getAddress()
	{
		return address;
	}

	public OSThreadState getState()
	{
		return state;
	}

	public int getStateAddress()
	{
		return stateAddress;
	}

	public String geName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return "Name: " + name + System.lineSeparator()
				+ "Address: " + new Hexadecimal(address, 8) + System.lineSeparator()
				+ "State: " + state;
	}
}