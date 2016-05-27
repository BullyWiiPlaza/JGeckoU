package wiiudev.gecko.client.titles;

public class TitleNotFoundException extends IllegalArgumentException
{
	TitleNotFoundException(String titleId)
	{
		super("The title id " + titleId + " has not been found in the database");
	}
}