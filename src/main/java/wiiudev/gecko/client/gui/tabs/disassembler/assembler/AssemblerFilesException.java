package wiiudev.gecko.client.gui.tabs.disassembler.assembler;

import java.io.FileNotFoundException;
import java.nio.file.Path;

public class AssemblerFilesException extends FileNotFoundException
{
	public AssemblerFilesException(Path filePath)
	{
		super(filePath.getFileName().toString() + " not found!");
	}
}