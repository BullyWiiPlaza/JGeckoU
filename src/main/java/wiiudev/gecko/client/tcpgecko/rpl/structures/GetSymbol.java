package wiiudev.gecko.client.tcpgecko.rpl.structures;

import java.io.IOException;

public class GetSymbol extends AllocatedMemory
{
	public GetSymbol() throws IOException
	{
		super(8 + 4 + 8, 0x20);
	}
}