package wiiudev.gecko.client.titles;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TitlesDownloader
{
	/**
	 * Parses and stores all Wii U game's title ids.
	 * This method may take a while and slow down your computer
	 */
	public static List<Title> getTitles() throws Exception
	{
		List<Title> titles = Collections.synchronizedList(new LinkedList<>());

		int poolSize = Runtime.getRuntime().availableProcessors() * 2;
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
		List<Future<?>> tasks = new ArrayList<>();

		// Connect to the website and parse the table
		String titleDatabaseURL = "http://wiiubrew.org/wiki/Title_database";
		Document titleDatabaseDocument = Jsoup.connect(titleDatabaseURL).get();
		Elements titlesTable = titleDatabaseDocument.select("#mw-content-text > table:nth-child(16) > tbody");
		Elements rows = titlesTable.select("tr");
		int rowsCount = rows.size();

		for (int rowsIndex = 1; rowsIndex < rowsCount; rowsIndex++)
		{
			Element row = rows.get(rowsIndex);

			// Parse each row multi-threaded
			Future task = threadPool.submit(new Thread(() ->
			{
				int columnIndex = 0;
				String titleId = row.child(columnIndex++).text();
				String gameName = row.child(columnIndex++).text();
				String productCode = row.child(columnIndex++).text();
				String companyCode = row.child(columnIndex).text();

				Title title = new Title(titleId, gameName, productCode, companyCode);
				titles.add(title);
			}));

			tasks.add(task);
		}

		// Wait for all tasks to finish
		for (Future<?> task : tasks)
		{
			task.get();
		}

		threadPool.shutdownNow();

		return titles;
	}
}
