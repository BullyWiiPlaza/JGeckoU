package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.AddressRange;

import java.io.IOException;

public class MemoryPointerExpression
{
	private long baseAddress;
	private int[] offsets;
	private long value;

	private String notation;
	public static final int INVALID_POINTER = -1;

	public MemoryPointerExpression(long baseAddress, int[] offsets)
	{
		setBaseAddress((int) baseAddress);
		this.offsets = offsets;
	}

	public MemoryPointerExpression(String notation)
	{
		this.notation = notation;
		notation = notation.replaceAll(" ", ""); // Remove spaces
		int pointerDepth = countOpeningBrackets(notation);

		if (pointerDepth == 0)
		{
			baseAddress = Long.parseLong(notation, 16);
		} else
		{
			parseOffsets(notation, pointerDepth);
		}
	}

	private void parseOffsets(String notation, int pointerDepth)
	{
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

	public boolean isAddress()
	{
		return offsets == null;
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

	private int countOpeningBrackets(String text)
	{
		int counted = 0;

		for (int textIndex = 0; textIndex < text.length(); textIndex++)
		{
			if (text.charAt(textIndex) == '[')
			{
				counted++;
			} else
			{
				return counted;
			}
		}

		return counted;
	}

	private void setBaseAddress(int baseAddress)
	{
		if (!AddressRange.isInApplicationDataSection(baseAddress))
		{
			throw new IllegalArgumentException("Invalid base address");
		}

		this.baseAddress = baseAddress;
	}

	private void setOffsets(int[] offsets)
	{
		this.offsets = offsets;
	}

	/**
	 * Gets the destination address the given pointer is pointing to
	 *
	 * @return The destination address the given pointer is pointing to OR {@value #INVALID_POINTER} if not
	 * possibles
	 */
	public long getDestinationAddress() throws IOException
	{
		long destinationAddress = baseAddress;

		if (offsets != null)
		{
			for (int offset : offsets)
			{
				MemoryReader memoryReader = new MemoryReader();
				// System.out.println("getDestinationAddress() Reading from " + Long.toHexString(destinationAddress).toUpperCase());
				int pointerValue = memoryReader.readInt((int) destinationAddress);
				// System.out.println("getDestinationAddress() Pointer value read: " + Long.toHexString(pointerValue).toUpperCase());

				try
				{
					Thread.sleep(10);
				} catch (InterruptedException exception)
				{
					exception.printStackTrace();
				}

				destinationAddress = pointerValue + offset;
				// System.out.println("getDestinationAddress() New destination address after adding: " + Long.toHexString(destinationAddress).toUpperCase());

				if (!AddressRange.isInApplicationDataSection((int) destinationAddress))
				{
					// System.out.println("getDestinationAddress() Invalid pointer (not in data section)");
					destinationAddress = INVALID_POINTER;

					break;
				}

				// System.out.println("getDestinationAddress() Valid pointer in data section");
			}

			// System.out.println();
		}

		return destinationAddress;
	}

	@Override
	public String toString()
	{
		return notation;
	}
}