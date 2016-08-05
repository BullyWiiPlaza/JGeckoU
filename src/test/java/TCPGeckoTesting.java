import org.junit.*;
import wiiudev.gecko.client.tcpgecko.main.Connector;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;
import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.RemoteFileSystem;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.ErrorHandling;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.FileSystemStatus;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class TCPGeckoTesting
{
	private static Connector connector = Connector.getInstance();

	@BeforeClass
	public static void connect() throws IOException
	{
		connector.connect("192.168.178.35");
	}

	@Test
	public void testRemoteProcedureCalls() throws Exception
	{
		/*int physical = CoreInit.getEffectiveToPhysical(0x10000000);
		Assert.assertEquals(0x50000000, physical);

		long processID = CoreInit.getProcessPFID();
		Assert.assertEquals(processID, 0xF00000000L);

		testFileSystem(new String[]{"content"});*/

		int allocated = CoreInit.allocateDefaultHeapMemory(0x50, 0x20);
		System.out.println("Allocated: " + new Hexadecimal(allocated));
		CoreInit.freeDefaultHeapMemory(0);
		System.out.println("De-allocated!");

		/*if(TitleDatabaseManager.isPlaying("Call of Duty: Black Ops II"))
		{
			RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
			ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("t6mp_cafef_rpl.rpl", "Com_SessionMode_IsPublicOnlineGame");
			System.out.println(new Hexadecimal(exportedSymbol.getAddress()));
			// long result = exportedSymbol.call();
			// System.out.println(result);
		}*/

		/*int heap = CoreInit.allocateDefaultHeapMemory(0x50, 0x20);
		CoreInit.freeDefaultHeapMemory(heap);
		System.out.println(heap);*/

		/*int stringAddress = CoreInit.allocateString("This is my String");
		System.out.println(stringAddress);
		CoreInit.freeSystemMemory(stringAddress);*/
	}

	private void testFileSystem(String[] folders) throws IOException
	{
		RemoteFileSystem remoteFileSystem = new RemoteFileSystem();
		FileSystemClient client = new FileSystemClient();
		FileSystemCommandBlock commandBlock = new FileSystemCommandBlock();
		remoteFileSystem.addClient(client, ErrorHandling.NONE);
		remoteFileSystem.initializeCommandBlock(commandBlock);

		FileSystemDirectoryHandle directoryHandle = new FileSystemDirectoryHandle();
		FileSystemPath path = new FileSystemPath();
		FileSystemBuffer buffer = new FileSystemBuffer();

		FileStructure root = new FileStructure("vol", -1);
		Queue<FileStructure> scanQueue = new LinkedList<>();

		for (String item : folders)
		{
			scanQueue.add(root.addSubFolder(item, -1));
		}

		while (scanQueue.size() > 0)
		{
			FileStructure current = scanQueue.remove();
			String thePath = current.Path();
			MemoryWriter memoryWriter = new MemoryWriter();
			memoryWriter.writeString(path.getAddress(), thePath);

			FileSystemStatus status = remoteFileSystem.openDirectory(client, commandBlock, path, directoryHandle, ErrorHandling.ALL);

			if (status != FileSystemStatus.OK)
			{
				continue;
			}

			do
			{
				status = remoteFileSystem.readDirectory(client, commandBlock, directoryHandle, buffer, ErrorHandling.ALL);

				if(status != FileSystemStatus.OK)
				{
					break;
				}

				MemoryReader memoryReader = new MemoryReader();
				int bufferAddress = buffer.getAddress();
				byte[] bytes = memoryReader.readBytes(bufferAddress, bufferAddress + 0x200);
				ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
				long attr = Long.reverseBytes(byteBuffer.getLong(0));
				long size = Long.reverseBytes(byteBuffer.getLong(8));
				byte [] subArray = Arrays.copyOfRange(bytes, 0x64, 0x100);
				String name = new String(subArray);
				name = name.replace("\0", "");

				if ((attr & 0x80000000) != 0)
				{
					scanQueue.add(current.addSubFolder(name, -1));
				}
				else
				{
					current.addFile(name, -1, size);
				}
			} while (true);

			remoteFileSystem.closeDirectory(client, commandBlock, directoryHandle, ErrorHandling.NONE);
		}

		// Clean up again
		buffer.free();
		path.free();
		directoryHandle.free();
		remoteFileSystem.unregisterClient(client, ErrorHandling.NONE);
		commandBlock.free();
		client.free();
	}

	@Test
	public void testMemoryReading() throws IOException, URISyntaxException
	{
		MemoryReader memoryReader = new MemoryReader();

		int readInteger = memoryReader.readInt(0x10000004);
		Assert.assertEquals(readInteger, 0x48344120);

		/*readInteger = memoryReader.readKernelInt(0x01000000);
		Assert.assertEquals(readInteger, 0x38005E00);*/

		byte readByte = memoryReader.read(0x10000005);
		Assert.assertEquals(readByte, 0x34);

		short readShort = memoryReader.readShort(0x10000006);
		Assert.assertEquals(readShort, 0x4120);

		String readString = memoryReader.readString(0x10000130);
		Assert.assertEquals(readString, "psq_stx");

		boolean readBoolean = memoryReader.readBoolean(0x1000ED4B);
		Assert.assertEquals(readBoolean, true);
		readBoolean = memoryReader.readBoolean(0x1000ED4A);
		Assert.assertEquals(readBoolean, false);

		float readFloat = memoryReader.readFloat(0x10004BC8);
		Assert.assertEquals(readFloat, 1.3, 0.01);

		int firmware = memoryReader.readFirmwareVersion();
		Assert.assertEquals(firmware, 532);

		// Read small byte array
		byte[] bytes = memoryReader.readBytes(0x10000004, 0x34);
		Assert.assertEquals(new String(bytes), "H4A should not be cleared because of Broadway errata");

		// Read large byte array
		byte[] largeBytes = memoryReader.readBytes(0x10000000, 0x1000);
		Path path = getDumpFile();
		byte[] savedBytes = Files.readAllBytes(path);
		Assert.assertTrue(Arrays.equals(largeBytes, savedBytes));
	}

	public Path getDumpFile() throws URISyntaxException
	{
		return Paths.get(ClassLoader.getSystemResource("dump.bin").toURI());
	}

	@Ignore
	public void testMemoryWriting() throws IOException
	{
		MemoryWriter memoryWriter = new MemoryWriter();
		MemoryReader memoryReader = new MemoryReader();

		memoryWriter.write(0x10000001, (byte) 0xFE);
		int readByte = memoryReader.readInt(0x10000000);
		Assert.assertEquals(readByte, 0x00FE03E8);
		memoryWriter.write(0x10000001, (byte) 0x00);
		readByte = memoryReader.readInt(0x10000000);
		Assert.assertEquals(readByte, 0x000003E8);

		memoryWriter.writeBoolean(0x100000BF, true);
		int readBoolean = memoryReader.readInt(0x100000BC);
		Assert.assertEquals(readBoolean, 0x650A0001);
		memoryWriter.writeBoolean(0x100000BF, false);
		readBoolean = memoryReader.readInt(0x100000BC);
		Assert.assertEquals(readBoolean, 0x650A0000);

		memoryWriter.writeBytes(0x10000138, "Hello".getBytes(StandardCharsets.UTF_8));
		String writtenString = memoryReader.readString(0x10000138);
		Assert.assertEquals(writtenString, "Hello  ");
		memoryWriter.writeBytes(0x10000138, "     ".getBytes(StandardCharsets.UTF_8));
		writtenString = memoryReader.readString(0x10000138);
		Assert.assertEquals(writtenString, "       ");

		memoryWriter.writeFloat(0x10004B9C, 1337f);
		float readFloat = memoryReader.readFloat(0x10004B9C);
		Assert.assertEquals(readFloat, 1337f, 0);
		memoryWriter.writeFloat(0x10004B9C, 0f);
		readFloat = memoryReader.readFloat(0x10004B9C);
		Assert.assertEquals(readFloat, 0f, 0);

		memoryWriter.writeInt(0x10004BD8, 1337);
		int readInteger = memoryReader.readInt(0x10004BD8);
		Assert.assertEquals(readInteger, 1337);
		memoryWriter.writeInt(0x10004BD8, 0x43300000);
		readInteger = memoryReader.readInt(0x10004BD8);
		Assert.assertEquals(readInteger, 0x43300000);

		memoryWriter.writeString(0x10000004, "Hello");
		String readString = memoryReader.readString(0x10000004);
		Assert.assertEquals(readString, "Hello");
		memoryWriter.writeInt(0x10000004, 0x48344120);
		memoryWriter.writeInt(0x10000008, 0x73686F75);
		// We tested writing integers already so no more assertion here

		memoryWriter.writeShort(0x1000003A, (short) 0x1337);
		int readShort = memoryReader.readInt(0x10000038);
		Assert.assertEquals(readShort, 0x2E0A1337);
		memoryWriter.writeShort(0x1000003A, (short) 0);
		readShort = memoryReader.readInt(0x10000038);
		Assert.assertEquals(readShort, 0x2E0A0000);
	}

	@AfterClass
	public static void disconnect() throws IOException, InterruptedException
	{
		Connector.getInstance().closeConnection();
	}
}