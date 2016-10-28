package wiiudev.gecko.client.tcpgecko.rpl;

import wiiudev.gecko.client.tcpgecko.rpl.structures.OSSystemInfo;
import wiiudev.gecko.client.tcpgecko.rpl.structures.RemoteString;

import java.io.IOException;

public class CoreInit
{
	public static int getOSCoreCount() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "OSGetCoreCount");

		return remoteProcedureCall.callInt(exportedSymbol);
	}

	public static long getOSTime() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "OSGetSystemTime");

		return remoteProcedureCall.callLong(exportedSymbol);
	}

	public static int allocateDefaultHeapMemory(int size, int alignment) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "MEMAllocFromDefaultHeapEx", true, true);

		return remoteProcedureCall.callInt(exportedSymbol, size, alignment);
	}

	public static void freeDefaultHeapMemory(int address) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "MEMFreeToDefaultHeap", true, true);

		remoteProcedureCall.call(exportedSymbol, address);
	}

	public static int allocateSystemMemory(int size, int alignment) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "OSAllocFromSystem");

		return remoteProcedureCall.callInt(exportedSymbol, size, alignment);
	}

	public static void freeSystemMemory(int address) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "OSFreeToSystem");

		remoteProcedureCall.call(exportedSymbol, address);
	}

	public static void clearMemory(int address, int size) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "memclr");

		remoteProcedureCall.call(exportedSymbol, address, size);
	}

	public static void setMemory(int address, int value, int size) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "memset");

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
				ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "OSFatal");
				remoteProcedureCall.call(exportedSymbol, remoteString.getAddress());
			} else
			{
				// void OSScreenPutFontEx(int bufferNum, uint32_t posX, uint32_t posY, const char *str)
				ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "OSScreenPutFontEx");
				remoteProcedureCall.call(exportedSymbol, 1, 0, 0, remoteString.getAddress());

				// void OSScreenFlipBuffersEx(int bufferNum)
				exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "OSScreenFlipBuffersEx");
				remoteProcedureCall.call(exportedSymbol, 1);
			}
		}
	}

	/**
	 * @param address The address to check
	 * @return Whether the address is readable
	 * @throws IOException
	 */
	public static boolean isAddressReadable(int address) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "OSIsAddressValid");

		return remoteProcedureCall.callBoolean(exportedSymbol, address);
	}

	public static int getEffectiveToPhysical(int address) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "OSEffectiveToPhysical");

		return remoteProcedureCall.callInt(exportedSymbol, address);
	}

	public static OSSystemInfo getSystemInformation() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(),
				"OSGetSystemInfo");
		int address = remoteProcedureCall.callInt(exportedSymbol);

		return new OSSystemInfo(address);
	}

	public static int getProcessPFID() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "OSGetPFID");

		return remoteProcedureCall.callInt(exportedSymbol);
	}

	public static long getTitleID() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "OSGetTitleID");

		return remoteProcedureCall.callLong(exportedSymbol);
	}

	public static long getOSIdentifier() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "OSGetOSID");

		return remoteProcedureCall.callLong(exportedSymbol);
	}

	public static int getAppFlags() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "__OSGetAppFlags");

		return remoteProcedureCall.callInt(exportedSymbol);
	}

	public static int getSDKVersion() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "__OSGetProcessSDKVersion");

		return remoteProcedureCall.callInt(exportedSymbol);
	}

	public static void shutdown() throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "OSShutdown");

		remoteProcedureCall.call(exportedSymbol);
	}

}