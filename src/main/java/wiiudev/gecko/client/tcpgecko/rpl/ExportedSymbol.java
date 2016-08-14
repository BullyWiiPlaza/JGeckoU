package wiiudev.gecko.client.tcpgecko.rpl;

/**
 * A class to represent a symbol obtained from the Wii U
 *
 * @author gudenau
 */
public class ExportedSymbol
{
	private int address;
	private String rplName;
	private String symbolName;

	public ExportedSymbol(int address, String rplName, String symbolName)
	{
		this.address = address;
		this.rplName = rplName;
		this.symbolName = symbolName;
	}

	/**
	 * Gets the address of the symbol
	 */
	public int getAddress()
	{
		return address;
	}

	public void setAddress(int address)
	{
		this.address = address;
	}

	public String getSymbolName()
	{
		return symbolName;
	}

	public String getRplName()
	{
		return rplName;
	}
}