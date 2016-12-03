package wiiudev.gecko.client.debugging;

public class Benchmark
{
	private long startingTime;

	public void start()
	{
		startingTime = getSystemTime();
	}

	private long getSystemTime()
	{
		return System.currentTimeMillis();
	}

	/**
	 * @return The elapsed time in seconds
	 */
	private double getElapsedTime()
	{
		long elapsedTime = getSystemTime() - startingTime;
		return (double) elapsedTime / 1000;
	}

	public void printElapsedTime()
	{
		System.out.println(getElapsedTime() + " seconds taken");
	}
}