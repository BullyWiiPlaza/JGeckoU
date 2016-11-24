package wiiudev.gecko.client.tcpgecko;

import lzma.sdk.lzma.Decoder;
import lzma.streams.LzmaInputStream;
import lzma.streams.LzmaOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.file.Path;

public class LzmaCompressor
{
	private Path rawFilePath;
	private Path compressedFilePath;

	public LzmaCompressor(Path rawFilePath, Path compressedFilePath)
	{
		this.rawFilePath = rawFilePath;
		this.compressedFilePath = compressedFilePath;
	}

	public void compress() throws IOException
	{
		try (LzmaOutputStream outputStream = new LzmaOutputStream.Builder(
				new BufferedOutputStream(new FileOutputStream(compressedFilePath.toFile())))
				.useMaximalDictionarySize()
				.useMaximalFastBytes()
				.build();
		     InputStream inputStream = new BufferedInputStream(new FileInputStream(rawFilePath.toFile())))
		{
			IOUtils.copy(inputStream, outputStream);
		}
	}

	public void decompress() throws IOException
	{
		try (LzmaInputStream inputStream = new LzmaInputStream(
				new BufferedInputStream(new FileInputStream(compressedFilePath.toFile())),
				new Decoder());
		     OutputStream outputStream = new BufferedOutputStream(
				     new FileOutputStream(rawFilePath.toFile())))
		{
			IOUtils.copy(inputStream, outputStream);
		}
	}
}