package wiiudev.gecko.client.search;

public enum ValueSize
{
	EIGHT_BIT("8-Bit"),
	SIXTEEN_BIT("16-Bit"),
	THIRTY_TWO_BIT("32-Bit"),
	SIXTY_FOUR_BIT("64-Bit");

	private String name;

	ValueSize(String name)
	{
		this.name = name;
	}

	public int getBytesCount()
	{
		switch (this)
		{
			case EIGHT_BIT:
				return 1;

			case SIXTEEN_BIT:
				return 2;

			case THIRTY_TWO_BIT:
				return 4;

			case SIXTY_FOUR_BIT:
				return 8;
		}

		throw new IllegalStateException("Bytes count undefined for " + this);
	}

	@Override
	public String toString()
	{
		return name;
	}

	public static ValueSize parse(String name)
	{
		ValueSize[] valueSizes = values();

		for (ValueSize valueSize : valueSizes)
		{
			String currentName = valueSize.toString();

			if (currentName.equals(name))
			{
				return valueSize;
			}
		}

		throw new IllegalArgumentException("No value size associated");
	}
}