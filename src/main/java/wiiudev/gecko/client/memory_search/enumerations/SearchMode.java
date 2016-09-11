package wiiudev.gecko.client.memory_search.enumerations;

public enum SearchMode
{
	UNKNOWN("Unknown"),
	SPECIFIC("Specific");

	private String name;

	SearchMode(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public static SearchMode parse(String searchMode)
	{
		for(SearchMode mode : values())
		{
			if(mode.name.equals(searchMode))
			{
				return mode;
			}
		}

		throw new IllegalArgumentException("Illegal memory_search mode");
	}
}