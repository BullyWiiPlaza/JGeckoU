package wiiudev.gecko.client.connector;

import java.io.IOException;

/**
 * A class to represent a symbol obtained from the Wii U
 *
 * @author gudenau
 */
public class ExportedSymbol
{
	private final int address;
	private final String rplName;
	private final String symbolName;

	ExportedSymbol(int address, String rplName, String symbolName)
	{
		this.address = address;
		this.rplName = rplName;
		this.symbolName = symbolName;
	}

	/**
	 * Calls this symbol with the provided parameters
	 * @param parameters The parameters to pass
	 */
	public long call(int... parameters) throws IOException
	{
		MemoryReader memoryReader = new MemoryReader();
		return memoryReader.call(this, parameters);
	}

	/**
	 * Gets the address of the symbol
	 */
	public int getAddress()
	{
		return address;
	}
}