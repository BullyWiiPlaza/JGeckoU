package wiiudev.gecko.client.connector;

import java.util.regex.Pattern;

public class IPAddressValidator
{
	/**
	 * Checks whether the <code>ipAddress</code> is a valid IPv4 address
	 *
	 * @param ipAddress The IP address to validate
	 * @return True if the given String is a valid IPv4 address, false otherwise
	 */
	public static boolean validateIPv4Address(String ipAddress)
	{
		Pattern pattern = Pattern.compile(
				"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

		return pattern.matcher(ipAddress).matches();
	}
}