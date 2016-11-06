package wiiudev.gecko.client.memory_search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ArchivingUtilities
{
	public static void pack(File archiveFile, String sourceFilePath) throws IOException
	{
		List<File> sourceFilePaths = new ArrayList<>();
		sourceFilePaths.add(new File(sourceFilePath));
		pack(archiveFile, sourceFilePaths);
	}

	public static void pack(File archiveFile, List<File> sourceFilePaths) throws IOException
	{
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(archiveFile)))
		{
			zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);

			for (File sourceFile : sourceFilePaths)
			{
				if (sourceFile.isDirectory())
				{
					packDirectory(zipOutputStream, "", sourceFile);
				} else
				{
					packFile(zipOutputStream, "", sourceFile);
				}
			}

			zipOutputStream.flush();
		}
	}

	private static String buildPath(String path, String file)
	{
		if (path == null || path.isEmpty())
		{
			return file;
		} else
		{
			return path + "/" + file;
		}
	}

	private static void packDirectory(ZipOutputStream zipOutputStream, String path, File directory) throws IOException
	{
		File[] files = directory.listFiles();

		if (files != null)
		{
			path = buildPath(path, directory.getName());

			for (File source : files)
			{
				if (source.isDirectory())
				{
					packDirectory(zipOutputStream, path, source);
				} else
				{
					packFile(zipOutputStream, path, source);
				}
			}
		}
	}

	private static void packFile(ZipOutputStream zipOutputStream, String path, File file) throws IOException
	{
		String name = buildPath(path, file.getName());
		zipOutputStream.putNextEntry(new ZipEntry(name));

		try (FileInputStream fileInputStream = new FileInputStream(file))
		{
			byte[] buffer = new byte[4092];
			int byteCount;
			while ((byteCount = fileInputStream.read(buffer)) != -1)
			{
				zipOutputStream.write(buffer, 0, byteCount);
			}
		}

		zipOutputStream.closeEntry();
	}

	public static void unpack(String zipFile) throws IOException
	{
		String outputFolder = Paths.get(zipFile).getParent().toString();
		unpack(zipFile, outputFolder);
	}

	/**
	 * Unzip it
	 *
	 * @param zipFile      input zip file
	 * @param outputFolder zip file output folder
	 */
	private static void unpack(String zipFile, String outputFolder) throws IOException
	{
		// Create parent directory if necessary
		Files.createDirectories(Paths.get(outputFolder));

		try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile)))
		{
			// Get the zipped file list entry
			ZipEntry zipEntry = zipInputStream.getNextEntry();

			while (zipEntry != null)
			{
				String fileName = zipEntry.getName();
				File newFile = new File(outputFolder + File.separator + fileName);

				// Create all non existent folders
				Files.createDirectories(newFile.getParentFile().toPath());

				// Write out the stream
				writeStream(zipInputStream, newFile);

				zipEntry = zipInputStream.getNextEntry();
			}

			zipInputStream.closeEntry();
		}
	}

	private static void writeStream(ZipInputStream zipInputStream, File newFile) throws IOException
	{
		try (FileOutputStream fileOutputStream = new FileOutputStream(newFile))
		{
			int length;
			byte[] buffer = new byte[1024];
			while ((length = zipInputStream.read(buffer)) > 0)
			{
				fileOutputStream.write(buffer, 0, length);
			}
		}
	}
}