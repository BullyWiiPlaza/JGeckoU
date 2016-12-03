package parallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Parallel
{
	private static int NUM_CORES = Runtime.getRuntime().availableProcessors();
	private static ExecutorService forPool = Executors.newFixedThreadPool(NUM_CORES * 2, new NamedThreadFactory("Parallel.For"));

	public static <T> void For(List<T> elements,
	                           Operation<T> operation) throws Exception
	{
		forPool.invokeAll(createCallables(elements, operation));
		forPool.shutdownNow();
	}

	private static <T> Collection<Callable<Void>> createCallables(Iterable<T> elements,
	                                                              Operation<T> operation)
	{
		List<Callable<Void>> callables = new ArrayList<>();
		for (T elem : elements)
		{
			callables.add(() ->
			{
				operation.perform(elem);
				return null;
			});
		}

		return callables;
	}

	public interface Operation<T>
	{
		void perform(T parameter);
	}
}