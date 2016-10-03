package wiiudev.gecko.client.tcpgecko.main.threads;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;
import wiiudev.gecko.client.tcpgecko.rpl.ExportedSymbol;
import wiiudev.gecko.client.tcpgecko.rpl.RemoteProcedureCall;
import wiiudev.gecko.client.tcpgecko.rpl.structures.RemoteString;

import java.io.IOException;

public class OSThreadRPC
{
	public static void setState(OSThread thread, OSThreadState state) throws IOException
	{
		MemoryWriter memoryWriter = new MemoryWriter();
		int threadAddress = thread.getAddress();
		int threadStateAddress = thread.getSuspendedAddress();
		memoryWriter.writeBoolean(threadStateAddress, state == OSThreadState.PAUSED);

		String symbolName = (state == OSThreadState.PAUSED) ? "OSSuspendThread" : "OSResumeThread";
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol setThreadState = remoteProcedureCall.getSymbol("coreinit.rpl", symbolName);
		remoteProcedureCall.call(setThreadState, threadAddress);
	}

	public static OSThread getCurrent() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol getCurrentThread = remoteProcedureCall.getSymbol("coreinit.rpl",
				"OSGetCurrentThread");
		int address = remoteProcedureCall.callInt(getCurrentThread);

		return new OSThread(address);
	}

	public static void setName(OSThread thread, String name) throws IOException
	{
		try (RemoteString allocatedName = new RemoteString(name))
		{
			RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
			ExportedSymbol setThreadName = remoteProcedureCall.getSymbol("coreinit.rpl",
					"OSSetThreadName");
			remoteProcedureCall.callInt(setThreadName,
					thread.getAddress(),
					allocatedName.getAddress());
		}
	}

	public static String getName(OSThread thread) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol getName = remoteProcedureCall.getSymbol("coreinit.rpl",
				"OSGetThreadName");
		int address = remoteProcedureCall.callInt(getName, thread.getAddress());
		String name;

		if (address == 0)
		{
			// No name, we're using the thread's address instead
			name = new Hexadecimal(thread.getAddress(), 8).toString();
		} else
		{
			// Alright, read the name
			MemoryReader memoryReader = new MemoryReader();
			name = memoryReader.readString(address);

			// Replace those encapsulating brackets
			name = name.replaceAll("\\{", "");
			name = name.replaceAll("\\}", "");
		}

		return name;
	}

	public static void disableInterrupts() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol disableInterrupts = remoteProcedureCall.getSymbol("coreinit.rpl",
				"OSDisableInterrupts");
		remoteProcedureCall.call(disableInterrupts);
	}

	public static void enableInterrupts() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol enableInterrupts = remoteProcedureCall.getSymbol("coreinit.rpl",
				"OSEnableInterrupts");
		remoteProcedureCall.call(enableInterrupts);
	}

	public static boolean areInterruptsEnabled() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol enableInterrupts = remoteProcedureCall.getSymbol("coreinit.rpl",
				"OSIsInterruptEnabled");
		return remoteProcedureCall.callBoolean(enableInterrupts);
	}

	public static int getAffinity(OSThread osThread) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol getAffinity = remoteProcedureCall.getSymbol("coreinit.rpl",
				"OSGetThreadAffinity");
		return remoteProcedureCall.callInt(getAffinity, osThread.getAddress());
	}

	public static int getPriority(OSThread osThread) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol getPriority = remoteProcedureCall.getSymbol("coreinit.rpl",
				"OSGetThreadPriority");
		return remoteProcedureCall.callInt(getPriority, osThread.getAddress());
	}
}