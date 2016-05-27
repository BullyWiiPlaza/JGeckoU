package wiiudev.gecko.client.titles;

public class FirmwareNotImplementedException extends IllegalArgumentException
{
	public FirmwareNotImplementedException(String message)
	{
		super(message);
	}
}