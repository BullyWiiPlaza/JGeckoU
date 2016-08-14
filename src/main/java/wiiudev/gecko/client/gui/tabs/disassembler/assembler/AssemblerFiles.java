package wiiudev.gecko.client.gui.tabs.disassembler.assembler;

import org.apache.commons.lang.SystemUtils;

import java.io.File;
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

		if(SystemUtils.IS_OS_WINDOWS)
		{
			librariesDirectory += "windows";
		}
		else if(SystemUtils.IS_OS_UNIX)
		{
			librariesDirectory += "unix";
		}

		return Paths.get(librariesDirectory);
	}
}