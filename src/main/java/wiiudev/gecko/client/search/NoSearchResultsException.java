package wiiudev.gecko.client.search;

public class NoSearchResultsException extends IllegalArgumentException
{
	public NoSearchResultsException(String message)
	{
		super(message);
	}
}