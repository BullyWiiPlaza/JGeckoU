package wiiudev.gecko.client.connector.rpl.filesystem.memory;

import java.io.IOException;

import com.wiiudev.tcpgecko.TcpGecko;
import com.wiiudev.tcpgecko.WiiUException;
import com.wiiudev.tcpgecko.rpl.CoreInit;
import wiiudev.gecko.client.connector.rpl.CoreInit;

public class SimpleAllocatedBuffer extends SimpleBuffer implements IAllocatedBuffer
{
	private final CoreInit coreInit;
	private final boolean isHeap;
	private boolean isAllocated = true;

	public SimpleAllocatedBuffer(CoreInit coreInit, boolean isHeap, int address, byte[] data)
	{
		super(address, data);

		this.coreInit = coreInit;
		this.isHeap = isHeap;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void free() throws IOException
	{
		if (isAllocated)
		{
			return;
		}
		isAllocated = false;

		if (isHeap)
		{
			coreInit.freeHeap(getAddress());
		} else
		{
			coreInit.free(getAddress());
		}
	}
}