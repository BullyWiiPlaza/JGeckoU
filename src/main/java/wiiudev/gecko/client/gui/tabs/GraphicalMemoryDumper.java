package wiiudev.gecko.client.gui.tabs;

import org.apache.commons.io.FileUtils;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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
				MemoryReader memoryReader = new MemoryReader();
				long bytesDumped = 0;

				// Delete the target file before dumping into it
				FileUtils.deleteQuietly(targetFile);

				memoryReader.requestBytes(address, bytesCount);

				// Read in chunks
				while (bytesCount > TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE)
				{
					byte[] retrievedBytes = memoryReader.readBytes(TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE);
					FileUtils.writeByteArrayToFile(targetFile, retrievedBytes, true);

					bytesCount -= TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE;
					address += TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE;
					bytesDumped += TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE;

					long progress = bytesDumped * 100 / startingBytesCount;
					setProgress((int) progress);
					publish(bytesDumped);
				}

				if (bytesCount <= TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE)
				{
					byte[] retrievedBytes = memoryReader.readBytes(bytesCount);
					FileUtils.writeByteArrayToFile(targetFile, retrievedBytes, true);
					bytesDumped += retrievedBytes.length;
				}

				long progress = bytesDumped * 100 / startingBytesCount;
				setProgress((int) progress);

				Toolkit.getDefaultToolkit().beep();

				return bytesDumped;
			} catch (Exception exception)
			{
				StackTraceUtils.handleException(JGeckoUGUI.getInstance().getRootPane(), exception);
			} finally
			{
				TCPGecko.isRequestingBytes = false;
				dumpMemoryButton.setText(dumpText);
				dumpMemoryButton.setEnabled(true);
			}

			return null;
		}
	}
}