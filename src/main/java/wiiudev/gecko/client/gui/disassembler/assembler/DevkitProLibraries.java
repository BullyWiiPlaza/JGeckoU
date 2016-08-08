package wiiudev.gecko.client.gui.disassembler.assembler;

import org.apache.commons.lang.SystemUtils;

public class DevkitProLibraries
{
	private static final String FILE_HEADER = "powerpc-eabi-";
	private static final String COMPILER = FILE_HEADER + "gcc";
	private static final String LINKER = FILE_HEADER + "ld";

	public static String getCompilerFileName()
	{
		return getExecutableName(COMPILER);
	}

	public static String getLinkerFileName()
	{
		return getExecutableName(LINKER);
	}

	private static String getExecutableName(String executable)
	{
		if (SystemUtils.IS_OS_WINDOWS)
		{
			return executable + ".exe";
		} else
		{
			return executable;
		}
	}
}