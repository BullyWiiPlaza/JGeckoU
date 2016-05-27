package wiiudev.gecko.client.connector.utilities;

public class Hexadecimal
{
	String hexadecimal;

	public Hexadecimal(int number)
	{
		hexadecimal = Integer.toHexString(number).toUpperCase();
	}

	public Hexadecimal(int number, int length)
	{
		hexadecimal = Integer.toHexString(number).toUpperCase();

		while (hexadecimal.length() < length)
		{
			hexadecimal = "0" + hexadecimal;
		}
	}

	@Override
	public String toString()
	{
		return hexadecimal;
	}
}