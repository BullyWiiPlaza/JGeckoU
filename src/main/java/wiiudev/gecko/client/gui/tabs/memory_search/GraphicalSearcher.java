package wiiudev.gecko.client.gui.tabs.memory_search;

import org.apache.commons.io.output.ByteArrayOutputStream;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class GraphicalSearcher
{
	private int address;
	private int length;
	private JProgressBar progressBar;
	private byte[] dumpedBytes;

	public GraphicalSearcher(int address, int length, JProgressBar progressBar)
	{
		this.address = address;
		this.length = length;
		this.progressBar = progressBar;
	}

	public byte[] dumpMemory() throws ExecutionException, InterruptedException
	{
		MemoryDumpingTask task = new MemoryDumpingTask();

		task.addPropertyChangeListener(changeEvent ->
		{
			if ("progress".equals(changeEvent.getPropertyName()))
			{
				int newValue = (Integer) changeEvent.getNewValue();
				progressBar.setValue(newValue);
			}
		});

		task.execute();

		// Wait till done
		task.get();

		return dumpedBytes;
	}

	private class MemoryDumpingTask extends SwingWorker<Long, Object>
	{
		@Override
		protected Long doInBackground() throws Exception
		{
			progressBar.setValue(0);
			long startingBytesCount = length;

			try
			{
				MemoryReader memoryReader = new MemoryReader();
				long bytesDumped = 0;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				TCPGecko.reentrantLock.lock();

				try
				{
					memoryReader.requestBytes(address, length);

					// Read in chunks
					while (length > TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE)
					{
						byte[] retrievedBytes = memoryReader.readBytes(TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE);
						byteArrayOutputStream.write(retrievedBytes);

						length -= TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE;
						address += TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE;
						bytesDumped += TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE;

						long progress = bytesDumped * 100 / startingBytesCount;
						setProgress((int) progress);
						publish(bytesDumped);
					}

					if (length <= TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE)
					{
						byte[] retrievedBytes = memoryReader.readBytes(length);
						byteArrayOutputStream.write(retrievedBytes);
						bytesDumped += retrievedBytes.length;
					}
				} finally
				{
					TCPGecko.reentrantLock.unlock();
				}

				long progress = bytesDumped * 100 / startingBytesCount;
				setProgress((int) progress);

				dumpedBytes = byteArrayOutputStream.toByteArray();

				return bytesDumped;
			} catch (Exception exception)
			{
				StackTraceUtils.handleException(JGeckoUGUI.getInstance().getRootPane(), exception);
			} finally
			{
				TCPGecko.hasRequestedBytes = false;
			}

			return null;
		}
	}
}