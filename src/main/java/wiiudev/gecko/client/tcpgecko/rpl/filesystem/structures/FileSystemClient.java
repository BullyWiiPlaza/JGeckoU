package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import wiiudev.gecko.client.tcpgecko.rpl.filesystem.RemoteFileSystem;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.ErrorHandling;

import java.io.IOException;

public class FileSystemClient extends FileSystemObject
{
	private RemoteFileSystem remoteFileSystem;

	public FileSystemClient() throws IOException
	{
		super(0x1700, 0x20);
	}

	public boolean isRegistered()
	{
		return remoteFileSystem != null;
	}

	public void setRegistered(RemoteFileSystem remoteFileSystem)
	{
		this.remoteFileSystem = remoteFileSystem;
	}

	@Override
	public void close() throws IOException
	{
		super.close();

		if (isRegistered())
		{
			remoteFileSystem.unregisterClient(this, ErrorHandling.NONE);
		}
	}
}