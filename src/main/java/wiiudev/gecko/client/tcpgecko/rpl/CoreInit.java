package wiiudev.gecko.client.tcpgecko.rpl;

import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThread;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThreadState;
import wiiudev.gecko.client.tcpgecko.rpl.structures.OSSystemInfo;
import wiiudev.gecko.client.tcpgecko.rpl.structures.RemoteString;

import java.io.IOException;

public class CoreInit
{
	public static long getOSTime() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "OSGetSystemTime");

		return remoteProcedureCall.call64(exportedSymbol);
	}

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

	public static int allocateDefaultHeapMemory(int size, int alignment) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl",
				"MEMAllocFromDefaultHeapEx", true, true);

		return remoteProcedureCall.call32(exportedSymbol, size, alignment);
	}

	public static void freeDefaultHeapMemory(int address) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl",
				"MEMFreeToDefaultHeap", true, true);

		remoteProcedureCall.call(exportedSymbol, address);
	}

	public static int allocateSystemMemory(int size, int alignment) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "OSAllocFromSystem");

		return remoteProcedureCall.call32(exportedSymbol, size, alignment);
	}

	public static void freeSystemMemory(int address) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl",
				"OSFreeToSystem");

		remoteProcedureCall.call(exportedSymbol, address);
	}

	public static void setMemory(int address, int value, int size) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "memset");

		// void memset(void * dest, uint32_t value, uint32_t bytes)
		remoteProcedureCall.call(exportedSymbol, address, value, size);
	}

	/**
	 * Print the message <code>message</code> to the screen.
	 *
	 * @param message The message to print
	 * @param isFatal Whether to halt the system or not
	 */
	public static void displayMessage(String message, boolean isFatal) throws IOException
	{
		try (RemoteString remoteString = new RemoteString(message))
		{
			RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();

			if (isFatal)
			{
				// void OSFatal(const char *msg)
				ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "OSFatal");
				remoteProcedureCall.call(exportedSymbol, remoteString.getAddress());
			} else
			{
				// void OSScreenPutFontEx(int bufferNum, uint32_t posX, uint32_t posY, const char *str)
				ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "OSScreenPutFontEx");
				remoteProcedureCall.call(exportedSymbol, 1, 0, 0, remoteString.getAddress());

				// void OSScreenFlipBuffersEx(int bufferNum)
				exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "OSScreenFlipBuffersEx");
				remoteProcedureCall.call(exportedSymbol, 1);
			}
		}
	}

	public static int getEffectiveToPhysical(int address) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "OSEffectiveToPhysical");

		return remoteProcedureCall.call32(exportedSymbol, address);
	}

	/**
	 * Freezes?
	 osThread: OSThread  = {OSThread@841}
	 address: int  = 0x105EB648
	 suspendedAddress: int  = 0x105EB970
	 nameLocationPointer: int  = 0x105EBC08
	 name: String  = "105EB648"
	 state: OSThreadState  = "Running"
	 */
	public static OSThread getCurrentThread() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "OSGetCurrentThread");
		int address = remoteProcedureCall.call32(exportedSymbol);

		return new OSThread(address);
	}

	/**
	 * Freezes?
	 */
	public static OSSystemInfo getSystemInformation() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "OSSystemInfo");
		int address = remoteProcedureCall.call32(exportedSymbol);

		return new OSSystemInfo(address);
	}

	public static int getProcessPFID() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "OSGetPFID");

		return remoteProcedureCall.call32(exportedSymbol);
	}

	public static long getTitleID() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "OSGetTitleID");

		return remoteProcedureCall.call64(exportedSymbol);
	}

	public static boolean isAddressMapped(int address) throws IOException
	{
		return getEffectiveToPhysical(address) != 0;
	}

	public static long getOSID() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "OSGetOSID");

		return remoteProcedureCall.call64(exportedSymbol);
	}

	public static int getAppFlags() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "__OSGetAppFlags");

		return remoteProcedureCall.call32(exportedSymbol);
	}

	public static int getSDKVersion() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "__OSGetProcessSDKVersion");

		return remoteProcedureCall.call32(exportedSymbol);
	}

	public static void shutdown() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "OSShutdown");

		remoteProcedureCall.call(exportedSymbol);
	}
}