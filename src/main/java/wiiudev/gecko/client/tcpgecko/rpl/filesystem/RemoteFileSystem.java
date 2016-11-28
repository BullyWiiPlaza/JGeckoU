package wiiudev.gecko.client.tcpgecko.rpl.filesystem;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;
import wiiudev.gecko.client.tcpgecko.rpl.ExportedSymbol;
import wiiudev.gecko.client.tcpgecko.rpl.RPL;
import wiiudev.gecko.client.tcpgecko.rpl.RemoteProcedureCall;
import wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations.FileSystemReturnFlag;
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
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "FSInit");
		int status = remoteProcedureCall.callInt(exportedSymbol);

		return FileSystemStatus.getStatus(status);
	}

	public FileSystemStatus registerClient(FileSystemClient fileSystemClient, FileSystemReturnFlag returnFlag) throws IOException
	{
		if (fileSystemClient.isRegistered())
		{
			throw new IllegalArgumentException("The client is already registered!");
		} else
		{
			return registerClient(fileSystemClient, returnFlag, true);
		}
	}

	public FileSystemStatus unRegisterClient(FileSystemClient fileSystemClient, FileSystemReturnFlag returnFlag) throws IOException
	{
		if (fileSystemClient.isRegistered())
		{
			return registerClient(fileSystemClient, returnFlag, false);
		} else
		{
			throw new IllegalArgumentException("The client is not registered yet!");
		}
	}

	public FileSystemStatus addClient(FileSystemClient client) throws IOException
	{
		ExportedSymbol addClient = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(),
				"FSAddClientEx");
		int status = remoteProcedureCall.callInt(addClient,
				client.getAddress(),
				0,
				-1);

		return FileSystemStatus.getStatus(status);
	}

	private FileSystemStatus registerClient(FileSystemClient client,
	                                        FileSystemReturnFlag returnFlag,
	                                        boolean register) throws IOException
	{
		int clientAddress = client.getAddress();
		String symbolName = register ? "FSAddClient" : "FSDelClient";
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), symbolName);
		int status = remoteProcedureCall.callInt(exportedSymbol, clientAddress, returnFlag.getValue());
		client.setRegistered(this);

		return FileSystemStatus.getStatus(status);
	}

	public FileSystemStatus openDirectory(FileSystemClient client,
	                                      FileSystemCommandBlock commandBlock,
	                                      FileSystemPath path,
	                                      FileSystemHandle directoryHandle,
	                                      FileSystemReturnFlag returnFlag) throws IOException
	{
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "FSOpenDir");
		int status = remoteProcedureCall.callInt(exportedSymbol, client.getAddress(),
				commandBlock.getAddress(), path.getAddress(),
				directoryHandle.getAddress(), returnFlag.getValue());
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
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "FSInitCmdBlock");
		remoteProcedureCall.call(exportedSymbol, address);
	}

	public FileSystemStatus openFile(FileSystemClient client,
	                                 FileSystemCommandBlock commandBlock,
	                                 FileSystemPath path,
	                                 FileSystemAccessMode accessMode,
	                                 FileSystemHandle handle,
	                                 FileSystemReturnFlag returnFlag) throws IOException
	{
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "FSOpenFile");
		int status = remoteProcedureCall.callInt(exportedSymbol,
				client.getAddress(),
				commandBlock.getAddress(),
				path.getAddress(),
				accessMode.getAddress(),
				handle.getAddress(),
				returnFlag.getValue());

		return FileSystemStatus.getStatus(status);
	}

	public int readFile(FileSystemClient client,
	                    FileSystemCommandBlock commandBlock,
	                    AllocatedMemory dataBuffer,
	                    FileSystemHandle fileDescriptor,
	                    FileSystemReturnFlag returnFlag) throws IOException

	{
		ExportedSymbol readFile = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "FSReadFile");

		return remoteProcedureCall.callInt(readFile,
				client.getAddress(),
				commandBlock.getAddress(),
				dataBuffer.getAddress(),
				0x1,
				0x1000,
				// dataBuffer.getSize(),
				fileDescriptor.dereference(),
				0,
				// returnFlag.getValue());
				-1);
	}

	public FileSystemStatus readDirectory(FileSystemClient client,
	                                      FileSystemCommandBlock commandBlock,
	                                      FileSystemHandle directoryHandle,
	                                      DirectoryEntry directoryEntry,
	                                      FileSystemReturnFlag returnFlag) throws IOException
	{
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "FSReadDir");

		int status = remoteProcedureCall.callInt(exportedSymbol, client.getAddress(),
				commandBlock.getAddress(),
				directoryHandle.dereference(),
				directoryEntry.getAddress(),
				returnFlag.getValue());

		directoryEntry.retrieveData();

		return FileSystemStatus.getStatus(status);
	}

	public FileSystemStatus closeDirectory(FileSystemClient client,
	                                       FileSystemCommandBlock commandBlock,
	                                       FileSystemHandle directoryHandle,
	                                       FileSystemReturnFlag returnFlag) throws IOException
	{
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "FSCloseDir");
		int status = remoteProcedureCall.callInt(exportedSymbol, client.getAddress(),
				commandBlock.getAddress(),
				directoryHandle.dereference(),
				returnFlag.getValue());

		return FileSystemStatus.getStatus(status);
	}

	public FileSystemStatus closeFile(FileSystemClient client,
	                                  FileSystemCommandBlock commandBlock,
	                                  FileSystemHandle handle,
	                                  FileSystemReturnFlag returnFlag) throws IOException
	{
		ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "FSCloseFile");
		int status = remoteProcedureCall.callInt(exportedSymbol,
				client.getAddress(),
				commandBlock.getAddress(),
				handle.dereference(),
				returnFlag.getValue());

		return FileSystemStatus.getStatus(status);
	}
}