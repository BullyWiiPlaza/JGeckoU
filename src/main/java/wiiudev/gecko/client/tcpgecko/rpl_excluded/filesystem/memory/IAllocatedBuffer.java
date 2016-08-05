package wiiudev.gecko.client.connector.rpl.filesystem.memory;

import java.io.IOException;

/**
 * An interface to represent a block of memory we allocated on the Wii U, used to create an abstraction layer to hopefully reduce bandwidth requirements
 *
 * @author gudenau
 */
public interface IAllocatedBuffer extends IRemoteBuffer
{
	/**
	 * Used to free this buffer
	 */
	void free() throws IOException;
}