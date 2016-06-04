package wiiudev.gecko.client.connector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteUtilities
{
	public static List<byte[]> readPartitionedBytes(Path sourcePath, int chunkSize) throws IOException
	{
		byte[] fileBytes = Files.readAllBytes(sourcePath);

		return partition(fileBytes, chunkSize);
	}

	public static List<byte[]> partition(byte[] bytes, int chunkSize)
	{
		List<byte[]> byteArrayChunks = new ArrayList<>();
		int startingIndex = 0;

		while (startingIndex < bytes.length)
		{
			int end = Math.min(bytes.length, startingIndex + chunkSize);
			byteArrayChunks.add(Arrays.copyOfRange(bytes, startingIndex, end));
			startingIndex += chunkSize;
		}

		return byteArrayChunks;
	}
}