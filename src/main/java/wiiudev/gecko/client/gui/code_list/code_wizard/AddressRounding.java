package wiiudev.gecko.client.gui.code_list.code_wizard;

public class AddressRounding
{
	public static long roundDown(long address, double percentage)
	{
		return round(address, percentage, false);
	}

	public static long roundUp(long address, double percentage)
	{
		return round(address, percentage, true);
	}

	private static long round(long address, double percentage, boolean up)
	{
		if (up)
		{
			percentage = -1 * percentage;
		}

		long rounded = (long) (address - percentage * address);

		return align_32(rounded);
	}

	private static long align_32(long address)
	{
		return address & -4;
	}
}