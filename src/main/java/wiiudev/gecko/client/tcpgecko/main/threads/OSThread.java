package wiiudev.gecko.client.tcpgecko.main.threads;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;
import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OSThread
{
	private int address;
	private int suspendedAddress;
	private int nameLocationPointer;
	private String name;
	private OSThreadState state;

	private MemoryReader memoryReader;

	public OSThread(int address) throws IOException
	{
		this.address = address;
		suspendedAddress = address + OSThreadStruct.SUSPEND.getOffset();
		nameLocationPointer = address + OSThreadStruct.NAME.getOffset();
		memoryReader = new MemoryReader();

		setFields();
	}

	public static List<OSThread> readThreads() throws Exception
	{
		List<OSThread> osThreads = new ArrayList<>();

		MemoryReader memoryReader = new MemoryReader();
		int threadAddress = memoryReader.readInt(0xFFFFFFE0);
		int temporaryThreadAddress;

		while ((temporaryThreadAddress = memoryReader.readInt(threadAddress + 0x390)) != 0)
		{
			threadAddress = temporaryThreadAddress;
			Thread.sleep(10); // Sleeps to avoid getting stuck (to be investigated why)
		}

		while ((temporaryThreadAddress = memoryReader.readInt(threadAddress + OSThreadStruct.LINK_ACTIVE.getOffset())) != 0)
		{
			OSThread osThread = new OSThread(threadAddress);
			osThreads.add(osThread);
			threadAddress = temporaryThreadAddress;
			Thread.sleep(10);
		}

		// The previous while would skip the last thread
		OSThread osThread = new OSThread(threadAddress);
		osThreads.add(osThread);

		return osThreads;
	}

	public void setState(OSThreadState state) throws IOException
	{
		CoreInit.setThreadState(this, state);
		this.state = state;
	}

	public void toggleState() throws IOException
	{
		if (state == OSThreadState.RUNNING)
		{
			setState(OSThreadState.PAUSED);
		} else
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
		} else
		{
			// Read the name
			name = memoryReader.readString(nameLocation);
		}

		setInternalState();
	}

	private void setInternalState() throws IOException
	{
		int threadState = memoryReader.readInt(suspendedAddress);
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

	public int getSuspendedAddress()
	{
		return suspendedAddress;
	}

	public String getName()
	{
		return name;
	}

	public String readRegisters() throws IOException
	{
		OSContext osContext = new OSContext(address);
		return osContext.toString();
	}
}