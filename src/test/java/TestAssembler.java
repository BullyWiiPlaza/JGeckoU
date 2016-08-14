import org.junit.Assert;
import org.junit.Test;
import wiiudev.gecko.client.gui.tabs.disassembler.DisassembledInstruction;
import wiiudev.gecko.client.gui.tabs.disassembler.assembler.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestAssembler
{
	@Test
	public void testAssembler() throws Exception
	{
		String assembled = Assembler.assemble("nop");
		Assert.assertTrue(assembled.equals("60000000"));
		assertCleanDirectory();
	}

	@Test
	public void testDisassembler() throws Exception
	{
		byte[] machineCode = new byte[]{(byte) 0x80, (byte) 0xFD, 0x00, 0x18, (byte) 0x81, (byte) 0xFD, 0x10, 0x18};
		List<DisassembledInstruction> disassembledInstructions = Disassembler.disassemble(machineCode, 0x10000000);

		DisassembledInstruction firstInstruction = disassembledInstructions.get(0);
		Assert.assertEquals(firstInstruction.getAddress(), 0x10000000);
		Assert.assertEquals(firstInstruction.getValue(), 0x80FD0018);
		Assert.assertEquals(firstInstruction.getInstruction(), "lwz r7,24(r29)");

		DisassembledInstruction secondInstruction = disassembledInstructions.get(1);
		Assert.assertEquals(secondInstruction.getAddress(), 0x10000004);
		Assert.assertEquals(secondInstruction.getValue(), 0x81FD1018);
		Assert.assertEquals(secondInstruction.getInstruction(), "lwz r15,4120(r29)");

		assertCleanDirectory();
	}

	@Test
	public void testAssemblerError() throws Exception
	{
		try
		{
			Assembler.assemble("nonsense");
			Assert.fail();
		} catch (AssemblerException ignored)
		{

		} finally
		{
			assertCleanDirectory();
		}
	}

	@Test
	public void testLibrariesMissing() throws Exception
	{
		// Make sure the library is not found
		Path compiler = AssemblerFiles.getCompilerFilePath();
		Path renamed = rename(compiler, "powerpc-eabi-gcc2.exe");

		try
		{
			Assembler.assemble("");
			Assert.fail();
		} catch (AssemblerFilesException ignored)
		{

		} finally
		{
			rename(renamed, compiler.getFileName().toString());
			assertCleanDirectory();
		}
	}

	private Path rename(Path oldName, String newNameString) throws IOException
	{
		return Files.move(oldName, oldName.resolveSibling(newNameString));
	}

	private void assertCleanDirectory() throws IOException
	{
		Path directory = AssemblerFiles.getLibrariesDirectory();
		List<Path> files = listFiles(directory);

		files.stream().filter(file -> file.toString().endsWith(".s")
				|| file.toString().endsWith(".bin")
				|| file.toString().endsWith(".o")
				|| file.toString().endsWith(".out")).forEach(file ->
				Assert.fail("Directory not clean: " + file.getFileName()));
	}

	private List<Path> listFiles(Path directory) throws IOException
	{
		List<Path> fileNames = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory))
		{
			for (Path path : directoryStream)
			{
				fileNames.add(path);
			}
		}

		return fileNames;
	}
}