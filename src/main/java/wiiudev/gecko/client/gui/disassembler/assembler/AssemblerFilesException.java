package wiiudev.gecko.client.gui.disassembler.assembler;

import java.io.FileNotFoundException;

public class AssemblerFilesException extends FileNotFoundException
{
	public AssemblerFilesException(String filePath)
	{
		super(filePath + " not found!");
	}
}