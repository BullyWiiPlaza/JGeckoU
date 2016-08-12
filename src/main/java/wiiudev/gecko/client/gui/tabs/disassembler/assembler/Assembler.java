package wiiudev.gecko.client.gui.tabs.disassembler.assembler;

import org.apache.commons.io.IOUtils;

import javax.xml.bind.DatatypeConverter;
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

public class Assembler
{
	private Path compiler;
	private Path linker;

	private Path sourceFile;
	private Path objectCodeFile;
	private Path binaryFile;

	private Assembler(Path sourceFile) throws FileNotFoundException
	{
		this.sourceFile = sourceFile;

		initializeLibraries();
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
		try
		{
			compile();
			link();

			return Files.readAllBytes(binaryFile);
		} finally
		{
			cleanAuxiliaryFiles();
		}
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

		if (errorMessage.contains("Error"))
		{
			throw new AssemblerException(errorMessage);
		}

		process.waitFor();
	}

	private void cleanAuxiliaryFiles() throws IOException
	{
		Files.deleteIfExists(objectCodeFile);
		Files.deleteIfExists(binaryFile);
	}

	public static String assemble(String instruction) throws Exception
	{
		String assemblyFileName = UUID.randomUUID().toString();
		Path assemblySourceFile = Paths.get(AssemblerFiles.getLibrariesDirectory()
				+ File.separator + assemblyFileName + ".s");

		try
		{
			byte[] instructionBytes = instruction.getBytes(StandardCharsets.UTF_8);
			Files.write(assemblySourceFile, instructionBytes);
			Assembler assembler = new Assembler(assemblySourceFile);
			byte[] bytes = assembler.getAssembledBytes();

			return DatatypeConverter.printHexBinary(bytes);
		} finally
		{
			Files.deleteIfExists(assemblySourceFile);
		}
	}
}