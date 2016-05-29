package wiiudev.gecko.client.conversion;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

public class Conversions
{
	/**
	 * Converts the given <code>hexadecimal</code> to it's decimal representation
	 *
	 * @param hexadecimal The <code>hexadecimal</code> to convert
	 * @return The converted decimal
	 */
	public static String hexadecimalToDecimal(String hexadecimal)
	{
		return new BigInteger(hexadecimal, 16) + "";
	}

	/**
	 * Converts a given <code>decimal</code> number to hexadecimal
	 *
	 * @param decimal The <code>decimal</code> to convert
	 * @return The converted hexadecimal
	 */
	public static String decimalToHexadecimal(String decimal)
	{
		return new BigInteger(decimal).toString(16).toUpperCase();
	}

	/**
	 * Converts the given <code>hexadecimal</code> representing text to it's text form
	 *
	 * @param hexadecimal The <code>hexadecimal</code> to convert
	 * @return The converted text
	 */
	public static String hexadecimalToASCII(String hexadecimal)
	{
		StringBuilder textBuilder = new StringBuilder();
		for (int hexadecimalBytesIndex = 0; hexadecimalBytesIndex < hexadecimal.length(); hexadecimalBytesIndex += 2)
		{
			String subString = hexadecimal.substring(hexadecimalBytesIndex, hexadecimalBytesIndex + 2);
			textBuilder.append((char) Integer.parseInt(subString, 16));
		}

		return textBuilder.toString();
	}

	/**
	 * Converts a given <code>text</code> to it's hexadecimal representation
	 *
	 * @param text The <code>text</code> to convert
	 * @return The hexadecimal result
	 */
	public static String asciiToHexadecimal(String text)
	{
		char[] characters = text.toCharArray();
		StringBuilder hexadecimalBuilder = new StringBuilder();

		for (char character : characters)
		{
			String convertedCharacter = Integer.toHexString((int) character);

			if (convertedCharacter.length() == 1)
			{
				convertedCharacter = "0" + convertedCharacter;
			}

			hexadecimalBuilder.append(convertedCharacter);
		}

		return hexadecimalBuilder.toString().toUpperCase();
	}

	/**
	 * Converts a hexadecimal value to its single precision floating point representation
	 *
	 * @param hexadecimal The <code>hexadecimal</code> to convert
	 * @return The converted value
	 */
	public static String hexadecimalToFloatingPoint(String hexadecimal)
	{
		Long longValue = Long.parseLong(hexadecimal, 16);
		Float floatValue = Float.intBitsToFloat(longValue.intValue());

		return floatValue.toString();
	}

	public static String floatingPointToHexadecimal(String floatingPoint)
	{
		float floating = Float.parseFloat(floatingPoint);
		return decimalToHexadecimal(Integer.toString(Float.floatToRawIntBits(floating)));
	}

	public static String decimalToHexadecimalMemoryAddress(int integer)
	{
		String hexadecimalValue = Integer.toHexString(integer).toUpperCase();

		while (hexadecimalValue.length() < 8)
		{
			hexadecimalValue = "0" + hexadecimalValue;
		}

		return hexadecimalValue;
	}

	public static int[] toIntegerArray(byte[] readBytes)
	{
		IntBuffer integerBuffer =
				ByteBuffer.wrap(readBytes)
						.order(ByteOrder.BIG_ENDIAN)
						.asIntBuffer();
		int[] integers = new int[integerBuffer.remaining()];
		integerBuffer.get(integers);

		return integers;
	}

	/**
	 * Converts a hexadecimal String to a decimal number
	 *
	 * @param hexadecimal The hexadecimal to convert
	 * @return The converted decimal number
	 */
	public static int toDecimal(String hexadecimal)
	{
		return Integer.parseUnsignedInt(hexadecimal, 16);
	}

	/**
	 * Converts a decimal number to a hexadecimal String with padding
	 *
	 * @param decimal The decimal to convert
	 * @param length  The forced length of the result
	 * @return The converted hexadecimal
	 */
	public static String toHexadecimal(int decimal, int length)
	{
		String hexadecimal = Integer.toHexString(decimal);
		hexadecimal = hexadecimal.toUpperCase();

		while (hexadecimal.length() < length)
		{
			hexadecimal = "0" + hexadecimal;
		}

		return hexadecimal;
	}

	/**
	 * Converts a decimal number to a hexadecimal String with a padded length of 8
	 *
	 * @param decimal The decimal to convert
	 * @return The converted hexadecimal String
	 */
	public static String toHexadecimal(int decimal)
	{
		return toHexadecimal(decimal, 8);
	}

	public static String toHexadecimalNoPadding(int decimal)
	{
		String hexadecimal = Integer.toHexString(decimal);

		return hexadecimal.toUpperCase();
	}

	public static String hexadecimalToUnicode(String hexadecimal)
	{
		StringBuilder unicodeBuilder = new StringBuilder();

		int beginIndex = 0;
		int length = 4;
		int endIndex = beginIndex + length;

		while (endIndex <= hexadecimal.length())
		{
			String sliced = hexadecimal.substring(beginIndex, endIndex);
			String unicode = toUnicode(toDecimal(sliced));
			unicodeBuilder.append(unicode);

			beginIndex += length;
			endIndex += length;
		}

		return unicodeBuilder.toString();
	}

	public static byte[] getNullTerminatedBytes(String text)
	{
		byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
		byte[] nullTerminated = new byte[bytes.length + 1];
		System.arraycopy(bytes, 0, nullTerminated, 0, bytes.length);

		return nullTerminated;
	}

	public static String unicodeToHexadecimal(String unicode)
	{
		StringBuilder hexadecimalBuilder = new StringBuilder();

		int unicodeIndex = 0;

		while (unicodeIndex < unicode.length())
		{
			hexadecimalBuilder.append(String.format("%04x", (int) unicode.charAt(unicodeIndex)));
			unicodeIndex++;
		}

		return hexadecimalBuilder.toString().toUpperCase();
	}

	public static String toUnicode(int value)
	{
		String unicodeLetters = "";
		String hexadecimal = toHexadecimal(value);
		unicodeLetters = unicodeLetters.concat("" + Character.toChars(toDecimal(hexadecimal.substring(0, 4)))[0]);
		unicodeLetters = unicodeLetters.concat("" + Character.toChars(toDecimal(hexadecimal.substring(4, 8)))[0]);

		return unicodeLetters;
	}

	public static byte[] toUnicode(byte[] bytes)
	{
		byte[] unicodeBytes = new byte[bytes.length * 2];

		int bytesIndex = 0;

		for (int unicodeBytesIndex = 1; unicodeBytesIndex < unicodeBytes.length; unicodeBytesIndex += 2)
		{
			unicodeBytes[unicodeBytesIndex] = bytes[bytesIndex];
			bytesIndex++;
		}

		return unicodeBytes;
	}

	public static int parseSignedInteger(String hexadecimal)
	{
		return (int) Long.parseLong(hexadecimal, 16);
	}
}