import org.junit.*;
import wiiudev.gecko.client.tcpgecko.main.Connector;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.main.threads.OSContext;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThread;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThreadState;
import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;
import wiiudev.gecko.client.tcpgecko.rpl.RemoteDisassembler;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.RemoteFileSystem;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.ErrorHandling;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.FileSystemStatus;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures.*;
import wiiudev.gecko.client.tcpgecko.rpl.structures.AllocatedMemory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TCPGeckoTesting
{
	private static Connector connector = Connector.getInstance();

	@BeforeClass
	public static void connect() throws IOException
	{
		connector.connect("192.168.178.35");
		System.out.println("Connected to TCP Gecko...");
	}

	@Test
	public void testRemoteProcedureCalls() throws Exception
	{
		CoreInit.getOSTime(); // Can't check this but just run it

		int physical = CoreInit.getEffectiveToPhysical(0x10000000);
		Assert.assertEquals(0x50000000, physical);

		int processID = CoreInit.getProcessPFID();
		Assert.assertEquals(processID, 0x0000000F);

		long titleID = CoreInit.getTitleID();
		System.out.println(titleID);

		boolean readable = CoreInit.isAddressReadable(0x01000000);
		Assert.assertTrue(readable);
		readable = CoreInit.isAddressReadable(0x01000000 - 1);
		Assert.assertFalse(readable);

		int cores = CoreInit.getOSCoreCount();
		Assert.assertEquals(cores, 3);
	}

	@Test
	public void testRemoteDisassembler() throws IOException
	{
		String disassembled = RemoteDisassembler.disassembleValue(0x60000000);
		Assert.assertEquals(disassembled, "nop");

		disassembled = RemoteDisassembler.disassembleAddress(0x01087BC0);
		Assert.assertEquals(disassembled, "addi r11, r11, 4");
	}

	@Test
	public void testMemoryAllocation() throws IOException
	{
		int allocated = CoreInit.allocateDefaultHeapMemory(0x50, 0x20);
		CoreInit.freeDefaultHeapMemory(allocated);

		int allocated2 = CoreInit.allocateDefaultHeapMemory(0x50, 0x20);
		CoreInit.freeDefaultHeapMemory(allocated2);

		Assert.assertEquals(allocated, allocated2);
	}

	@Ignore
	public void testThreads() throws Exception
	{
		List<OSThread> osThreads = OSThread.readThreads();

		OSThread osThread = osThreads.get(0);
		OSContext osContext = new OSContext(osThread.getAddress());
		testThreadState(osThread);
	}

	private void testThreadState(OSThread osThread) throws IOException
	{
		OSThreadState state = osThread.getState();

		switch (state)
		{
			case PAUSED:
				osThread.setState(OSThreadState.RUNNING);
				Assert.assertTrue(osThread.getState() == OSThreadState.RUNNING);
				osThread.setState(state);
				break;

			case RUNNING:
				osThread.setState(OSThreadState.PAUSED);
				Assert.assertTrue(osThread.getState() == OSThreadState.PAUSED);
				osThread.setState(state);
				break;
		}
	}

	@Ignore
	public void testFileSystem() throws IOException
	{
		List<DirectoryEntry> directoryEntries = new ArrayList<>();

		try (FileSystemClient client = new FileSystemClient();
		     FileSystemCommandBlock commandBlock = new FileSystemCommandBlock();
		     FileSystemHandle directoryHandle = new FileSystemHandle();
		     FileSystemHandle fileHandle = new FileSystemHandle();
		     FileSystemPath filePath = new FileSystemPath("/vol/content");
		     DirectoryEntry directoryEntry = new DirectoryEntry();
			 AccessMode accessMode = new AccessMode(AccessMode.READ);
			 AllocatedMemory destinationBuffer = new AllocatedMemory(0x40, 0x1000))
		{
			RemoteFileSystem remoteFileSystem = new RemoteFileSystem();

			if (remoteFileSystem.initialize() == FileSystemStatus.OK)
			{
				remoteFileSystem.initializeCommandBlock(commandBlock);

				if (remoteFileSystem.addClient(client,
						ErrorHandling.NONE) == FileSystemStatus.OK)
				{
					if (remoteFileSystem.openDirectory(client, commandBlock, filePath,
							directoryHandle, ErrorHandling.ALL) == FileSystemStatus.OK)
					{
						while (remoteFileSystem.readDirectory(client, commandBlock, directoryHandle,
								directoryEntry, ErrorHandling.ALL) == FileSystemStatus.OK)
						{
							int flag = directoryEntry.getFlag();
							System.out.println("Flag: " + flag);
							System.out.println("Directory: " +
									directoryEntry.isDirectory());
							int size = directoryEntry.getSize();
							System.out.println("Size: " + size);
							String name = directoryEntry.getName();
							System.out.println("Name: " + name);

							if(directoryEntry.isDirectory())
							{

							}
							else
							{
								directoryEntries.add(directoryEntry);

								/*// TODO Stuck, no reply but no freeze?
								if(remoteFileSystem.openFile(client, commandBlock, filePath,
										accessMode, fileHandle, ErrorHandling.NONE) == FileSystemStatus.OK)
								{
									int fileSize = remoteFileSystem.readFile(client, commandBlock, destinationBuffer,
											fileHandle, ErrorHandling.NONE);
									MemoryReader memoryReader = new MemoryReader();
									byte[] bytes = memoryReader.readBytes(destinationBuffer.getAddress(), fileSize);
									Files.write(Paths.get(name), bytes);
								}

								remoteFileSystem.closeFile(client, commandBlock, directoryHandle, ErrorHandling.NONE);
								break;*/
							}
						}
					}

					remoteFileSystem.closeDirectory(client, commandBlock, directoryHandle, ErrorHandling.NONE);
				}
			}
		}

		System.out.println(directoryEntries.size());
	}

	private void testFileSystem(String[] folders) throws IOException
	{
		/*try (RemoteFileSystem remoteFileSystem = new RemoteFileSystem();
		     FileSystemClient client = new FileSystemClient();
		     FileSystemCommandBlock commandBlock = new FileSystemCommandBlock();
		     FileSystemHandle directoryHandle = new FileSystemHandle();
		     FileSystemPath path = new FileSystemPath("");
		     FileSystemBuffer buffer = new FileSystemBuffer())
		{
			FileSystemStatus status;

			remoteFileSystem.initialize();

			System.out.println("Client Address: " + new Hexadecimal(client.getAddress()));
			System.out.println("Command Block Address: " + new Hexadecimal(commandBlock.getAddress()));
			status = remoteFileSystem.addClient(client, ErrorHandling.NONE);
			// System.out.println("Add Client Status: " + new Hexadecimal(status2.value));
			remoteFileSystem.initializeCommandBlock(commandBlock);
			// System.out.println("Initialize Command Block Status: " + new Hexadecimal(status2.value));

			System.out.println("Directory Handle Address: " + new Hexadecimal(directoryHandle.getAddress()));
			System.out.println("getPath Address: " + new Hexadecimal(path.getAddress()));
			System.out.println("Buffer Address: " + new Hexadecimal(buffer.getAddress()));

			FileStructure rootFolder = new FileStructure("vol", -1);
			Queue<FileStructure> scanQueue = new LinkedList<>();

			for (String item : folders)
			{
				FileStructure subFolder = rootFolder.addSubFolder(item, -1);
				scanQueue.add(subFolder);
			}

			while (scanQueue.size() > 0)
			{
				FileStructure current = scanQueue.remove();
				String folderPath = current.getPath();
				MemoryWriter memoryWriter = new MemoryWriter();
				int pathAddress = path.getAddress();
				memoryWriter.writeString(pathAddress, folderPath);

				status = remoteFileSystem.openDirectory(client, commandBlock, path, directoryHandle, ErrorHandling.ALL);

				if (status != FileSystemStatus.OK)
				{
					continue;
				}

				do
				{
					// status = remoteFileSystem.readDirectory(client, commandBlock, directoryHandle, buffer, ErrorHandling.ALL);

					if (status != FileSystemStatus.OK)
					{
						break;
					}

					MemoryReader memoryReader = new MemoryReader();
					int bufferAddress = buffer.getAddress();
					byte[] bytes = memoryReader.readBytes(bufferAddress, 0x200);
					System.out.println("Buffer: " + DatatypeConverter.printHexBinary(bytes));
					ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
					int attribute = byteBuffer.getInt(0);
					System.out.println("Attribute: " + new Hexadecimal(attribute, 8));
					int size = byteBuffer.getInt(8);
					System.out.println("Size: " + new Hexadecimal(size, 8));
					System.out.println("Size de-referenced: " + new Hexadecimal(new MemoryReader().readInt(size), 8));
					byte[] nameArray = Arrays.copyOfRange(bytes, 0x64, 0x100);
					String name = new String(nameArray);

					System.out.println("Name: " + name);

					if ((attribute & 0x80000000) != 0)
					{
						scanQueue.add(current.addSubFolder(name, -1));
					} else
					{
						current.addFile(name, -1, size);
					}
				} while (true);

				remoteFileSystem.closeDirectory(client, commandBlock, directoryHandle, ErrorHandling.NONE);
			}

			remoteFileSystem.unregisterClient(client, ErrorHandling.NONE);
		}*/
	}

	@Test
	public void testMemoryReading() throws IOException, URISyntaxException
	{
		MemoryReader memoryReader = new MemoryReader();

		int readInteger = memoryReader.readInt(0x10000004);
		Assert.assertEquals(readInteger, 0x48344120);

		/*readInteger = memoryReader.kernelReadInt(0x01000000);
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

		int kernelInt = memoryReader.kernelReadInt(0x01000000);
		Assert.assertEquals(kernelInt, 0x38005E00);

		int ret = memoryReader.search(0x10000000, 0x10000, 0x4E554C4C);
		System.out.println(ret);
		Assert.assertEquals(ret, 0x10004744);
	}

	private Path getDumpFile() throws URISyntaxException
	{
		return Paths.get(ClassLoader.getSystemResource("dump.bin").toURI());
	}

	@Test
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

		/*memoryWriter.kernelWriteInt(0x01100000, 0x12345678);
		int readKernelInt = memoryReader.kernelReadInt(0x01100000);
		Assert.assertEquals(readKernelInt, 0x12345678);
		memoryWriter.kernelWriteInt(0x01100000, 0x0);
		readKernelInt = memoryReader.kernelReadInt(0x01100000);
		Assert.assertEquals(readKernelInt, 0);*/
	}

	@AfterClass
	public static void disconnect() throws IOException, InterruptedException
	{
		Connector.getInstance().closeConnection();
		System.out.println("Disconnected from TCP Gecko...");
	}
}