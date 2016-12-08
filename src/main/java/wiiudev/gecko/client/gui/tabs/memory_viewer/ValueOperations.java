package wiiudev.gecko.client.gui.tabs.memory_viewer;

public enum ValueOperations
{
	ASSIGN("Assign"),
	ADD("Add"),
	SUBTRACT("Subtract"),
	MULTIPLY("Multiply"),
	DIVIDE("Divide"),
	MODULUS("Modulus"),
	AND("And"),
	OR("Or"),
	XOR("Xor"),
	COMPLIMENT("Compliment"),
	LEFT_SHIFT("Left Shift"),
	RIGHT_SHIFT("Right Shift"),
	ZERO_FILL_RIGHT_SHIFT("Zero Fill Right Shift");

	private String operation;

	ValueOperations(String operation)
	{
		this.operation = operation;
	}

	@Override
	public String toString()
	{
		return operation;
	}
}