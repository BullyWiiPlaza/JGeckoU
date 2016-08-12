package wiiudev.gecko.client.tcpgecko.rpl.structures;

import java.io.IOException;

public class FindSymbol extends AllocatedMemory
{
	public FindSymbol() throws IOException
	{
		super(4 * 3, 0x20);
	}
}