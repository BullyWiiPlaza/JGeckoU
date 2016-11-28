package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DirectoryEntry extends FileSystemObject
{
	private int flag;
	private int permission;
	private int owner_id;
	private int group_id;
	private int size;
	private int alloc_size;
	private long quota_size;
	private int ent_id;
	private long ctime;
	private long mtime;
	private byte[] attributes;
	private String name;

	private ByteBuffer byteBuffer;
	private int bytesCount = 7 * 4 + 3 * 8 + 48 + 256;
	private FileSystemPath fileSystemPath;

	public DirectoryEntry() throws IOException
	{
		super(0x200, 0x20);
	}

	/*public DirectoryEntry()
	{
		byte[] randomBytes = new byte[bytesCount];
		Random random = new Random();
		random.nextBytes(randomBytes);
		byteBuffer = ByteBuffer.wrap(randomBytes);

		assignFields();
	}*/

	public void retrieveData() throws IOException
	{
		MemoryReader memoryReader = new MemoryReader();
		byte[] readBytes = memoryReader.readBytes(getAddress(), bytesCount);
		byteBuffer = ByteBuffer.wrap(readBytes);

		assignFields();
	}

	private void assignFields()
	{
		flag = byteBuffer.getInt();
		permission = byteBuffer.getInt();
		owner_id = byteBuffer.getInt();
		group_id = byteBuffer.getInt();
		size = byteBuffer.getInt();
		alloc_size = byteBuffer.getInt();
		quota_size = byteBuffer.getLong();
		ent_id = byteBuffer.getInt();
		ctime = byteBuffer.getLong();
		mtime = byteBuffer.getLong();
		attributes = new byte[48];

		for (int attributesIndex = 0; attributesIndex < attributes.length; attributesIndex++)
		{
			attributes[attributesIndex] = byteBuffer.get();
		}

		byte[] nameBytes = StringReadingUtilities.readNullTerminatedBytes(byteBuffer);
		name = new String(nameBytes, StandardCharsets.UTF_8);
	}

	public int getFlag()
	{
		return flag;
	}

	public int getSize()
	{
		return size;
	}

	public String getName()
	{
		return name;
	}

	public boolean isDirectory()
	{
		return (flag & 0x80000000) != 0;
	}

	public void setFileSystemPath(FileSystemPath fileSystemPath)
	{
		this.fileSystemPath = fileSystemPath;
	}

	public String getFileSystemPath()
	{
		return fileSystemPath.getPath();
	}

	private static class StringReadingUtilities
	{
		static byte[] readNullTerminatedBytes(ByteBuffer byteBuffer)
		{
			byte[] bytes = byteBuffer.array();
			int position = byteBuffer.position();

			int length = getNullTerminatedStringLength(bytes, position);
			return Arrays.copyOfRange(bytes, position, position + length);
		}

		static int getNullTerminatedStringLength(byte[] bytes, int position)
		{
			int currentArrayPosition = position;

			while (true)
			{
				byte arrayByte = bytes[currentArrayPosition];

				if (arrayByte == 0x00 || currentArrayPosition == bytes.length - 1)
				{
					break;
				}

				currentArrayPosition++;
			}

			return currentArrayPosition - position;
		}
	}
}
