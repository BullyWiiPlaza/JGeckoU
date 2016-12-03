package wiiudev.gecko.client.gui.tabs.disassembler.assembler;

import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.tabs.disassembler.DisassembledInstruction;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

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
	public static final int INVALID_ADDRESS = -1;
	public static final int CANCELED = -2;

	public static List<DisassembledInstruction> disassemble(int address, int length) throws Exception
	{
		MemoryReader memoryReader = new MemoryReader();
		byte[] values = memoryReader.readBytes(address, length);

		return Disassembler.disassemble(values, address);
	}

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
		int instructionAddress = Integer.parseUnsignedInt(addressAndValueInstruction[0], 16);
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
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String readLine;

		while ((readLine = reader.readLine()) != null)
		{
			builder.append(readLine);
			builder.append(System.lineSeparator());
		}

		return builder.toString().trim();
	}

	public static int search(int address, String regularExpression) throws Exception
	{
		// Start at the next instruction
		address += 4;

		while (true)
		{
			try
			{
				List<DisassembledInstruction> disassembledInstructions = Disassembler.disassemble(address, TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE);

				for (DisassembledInstruction disassembledInstruction : disassembledInstructions)
				{
					String instruction = disassembledInstruction.getInstruction();
					if (instruction.matches(regularExpression))
					{
						return disassembledInstruction.getAddress();
					}

					if (JGeckoUGUI.getInstance().isRegularExpressionSearchCanceled())
					{
						return CANCELED;
					}
				}
			} catch (IOException exception)
			{
				exception.printStackTrace();
				return INVALID_ADDRESS;
			}

			address += TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE;
		}
	}
}