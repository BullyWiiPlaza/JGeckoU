package parallel;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory
{
	private final String baseName;
	private final AtomicInteger threadNum = new AtomicInteger(0);

	public NamedThreadFactory(String baseName)
	{
		this.baseName = baseName;
	}

	@Override
	public synchronized Thread newThread(Runnable r)
	{
		Thread thread = Executors.defaultThreadFactory().newThread(r);

		thread.setName(baseName + "-" + threadNum.getAndIncrement());

		return thread;
	}
}