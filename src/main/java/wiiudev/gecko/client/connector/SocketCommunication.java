package wiiudev.gecko.client.connector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public abstract class SocketCommunication
{
	protected DataOutputStream dataSender;
	protected DataInputStream dataReceiver;
	protected static final ReentrantLock reentrantLock = new ReentrantLock(true);

	public SocketCommunication()
	{
		if (Connector.getInstance().getClientSocket() == null)
		{
			throw new IllegalStateException("Not connected");
		}

		dataSender = Connector.getInstance().getDataSender();
		dataReceiver = Connector.getInstance().getDataReceiver();
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