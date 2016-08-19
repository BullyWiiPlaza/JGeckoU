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
}