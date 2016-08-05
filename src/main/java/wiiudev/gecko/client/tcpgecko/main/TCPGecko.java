package wiiudev.gecko.client.tcpgecko.main;

import wiiudev.gecko.client.tcpgecko.main.enumerations.Commands;
import wiiudev.gecko.client.tcpgecko.main.enumerations.Status;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public abstract class TCPGecko
{
	protected DataOutputStream dataSender;
	protected DataInputStream dataReceiver;
	static final ReentrantLock reentrantLock = new ReentrantLock(true);
	public static final int MAXIMUM_MEMORY_CHUNK_SIZE = 0x400;
	public static boolean enforceMemoryAccessProtection = true;

	public TCPGecko()
	{
		if (Connector.getInstance().getClientSocket() == null)
		{
			throw new IllegalStateException("Not connected");
		}

		dataSender = Connector.getInstance().getDataSender();
		dataReceiver = Connector.getInstance().getDataReceiver();
	}

	public static boolean isConnected()
	{
		return Connector.getInstance().getDataSender() != null
				&& Connector.getInstance().getDataReceiver() != null;
	}

	protected Status readStatus() throws IOException
	{
		reentrantLock.lock();

		try
		{
			byte serverStatus = dataReceiver.readByte();
			return Status.getStatus(serverStatus);
		} finally
		{
			reentrantLock.unlock();
		}
	}

	/**
	 * Sends a command to the Wii U
	 *
	 * @param command The command to send
	 * @throws IOException If there was an error writing to the Wii U
	 */
	protected void sendCommand(Commands command) throws IOException
	{
		dataSender.write(command.value);
	}
}