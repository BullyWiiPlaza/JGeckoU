package wiiudev.gecko.client.network_scanner;

import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PingUtilities
{
	/**
	 * Pings an IP address
	 *
	 * @param internetProtocolAddress The internet protocol address to ping
	 * @return True if the address is responsive, false otherwise
	 */
	public static boolean isReachable(String internetProtocolAddress) throws IOException
	{
		List<String> command = new ArrayList<>();
		command.add("ping");

		if (SystemUtils.IS_OS_WINDOWS)
		{
			command.add("-n");
		} else if (SystemUtils.IS_OS_UNIX)
		{
			// TODO Test on an actual Unix machine (not virtual since the sub network won't match)
			command.add("-c");
		} else
		{
			throw new UnsupportedOperationException("Unsupported operating system");
		}

		command.add("1"); // The number of echo requests to send
		command.add("-w");
		command.add("100"); // Timeout in milliseconds to wait after each reply before it timed out

		command.add(internetProtocolAddress);

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();

		BufferedReader standardOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String outputLine;

		while ((outputLine = standardOutput.readLine()) != null)
		{
			// This should pick up Windows AND Unix hosts when the ping was unsuccessful
			if (outputLine.toLowerCase().contains("100%".toLowerCase()))
			{
				return false;
			}
		}

		// The host is reachable so it might be a Wii U
		return true;
	}
}