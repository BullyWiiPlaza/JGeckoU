package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import wiiudev.gecko.client.tcpgecko.rpl.structures.RemoteString;

import java.io.IOException;

public class AccessMode extends RemoteString
{
	public static final String READ = "r";
	public static final String WRITE = "w";

	public AccessMode(String string) throws IOException
	{
		super(string);

		if(!string.equals("r") && !string.equals("w"))
		{
			throw new IllegalArgumentException("Illegal access mode!");
		}
	}
}