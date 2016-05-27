package wiiudev.gecko.client.gui.utilities;

public class Benchmark
{
	private long currentTime;

	public void start()
	{
		currentTime = System.currentTimeMillis();
	}

	public double getElapsedTime()
	{
		long elapsedTime = System.currentTimeMillis() - currentTime;

		return (double) elapsedTime / 1000;
	}
}