package wiiudev.gecko.client.memory_search.enumerations;

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

		throw new IllegalArgumentException("Illegal memory_search mode");
	}
}