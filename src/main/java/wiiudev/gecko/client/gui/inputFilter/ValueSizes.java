package wiiudev.gecko.client.gui.inputFilter;

public enum ValueSizes
{
	EIGHT_BIT("8-Bit", 2),
	SIXTEEN_BIT("16-Bit", 4),
	THIRTY_TWO_BIT("32-Bit", 8);

	private String text;
	private int size;

	ValueSizes(String text, int size)
	{
		this.text = text;
		this.size = size;
	}

	public int getSize()
	{
		return size;
	}

	@Override
	public String toString()
	{
		return text;
	}
}