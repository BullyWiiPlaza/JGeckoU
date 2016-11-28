package wiiudev.gecko.client.gui.tabs.disassembler.assembler;

import org.apache.commons.io.IOUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Assembler implements Closeable
{
	private Path assembler;
	private Path objectCopy;

	private Path sourceFile;
	private Path objectCodeFile;
	private Path binaryFile;

	private Assembler(String input) throws IOException
	{
		String assemblyFileName = UUID.randomUUID().toString();
		Path sourceFile = Paths.get(AssemblerFiles.getLibrariesDirectory()
				+ File.separator + assemblyFileName + ".as");

		this.sourceFile = sourceFile;

		initializeLibraries();

		// Avoid "end of file not end of line" assembler warning
		if (!input.endsWith(System.lineSeparator()))
		{
			input += System.lineSeparator();
		}

		byte[] instructionBytes = input.getBytes(StandardCharsets.UTF_8);
		Files.write(sourceFile, instructionBytes);

		setUtilityPaths();
	}

	private void setUtilityPaths()
	{
		objectCodeFile = sourceFile.getParent().resolve(UUID.randomUUID() + ".obj");
		binaryFile = sourceFile.getParent().resolve(UUID.randomUUID() + ".bin");
	}

	private void initializeLibraries() throws FileNotFoundException
	{
		assembler = AssemblerFiles.getAssemblerFilePath();
		assertExistence(assembler);

		objectCopy = AssemblerFiles.getObjectCopyFilePath();
		assertExistence(objectCopy);
	}

	private void assertExistence(Path filePath) throws AssemblerFilesException
	{
		if (!Files.exists(filePath))
		{
			throw new AssemblerFilesException(filePath);
		}
	}

	private byte[] getAssembledBytes() throws Exception
	{
		assemble();
		copyObject();

		return Files.readAllBytes(binaryFile);
	}

	private void assemble() throws Exception
	{
		// powerpc-eabi-as -mregnames asm.s -o asm.o
		executeCommand(assembler.toString(), "-mregnames", sourceFile.getFileName().toString(), "-o", objectCodeFile.getFileName().toString());
	}

	private void copyObject() throws Exception
	{
		// powerpc-eabi-objcopy -O "binary" asm.o asm
		executeCommand(objectCopy.toString(), "-O", "binary", objectCodeFile.getFileName().toString(), binaryFile.getFileName().toString());
	}

	private void executeCommand(String... parameters) throws Exception
	{
		List<String> parametersList = Arrays.asList(parameters);
		String command = getCommand(parameters);
		System.out.println(command);
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.directory(sourceFile.getParent().toFile());
		processBuilder.command(parametersList);
		Process process = processBuilder.start();
		String processOutput = IOUtils.toString(process.getErrorStream(), "UTF-8");
		processOutput = processOutput.toLowerCase();
		String baseSourceFileName = getBaseFileName(sourceFile.toFile().getAbsolutePath());
		processOutput = processOutput.replaceAll(baseSourceFileName, "assembly_instruction");

		if (processOutput.contains("error")
				|| processOutput.contains("undefined"))
		{
			throw new AssemblerException(processOutput);
		}

		process.waitFor();
	}

	private String getCommand(String[] parameters)
	{
		StringBuilder commandBuilder = new StringBuilder();
		for (String parameter : parameters)
		{
			boolean containsSpace = parameter.contains(" ");

			if (containsSpace)
			{
				commandBuilder.append("\"");
			}

			commandBuilder.append(parameter);

			if (containsSpace)
			{
				commandBuilder.append("\"");
			}

			commandBuilder.append(" ");
		}

		return commandBuilder.toString().trim();
	}

	private String getBaseFileName(String absoluteFileName)
	{
		Path path = Paths.get(absoluteFileName);

		return path.getFileName().toString().split("\\.")[0];
	}

	public static byte[] assembleBytes(String input) throws Exception
	{
		try (Assembler assembler = new Assembler(input))
		{
			return assembler.getAssembledBytes();
		}
	}

	public static String assembleHexadecimal(String input) throws Exception
	{
		byte[] bytes = assembleBytes(input);

		return DatatypeConverter.printHexBinary(bytes);
	}

	@Override
	public void close() throws IOException
	{
		Files.deleteIfExists(sourceFile);
		Files.deleteIfExists(objectCodeFile);
		Files.deleteIfExists(binaryFile);
	}
}