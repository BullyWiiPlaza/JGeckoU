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
	private Path compiler;
	private Path linker;

	private Path sourceFile;
	private Path objectCodeFile;
	private Path binaryFile;

	private Assembler(String input) throws IOException
	{
		String assemblyFileName = UUID.randomUUID().toString();
		Path sourceFile = Paths.get(AssemblerFiles.getLibrariesDirectory()
				+ File.separator + assemblyFileName + ".s");

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
		objectCodeFile = sourceFile.getParent().resolve(UUID.randomUUID() + ".o");
		binaryFile = sourceFile.getParent().resolve(UUID.randomUUID() + ".bin");
	}

	private void initializeLibraries() throws FileNotFoundException
	{
		compiler = AssemblerFiles.getCompilerFilePath();
		assertExistence(compiler);

		linker = AssemblerFiles.getLinkerFilePath();
		assertExistence(linker);
	}

	private void assertExistence(Path filePath) throws FileNotFoundException
	{
		if (!Files.exists(filePath))
		{
			throw new AssemblerFilesException(filePath);
		}
	}

	private byte[] getAssembledBytes() throws Exception
	{
		compile();
		link();

		return Files.readAllBytes(binaryFile);
	}

	private void compile() throws Exception
	{
		executeCommand(compiler.getFileName().toString(), "-nostdinc", "-fno-builtin", "-mregnames", "-c", sourceFile.getFileName().toString(), "-o", objectCodeFile.getFileName().toString());
	}

	private void link() throws Exception
	{
		executeCommand(linker.getFileName().toString(), "-Ttext", "1800000", "--oformat", "binary", objectCodeFile.getFileName().toString(), "-o", binaryFile.getFileName().toString());
	}

	private void executeCommand(String... parameters) throws Exception
	{
		List<String> parametersList = Arrays.asList(parameters);
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.directory(sourceFile.getParent().toFile());
		processBuilder.command(parametersList);
		Process process = processBuilder.start();
		String errorMessage = IOUtils.toString(process.getErrorStream(), "UTF-8");
		errorMessage = errorMessage.toLowerCase();
		String baseSourceFileName = getBaseFileName(sourceFile.toFile().getAbsolutePath());
		errorMessage = errorMessage.replaceAll(baseSourceFileName, "assembly_instruction");

		if (errorMessage.contains("error"))
		{
			throw new AssemblerException(errorMessage);
		}

		process.waitFor();
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