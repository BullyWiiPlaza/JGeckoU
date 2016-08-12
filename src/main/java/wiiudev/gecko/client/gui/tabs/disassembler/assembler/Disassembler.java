package wiiudev.gecko.client.gui.tabs.disassembler.assembler;

import wiiudev.gecko.client.gui.tabs.disassembler.DisassembledInstruction;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Disassembler
{
	public static List<DisassembledInstruction> disassemble(byte[] values, int address) throws Exception
	{
		Path tempFile = Files.createTempFile("machine-instructions", ".bin");
		Files.write(tempFile, values);
		String disassemblerOutput = runDisassembler(tempFile, address);
		String[] disassembledLines = disassemblerOutput.split(System.lineSeparator());
		List<DisassembledInstruction> disassembledInstructions = new ArrayList<>();

		for (String disassembledLine : disassembledLines)
		{
			DisassembledInstruction disassembledInstruction = parse(disassembledLine);
			disassembledInstructions.add(disassembledInstruction);
		}

		return disassembledInstructions;
	}

	private static DisassembledInstruction parse(String disassembledLine)
	{
		String[] addressAndValueInstruction = disassembledLine.split(":");
		int instructionAddress = Integer.parseInt(addressAndValueInstruction[0], 16);
		String valuePart = addressAndValueInstruction[1].trim();
		int spaceIndex = valuePart.indexOf("\t");
		int value = Integer.parseUnsignedInt(valuePart.substring(0, spaceIndex), 16);
		valuePart = valuePart.substring(spaceIndex + 1);
		valuePart = valuePart.trim();
		valuePart = valuePart.replaceAll("\t", " ");
		String instruction = valuePart;

		return new DisassembledInstruction(instructionAddress, value, instruction);
	}

	private static String runDisassembler(Path machineInstructionsFilePath, int address) throws Exception
	{
		Path binaryName = AssemblerFiles.getDisassemblerFilePath();

		if (!Files.exists(binaryName))
		{
			throw new AssemblerFilesException(binaryName);
		}

		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.directory(new File(binaryName.getParent().toString()));
		processBuilder.command(binaryName.toString(),
				machineInstructionsFilePath.toString(),
				"0x" + Integer.toHexString(address));
		Process process = processBuilder.start();
		String output = readOutput(process);

		process.waitFor();

		return output;
	}

	private static String readOutput(Process process) throws IOException
	{
		BufferedReader reader =
				new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String readLine;

		while ((readLine = reader.readLine()) != null)
		{
			builder.append(readLine);
			builder.append(System.lineSeparator());
		}

		return builder.toString().trim();
	}
}