package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import wiiudev.gecko.client.tcpgecko.rpl.structures.RemoteString;

import java.io.IOException;

public class FileSystemAccessMode extends RemoteString
{
	public FileSystemAccessMode(FileAccessMode fileAccessMode) throws IOException
	{
		super(fileAccessMode.toString());
	}
}