package wiiudev.gecko.client.tcpgecko.rpl.filesystem;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;
import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.ErrorHandling;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.FileSystemStatus;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures.*;

import java.io.Closeable;
import java.io.IOException;

public class RemoteFileSystem extends TCPGecko implements Closeable
{
	/**
	 * Initializes the file system
	 */
	public FileSystemStatus initialize() throws IOException
	{
		int status = CoreInit.call("FSInit");

		return FileSystemStatus.getStatus(status);
	}

	/**
	 * Shuts down the file system
	 */
	private void shutdown() throws IOException
	{
		CoreInit.call("FSShutdown");
	}

	public FileSystemStatus addClient(FileSystemClient fileSystemClient, ErrorHandling errorHandling) throws IOException
	{
		if(fileSystemClient.isRegistered())
		{
			throw new IllegalArgumentException("The client is already registered!");
		}
		else
		{
			return registerClient(fileSystemClient, errorHandling, true);
		}
	}

	public FileSystemStatus unregisterClient(FileSystemClient fileSystemClient, ErrorHandling errorHandling) throws IOException
	{
		if(fileSystemClient.isRegistered())
		{
			return registerClient(fileSystemClient, errorHandling, false);
		}
		else
		{
			throw new IllegalArgumentException("The client is not registered yet!");
		}
	}

	private FileSystemStatus registerClient(FileSystemClient client,
	                                        ErrorHandling errorHandling,
	                                        boolean register) throws IOException
	{
		int clientAddress = client.getAddress();
		String symbolName = register ? "FSAddClient" : "FSDelClient";
		int status = CoreInit.call(symbolName, clientAddress, errorHandling.getValue());
		client.setRegistered(register);

		return FileSystemStatus.getStatus(status);
	}

	public void registerCommandBlock(FileSystemCommandBlock commandBlock) throws IOException
	{
		if(commandBlock.isRegistered())
		{
			throw new IllegalArgumentException("The command block is already registered!");
		}
		else
		{
			int address = commandBlock.getAddress();
			CoreInit.call("FSInitCmdBlock", address);
			commandBlock.setRegistered(true);
		}
	}

	public FileSystemStatus openDirectory(FileSystemClient client,
	                                      FileSystemCommandBlock commandBlock,
	                                      FileSystemPath path,
	                                      FileSystemDirectoryHandle directoryHandle,
	                                      ErrorHandling errorHandling) throws IOException
	{
		int status = CoreInit.call("FSOpenDir", client.getAddress(),
				commandBlock.getAddress(), path.getAddress(),
				directoryHandle.getAddress(), errorHandling.getValue());
		FileSystemStatus fileSystemFileSystemStatus = FileSystemStatus.getStatus(status);
		System.out.println("File system status: " + fileSystemFileSystemStatus);
		MemoryReader memoryReader = new MemoryReader();
		int directoryHandleValue = memoryReader.readInt(directoryHandle.getAddress());
		System.out.println("Dir handle value: "  + new Hexadecimal(directoryHandleValue));

		return fileSystemFileSystemStatus;
	}

	public int getRegisteredClientsCount() throws IOException
	{
		return CoreInit.call("FSGetClientNum");
	}

	public void initializeCommandBlock(FileSystemCommandBlock commandBlock) throws IOException
	{
		int address = commandBlock.getAddress();
		CoreInit.call("FSInitCmdBlock", address);
	}

	public FileSystemStatus readDirectory(FileSystemClient client,
	                                      FileSystemCommandBlock commandBlock,
	                                      FileSystemDirectoryHandle directoryHandle,
	                                      FileSystemBuffer buffer,
	                                      ErrorHandling errorHandling) throws IOException
	{
		int status = CoreInit.call("FSReadDir",
				client.getAddress(),
				commandBlock.getAddress(),
				directoryHandle.dereference(),
				buffer.getAddress(),
				errorHandling.getValue());

		return FileSystemStatus.getStatus(status);
	}

	public FileSystemStatus closeDirectory(FileSystemClient client,
	                                       FileSystemCommandBlock commandBlock,
	                                       FileSystemDirectoryHandle directoryHandle,
	                                       ErrorHandling errorHandling) throws IOException
	{
		int status = CoreInit.call("FSCloseDir", client.getAddress(),
				commandBlock.getAddress(),
				directoryHandle.dereference(),
				errorHandling.getValue());

		return FileSystemStatus.getStatus(status);
	}

	@Override
	public void close() throws IOException
	{
		shutdown();
	}
}