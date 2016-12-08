package wiiudev.gecko.client.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ApplicationUtilities
{
	// http://stackoverflow.com/a/19005828/3764804
	public static boolean isProcessRunning(String processName) throws IOException
	{
		ProcessBuilder processBuilder = new ProcessBuilder("tasklist");
		Process process = processBuilder.start();
		String tasksList = toString(process.getInputStream());

		return tasksList.contains(processName);
	}

	// http://stackoverflow.com/a/5445161/3764804
	public static String toString(InputStream inputStream)
	{
		Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
		String string = scanner.hasNext() ? scanner.next() : "";
		scanner.close();

		return string;
	}
}