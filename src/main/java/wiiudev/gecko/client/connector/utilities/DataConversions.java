package wiiudev.gecko.client.connector.utilities;

import java.nio.ByteBuffer;

public class DataConversions
{
	public static float toFloat(Long value)
	{
		return Float.intBitsToFloat(value.intValue());
	}

	public static int toInteger(float value)
	{
		return Float.floatToIntBits(value);
	}

	public static String toHexadecimal(float value)
	{
		return toHexadecimal(Float.floatToRawIntBits(value));
	}

	public static char toCharacter(byte b)
	{
		return (char) b;
	}

	public static byte[] toByteArray(short value)
	{
		return ByteBuffer.allocate(2).putShort(value).array();
	}

	public static int toInteger(byte[] bytes)
	{
		return ByteBuffer.wrap(bytes).getInt();
	}
}