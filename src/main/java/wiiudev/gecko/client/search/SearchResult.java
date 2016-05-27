package wiiudev.gecko.client.search;

public class SearchResult
{
	private boolean isActive;
	private int value;

	public SearchResult(int value)
	{
		this.value = value;
		this.isActive = true;
	}

	public void setInactive()
	{
		isActive = false;
	}

	public int getValue()
	{
		return value;
	}
}