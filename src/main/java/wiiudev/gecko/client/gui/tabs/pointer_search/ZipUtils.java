package wiiudev.gecko.client.gui.tabs.pointer_search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils
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
}