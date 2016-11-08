package wiiudev.gecko.client.gui.tabs.memory_search;

import org.apache.commons.io.output.ByteArrayOutputStream;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.tcpgecko.main.CloseableReentrantLock;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class GraphicalSearcher
{
	private int address;
	private int length;
	private byte[] dumpedBytes;

	public GraphicalSearcher(int address, int length)
	{
		this.address = address;
		this.length = length;
	}

	public byte[] dumpMemory() throws ExecutionException, InterruptedException
	{
		MemoryDumpingTask task = new MemoryDumpingTask();

		task.execute();

		// Wait till done
		task.get();

		if (dumpedBytes == null)
		{
			throw new IllegalStateException("Failed dumping bytes for the search");
		}

		return dumpedBytes;
	}

	private class MemoryDumpingTask extends SwingWorker<Long, Object>
	{
		@Override
		protected Long doInBackground() throws Exception
		{
			long startingBytesCount = length;

			try
			{
				long bytesDumped = 0;
				MemoryReader memoryReader = new MemoryReader();
				ByteArrayOutputStream dumpedBytesStream = new ByteArrayOutputStream();

				try (CloseableReentrantLock ignored = TCPGecko.reentrantLock.acquire())
				{
					memoryReader.requestBytes(address, length);

					// Read in chunks
					while (length > 0)
					{
						int bytesToRead = Math.min(length, TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE);
						byte[] readBytes = memoryReader.readBytes(bytesToRead);
						dumpedBytesStream.write(readBytes);

						length -= bytesToRead;
						bytesDumped += bytesToRead;

						if (bytesDumped % TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE * 10 == 0)
						{
							ProgressVisualization.updateProgress("Dumped Bytes", bytesDumped, startingBytesCount);
						}

						if (JGeckoUGUI.getInstance().isDumpingCanceled())
						{
							return null;
						}
					}

					ProgressVisualization.deleteUpdateLabel();
				}

				dumpedBytes = dumpedBytesStream.toByteArray();

				return bytesDumped;
			} catch (Exception exception)
			{
				JRootPane rootPane = JGeckoUGUI.getInstance().getRootPane();
				StackTraceUtils.handleException(rootPane, exception);
			} finally
			{
				TCPGecko.hasRequestedBytes = false;
			}

			return null;
		}
	}
}