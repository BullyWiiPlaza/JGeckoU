package wiiudev.gecko.client.tcpgecko.rpl.filesystem;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;
import wiiudev.gecko.client.tcpgecko.rpl.ExportedSymbol;
import wiiudev.gecko.client.tcpgecko.rpl.RemoteProcedureCall;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.ErrorHandling;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.FileSystemStatus;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures.*;
import wiiudev.gecko.client.tcpgecko.rpl.structures.AllocatedMemory;

import java.io.IOException;

public class RemoteFileSystem extends TCPGecko
{
	private RemoteProcedureCall remoteProcedureCall;

	public RemoteFileSystem()
	{
		this.remoteProcedureCall = new RemoteProcedureCall();
	}

	/**
	 * Initializes the file system
	 */
	public FileSystemStatus initialize() throws IOException
	{
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "FSInit");
		int status = remoteProcedureCall.call32(exportedSymbol);

		return FileSystemStatus.getStatus(status);
	}

	public FileSystemStatus addClient(FileSystemClient fileSystemClient, ErrorHandling errorHandling) throws IOException
	{
		if (fileSystemClient.isRegistered())
		{
			throw new IllegalArgumentException("The client is already registered!");
		} else
		{
			return registerClient(fileSystemClient, errorHandling, true);
		}
	}

	public FileSystemStatus unregisterClient(FileSystemClient fileSystemClient, ErrorHandling errorHandling) throws IOException
	{
		if (fileSystemClient.isRegistered())
		{
			return registerClient(fileSystemClient, errorHandling, false);
		} else
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
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", symbolName);
		int status = remoteProcedureCall.call32(exportedSymbol, clientAddress, errorHandling.getValue());
		client.setRegistered(this);

		return FileSystemStatus.getStatus(status);
	}

	public FileSystemStatus openDirectory(FileSystemClient client,
	                                      FileSystemCommandBlock commandBlock,
	                                      FileSystemPath path,
	                                      FileSystemHandle directoryHandle,
	                                      ErrorHandling errorHandling) throws IOException
	{
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "FSOpenDir");
		int status = remoteProcedureCall.call32(exportedSymbol, client.getAddress(),
				commandBlock.getAddress(), path.getAddress(),
				directoryHandle.getAddress(), errorHandling.getValue());
		FileSystemStatus fileSystemFileSystemStatus = FileSystemStatus.getStatus(status);
		System.out.println("File system status: " + fileSystemFileSystemStatus);

		MemoryReader memoryReader = new MemoryReader();
		int directoryHandleValue = memoryReader.readInt(directoryHandle.getAddress());
		System.out.println("Dir handle value: " + new Hexadecimal(directoryHandleValue));

		return fileSystemFileSystemStatus;
	}

	public void initializeCommandBlock(FileSystemCommandBlock commandBlock) throws IOException
	{
		int address = commandBlock.getAddress();
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "FSInitCmdBlock");
		remoteProcedureCall.call(exportedSymbol, address);
	}

	public FileSystemStatus openFile(FileSystemClient client,
	                                 FileSystemCommandBlock commandBlock,
	                                 FileSystemPath path,
	                                 AccessMode accessMode,
	                                 FileSystemHandle handle,
	                                 ErrorHandling errorHandling) throws IOException
	{
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "FSOpenFile");
		int status = remoteProcedureCall.call32(exportedSymbol,
				client.getAddress(),
				commandBlock.getAddress(),
				path.getAddress(),
				accessMode.getAddress(),
				handle.getAddress(),
				errorHandling.getValue());

		return FileSystemStatus.getStatus(status);
	}

	public int readFile(FileSystemClient client,
	                                 FileSystemCommandBlock commandBlock,
	                                 AllocatedMemory destinationBuffer,
	                                 FileSystemHandle handle,
	                                 ErrorHandling errorHandling) throws IOException

	{
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "FSReadFile");

		return remoteProcedureCall.call32(exportedSymbol,
				client.getAddress(),
				commandBlock.getAddress(),
				destinationBuffer.getAddress(),
				0x1,
				destinationBuffer.getSize(),
				handle.getAddress(),
				0,
				errorHandling.getValue());
	}

	public FileSystemStatus readDirectory(FileSystemClient client,
	                                      FileSystemCommandBlock commandBlock,
	                                      FileSystemHandle directoryHandle,
	                                      DirectoryEntry directoryEntry,
	                                      ErrorHandling errorHandling) throws IOException
	{
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "FSReadDir");
		int status = remoteProcedureCall.call32(exportedSymbol, client.getAddress(),
				commandBlock.getAddress(),
				directoryHandle.dereference(),
				directoryEntry.getAddress(),
				errorHandling.getValue());

		directoryEntry.retrieveData();

		return FileSystemStatus.getStatus(status);
	}

	public FileSystemStatus closeDirectory(FileSystemClient client,
	                                       FileSystemCommandBlock commandBlock,
	                                       FileSystemHandle directoryHandle,
	                                       ErrorHandling errorHandling) throws IOException
	{
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "FSCloseDir");
		int status = remoteProcedureCall.call32(exportedSymbol, client.getAddress(),
				commandBlock.getAddress(),
				directoryHandle.dereference(),
				errorHandling.getValue());

		return FileSystemStatus.getStatus(status);
	}

	public FileSystemStatus closeFile(FileSystemClient client,
	                                  FileSystemCommandBlock commandBlock,
	                                  FileSystemHandle directoryHandle,
	                                  ErrorHandling errorHandling) throws IOException
	{
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "FSCloseFile");
		int status = remoteProcedureCall.call32(exportedSymbol,
				client.getAddress(),
				commandBlock.getAddress(),
				directoryHandle.dereference(),
				errorHandling.getValue());

		return FileSystemStatus.getStatus(status);
	}
}