package wiiudev.gecko.client.gui;

import org.apache.commons.io.FileUtils;
import wiiudev.gecko.client.connector.MemoryReader;
import wiiudev.gecko.client.connector.SocketCommunication;
import wiiudev.gecko.client.gui.utilities.Benchmark;

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
			Benchmark benchmark = new Benchmark();
			benchmark.start();
			long startingBytesCount = bytesCount;

			try
			{
				MemoryReader memoryReader = new MemoryReader();
				long bytesDumped = 0;

				// Delete the target file before dumping into it
				FileUtils.deleteQuietly(targetFile);

				// Read in chunks
				while (bytesCount > SocketCommunication.MAXIMUM_MEMORY_CHUNK_SIZE)
				{
					byte[] retrievedBytes = memoryReader.readBytes(address, SocketCommunication.MAXIMUM_MEMORY_CHUNK_SIZE);
					FileUtils.writeByteArrayToFile(targetFile, retrievedBytes, true);

					bytesCount -= SocketCommunication.MAXIMUM_MEMORY_CHUNK_SIZE;
					address += SocketCommunication.MAXIMUM_MEMORY_CHUNK_SIZE;
					bytesDumped += SocketCommunication.MAXIMUM_MEMORY_CHUNK_SIZE;
					long progress = bytesDumped * 100 / startingBytesCount;
					setProgress((int) progress);
					publish(bytesDumped);
				}

				if (bytesCount <= SocketCommunication.MAXIMUM_MEMORY_CHUNK_SIZE)
				{
					byte[] retrievedBytes = memoryReader.readBytes(address, bytesCount);
					FileUtils.writeByteArrayToFile(targetFile, retrievedBytes, true);
					bytesDumped += retrievedBytes.length;
				}

				long progress = bytesDumped * 100 / startingBytesCount;
				setProgress((int) progress);

				return bytesDumped;
			} catch (Exception exception)
			{
				exception.printStackTrace();
			} finally
			{
				dumpMemoryButton.setText(dumpText);
				dumpMemoryButton.setEnabled(true);
				double elapsedSeconds = benchmark.getElapsedTime();
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(null,
						"Dumped " + startingBytesCount + " bytes after " + elapsedSeconds + " second(s)",
						"Success",
						JOptionPane.INFORMATION_MESSAGE);
			}

			return null;
		}
	}
}