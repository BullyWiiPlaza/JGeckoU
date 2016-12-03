package parallel;

import wiiudev.gecko.client.debugging.Benchmark;

import java.util.ArrayList;
import java.util.List;

public class ParallelForExample
{
	public static void main(String[] arguments) throws Exception
	{
		Benchmark benchmark = new Benchmark();
		benchmark.start();
		List<Integer> elements = new ArrayList<>();

		for (int elementsIndex = 0; elementsIndex < 40; ++elementsIndex)
		{
			elements.add(elementsIndex);
		}

		Parallel.For(elements, System.out::println);
		benchmark.printElapsedTime();

		System.exit(0);

		/*List<Integer> elements = new LinkedList<>();

		for (int i = 0; i < 1000000; ++i)
		{
			elements.add(i);
		}

		Parallel.For(elements, System.out::println);*/
	}
}