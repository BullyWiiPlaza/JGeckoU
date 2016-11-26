package wiiudev.gecko.client.gui.tabs.pointer_search;

import org.apache.commons.lang.SystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipArchiveUtilities
{
	public static File unZipFile(String zipFile) throws IOException
	{
		byte[] buffer = new byte[1024];
		File newFile = null;

		String programDirectory = System.getProperty("user.dir");
		File folder = new File(programDirectory);

		if (!folder.exists())
		{
			Files.createDirectories(folder.toPath());
		}

		ZipInputStream zipInputStream =
				new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry zipEntry = zipInputStream.getNextEntry();

		while (zipEntry != null)
		{
			String fileName = zipEntry.getName();
			newFile = new File(programDirectory + File.separator + fileName);

			Files.createDirectories(new File(newFile.getParent()).toPath());

			FileOutputStream fileOutputStream = new FileOutputStream(newFile);

			int length;

			while ((length = zipInputStream.read(buffer)) > 0)
			{
				fileOutputStream.write(buffer, 0, length);
			}

			fileOutputStream.close();
			zipEntry = zipInputStream.getNextEntry();
		}

		zipInputStream.closeEntry();
		zipInputStream.close();

		return newFile;
	}

	public static Path rename(Path path, String name) throws IOException
	{
		return Files.move(path, path.resolveSibling(name));
	}

	public static Path unzip(String zipFilePath) throws IOException
	{
		String currentDirectory = System.getProperty("user.dir");
		return unzip(zipFilePath, currentDirectory);
	}

	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified by
	 * destDirectory (will be created if does not exists)
	 *
	 * @param zipFilePath                  The file path to the zip archive
	 * @param destinationDirectoryFilePath The directory to extract to
	 */
	private static Path unzip(String zipFilePath, String destinationDirectoryFilePath) throws IOException
	{
		Path destinationDirectoryPath = Paths.get(destinationDirectoryFilePath);

		if (Files.isRegularFile(destinationDirectoryPath))
		{
			Files.delete(destinationDirectoryPath);
		}

		// Create the destination directory if necessary
		Files.createDirectories(destinationDirectoryPath);

		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipInputStream.getNextEntry();

		String folderName = entry.getName().substring(0, entry.getName().length() - 1);

		// Iterate over entries in the zip file
		while (entry != null)
		{
			String filePath = destinationDirectoryFilePath + File.separator + entry.getName();

			if (entry.isDirectory())
			{
				// Make the directory
				Path directory = Paths.get(filePath);
				Files.createDirectories(directory);

			} else
			{
				// Extract the file
				writeFile(zipInputStream, filePath);

				// Set file permissions for Unix
				if (SystemUtils.IS_OS_UNIX)
				{
					Path extractedFile = Paths.get(filePath);
					FilePermissionUtilities.giveAllPermissionsFor(extractedFile);
				}
			}

			zipInputStream.closeEntry();
			entry = zipInputStream.getNextEntry();
		}

		zipInputStream.close();

		return Paths.get(folderName);
	}

	/**
	 * Extracts a zip entry (file entry)
	 *
	 * @param zipInputStream The zip file
	 * @param targetFilePath The target file path
	 */
	private static void writeFile(ZipInputStream zipInputStream, String targetFilePath) throws IOException
	{
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(targetFilePath));
		byte[] byteBuffer = new byte[4096];
		int readBytes;

		while ((readBytes = zipInputStream.read(byteBuffer)) != -1)
		{
			bufferedOutputStream.write(byteBuffer, 0, readBytes);
		}

		bufferedOutputStream.close();
	}

	public static class FilePermissionUtilities
	{
		public static void giveAllPermissionsFor(Path extractedFile) throws IOException
		{
			Set<PosixFilePermission> permissions = new HashSet<>();
			permissions.addAll(Arrays.asList(PosixFilePermission.values()));
			Files.setPosixFilePermissions(extractedFile, permissions);
		}
	}
}