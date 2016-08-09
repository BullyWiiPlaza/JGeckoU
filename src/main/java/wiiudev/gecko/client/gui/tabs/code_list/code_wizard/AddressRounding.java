package wiiudev.gecko.client.gui.tabs.code_list.code_wizard;

public class AddressRounding
{
	private double percentage;

	public AddressRounding(double percentage)
	{
		this.percentage = percentage;
	}

	public long roundDown(long address)
	{
		return round(address, false);
	}

	public long roundUp(long address)
	{
		return round(address, true);
	}

	private long round(long address, boolean up)
	{
		double percentage = this.percentage;

		if (up)
		{
			percentage = -1 * percentage;
		}

		long rounded = (long) (address - percentage * address);

		return align_32(rounded);
	}

	private long align_32(long address)
	{
		return address & -4;
	}
}