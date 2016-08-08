package wiiudev.gecko.client.gui.disassembler.assembler;

import org.apache.commons.io.FilenameUtils;
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

public class Assembler
{
	private String compiler;
	private String linker;

	private String inputFileName;
	private String objectFileName;
	private String assemblyBinaryFileName;
	private String outputFileName;

	public Assembler(String inputFileName) throws FileNotFoundException
	{
		String intermediaryFileName = inputFileName;
		String forcedExtension = ".s";

		if (!intermediaryFileName.endsWith(forcedExtension))
		{
			intermediaryFileName = intermediaryFileName.concat(forcedExtension);
		}

		this.inputFileName = intermediaryFileName;

		initializeLibraries();

		String inputBaseFileName = FilenameUtils.getBaseName(inputFileName);
		objectFileName = inputBaseFileName + ".o";
		assemblyBinaryFileName = inputBaseFileName + ".bin";
		outputFileName = "a.out";
	}

	public static String assemble(String instruction) throws Exception
	{
		String assemblyFileName = "assembly";
		Path assemblySourceFile = Paths.get(assemblyFileName + ".s");

		try
		{
			Files.write(assemblySourceFile, instruction.getBytes(StandardCharsets.UTF_8));
			Assembler assembler = new Assembler(assemblyFileName);
			String binaryFileName = assembler.writeAssembledFile();
			byte[] bytes = Files.readAllBytes(Paths.get(binaryFileName));

			return DatatypeConverter.printHexBinary(bytes);
		} finally
		{
			// Definitely clean up
			Files.deleteIfExists(Paths.get(assemblyFileName + ".bin"));
			Files.deleteIfExists(assemblySourceFile);
		}
	}

	private void initializeLibraries() throws FileNotFoundException
	{
		compiler = DevkitProLibraries.getCompilerFileName();
		assertExistence(compiler);

		linker = DevkitProLibraries.getLinkerFileName();
		assertExistence(linker);
	}

	private void assertExistence(String filePath) throws FileNotFoundException
	{
		if (!new File(filePath).exists())
		{
			throw new AssemblerFilesException(filePath);
		}
	}

	private String writeAssembledFile() throws Exception
	{
		try
		{
			delete(assemblyBinaryFileName);
			compile();
			link();
		} finally
		{
			cleanAuxiliaryFiles();
		}

		return assemblyBinaryFileName;
	}

	private void compile() throws Exception
	{
		executeCommand(compiler, "-nostdinc", "-fno-builtin", "-mregnames", "-c", inputFileName, "-o", objectFileName);
	}

	private void link() throws Exception
	{
		executeCommand(linker, "-Ttext", "1800000", "--oformat", "binary", objectFileName, "-o", assemblyBinaryFileName);
	}

	private void executeCommand(String... parameters) throws Exception
	{
		List<String> parametersList = Arrays.asList(parameters);
		ProcessBuilder processBuilder = new ProcessBuilder(parametersList);
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
		delete(outputFileName);
		delete(objectFileName);
	}

	private void delete(String fileName) throws IOException
	{
		Path path = Paths.get(fileName);
		Files.deleteIfExists(path);
	}
}