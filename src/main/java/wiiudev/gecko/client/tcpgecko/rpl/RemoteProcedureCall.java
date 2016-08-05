package wiiudev.gecko.client.tcpgecko.rpl;

import org.apache.commons.io.output.ByteArrayOutputStream;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;
import wiiudev.gecko.client.tcpgecko.main.enumerations.Commands;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RemoteProcedureCall extends TCPGecko
{
	private static int SMALL_RPC_PARAMETERS_COUNT = 8;

	public ExportedSymbol getSymbol(String rplName, String symbolName) throws IOException
	{
		return getSymbol(rplName, symbolName, false, false);
	}

	public ExportedSymbol getSymbol(String rplName, String symbolName, boolean isPointer, boolean isData) throws IOException
	{
		sendCommand(Commands.GET_SYMBOL);

		ByteArrayOutputStream symbolRequest = getSymbolRequest(rplName, symbolName);

		dataSender.writeByte(symbolRequest.size());
		dataSender.write(symbolRequest.toByteArray());
		dataSender.writeByte(isData ? 1 : 0);
		dataSender.flush();

		int address = dataReceiver.readInt();

		if (isPointer)
		{
			// Dereference pointer if we need to
			address = MemoryReader.dereference(address);
		}

		return new ExportedSymbol(address, rplName, symbolName);
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

	public long call(String rplName, String symbolName, int... parameters) throws IOException
	{
		ExportedSymbol exportedSymbol = getSymbol(rplName, symbolName, false, false);
		return exportedSymbol.call(parameters);
	}

	/**
	 * Calls a remote method.
	 *
	 * @param exportedSymbol The symbol that defines the method
	 * @param parameters     The parameters for the method
	 * @throws IllegalArgumentException If there are to many parameters
	 */
	public long call(ExportedSymbol exportedSymbol, int... parameters) throws IOException
	{
		int paddingIntegersCount = getPaddingIntegersCount(parameters);

		List<Integer> parametersList = Arrays.stream(parameters).boxed().collect(Collectors.toList());
		for (int paddingCountIndex = 0; paddingCountIndex < paddingIntegersCount; paddingCountIndex++)
		{
			// Pad for the parameters count for the small or big RPC calls respectively
			parametersList.add(0);
		}

		Commands command = parametersList.size() == SMALL_RPC_PARAMETERS_COUNT
				? Commands.RPC : Commands.RPC_BIG;
		sendCommand(command);
		int functionAddress = exportedSymbol.getAddress();
		dataSender.writeInt(functionAddress);

		for (int parameter : parametersList)
		{
			dataSender.writeInt(parameter);
		}

		dataSender.flush();

		return dataReceiver.readLong();
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

		throw new IllegalArgumentException("Invalid parameters count: " + parametersCount);
	}
}