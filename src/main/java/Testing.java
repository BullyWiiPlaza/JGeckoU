import wiiudev.gecko.client.gui.tabs.disassembler.assembler.Assembler;
import wiiudev.gecko.client.gui.utilities.ProgramDirectoryUtilities;
import wiiudev.gecko.client.gui.utilities.SaveMemoryDumpsFileDialog;
import wiiudev.gecko.client.memory_search.SearchResult;
import wiiudev.gecko.client.memory_search.enumerations.ValueSize;
import wiiudev.gecko.client.tcpgecko.LzmaCompressor;
import wiiudev.gecko.client.tcpgecko.main.Connector;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.RemoteFileSystem;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.FileSystemReturnFlag;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.FileSystemStatus;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures.*;
import wiiudev.gecko.client.tcpgecko.rpl.structures.AllocatedMemory;
import wiiudev.gecko.client.titles.Title;
import wiiudev.gecko.client.titles.TitleDatabaseManager;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Testing
{
	private static void readFile(String remoteFilePath) throws IOException
	{
		Path remoteFile = Paths.get(remoteFilePath);
		boolean isFile = remoteFile.toString().contains(".");

		if (!isFile)
		{
			throw new IllegalArgumentException("Not a remote file path");
		}

		RemoteFileSystem remoteFileSystem = new RemoteFileSystem();
		FileSystemStatus status = remoteFileSystem.initialize();

		if (status == FileSystemStatus.OK)
		{
			FileSystemClient client = new FileSystemClient();

			if (client.isAllocated())
			{
				status = remoteFileSystem.addClient(client);

				if (status == FileSystemStatus.OK)
				{
					FileSystemCommandBlock commandBlock = new FileSystemCommandBlock();

					if (commandBlock.isAllocated())
					{
						remoteFileSystem.initializeCommandBlock(commandBlock);

						int bufferSize = 0x1000;
						FileSystemHandle fileSystemHandle = new FileSystemHandle();

						if (fileSystemHandle.isAllocated())
						{
							String parentDirectory = remoteFile.getParent().toString().replace("\\", "/");
							FileSystemPath allocatedDirectory = new FileSystemPath(parentDirectory);

							if (allocatedDirectory.isAllocated())
							{
								status = remoteFileSystem.openDirectory(client,
										commandBlock,
										allocatedDirectory,
										fileSystemHandle,
										FileSystemReturnFlag.ALL);

								if (status == FileSystemStatus.OK)
								{
									DirectoryEntry directoryEntry = new DirectoryEntry();

									if (directoryEntry.isAllocated())
									{
										status = remoteFileSystem.readDirectory(client,
												commandBlock,
												fileSystemHandle,
												directoryEntry,
												FileSystemReturnFlag.ALL);

										if (status == FileSystemStatus.OK)
										{
											FileSystemPath allocatedRemoteFilePath = new FileSystemPath(remoteFilePath);

											if (allocatedRemoteFilePath.isAllocated())
											{
												FileSystemAccessMode accessMode = new FileSystemAccessMode(FileAccessMode.READ);

												if (accessMode.isAllocated())
												{
													status = remoteFileSystem.openFile(client,
															commandBlock,
															allocatedRemoteFilePath,
															accessMode,
															fileSystemHandle,
															FileSystemReturnFlag.ALL);

													if (status == FileSystemStatus.OK)
													{
														int totalFileSize = 0;
														int bytesRead;

														AllocatedMemory dataBuffer = new AllocatedMemory(0x40, bufferSize);

														if (dataBuffer.isAllocated())
														{
															while ((bytesRead = remoteFileSystem.readFile(client,
																	commandBlock,
																	dataBuffer,
																	fileSystemHandle,
																	FileSystemReturnFlag.ALL)) > 0)
															{
																totalFileSize += bytesRead;
															}

															dataBuffer.close();
														}

														status = remoteFileSystem.closeFile(client,
																commandBlock,
																fileSystemHandle,
																FileSystemReturnFlag.ALL);

														if (status == FileSystemStatus.OK)
														{
															System.out.println("Total file size: " + totalFileSize);
														} else
														{
															System.out.println("Failed closing file: " + status);
														}
													}

													accessMode.close();
												}

												allocatedRemoteFilePath.close();
											}
										}

										directoryEntry.close();
									}

									status = remoteFileSystem.closeDirectory(client,
											commandBlock,
											fileSystemHandle,
											FileSystemReturnFlag.ALL);

									if (status != FileSystemStatus.OK)
									{
										System.out.println("Failed closing directory: " + status);
									}
								}

								allocatedDirectory.close();
							}

							fileSystemHandle.close();
						}

						commandBlock.close();
					}
				}

				client.close();
			}
		}
	}

	public static void main(String[] arguments) throws Exception
	{
		String remoteFilePath = "/vol/content/afghanistan_gump_arena.ipak";
		boolean b = Paths.get(remoteFilePath).toString().contains(".");
		System.out.println("Is File: " + b);
		String parentDirectory = Paths.get(remoteFilePath).getParent().toString().replace("\\", "/");
		System.out.println(remoteFilePath);
		System.out.println(parentDirectory);

		System.exit(0);

		SaveMemoryDumpsFileDialog openFileDialog = new SaveMemoryDumpsFileDialog(null);
		openFileDialog.showDialog();
		Path selectedFilePath = openFileDialog.getSelectedFilePath();

		if (selectedFilePath == null)
		{
			System.out.println("Canceled");
		} else
		{
			System.out.println(selectedFilePath.toString());
		}

		System.exit(0);

		Connector.getInstance().connect("192.168.178.35");
		MemoryReader.setDataBufferSize();
		readFile("/vol/content/afghanistan_gump_arena.ipak");
		/*MemoryReader memoryReader = new MemoryReader();
		memoryReader.readFile("/vol/content/afghanistan_gump_arena.ipak");*/
		// System.out.println(b);
		// Disassembler.search(0x01000000, "lwz r3.*");
		Connector.getInstance().closeConnection();
		System.exit(0);

		long milliseconds = System.currentTimeMillis();
		int size = 1000000;
		List<SearchResult> searchResults = new ArrayList<>();

		for (int i = 0; i < size; i++)
		{
			// System.out.println(i);
			SearchResult searchResult = new SearchResult(0x12345678, new BigInteger("3"), new BigInteger("1"), ValueSize.EIGHT_BIT);
			searchResults.add(searchResult);
		}

		System.out.println((System.currentTimeMillis() - milliseconds) / (double) 1000 + " seconds");
		System.exit(0);

		TitleDatabaseManager titleDatabaseManager = new TitleDatabaseManager();
		Title title = titleDatabaseManager.getTitle("00050000-1010ED00");
		System.out.println(title);
		System.exit(0);

		String currentDirectory = ProgramDirectoryUtilities.getProgramDirectory();
		System.out.println(currentDirectory);
		System.exit(0);

		String assembled = Assembler.assembleHexadecimal("nop\nli r0, 1");
		System.out.println(assembled);

		// installAssemblyLibraries();

		// Connector.getInstance().connect("192.168.178.35");

		// MemoryWriter memoryWriter = new MemoryWriter();
		// memoryWriter.unHook(0x010F4000);
		// memoryWriter.hook(0x010F4000, new byte[]{0x60, 0x00, 0x00, 0x00, 0x60, 0x00, 0x00, 0x00});

		// RemoteDisassembler.disassembleRange(0x01000000, 0x8);

		// MemoryWriter memoryWriter = new MemoryWriter();
		// memoryWriter.writeInt(0x10000004, 0x48344120);

		/*
		MemoryReader memoryReader = new MemoryReader();
		byte[] bytes = memoryReader.readBytes(0x01000000, 0x4);
		System.out.println(Conversions.toHexadecimal(bytes, ValueSize.THIRTY_TWO_BIT));
		memoryWriter.writeInt(0x01000000, 0x38005E00);*/
		// memoryWriter.writeInt(0x10000000, 1001);
		// MemoryReader memoryReader = new MemoryReader();
		// int address = memoryReader.search(0x10000000, 0x1000000, new byte[]{0x73, 0x68, 0x6F, 0x75});
		// System.out.println(Integer.toHexString(memoryReader.readInt(0x10000000)));

		/*MemoryReader memoryReader = new MemoryReader();
		byte[] bytes = memoryReader.readBytes(0x10000000, 0x2000);
		Files.write(Paths.get("file4.bin"), bytes);*/

		/*MemoryWriter memoryWriter = new MemoryWriter();
		memoryWriter.kernelWriteInt(0x01100000, 0x60000000);
		MemoryWriter memoryWriter = new MemoryWriter();
		int address = 0x11000000;
		memoryWriter.writeBytes(address, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08});
		MemoryReader memoryReader = new MemoryReader();
		int i = memoryReader.readInt(address);
		System.out.println(Integer.toHexString(i).toUpperCase());
		address += 4;
		i = memoryReader.readInt(address);
		System.out.println(Integer.toHexString(i).toUpperCase());*/
		/*MemoryReader memoryReader = new MemoryReader();
		memoryReader.disassembleRange(0x02000000, 0x10);*/
		Connector.getInstance().closeConnection();

		// ArchivingUtilities.unpack("D:\\Programs\\Source Codes\\Java\\IntelliJ\\JGecko U\\searches\\AMKP01\\Character 2.xml.zip");

		System.exit(0);

		Path rawFile = Paths.get("raw.txt");
		Path compressedFile = Paths.get("raw.lzma");

		LzmaCompressor lzmaCompressor = new LzmaCompressor(rawFile, compressedFile);
		lzmaCompressor.compress();
		// lzmaCompressor.decompress();

		System.exit(0);
		// System.out.println(removeScientificNotation("3.0103E-7"));
	}
	// System.out.println(decimalToHex(-20f));
	//

		/*MemoryRange memoryRange = new MemoryRange(0x01800000, 0x10000000);
		memoryRange.updateMemoryRange(false);
		System.out.println(memoryRange.getEndingAddress());*/

		/*RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = new ExportedSymbol(0x0249EEE0);
		int result = remoteProcedureCall.callInt(exportedSymbol);
		System.out.println(Conversions.toHexadecimal(result, 8));*/
		/*ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("t6mp_cafef_rpl.rpl", "Party_IsPrivateOnlineGame");
		System.out.println(Conversions.toHexadecimal(exportedSymbol.getAddress(), 8));*/
		/*MemoryRange memoryRange = new MemoryRange(0xA0000000, 0xB0000000);
		memoryRange.updateMemoryRange(false);
		System.out.println(Conversions.toHexadecimal(memoryRange.getEndingAddress(), 8));*/
		/*byte[] bytes = RandomUtils.nextBytes(0x900);
		MemoryWriter memoryWriter = new MemoryWriter();
		memoryWriter.writeBytes(0x1FFFFFF0, bytes);*/

	// Connector.getInstance().closeConnection();
	// IDAProFunctionsDumpParser idaProFunctionsDumpParser = new IDAProFunctionsDumpParser();
	// Connector.getInstance().connect("192.168.178.35");
	// MemoryReader memoryReader = new MemoryReader();
		/*Connector.getInstance().connect("192.168.178.35");
		MemoryReader memoryReader = new MemoryReader();
		int physical = memoryReader.getEffectiveToPhysical(0x10000000);
		System.out.println(physical);
		Connector.getInstance().closeConnection();*/
		/*String rplName = "coreinit.rpl";
		int length = 8 + rplName.length() + 1;
		byte[] lengthBytes = ByteBuffer.allocate(4).putInt(length).array();
		System.out.println(length);
		System.out.println(Arrays.toString(lengthBytes));*/

		/*TitleDatabaseManager titleDatabaseManager = new TitleDatabaseManager();
		Title title = titleDatabaseManager.readTitle("00050000-10102000");
		System.out.println(title.getGameId());*/

		/*Connector.getInstance().connect("192.168.178.35");
		MemoryWriter memoryWriter = new MemoryWriter();
		memoryWriter.upload(0x3FD00000, Paths.get("binary.bin"));
		Connector.getInstance().closeConnection();*/

	// String waitingTime = MemoryReader.getExpectedWaitingTime(0x49000000 - 0x3A000000);
	// System.out.println(waitingTime);
		/*Field field = System.class.getDeclaredField("lineSeparator");
		field.setAccessible(true);
		field.set(System.class, "\n");
		String formatted = CheatCodeFormatter.format("12345678123456781234567812345678", false);
		System.out.println(formatted);
		formatted = CheatCodeFormatter.format("00220000 3FB467E0\n" +
				"10000000 50000000\n" +
				"00000024 00000000\n" +
				"10000000 50000000\n" +
				"000002E8 41000000", false);
		System.out.println(formatted);*/
	// String s = "FFFFF160";
	// System.out.println();
	// MemoryPointerExpression memoryPointerExpression = new MemoryPointerExpression("[0x4443BB4C]");
	// System.out.println();

		/*try
		{
			CodeDatabaseDownloader codeDatabaseDownloader = new CodeDatabaseDownloader("AGMP01");
			boolean codesExist = codeDatabaseDownloader.codesExist();

			if(codesExist)
			{
				List<GeckoCode> codes = codeDatabaseDownloader.parseCodes();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}*/

		/*TitleDatabaseManager titleDatabaseManager = new TitleDatabaseManager();
		titleDatabaseManager.restore();
		Title title = titleDatabaseManager.getTitleFromGameId("AGMP01");
		System.out.println(title.getTitleID().replace("-", ""));*/

	// System.out.println();

		/*Connector.getInstance().connect("192.168.178.35");
		int address = new MemoryReader().search_old(0x10000000, 0x2A0A0000, 0x1000);
		System.out.println(Integer.toHexString(address).toUpperCase());
		/*MemorySearch memorySearch = new MemorySearch(0x10000000, 0x1000);
		memorySearch.dump();
		memorySearch.dumpBytes(0x6C64206E, SearchConditions.EQUAL);
		memorySearch.dump();
		// memorySearch.dumpBytes(0x20202000, SearchConditions.NOT_EQUAL);
		// WordSearch_old wordSearchOld = new WordSearch_old(0x10000000, 0x1000);
		// wordSearchOld.dumpBytes(0x6C64206E);
		// wordSearchOld.dumpBytes(0x6C64206E);
		Connector.getInstance().closeConnection();*/
}