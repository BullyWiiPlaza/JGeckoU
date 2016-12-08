package wiiudev.gecko.client.network_scanner;

import wiiudev.gecko.client.tcpgecko.main.Connector;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;

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
						MemoryReader memoryReader = new MemoryReader();
						int readValue = memoryReader.read(0x10000000);
						int expected = 0x1000;

						if (readValue != expected)
						{
							throw new IllegalStateException("Read value was " + readValue + " but expected " + expected + "!");
						}

						return subNetworkAddress;
					}
				} catch (Exception ignored)
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