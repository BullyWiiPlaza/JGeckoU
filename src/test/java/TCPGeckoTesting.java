import org.junit.*;
import wiiudev.gecko.client.tcpgecko.main.Connector;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.main.threads.OSContext;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThread;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThreadState;
import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.RemoteFileSystem;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.FileSystemReturnFlag;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.FileSystemStatus;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures.*;
import wiiudev.gecko.client.tcpgecko.rpl.structures.AllocatedMemory;

import java.io.ByteArrayOutputStream;
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
		MemoryReader.setDataBufferSize();
		System.out.println("Connected to TCP Gecko...");
	}

	@Test
	public void testAddressValidation() throws IOException
	{
		MemoryReader memoryReader = new MemoryReader();
		boolean isAddressValid;

		// Valid address
		isAddressValid = memoryReader.validateAddress(0x10000000);
		Assert.assertTrue(isAddressValid);

		// (Valid) executable data address
		isAddressValid = memoryReader.validateAddress(0x01000000);
		Assert.assertTrue(isAddressValid);

		// Invalid address
		isAddressValid = memoryReader.validateAddress(0x50000000);
		Assert.assertFalse(isAddressValid);

		// Valid address range
		isAddressValid = memoryReader.validateAddressRange(0x10800000, 0x10801000);
		Assert.assertTrue(isAddressValid);

		// Valid start but invalid end
		/*isAddressValid = memoryReader.validateAddressRange(0x40000000, 0x50000000);
		Assert.assertFalse(isAddressValid);*/

		// Invalid address range input
		try
		{
			memoryReader.validateAddressRange(0x1, 0x0);
			Assert.fail("Invalid range input not handled!");
		} catch (IllegalArgumentException ignored)
		{

		}

		// Range of one valid address
		isAddressValid = memoryReader.validateAddressRange(0x10000001, 0x10000001);
		Assert.assertTrue(isAddressValid);

		// Range of one invalid address
		isAddressValid = memoryReader.validateAddressRange(0x1, 0x1);
		Assert.assertFalse(isAddressValid);
	}

	@Test
	public void testRemoteProcedureCalls() throws Exception
	{
		CoreInit.getOSTime(); // Can't check correctness but just run it

		int physical = CoreInit.getEffectiveToPhysical(0x10000000);
		Assert.assertEquals(0x50000000, physical);

		int processID = CoreInit.getProcessPFID();
		Assert.assertEquals(processID, 0x0000000F);

		long titleID = CoreInit.getTitleID();
		System.out.println("Title ID: " + Long.toHexString(titleID).toUpperCase());

		boolean readable = CoreInit.isAddressReadable(0x01000000);
		Assert.assertTrue(readable);
		readable = CoreInit.isAddressReadable(0x01000000 - 1);
		Assert.assertFalse(readable);

		int cores = CoreInit.getOSCoreCount();
		Assert.assertEquals(cores, 3);
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

	@Test
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
	public void extractFile() throws IOException
	{
		String filePath = "/vol/content/afghanistan_gump_arena.ipak";

		FileSystemStatus status;
		RemoteFileSystem fileSystem = new RemoteFileSystem();

		try (FileSystemClient client = new FileSystemClient();
		     FileSystemCommandBlock commandBlock = new FileSystemCommandBlock();
		     FileSystemHandle fileDescriptor = new FileSystemHandle();
		     FileSystemPath path = new FileSystemPath(filePath);
		     FileSystemAccessMode accessMode = new FileSystemAccessMode(FileAccessMode.READ);
		     AllocatedMemory destinationBuffer = new AllocatedMemory(0x400 * 256, 0x40))
		{
			// Initialize the file system
			status = fileSystem.initialize();

			if (status != FileSystemStatus.OK)
			{
				Assert.fail("Failed initializing file system: " + status);
			}

			// Initialize the command block
			fileSystem.initializeCommandBlock(commandBlock);

			// Register the client
			status = fileSystem.addClient(client);

			if (status != FileSystemStatus.OK)
			{
				Assert.fail("Failed registering client: " + status);
			}

			// Open the file
			status = fileSystem.openFile(client,
					commandBlock,
					path,
					accessMode,
					fileDescriptor,
					FileSystemReturnFlag.ALL);

			if (status != FileSystemStatus.OK)
			{
				Assert.fail("Failed opening file: " + status);
			}

			ByteArrayOutputStream fileBytesBuffer = new ByteArrayOutputStream();
			int bytesRead;

			// Read all bytes from the file
			while ((bytesRead = fileSystem.readFile(client,
					commandBlock,
					destinationBuffer,
					fileDescriptor,
					FileSystemReturnFlag.ALL)) > 0)
			{
				MemoryReader memoryReader = new MemoryReader();
				byte[] readBytes = memoryReader.readBytes(destinationBuffer.getAddress(), bytesRead);
				fileBytesBuffer.write(readBytes);
			}

			status = FileSystemStatus.getStatus(bytesRead);
			System.out.println("File reading exit status: " + status);

			// Close the file
			status = fileSystem.closeFile(client, commandBlock, fileDescriptor, FileSystemReturnFlag.NONE);

			if (status != FileSystemStatus.OK)
			{
				Assert.fail("Failed closing file: " + status);
			}

			// Delete the client registration
			status = fileSystem.unRegisterClient(client, FileSystemReturnFlag.NONE);

			if (status != FileSystemStatus.OK)
			{
				Assert.fail("Failed un-registering client: " + status);
			}

			storeFile(filePath, fileBytesBuffer);
		}
	}

	private void storeFile(String filePath, ByteArrayOutputStream outputStream) throws IOException
	{
		Path targetFilePath = Paths.get(filePath);

		// Create parent directory (if it doesn't exist yet)
		Path parentDirectory = targetFilePath.getParent();
		Files.createDirectories(parentDirectory);

		// Write the bytes
		byte[] bytes = outputStream.toByteArray();
		Files.write(targetFilePath, bytes);
	}

	@Ignore
	public void testFileSystem() throws IOException
	{
		String directoryPath = "/vol/content";

		List<DirectoryEntry> directoryEntries = new ArrayList<>();

		try (FileSystemClient client = new FileSystemClient();
		     FileSystemCommandBlock commandBlock = new FileSystemCommandBlock();
		     FileSystemHandle directoryHandle = new FileSystemHandle();
		     FileSystemHandle fileHandle = new FileSystemHandle();
		     FileSystemPath filePath = new FileSystemPath(directoryPath);
		     DirectoryEntry directoryEntry = new DirectoryEntry();
		     FileSystemAccessMode fileSystemAccessMode = new FileSystemAccessMode(FileAccessMode.READ);
		     AllocatedMemory destinationBuffer = new AllocatedMemory(0x40, 0x1000))
		{
			RemoteFileSystem remoteFileSystem = new RemoteFileSystem();

			// Initialize the file system
			if (remoteFileSystem.initialize() != FileSystemStatus.OK)
			{
				Assert.fail("Could not initialize file system");
			}

			// Initialize the command block
			remoteFileSystem.initializeCommandBlock(commandBlock);

			// Add the client
			if (remoteFileSystem.registerClient(client, FileSystemReturnFlag.NONE) != FileSystemStatus.OK)
			{
				Assert.fail("Could not add client");
			}

			readDirectory(directoryEntries, client, commandBlock, directoryHandle, filePath, directoryEntry, remoteFileSystem);
		}

		System.out.println(directoryEntries.size());
	}

	private void readDirectory(List<DirectoryEntry> directoryEntries,
	                           FileSystemClient client,
	                           FileSystemCommandBlock commandBlock,
	                           FileSystemHandle directoryHandle,
	                           FileSystemPath filePath,
	                           DirectoryEntry directoryEntry,
	                           RemoteFileSystem remoteFileSystem) throws IOException
	{
		// Open the (base) directory
		if (remoteFileSystem.openDirectory(client, commandBlock, filePath,
				directoryHandle, FileSystemReturnFlag.ALL) != FileSystemStatus.OK)
		{
			Assert.fail("Could not open directory");
		}

		FileSystemStatus status;

		// Read all files and folders in this directory
		while ((status = remoteFileSystem.readDirectory(client, commandBlock, directoryHandle,
				directoryEntry, FileSystemReturnFlag.ALL)) == FileSystemStatus.OK)
		{
			int flag = directoryEntry.getFlag();
			System.out.println("Flag: " + flag);
			System.out.println("Directory: " +
					directoryEntry.isDirectory());
			int size = directoryEntry.getSize();
			System.out.println("Size: " + size);
			String name = directoryEntry.getName();
			System.out.println("Name: " + name);
			filePath.append(directoryEntry.getName());
			directoryEntry.setFileSystemPath(filePath);
			System.out.println("File System Path: " + filePath);

			directoryEntries.add(directoryEntry);

			if (directoryEntry.isDirectory())
			{
				// TODO Getting stuck on re-opening directory inside recursive call
				/*if (remoteFileSystem.closeDirectory(client, commandBlock, directoryHandle, FileSystemReturnFlag.NONE) != FileSystemStatus.OK)
				{
					Assert.fail("Failed closing directory");
				}

				readDirectory(directoryEntries,
						client,
						commandBlock,
						directoryHandle,
						filePath,
						directoryEntry,
						remoteFileSystem);

				if (remoteFileSystem.openDirectory(client, commandBlock, filePath,
						directoryHandle, FileSystemReturnFlag.ALL) != FileSystemStatus.OK)
				{
					Assert.fail("Failed re-opening directory");
				}*/
			} else
			{
					/*// TODO Stuck, no reply but no freeze?
					if(remoteFileSystem.openFile(client, commandBlock, filePath,
							fileSystemAccessMode, fileHandle, FileSystemReturnFlag.NONE) == FileSystemStatus.OK)
					{
						int fileSize = remoteFileSystem.readFile(client, commandBlock, destinationBuffer,
								fileHandle, FileSystemReturnFlag.NONE);
						MemoryReader memoryReader = new MemoryReader();
						byte[] bytes = memoryReader.readBytes(destinationBuffer.getAddress(), fileSize);
						Files.write(Paths.get(name), bytes);
					}

					remoteFileSystem.closeFile(client, commandBlock, directoryHandle, FileSystemReturnFlag.NONE);
					break;*/
			}
		}

		System.out.println("File System Status: " + status);

		if (remoteFileSystem.closeDirectory(client, commandBlock, directoryHandle, FileSystemReturnFlag.NONE) != FileSystemStatus.OK)
		{
			Assert.fail("Failed closing directory");
		}
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
			status = remoteFileSystem.registerClient(client, FileSystemReturnFlag.NONE);
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

				status = remoteFileSystem.openDirectory(client, commandBlock, path, directoryHandle, FileSystemReturnFlag.ALL);

				if (status != FileSystemStatus.OK)
				{
					continue;
				}

				do
				{
					// status = remoteFileSystem.readDirectory(client, commandBlock, directoryHandle, buffer, FileSystemReturnFlag.ALL);

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

				remoteFileSystem.closeDirectory(client, commandBlock, directoryHandle, FileSystemReturnFlag.NONE);
			}

			remoteFileSystem.unRegisterClient(client, FileSystemReturnFlag.NONE);
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

		/*int kernelInt = memoryReader.kernelReadInt(0x01000000);
		Assert.assertEquals(kernelInt, 0x38005E00);*/

		int foundAddress = memoryReader.search(0x10000000, 0x10000, 0x4E554C4C);
		Assert.assertEquals(foundAddress, 0x10004744);

		int size = memoryReader.readDataBufferSize();
		Assert.assertEquals(size, 0x5000);

		int address = memoryReader.readCodeHandlerInstallationAddress();
		Assert.assertEquals(address, 0x010F4000);
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