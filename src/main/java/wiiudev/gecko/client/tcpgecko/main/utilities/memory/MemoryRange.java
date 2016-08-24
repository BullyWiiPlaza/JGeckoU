package wiiudev.gecko.client.tcpgecko.main.utilities.memory;

import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;

import java.io.IOException;

/**
 * Class for defining the allowed default memory range to avoid crashing by trying to read invalid memory
 */
public class MemoryRange
{
	private int startingAddress;
	private int endingAddress;

	public MemoryRange(int startingAddress, int endingAddress)
	{
		this.startingAddress = startingAddress;
		this.endingAddress = endingAddress;
	}

	public int updateMemoryRange(boolean convergeDownwards) throws IOException
	{
		int startingAddress = this.startingAddress;
		int endingAddress = this.endingAddress;
		int middle = (endingAddress - startingAddress) / 2 + startingAddress;
		int physicalAddress = CoreInit.getEffectiveToPhysical(middle);

		while (true)
		{
			// Is it mapped?
			if ((physicalAddress != 0 && convergeDownwards)
					|| (physicalAddress == 0 && !convergeDownwards))
			{
				endingAddress = middle;
			} else
			{
				startingAddress = middle;
			}

			int previousMiddle = middle;
			middle = (endingAddress - startingAddress) / 2 + startingAddress;

			// The middle does no longer update, algorithm terminates
			if (previousMiddle == middle)
			{
				break;
			}

			physicalAddress = CoreInit.getEffectiveToPhysical(middle);
		}

		if (convergeDownwards)
		{
			setStartingAddress(middle);
		} else
		{
			setEndingAddress(middle);
		}

		return middle;
	}

	public int getEndingAddress()
	{
		return endingAddress;
	}

	public int getStartingAddress()
	{
		return startingAddress;
	}

	public void setEndingAddress(int endingAddress)
	{
		this.endingAddress = endingAddress;
	}

	public void setStartingAddress(int startingAddress)
	{
		this.startingAddress = startingAddress;
	}
}