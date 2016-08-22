package wiiudev.gecko.client.search;

public enum SearchModes
{
	UNKNOWN("Unknown"),
	SPECIFIC("Specific");

	private String name;

	SearchModes(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public static SearchModes parse(String searchMode)
	{
		for(SearchModes mode : values())
		{
			if(mode.name.equals(searchMode))
			{
				return mode;
			}
		}

		throw new IllegalArgumentException("Illegal search mode");
	}
}