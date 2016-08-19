package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;

import java.io.IOException;

public class MemoryPointerExpression
{
	private long baseAddress;
	private int[] offsets;
	private long value;

	public String notation;
	public static final int INVALID_POINTER = -1;

	public MemoryPointerExpression(long baseAddress, int[] offsets)
	{
		this.baseAddress = baseAddress;
		this.offsets = offsets;
	}

	public MemoryPointerExpression(String notation)
	{
		this.notation = notation;
		notation = notation.replaceAll(" ", ""); // Remove spaces
		int pointerDepth = count('[', notation);

		if (pointerDepth == 0)
		{
			baseAddress = Long.parseLong(notation, 16);
			return;
		}

		offsets = new int[pointerDepth];

		int lastOpeningBracket = notation.lastIndexOf("[");
		int firstClosingBracket = notation.indexOf("]");
		String hexadecimalBaseAddress = notation.substring(lastOpeningBracket + 1, firstClosingBracket);
		if (hexadecimalBaseAddress.startsWith("0x"))
		{
			hexadecimalBaseAddress = hexadecimalBaseAddress.substring(2);
		}

		int baseAddress = Integer.parseInt(hexadecimalBaseAddress, 16);
		setBaseAddress(baseAddress);

		notation = notation.substring(firstClosingBracket + 1);
		int currentOffsetIndex = 0;

		while (currentOffsetIndex < pointerDepth)
		{
			int nextClosingBracket = notation.indexOf("]");

			String offsetString;

			if (nextClosingBracket == -1)
			{
				// Last offset
				int assignmentIndex = notation.indexOf("=");

				if (assignmentIndex != -1)
				{
					offsetString = notation.substring(0, assignmentIndex);
					notation = notation.substring(assignmentIndex);
				} else
				{
					offsetString = notation;
				}
			} else
			{
				// Inner offset
				offsetString = notation.substring(0, nextClosingBracket);
			}

			boolean isNegative = offsetString.charAt(0) == '-';

			// Omit sign
			offsetString = offsetString.substring(1);

			if (offsetString.startsWith("0x"))
			{
				offsetString = offsetString.substring(2);
			}

			int offset = Conversions.parseSignedInteger(offsetString);

			if (isNegative)
			{
				offset *= -1;
			}

			offsets[currentOffsetIndex] = offset;

			if (notation.startsWith("="))
			{
				notation = notation.substring(1);

				if (notation.startsWith("0x"))
				{
					notation = notation.substring(2);
				}

				value = Long.parseLong(notation, 16);
			}

			notation = notation.substring(nextClosingBracket + 1);

			currentOffsetIndex++;
		}

		setOffsets(offsets);
	}

	public long getBaseAddress()
	{
		return baseAddress;
	}

	public long getValue()
	{
		return value;
	}

	public int[] getOffsets()
	{
		return offsets;
	}

	private int count(char character, String text)
	{
		int counted = 0;

		for (int textIndex = 0; textIndex < text.length(); textIndex++)
		{
			if (text.charAt(textIndex) == character)
			{
				counted++;
			} else
			{
				return counted;
			}
		}

		return counted;
	}

	public void setBaseAddress(int baseAddress)
	{
		if (!isValidMemoryAddress(baseAddress))
		{
			throw new IllegalArgumentException("Invalid base address");
		}

		this.baseAddress = baseAddress;
	}

	public void setOffsets(int[] offsets)
	{
		this.offsets = offsets;
	}

	/**
	 * Gets the destination address the given POINTER is pointing to
	 *
	 * @return The destination address the given POINTER is pointing to OR {@value #INVALID_POINTER} if not
	 * possibles
	 * @throws IOException
	 */
	public long getDestinationAddress() throws IOException
	{
		long destinationAddress = baseAddress;

		if (offsets != null)
		{
			for (int offset : offsets)
			{
				int pointerValue = new MemoryReader().readInt((int) destinationAddress);

				destinationAddress = pointerValue + offset;

				if (!MemoryPointerExpression.isValidMemoryAddress((int) destinationAddress))
				{
					destinationAddress = INVALID_POINTER;

					break;
				}
			}
		}

		return destinationAddress;
	}

	/**
	 * @param address The address to check
	 * @return True if the given address is within valid memory bounds, false otherwise
	 */
	private static boolean isValidMemoryAddress(int address)
	{
		return address >= 0x10000000 && address < 0x50000000;
	}

	@Override
	public String toString()
	{
		return notation;
	}
}