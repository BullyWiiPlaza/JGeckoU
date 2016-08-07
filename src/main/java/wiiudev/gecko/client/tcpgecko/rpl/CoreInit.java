package wiiudev.gecko.client.tcpgecko.rpl;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThread;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThreadState;

import java.io.IOException;
import java.nio.ByteBuffer;

public class CoreInit
{
	public static long call(String symbolName, int... parameters) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		return remoteProcedureCall.call("coreinit.rpl", symbolName, parameters);
	}

	public static void setThreadState(OSThread thread, OSThreadState state) throws IOException
	{
		MemoryWriter memoryWriter = new MemoryWriter();
		int threadAddress = thread.getAddress();
		int threadStateAddress = thread.getSuspendedAddress();
		memoryWriter.writeBoolean(threadStateAddress, state == OSThreadState.PAUSED);
		String symbolName = (state == OSThreadState.PAUSED) ? "OSSuspendThread" : "OSResumeThread";
		call(symbolName, threadAddress);
	}

	public static int allocateDefaultHeapMemory(int size, int alignment) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl",
				"MEMAllocFromDefaultHeapEx", true, true);

		return (int) exportedSymbol.call(size, alignment);
	}

	public static void freeDefaultHeapMemory(int address) throws IOException
	{
		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl",
				"MEMFreeToDefaultHeap", true, true);

		exportedSymbol.call(address);
	}

	public static int allocateSystemMemory(int size, int alignment) throws IOException
	{
		return (int) call("OSAllocFromSystem", size, alignment);
	}

	public static void freeSystemMemory(int address) throws IOException
	{
		call("OSFreeToSystem", address);
		byte[] bytes = new MemoryReader().readBytes(address, 0x40);
		System.out.println(new String(bytes));
	}

	/**
	 * Allocates a String in the memory. The address is determined by the allocator
	 * @param string The String to allocate
	 * @return The address pointing to the beginning of the allocated String
	 */
	public static int allocateString(String string) throws IOException
	{
		int stringLength = string.length();
		int address = CoreInit.allocateSystemMemory(stringLength, 0x20);
		int size = getDataSize(stringLength);
		byte[] bytes = new MemoryReader().readBytes(address, stringLength + 5);
		System.out.println(new String(bytes));
		setMemory(address, 0x00, size);
		bytes = new MemoryReader().readBytes(address, stringLength + 5);
		System.out.println(new String(bytes));
		MemoryWriter memoryWriter = new MemoryWriter();
		memoryWriter.writeString(address, string);
		bytes = new MemoryReader().readBytes(address, stringLength + 5);
		System.out.println(new String(bytes));

		return address;
	}

	private static int getDataSize(int length)
	{
		return length + (32 - (length % 32));
	}

	private static void setMemory(int address, int value, int size) throws IOException
	{
		call("memset", address, value, size); // void memset(void * dest, uint32_t value, uint32_t bytes)
	}

	/**
	 * Print the message <code>message</code> to the screen.
	 * @param message The message to print
	 * @param isFatal Whether to halt the system or not
	 */
	public static void displayMessage(String message, boolean isFatal) throws IOException
	{
		int address = allocateString(message);

		if (isFatal)
		{
			call("OSFatal", address); // void OSFatal(const char *msg)
		} else
		{
			call("OSScreenPutFontEx", 1, 0, 0, address); // void OSScreenPutFontEx(int bufferNum, uint32_t posX, uint32_t posY, const char *str)
			call("OSScreenFlipBuffersEx", 1); // void OSScreenFlipBuffersEx(int bufferNum)
		}
	}

	public static int getEffectiveToPhysical(int address) throws IOException
	{
		long physical = call("OSEffectiveToPhysical", address);
		return getFirstInteger(physical);
	}

	private static int getFirstInteger(long value)
	{
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(value);

		return buffer.getInt(0);
	}

	/* // Crashes the console
	public static void exitCurrentProcess() throws IOException
	{
		call("_Exit", 0);
	}*/

	public static long getProcessPFID() throws IOException
	{
		return call("OSGetPFID");
	}

	public static boolean isAddressMapped(int address) throws IOException
	{
		return getEffectiveToPhysical(address) != 0;
	}
}