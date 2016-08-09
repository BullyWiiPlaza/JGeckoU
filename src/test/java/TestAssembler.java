import org.junit.Assert;
import org.junit.Test;
import wiiudev.gecko.client.gui.tabs.disassembler.DisassembledInstruction;
import wiiudev.gecko.client.gui.tabs.disassembler.assembler.Assembler;
import wiiudev.gecko.client.gui.tabs.disassembler.assembler.AssemblerException;
import wiiudev.gecko.client.gui.tabs.disassembler.assembler.AssemblerFilesException;
import wiiudev.gecko.client.gui.tabs.disassembler.assembler.Disassembler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestAssembler
{
	@Test
	public void testAssembler() throws Exception
	{
		String assembled = Assembler.assemble("nop");
		Assert.assertTrue(assembled.equals("60000000"));
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

		}
	}

	@Test
	public void testLibrariesMissing() throws Exception
	{
		// Make sure the library is not found
		Path gcc = Paths.get("powerpc-eabi-gcc.exe");
		Path renamed = rename(gcc, "powerpc-eabi-gcc2.exe");

		try
		{
			Assembler.assemble("");
			Assert.fail();
		} catch (AssemblerFilesException ignored)
		{

		} finally
		{
			rename(renamed, gcc.getFileName().toString());
		}
	}

	private Path rename(Path oldName, String newNameString) throws IOException
	{
		return Files.move(oldName, oldName.resolveSibling(newNameString));
	}
}