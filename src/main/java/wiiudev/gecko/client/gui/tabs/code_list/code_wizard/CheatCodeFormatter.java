package wiiudev.gecko.client.gui.tabs.code_list.code_wizard;

public class CheatCodeFormatter
{
	private static final String WINDOWS_SEPARATOR = "\r\n";
	private static final String UNIX_SEPARATOR = "\n";
	private static final String PLATFORM_SEPARATOR = System.lineSeparator();

	public static String formatWithPadding(String hexadecimal)
	{
		return format(hexadecimal, true, "00");
	}

	public static String formatWithPadding(String hexadecimal, String lastByte)
	{
		return format(hexadecimal, true, lastByte);
	}

	public static String format(String hexadecimal)
	{
		return format(hexadecimal, false);
	}

	public static String format(String hexadecimal, boolean padLine)
	{
		return format(hexadecimal, padLine, "00");
	}

	public static String format(String hexadecimal, boolean padLine, String lastPaddingByte)
	{
		hexadecimal = hexadecimal.replaceAll(" ", "");
		hexadecimal = hexadecimal.replaceAll(WINDOWS_SEPARATOR, "");
		hexadecimal = hexadecimal.replaceAll(UNIX_SEPARATOR, "");

		if (padLine)
		{
			hexadecimal = padWithZeroBytes(hexadecimal, lastPaddingByte);
		}

		for (int hexadecimalIndex = 0; hexadecimalIndex < hexadecimal.length(); hexadecimalIndex++)
		{
			if (reduceIndex(hexadecimalIndex, 8))
			{
				hexadecimal = placeInBetween(hexadecimal, hexadecimalIndex, " ");
			}

			if (reduceIndex(hexadecimalIndex, 17))
			{
				hexadecimal = placeInBetween(hexadecimal, hexadecimalIndex, PLATFORM_SEPARATOR);
			}
		}

		return hexadecimal;
	}

	public static String padWithZeroBytes(String text, String lastByte)
	{
		int missingZeroBytes = getMissingZeroBytesCount(text);

		for (int missingZeroBytesIndex = 0; missingZeroBytesIndex < missingZeroBytes - 1; missingZeroBytesIndex++)
		{
			text += "00";
		}

		text += lastByte;

		return text;
	}

	public static int getMissingZeroBytesCount(String text)
	{
		int textLength = text.length();
		int spilloverBytesCount = textLength % 16 / 2;

		return 8 - spilloverBytesCount;
	}

	private static String placeInBetween(String text, int textIndex, String added)
	{
		int textLength = text.length();
		return text.substring(0, textIndex) + added + text.substring(textIndex, textLength);
	}

	private static boolean reduceIndex(int index, int remainder)
	{
		int offset = 17 + PLATFORM_SEPARATOR.length();

		while (index > offset)
		{
			index -= offset;
		}

		return index == remainder;
	}
}
