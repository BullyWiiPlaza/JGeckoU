package wiiudev.gecko.client.tcpgecko.rpl;

import wiiudev.gecko.client.tcpgecko.main.CloseableReentrantLock;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;
import wiiudev.gecko.client.tcpgecko.main.enumerations.Commands;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

public class RemoteProcedureCall extends TCPGecko
{
	private static int SMALL_RPC_PARAMETERS_COUNT = 8;

	public ExportedSymbol getSymbol(String rplName, String symbolName) throws IOException
	{
		return getSymbol(rplName, symbolName, false, false);
	}

	ExportedSymbol getSymbol(String rplName, String symbolName, boolean isPointer, boolean isData) throws IOException
	{
		ByteArrayOutputStream symbolRequest = getSymbolRequest(rplName, symbolName);

		try (CloseableReentrantLock ignored = reentrantLock.acquire())
		{
			sendCommand(Commands.GET_SYMBOL);

			dataSender.writeByte(symbolRequest.size());
			dataSender.write(symbolRequest.toByteArray());
			dataSender.writeByte(isData ? 1 : 0);
			dataSender.flush();

			int address = dataReceiver.readInt();

			if (isPointer)
			{
				// Dereference the pointer if we need to
				address = MemoryReader.dereference(address);
			}

			return new ExportedSymbol(address, rplName, symbolName);
		}
	}

	private ByteArrayOutputStream getSymbolRequest(String rplName, String symbolName) throws IOException
	{
		// We need to buffer the strings to get the length
		ByteArrayOutputStream request = new ByteArrayOutputStream();

		// This length is static at least
		request.write(toByteArray(8));

		int length = 8 + rplName.length() + 1;
		byte[] lengthBytes = toByteArray(length);
		request.write(lengthBytes);

		// Write the UTF-8 encoded null-terminated names
		request.write(rplName.getBytes(StandardCharsets.UTF_8));
		request.write(0);
		request.write(symbolName.getBytes(StandardCharsets.UTF_8));
		request.write(0);

		return request;
	}

	private byte[] toByteArray(int integer)
	{
		return ByteBuffer.allocate(4).putInt(integer).array();
	}

	/**
	 * Calls a remote method.
	 *
	 * @param exportedSymbol The symbol that defines the method
	 * @param parameters     The parameters for the method
	 * @return The first 32-bit of the return value
	 * @throws IllegalArgumentException If there are too many parameters
	 */
	public int callInt(ExportedSymbol exportedSymbol, int... parameters) throws IOException
	{
		return (int) call(exportedSymbol, true, parameters);
	}

	public boolean callBoolean(ExportedSymbol exportedSymbol, int... parameters) throws IOException
	{
		return (int) call(exportedSymbol, true, parameters) == 1;
	}

	public String callString(ExportedSymbol exportedSymbol, int... parameters) throws IOException
	{
		int address = callInt(exportedSymbol, parameters);
		MemoryReader memoryReader = new MemoryReader();

		return memoryReader.readString(address);
	}

	public void call(ExportedSymbol exportedSymbol, int... parameters) throws IOException
	{
		callInt(exportedSymbol, parameters);
	}

	public long callLong(ExportedSymbol exportedSymbol, int... parameters) throws IOException
	{
		return (long) call(exportedSymbol, false, parameters);
	}

	private Number call(ExportedSymbol exportedSymbol, boolean returnFirstInteger, int... parameters) throws IOException
	{
		int paddingIntegersCount = getPaddingIntegersCount(parameters);
		int totalParametersCount = parameters.length + paddingIntegersCount;

		Commands command = totalParametersCount == SMALL_RPC_PARAMETERS_COUNT
				? Commands.RPC : Commands.RPC_BIG;


		int functionAddress = exportedSymbol.getAddress();

		// Function address and parameters
		IntBuffer request = IntBuffer.allocate(1 + totalParametersCount);
		request.put(functionAddress);

		for (int parameter : parameters)
		{
			request.put(parameter);
		}

		try (CloseableReentrantLock ignored = reentrantLock.acquire())
		{
			sendCommand(command);

			for (int requestComponent : request.array())
			{
				dataSender.writeInt(requestComponent);
			}

			dataSender.flush();

			// The reply is 8 bytes
			long reply = dataReceiver.readLong();

			if (returnFirstInteger)
			{
				return getFirstInteger(reply);
			} else
			{
				return reply;
			}
		}
	}

	/**
	 * @param value The input value to use
	 * @return The first 32-bit of the <code>value</code>
	 */
	private int getFirstInteger(long value)
	{
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(value);

		return buffer.getInt(0);
	}

	private int getPaddingIntegersCount(int... parameters)
	{
		int parametersCount = parameters.length;

		if (parametersCount <= SMALL_RPC_PARAMETERS_COUNT)
		{
			return SMALL_RPC_PARAMETERS_COUNT - parametersCount;
		}

		int BIG_RPC_PARAMETERS_COUNT = 16;
		if (parametersCount <= BIG_RPC_PARAMETERS_COUNT)
		{
			return BIG_RPC_PARAMETERS_COUNT - parametersCount;
		}

		throw new IllegalArgumentException("Invalid parameters count " + parametersCount);
	}
}