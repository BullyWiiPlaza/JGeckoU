package wiiudev.gecko.client.tcpgecko.main;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class CloseableReentrantLock extends ReentrantLock implements AutoCloseable
{
	public CloseableReentrantLock()
	{
		super(true);
	}

	public CloseableReentrantLock acquire()
	{
		try
		{
			// Clear interrupted status
			Thread.interrupted();
			tryLock(1, TimeUnit.SECONDS);
		} catch (InterruptedException interruptedException)
		{
			interruptedException.printStackTrace();
		}

		return this;
	}

	@Override
	public void close()
	{
		try
		{
			this.unlock();
		} catch (IllegalMonitorStateException illegalMonitorState)
		{
			illegalMonitorState.printStackTrace();
		}
	}
}