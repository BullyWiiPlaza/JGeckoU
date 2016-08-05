package wiiudev.gecko.client.connector.rpl.filesystem.memory;

import java.io.IOException;

/**
 * An interface to represent a block of memory on the Wii U, used to create an abstraction layer to hopefully reduce bandwidth requirements
 *
 * @author gudenau
 */
public interface IRemoteBuffer
{
	/**
	 * Gets the size of the remote memory block
	 *
	 * @return The size of the block
	 */
	int getSize();

	/**
	 * Gets the address of the start of the remote memory block
	 *
	 * @return The address of the start of the block
	 */
	int getAddress();

	/**
	 * Sets a block of data in the buffer
	 *
	 * @param address Address relative to the start of the block
	 * @param data    The data to set
	 * @param offset  The into the data buffer
	 * @param length  The amount of data to copy
	 */
	void setData(int address, byte[] data, int offset, int length);

	/**
	 * Gets a block of data in the buffer
	 *
	 * @param address Address relative to the start of the block
	 * @param data    The data to set
	 * @param offset  The into the data buffer
	 * @param length  The amount of data to copy
	 */
	void getData(int address, byte[] data, int offset, int length);

	/**
	 * Used to check if the local contents has changed since the last flush
	 *
	 * @return True if the buffer changed
	 */
	boolean isDirty();

	/**
	 * Used to flush changes to the Wii U
	 *
	 * @throws WiiUException If there was a general protocol error
	 * @throws IOException   If there was an IO error
	 */
	void flush() throws IOException, WiiUException;

	/**
	 * Used to update the buffer based on the Wii U's memory
	 *
	 * @throws WiiUException If there was a general protocol error
	 * @throws IOException   If there was an IO error
	 */
	void update() throws IOException, WiiUException;

	/**
	 * Used to update the buffer based on the Wii U's memory
	 *
	 * @param offset Offset relative to the buffer
	 * @param size   Size of data to get
	 * @throws WiiUException If there was a general protocol error
	 * @throws IOException   If there was an IO error
	 */
	void update(int offset, int size) throws IOException, WiiUException;


	/**
	 * Used to mark this buffer as dirty
	 */
	void markDirty();

	/**
	 * Used to mark this buffer as clean
	 */
	void clearDirty();
}
