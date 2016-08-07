package wiiudev.gecko.client.tcpgecko.main.utilities.conversions;

public class Hexadecimal
{
	private String hexadecimal;

	public Hexadecimal(int number)
	{
		hexadecimal = Integer.toHexString(number).toUpperCase();
	}

	public Hexadecimal(long number, int length)
	{
		hexadecimal = Long.toHexString(number).toUpperCase();

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