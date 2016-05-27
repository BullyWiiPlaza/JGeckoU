package wiiudev.gecko.client.scanner;

import wiiudev.gecko.client.connector.Connector;
import wiiudev.gecko.client.connector.MemoryReader;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Scans the network for a possible Wii U console in order to provide an auto-detection functionality
 */
public class WiiUFinder
{
	/**
	 * Gets all addresses from the computer's sub network AND checks them to find the Wii U console
	 *
	 * @return The Wii U console's local IP address
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static String getNintendoWiiUInternetProtocolAddress() throws IOException, InterruptedException,
			ExecutionException
	{
		String localIPAddress = NetworkUtilities.getLocalInternetProtocolAddress();
		int networkMaskLength = NetworkUtilities.getLocalNetworkMaskLength();
		String[] subNetworkAddresses = NetworkUtilities.getAllSubNetworkAddresses(localIPAddress, networkMaskLength);

		return getWiiUIPAddress(subNetworkAddresses);
	}

	/**
	 * Pings all available IP addresses in the current sub network in order to find the Wii U console
	 */
	private static String getWiiUIPAddress(String[] subNetworkAddresses) throws InterruptedException, ExecutionException
	{
		int subNetStartingIndex = 1;
		int subNetUpperBound = 256;
		int threadPoolSize = subNetUpperBound - subNetStartingIndex;
		ExecutorService pool = Executors.newFixedThreadPool(threadPoolSize);
		ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(pool);

		for (String subNetworkAddress : subNetworkAddresses)
		{
			completionService.submit(() ->
			{
				try
				{
					if (PingUtilities.isReachable(subNetworkAddress))
					{
						Connector.getInstance().connect(subNetworkAddress);
						assert new MemoryReader().readInt(0x10000000) == 0x1000 : "Read value didn't match";

						return subNetworkAddress;
					}
				} catch (IOException ignored)
				{

				}

				return null;
			});
		}

		for (int tasksIndex = 0; tasksIndex < threadPoolSize; tasksIndex++)
		{
			// Retrieve results as they become available
			String result = completionService.take().get();

			if (result != null)
			{
				return result;
			}
		}

		pool.shutdown();

		return null;
	}
}