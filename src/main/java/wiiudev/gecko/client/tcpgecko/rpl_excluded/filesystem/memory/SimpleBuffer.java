package wiiudev.gecko.client.connector.rpl.filesystem.memory;

import java.io.IOException;

/**
 * A simple and stupid implementation of a remote buffer, only tracks if it is dirty<br>
 * So it flushes the entire buffer, not just the changed parts
 *
 * @author gudenau
 */
class SimpleBuffer implements IRemoteBuffer
{
	private final int address;
	private final byte[] data;
	private boolean dirty = false;

	public SimpleBuffer(int address, byte[] data)
	{
		this.address = address;
		this.data = data;
	}

	@Override
	public int getSize()
	{
		return data.length;
	}

	@Override
	public int getAddress()
	{
		return address;
	}

	@Override
	public void setData(int address, byte[] data, int offset, int length)
	{
		System.arraycopy(data, offset, this.data, address, length);
		dirty = true;
	}

	@Override
	public void getData(int address, byte[] data, int offset, int length)
	{
		System.arraycopy(this.data, address, this.data, offset, length);
	}

	@Override
	public boolean isDirty()
	{
		return dirty;
	}

	@Override
	public void flush() throws IOException
	{
		// gecko.writeMemory(address, data, 0, data.length);
		dirty = false;
	}

	@Override
	public void update() throws IOException
	{
		update(0, data.length);
		dirty = false;
	}

	@Override
	public void update(int offset, int size) throws IOException
	{
		// gecko.readMemory(address, data, offset, size);
	}

	@Override
	public void markDirty()
	{
		dirty = true;
	}

	@Override
	public void clearDirty()
	{
		dirty = false;
	}
}