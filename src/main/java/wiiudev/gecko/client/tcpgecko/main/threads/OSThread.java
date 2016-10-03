package wiiudev.gecko.client.tcpgecko.main.threads;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static wiiudev.gecko.client.tcpgecko.main.threads.OSThreadStructure.*;

public class OSThread
{
	private int address;
	private int affinity;
	private OSThreadState state;
	private int priority;
	private String name;

	private MemoryReader memoryReader;
	private MemoryWriter memoryWriter;

	public OSThread(int address) throws IOException
	{
		this.address = address;
		memoryReader = new MemoryReader();
		memoryWriter = new MemoryWriter();

		setFields();
	}

	public static List<OSThread> readThreads() throws Exception
	{
		List<OSThread> osThreads = new ArrayList<>();

		MemoryReader memoryReader = new MemoryReader();
		int threadAddress = memoryReader.readInt(0xFFFFFFE0);
		int temporaryThreadAddress;

		while ((temporaryThreadAddress =
				memoryReader.readInt(threadAddress + 0x390)) != 0)
		{
			threadAddress = temporaryThreadAddress;
		}

		while ((temporaryThreadAddress =
				memoryReader.readInt(threadAddress
						+ LINK_ACTIVE.getOffset())) != 0)
		{
			OSThread osThread = new OSThread(threadAddress);
			osThreads.add(osThread);
			threadAddress = temporaryThreadAddress;
		}

		// The previous while would skip the last thread
		OSThread osThread = new OSThread(threadAddress);
		osThreads.add(osThread);

		return osThreads;
	}

	public void setState(OSThreadState state) throws IOException
	{
		OSThreadRPC.setState(this, state);
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
		priority = OSThreadRPC.getPriority(this);
		affinity = OSThreadRPC.getAffinity(this);
		setName();
		setInternalState();
	}

	private void setName() throws IOException
	{
		name = OSThreadRPC.getName(this);
		/*int nameLocation = memoryReader.readInt(address + NAME.getOffset());

		if (nameLocation == 0)
		{
			// No name, we're using the address instead
			name = new Hexadecimal(address, 8).toString();
		} else
		{
			// Read the name
			name = memoryReader.readString(nameLocation);

			// Replace those encapsulating brackets
			name = name.replaceAll("\\{", "");
			name = name.replaceAll("\\}", "");
		}*/
	}

	private void setInternalState() throws IOException
	{
		int threadState = memoryReader.readInt(getSuspendedAddress());
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
		return address + SUSPEND.getOffset();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name) throws IOException
	{
		OSThreadRPC.setName(this, name);
		this.name = name;
	}

	public String readRegisters() throws IOException
	{
		OSContext osContext = new OSContext(address);
		return osContext.toString();
	}

	public int getAffinity()
	{
		return affinity;
	}

	public void setAffinity(int affinity) throws IOException
	{
		memoryWriter.writeInt(address + AFFINITY.getOffset(), affinity);
	}

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority) throws IOException
	{
		memoryWriter.writeInt(address + PRIORITY.getOffset(), priority);
	}

	@Override
	public String toString()
	{
		return "Address: " + new Hexadecimal(address, 8) + System.lineSeparator()
				+ "Affinity: " + affinity + System.lineSeparator()
				+ "State: " + state + System.lineSeparator()
				+ "Priority: " + priority + System.lineSeparator()
				+ "Name: " + name;
	}
}