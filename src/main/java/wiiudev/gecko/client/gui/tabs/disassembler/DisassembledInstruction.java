package wiiudev.gecko.client.gui.tabs.disassembler;

import wiiudev.gecko.client.conversion.Conversions;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;

public class DisassembledInstruction
{
	private int address;
	private int value;
	private String instruction;

	public DisassembledInstruction(int address, int value, String instruction)
	{
		this.address = address;
		this.value = value;
		this.instruction = instruction;
	}

	public int getAddress()
	{
		return address;
	}

	public int getValue()
	{
		return value;
	}

	public String getInstruction()
	{
		return instruction;
	}

	@Override
	public String toString()
	{
		return new Hexadecimal(address, 8) + ": " + new Hexadecimal(value, 8) + " " + instruction;
	}

	public int getBranchDestination()
	{
		if(!isBranchWithDestination())
		{
			throw new IllegalArgumentException("Not a branch instruction with destination");
		}

		int addressIndex = instruction.indexOf("0x");
		String address = instruction.substring(addressIndex + 2);

		return Conversions.toDecimal(address);
	}

	public boolean isBranchWithDestination()
	{
		char firstCharacter = instruction.charAt(0);
		return firstCharacter == 'b' && instruction.contains("0x");
	}
}