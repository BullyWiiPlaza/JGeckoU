package wiiudev.gecko.client.pointer_search;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DownloadingUtilities
{
	public static void downloadAndExecute(String downloadURL) throws IOException
	{
		String fileName = getFileName(downloadURL);
		download(downloadURL);
		executeApplication(fileName);
	}

	public static boolean canDownload(String filePath)
	{
		try
		{
			// Cause an exception if the program is already running
			if(new File(filePath).exists())
			{
				Files.delete(Paths.get(filePath));
			}

			return true;
		}
		catch(IOException ignored)
		{
			return false;
		}
	}

	public static void download(String downloadURL) throws IOException
	{
		URL website = new URL(downloadURL);
		String fileName = getFileName(downloadURL);

		try (InputStream inputStream = website.openStream())
		{
			Files.copy(inputStream, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public static String getFileName(String downloadURL)
	{
		String baseName = FilenameUtils.getBaseName(downloadURL);
		String extension = FilenameUtils.getExtension(downloadURL);
		String fileName = baseName + "." + extension;

		int questionMarkIndex = fileName.indexOf("?");
		if (questionMarkIndex != -1)
		{
			fileName = fileName.substring(0, questionMarkIndex);
		}

		fileName = fileName.replaceAll("-", "");

		return fileName;
	}

	public static void executeApplication(String filePath) throws IOException
	{
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("java", "-jar", filePath);
		processBuilder.start();
	}
}