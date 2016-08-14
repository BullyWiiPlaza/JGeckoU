package wiiudev.gecko.client.tcpgecko.main.threads;

public enum OSThreadState
{
	PAUSED, RUNNING;

	public static OSThreadState parse(int state)
	{
		if(state > 0xFFFFFF)
		{
			return PAUSED;
		}

		return RUNNING;
	}

	@Override
	public String toString()
	{
		return name().charAt(0) +
				name().toLowerCase().substring(1);
	}
}