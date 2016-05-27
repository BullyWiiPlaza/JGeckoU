package wiiudev.gecko.client.conversion;

public class Validation
{
	/**
	 * @param input The input to verify
	 * @return True if the String only contains valid hexadecimal characters
	 */
	public static boolean isHexadecimal(String input)
	{
		return input.matches("^[0-9a-fA-F]+$");
	}
}