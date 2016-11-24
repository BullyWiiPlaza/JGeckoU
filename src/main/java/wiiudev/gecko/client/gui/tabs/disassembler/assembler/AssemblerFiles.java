package wiiudev.gecko.client.gui.tabs.disassembler.assembler;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import wiiudev.gecko.client.gui.tabs.pointer_search.DownloadingUtilities;
import wiiudev.gecko.client.gui.tabs.pointer_search.ZipUtils;
import wiiudev.gecko.client.gui.utilities.Unzip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AssemblerFiles
{
	private static final String FILE_HEADER = "powerpc-eabi-";
	private static final String COMPILER = FILE_HEADER + "gcc";
	private static final String LINKER = FILE_HEADER + "ld";
	private static final String DISASSEMBLER = "vdappc";

	public static Path getDisassemblerFilePath()
	{
		return getExecutableName(DISASSEMBLER);
	}

	public static Path getCompilerFilePath()
	{
		return getExecutableName(COMPILER);
	}

	public static Path getLinkerFilePath()
	{
		return getExecutableName(LINKER);
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
		} else if (SystemUtils.IS_OS_MAC)
		{
			librariesDirectory += "Mac OS X";
		}

		return Paths.get(librariesDirectory);
	}

	public static void installLibraries() throws IOException
	{
		String downloadRepository = "https://github.com/BullyWiiPlaza/devkitPPC/";
		String masterArchiveDownloadURL = downloadRepository + "archive/master.zip";
		Path downloadedArchive = DownloadingUtilities.download(masterArchiveDownloadURL);
		Unzip.unzip(downloadedArchive.toString());
		Path oldPath = Unzip.unzip(downloadedArchive.toString());
		String librariesFolderName = FilenameUtils.getName(AssemblerFiles.getLibrariesDirectory().getParent().toString());
		ZipUtils.rename(oldPath, librariesFolderName);
	}
}