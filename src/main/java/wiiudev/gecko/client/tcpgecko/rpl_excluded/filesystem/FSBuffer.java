package wiiudev.gecko.client.connector.rpl.filesystem;

import wiiudev.gecko.client.connector.rpl.filesystem.memory.IAllocatedBuffer;
import wiiudev.gecko.client.connector.rpl.filesystem.memory.IRemoteBuffer;

import java.io.IOException;

class FSBuffer
{
	private boolean initialized;
	private IRemoteBuffer buffer = null;

	public boolean isInitialized()
	{
		return initialized;
	}

	public void setInitialized(boolean added)
	{
		this.initialized = added;
	}

	public void setBuffer(IRemoteBuffer buffer)
	{
		this.buffer = buffer;
	}

	public IRemoteBuffer getBuffer()
	{
		return buffer;
	}

	public boolean hasBuffer()
	{
		return buffer != null;
	}

	public void removeBuffer() throws IOException
	{
		if (hasBuffer() && buffer instanceof IAllocatedBuffer)
		{
			((IAllocatedBuffer) buffer).free();
		}
	}
}