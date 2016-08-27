package wiiudev.gecko.client.gui.tabs;

import org.apache.commons.io.output.ByteArrayOutputStream;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

public class GraphicalMemoryDumper
{
	private int address;
	private int bytesCount;
	private File targetFile;
	private JProgressBar progressBar;
	private JButton dumpMemoryButton;

	public GraphicalMemoryDumper(int address, int bytesCount, File targetFile, JProgressBar progressBar, JButton dumpMemoryButton)
	{
		this.address = address;
		this.bytesCount = bytesCount;
		this.targetFile = targetFile;
		this.progressBar = progressBar;
		this.dumpMemoryButton = dumpMemoryButton;
	}

	public void dumpMemory()
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
	}

	private class MemoryDumpingTask extends SwingWorker<Long, Object>
	{
		@Override
		protected Long doInBackground() throws Exception
		{
			progressBar.setValue(0);
			String dumpText = dumpMemoryButton.getText();
			dumpMemoryButton.setText("Dumping...");
			dumpMemoryButton.setEnabled(false);
			long startingBytesCount = bytesCount;

			try
			{
				long bytesDumped = 0;
				MemoryReader memoryReader = new MemoryReader();
				ByteArrayOutputStream memoryDumpBuffer = new ByteArrayOutputStream();

				try
				{
					TCPGecko.reentrantLock.lock();

					memoryReader.requestBytes(address, bytesCount);

					// Read in chunks
					while (true)
					{
						int bytesToDump = Math.min(bytesCount, TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE);
						byte[] retrievedBytes = memoryReader.readBytes(bytesToDump);
						memoryDumpBuffer.write(retrievedBytes);

						bytesCount -= bytesToDump;
						address += bytesToDump;
						bytesDumped += bytesToDump;

						long progress = bytesDumped * 100 / startingBytesCount;
						setProgress((int) progress);
						publish(bytesDumped);

						if(bytesCount == 0)
						{
							break;
						}
					}
				} finally
				{
					TCPGecko.reentrantLock.unlock();
				}

				Files.write(targetFile.toPath(), memoryDumpBuffer.toByteArray());

				long progress = bytesDumped * 100 / startingBytesCount;
				setProgress((int) progress);

				Toolkit.getDefaultToolkit().beep();

				return bytesDumped;
			} catch (Exception exception)
			{
				StackTraceUtils.handleException(JGeckoUGUI.getInstance().getRootPane(), exception);
			} finally
			{
				TCPGecko.hasRequestedBytes = false;
				dumpMemoryButton.setText(dumpText);
				dumpMemoryButton.setEnabled(true);
			}

			return null;
		}
	}
}