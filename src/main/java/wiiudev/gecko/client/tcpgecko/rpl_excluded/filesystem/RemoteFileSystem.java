package wiiudev.gecko.client.connector.rpl.filesystem;

import wiiudev.gecko.client.connector.MemoryReader;
import wiiudev.gecko.client.connector.SocketCommunication;
import wiiudev.gecko.client.connector.rpl.filesystem.enumerations.FileSystemStatus;
import wiiudev.gecko.client.connector.rpl.filesystem.enumerations.FileSystemError;
import wiiudev.gecko.client.connector.rpl.filesystem.memory.IRemoteBuffer;

import java.io.Closeable;
import java.io.IOException;

/**
 * Methods relevant to the file system<br>
 * This includes save data<br>
 * @author gudenau
 */
public class RemoteFileSystem extends SocketCommunication implements Closeable
{
	public RemoteFileSystem() throws IOException
	{
		initializeFileSystem();
	}

	/**
	 * Initializes the file system
	 */
	private void initializeFileSystem() throws IOException
	{
		MemoryReader.callCoreInit("FSInit");
	}

	/**
	 * Shuts down the file system
	 */
	public void shutdownFileSystem() throws IOException
	{
		MemoryReader.callCoreInit("FSShutdown");
	}

	/**
	 * Registers a new client
	 *
	 * @param client Client to register
	 * @param errors Error handling info
	 * @return Result code
	 */
	public FileSystemStatus addFileSystemClient(FileSystemClient client, FileSystemError... errors) throws IOException
	{
		if (client.isInitialized())
		{
			return FileSystemStatus.OK;
		}

		int errorMask = getErrorMask(errors);

		IRemoteBuffer buffer = client.getBuffer();

		if (buffer == null)
		{
			buffer = coreInit.mallocHeap(0x1700, 0x20, false);
		}

		long result = gecko.getAndCallSymbol("FSAddClient", buffer.getAddress(), errorMask);
		client.setInitialized(true);

		return FileSystemStatus.getStatus((int) result);
	}

	/**
	 * Unregister a client
	 *
	 * @param client Client to unregister
	 * @param errors Error handling info
	 * @return Result code
	 */
	public FileSystemStatus FSDelClient(FileSystemClient client, FileSystemError... errors) throws IOException, WiiUException
	{
		if (!client.isInitialized())
		{
			return FileSystemStatus.OK;
		}

		IRemoteBuffer buffer = client.getBuffer();

		if (buffer == null)
		{
			throw new IllegalArgumentException("FileSystemClient did not have a buffer?");
		}

		int errorMask = getErrorMask(errors);

		long result = gecko.getAndCallSymbol("FSDelClient", buffer.getAddress(), errorMask);
		client.setInitialized(false);
		return FileSystemStatus.getStatus((int) result);
	}

	/**
	 * Gets number of registered clients
	 *
	 * @return Number of registered clients
	 */
	public int FSGetClientNum() throws IOException, WiiUException
	{
		return (int) gecko.getAndCallSymbol("FSGetClientNum");
	}

	/**
	 * Initializes the command block.
	 *
	 * @param block Command block
	 */
	public void FSInitCmdBlock(FSCmdBlock block) throws IOException, WiiUException
	{
		if (block.isInitialized())
		{
			return;
		}

		IRemoteBuffer buffer = block.getBuffer();

		if (buffer == null)
		{
			buffer = coreInit.mallocHeap(0x800, 0x20, false);
			block.setBuffer(buffer);
		}

		gecko.getAndCallSymbol("FSInitCmdBlock", buffer.getAddress());

		block.setInitialized(true);
	}

	/**
	 * Cancels specified waiting command block
	 *
	 * @param client Client
	 * @param block  Command block
	 */
	public void FSCancelCommand(FileSystemClient client, FSCmdBlock block) throws IOException, WiiUException
	{
		if (!client.isInitialized())
		{
			throw new IllegalArgumentException("Client was not initzlized!");
		}
		if (!block.isInitialized())
		{
			throw new IllegalArgumentException("Command block was not initzlized!");
		}

		gecko.getAndCallSymbol("FSCancelCommand", client.getBuffer().getAddress(), block.getBuffer().getAddress());
	}

	/**
	 * Cancels all waiting commands for a client
	 *
	 * @param client
	 */
	public void FSCancelAllCommands(FileSystemClient client) throws IOException, WiiUException
	{
		if (!client.isInitialized())
		{
			throw new IllegalArgumentException("Client was not initzlized!");
		}

		gecko.getAndCallSymbol("FSCancelCommand", client.getBuffer().getAddress());
	}

	public void FSSetUserData(FSCmdBlock block, int userData) throws IOException, WiiUException
	{
		//gecko.getAndCallSymbol("FSSetUserData", block.getBuffer(), userData);
	}

	public int FSGetUserData(FSCmdBlock block) throws IOException, WiiUException
	{
		//return (int)gecko.getAndCallSymbol("FSGetUserData", block.getBuffer());
		return 0;
	}

	public FileSystemStatus FSOpenDir(FileSystemClient client, FSCmdBlock block, String path, FSDirHandle handle, FileSystemError... errors) throws IOException, WiiUException
	{
		//int pathBuffer = coreInit.createString(path);
		int errorMask = getErrorMask(errors);

		int buffer;
		if (handle.hasBuffer())
		{
			buffer = handle.getBuffer();
		} else
		{
			//buffer = coreInit.malloc(4, 4);
			//handle.setBuffer(buffer);
		}

		//long returnStatus = gecko.getAndCallSymbol("FSOpenDir", client.getBuffer(), block.getBuffer(), pathBuffer, handle.getBuffer(), errorMask);

		//coreInit.free(pathBuffer);

		//return FileSystemStatus.getStatus((int) returnStatus);
		return FileSystemStatus.ACCESS_ERROR;
	}

	public FileSystemStatus FSCloseDir(FileSystemClient client, FSCmdBlock block, FSDirHandle handle, FileSystemError... errors) throws IOException, WiiUException
	{
		int errorMask = getErrorMask(errors);

		int buffer;
		if (handle.hasBuffer())
		{
			buffer = handle.getBuffer();
		} else
		{
			//buffer = coreInit.malloc(4, 4);
			//handle.setBuffer(buffer);
		}

		//long returnStatus = gecko.getAndCallSymbol("FSCloseDir", client.getBuffer(), block.getBuffer(), handle.getBuffer(), errorMask);

		//return FileSystemStatus.getStatus((int) returnStatus);
		return FileSystemStatus.ACCESS_ERROR;
	}

	/**
	 * Simple helper method to get the error mask
	 */
	private int getErrorMask(FileSystemError... errors)
	{
		int errorMask = 0;
		for (FileSystemError error : errors)
		{
			errorMask |= error.value;
		}

		return errorMask;
	}

	@Override
	public void close() throws IOException
	{
		shutdownFileSystem();
	}
}