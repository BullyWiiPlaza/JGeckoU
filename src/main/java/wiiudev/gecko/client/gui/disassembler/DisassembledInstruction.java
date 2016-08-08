package wiiudev.gecko.client.gui.disassembler;

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
}