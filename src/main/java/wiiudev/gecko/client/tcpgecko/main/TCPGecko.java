package wiiudev.gecko.client.tcpgecko.main;

import wiiudev.gecko.client.tcpgecko.main.enumerations.Command;
import wiiudev.gecko.client.tcpgecko.main.enumerations.Status;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class TCPGecko
{
	protected DataOutputStream dataSender;
	protected DataInputStream dataReceiver;
	public static final CloseableReentrantLock reentrantLock = new CloseableReentrantLock();
	public static int MAXIMUM_MEMORY_CHUNK_SIZE = 0x400; // runningFromIntelliJ() ? 0x5000 : 0x400;
	public static boolean enforceMemoryAccessProtection = true;
	public static boolean hasRequestedBytes = false;

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
		Connector connector = Connector.getInstance();

		return connector.getClientSocket() != null
				&& !connector.getClientSocket().isClosed();
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

	private static boolean runningFromIntelliJ()
	{
		String classPath = System.getProperty("java.class.path");
		return classPath.contains("IntelliJ IDEA");
	}

	/**
	 * Sends a command to the Wii U
	 *
	 * @param command The command to send
	 * @throws IOException If there was an error writing to the Wii U
	 */
	protected void sendCommand(Command command) throws IOException
	{
		dataSender.write(command.value);
	}
}