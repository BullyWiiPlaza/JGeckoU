package wiiudev.gecko.client.connector.rpl.filesystem;

/**
 * @author gudenau
 */
public class FSDirHandle
{
	private boolean initialized;
	private int buffer;
	private boolean hasBuffer;

	public boolean getInitialized()
	{
		return initialized;
	}

	public void setInitialized(boolean added)
	{
		this.initialized = added;
	}

	public void setBuffer(int buffer)
	{
		hasBuffer = true;
		this.buffer = buffer;
	}

	public int getBuffer()
	{
		return buffer;
	}

	public boolean hasBuffer()
	{
		return hasBuffer;
	}

	public void removeBuffer()
	{
		hasBuffer = false;
	}
}