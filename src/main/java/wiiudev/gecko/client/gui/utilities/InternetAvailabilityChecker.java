package wiiudev.gecko.client.gui.utilities;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class InternetAvailabilityChecker
{
	public static boolean isInternetAvailable() throws IOException
	{
		return isHostAvailable("google.com") || isHostAvailable("amazon.com")
				|| isHostAvailable("facebook.com") || isHostAvailable("apple.com");
	}

	private static boolean isHostAvailable(String hostName) throws IOException
	{
		try (Socket socket = new Socket())
		{
			int port = 80;
			InetSocketAddress socketAddress = new InetSocketAddress(hostName, port);
			socket.connect(socketAddress, 1000);

			return true;
		} catch (Exception exception)
		{
			return false;
		}
	}
}
