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
	public static void setThreadState(OSThread thread, OSThreadState state) throws IOException
	{
		MemoryWriter memoryWriter = new MemoryWriter();
		int threadAddress = thread.getAddress();
		int threadStateAddress = thread.getSuspendedAddress();
		memoryWriter.writeBoolean(threadStateAddress, state == OSThreadState.PAUSED);

		String symbolName = (state == OSThreadState.PAUSED) ? "OSSuspendThread" : "OSResumeThread";
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", symbolName);
		remoteProcedureCall.call(exportedSymbol, threadAddress);
	}

	public static OSThread getCurrentThread() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl",
				"OSGetCurrentThread");
		int address = remoteProcedureCall.call32(exportedSymbol);

		return new OSThread(address);
	}

	public static void setThreadName(OSThread thread, String name) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl",
				"OSSetThreadName");
		RemoteString remoteString = new RemoteString(name);
		remoteProcedureCall.call32(exportedSymbol,
				thread.getAddress(),
				remoteString.getAddress());
	}

	public static String getThreadName(OSThread thread) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl",
				"OSGetThreadName");
		int address = remoteProcedureCall.call32(exportedSymbol, thread.getAddress());

		if (address == 0)
		{
			// No name, we're using the thread's address instead
			return new Hexadecimal(thread.getAddress(), 8).toString();
		} else
		{
			// Alright, read the name
			MemoryReader memoryReader = new MemoryReader();
			return memoryReader.readString(address);
		}
	}
}