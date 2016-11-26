package wiiudev.gecko.client.gui.tabs.disassembler;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.gui.tabs.disassembler.assembler.Disassembler;
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

	public int getBranchDestination()
	{
		if (!isBranchWithDestination())
		{
			throw new IllegalArgumentException("Not a branch instruction with destination");
		}

		int addressIndex = instruction.indexOf("0x");
		String address = instruction.substring(addressIndex + 2);

		return Conversions.toDecimal(address);
	}

	public boolean isUnconditionalBranch()
	{
		return instruction.startsWith("b ");
	}

	public boolean isBranchWithDestination()
	{
		char firstCharacter = instruction.charAt(0);
		return firstCharacter == 'b' && instruction.contains("0x");
	}

	public boolean isLoadImmediateShifted()
	{
		return instruction.startsWith("lis");
	}

	@Override
	public String toString()
	{
		return new Hexadecimal(address, 8) + ": " + new Hexadecimal(value, 8) + " " + instruction;
	}

	public static DisassembledInstruction parse(int address) throws Exception
	{
		return Disassembler.disassemble(address, 4).get(0);
	}

	private static boolean isBranch(String instruction)
	{
		DisassembledInstruction disassembledInstruction = new DisassembledInstruction(0, 0, instruction);
		return disassembledInstruction.isBranchWithDestination();
	}

	public static String calculateBranch(int currentAddress, String instruction)
	{
		if (DisassembledInstruction.isBranch(instruction))
		{
			String[] components = instruction.split(" 0x");
			int absoluteJumpTargetAddress = Integer.parseInt(components[1], 16);
			int updatedOffset = absoluteJumpTargetAddress - currentAddress;
			instruction = components[0] + " " + updatedOffset;
		}

		return instruction;
	}
}