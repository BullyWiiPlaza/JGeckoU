package wiiudev.gecko.client.conversions;

import wiiudev.gecko.client.memory_search.enumerations.ValueSize;

import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
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
			char character = (char) Integer.parseInt(subString, 16);

			// Use dots to indicate "empty" characters
			if (character == '\u0000')
			{
				character = '.';
			}

			textBuilder.append(character);
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

	public static String prependPadding(String hexadecimalString, int targetLength)
	{
		while (hexadecimalString.length() < targetLength)
		{
			hexadecimalString = "0" + hexadecimalString;
		}

		return hexadecimalString;
	}

	public static String floatingPointToHexadecimal(String floatingPoint)
	{
		float floating = Float.parseFloat(floatingPoint);

		return decimalToHexadecimal(Float.floatToRawIntBits(floating));
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
		hexadecimal = hexadecimal.trim();

		if (hexadecimal.equals(""))
		{
			hexadecimal = "0";
		}

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
		hexadecimal = prependPadding(hexadecimal, length);

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

	public static String toHexadecimal(byte[] bytes, ValueSize valueSize)
	{
		String hexadecimal = DatatypeConverter.printHexBinary(bytes);
		int length = valueSize.getBytesCount() * 2;

		// Cut off null bytes
		while (hexadecimal.length() > length)
		{
			hexadecimal = hexadecimal.substring(2);
		}

		hexadecimal = prependPadding(hexadecimal, length);

		return hexadecimal;
	}

	public static String toHexadecimal(BigInteger bigInteger, ValueSize valueSize)
	{
		byte[] bytes = bigInteger.toByteArray();

		return toHexadecimal(bytes, valueSize);
	}

	public static byte[] hexStringToByteArray(String string)
	{
		int length = string.length();
		byte[] bytes = new byte[length / 2];

		for (int lengthIndex = 0; lengthIndex < length; lengthIndex += 2)
		{
			bytes[lengthIndex / 2] = (byte) ((Character.digit(string.charAt(lengthIndex), 16) << 4)
					+ Character.digit(string.charAt(lengthIndex + 1), 16));
		}

		return bytes;
	}

	private static String decimalToHexadecimal(int value)
	{
		return String.format("%8s", Integer.toHexString(value)).replace(' ', '0').toUpperCase();
	}

	private static String removeScientificNotation(float value)
	{
		try
		{
			return new BigDecimal(value).toPlainString();
		} catch (NumberFormatException numberFormatException)
		{
			return "NaN";
		}
	}

	/**
	 * Converts a hexadecimal value to its single precision floating point representation
	 *
	 * @param hexadecimal The <code>hexadecimal</code> to convert
	 * @return The converted value
	 */
	public static String hexadecimalToFloatingPoint(String hexadecimal)
	{
		Long longBits = Long.parseLong(hexadecimal, 16);
		Float floatValue = Float.intBitsToFloat(longBits.intValue());

		return removeScientificNotation(floatValue);
	}

	public static String coordinatesToHexadecimal(String coordinates)
	{
		String[] coordinatesArray = coordinates.split(",");
		StringBuilder hexadecimalBuilder = new StringBuilder();

		for (String coordinate : coordinatesArray)
		{
			coordinate = coordinate.trim();
			coordinate = coordinate.replace("[", "");
			coordinate = coordinate.replace("]", "");

			String hexadecimal = floatingPointToHexadecimal(coordinate);
			hexadecimalBuilder.append(hexadecimal);
		}

		return hexadecimalBuilder.toString();
	}

	public static String hexadecimalToCoordinates(String hexadecimal)
	{
		StringBuilder coordinatesBuilder = new StringBuilder("[");
		int valueSize = ValueSize.THIRTY_TWO_BIT.getBytesCount() * 2;

		for (int hexadecimalIndex = 0;
		     hexadecimalIndex < hexadecimal.length();
		     hexadecimalIndex += valueSize)
		{
			String value = hexadecimal.substring(hexadecimalIndex, hexadecimalIndex + valueSize);
			String floating = hexadecimalToFloatingPoint(value);
			coordinatesBuilder.append(floating);

			if (hexadecimalIndex != hexadecimal.length() - valueSize)
			{
				coordinatesBuilder.append(", ");
			}
		}

		coordinatesBuilder.append("]");
		return coordinatesBuilder.toString();
	}

	public static byte[] toByteArray(int[] ints)
	{
		ByteBuffer byteBuffer = ByteBuffer.allocate(ints.length * 4);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(ints);

		return byteBuffer.array();
	}
}