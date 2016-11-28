package wiiudev.gecko.client.gui.tabs.disassembler.assembler;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import wiiudev.gecko.client.gui.tabs.pointer_search.DownloadingUtilities;
import wiiudev.gecko.client.gui.tabs.pointer_search.ZipArchiveUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AssemblerFiles
{
	private static final String FILE_HEADER = "powerpc-eabi-";
	private static final String ASSEMBLER = FILE_HEADER + "as";
	private static final String OBJECT_COPY = FILE_HEADER + "objcopy";
	private static final String DISASSEMBLER = "vdappc";

	public static Path getDisassemblerFilePath()
	{
		return getExecutableName(DISASSEMBLER);
	}

	public static Path getAssemblerFilePath()
	{
		return getExecutableName(ASSEMBLER);
	}

	public static Path getObjectCopyFilePath()
	{
		return getExecutableName(OBJECT_COPY);
	}

	private static Path getExecutableName(String executable)
	{
		executable = getLibrariesDirectory() + File.separator + executable;

		if (SystemUtils.IS_OS_WINDOWS)
		{
			executable += ".exe";
		}

		return Paths.get(executable);
	}

	public static Path getLibrariesDirectory()
	{
		String librariesDirectory = "libraries" + File.separator;

		if (SystemUtils.IS_OS_WINDOWS)
		{
			librariesDirectory += "Windows";
		} else if (SystemUtils.IS_OS_LINUX)
		{
			librariesDirectory += "Linux";
		}

		return Paths.get(librariesDirectory);
	}

	public static void installLibraries() throws IOException
	{
		String downloadRepository = "https://github.com/BullyWiiPlaza/devkitPPC/";
		String masterArchiveDownloadURL = downloadRepository + "archive/master.zip";
		Path downloadedArchive = DownloadingUtilities.download(masterArchiveDownloadURL);
		ZipArchiveUtilities.unzip(downloadedArchive.toString());
		Path oldPath = ZipArchiveUtilities.unzip(downloadedArchive.toString());
		String librariesFolderName = FilenameUtils.getName(AssemblerFiles.getLibrariesDirectory().getParent().toString());
		ZipArchiveUtilities.rename(oldPath, librariesFolderName);
		Files.delete(downloadedArchive);
	}
}