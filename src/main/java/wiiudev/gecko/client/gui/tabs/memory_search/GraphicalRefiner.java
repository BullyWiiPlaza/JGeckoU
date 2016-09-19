package wiiudev.gecko.client.gui.tabs.memory_search;

import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.memory_search.SearchQueryOptimizer;

import javax.swing.*;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GraphicalRefiner
{
	private int address;
	private int length;
	private List<SearchQueryOptimizer.MemoryDumpingChunk> memoryDumpingChunks;
	private int bytesToDump;
	private JProgressBar progressBar;

	private byte[] dumpedBytes;

	public GraphicalRefiner(int address, int length, List<SearchQueryOptimizer.MemoryDumpingChunk> memoryDumpingChunks, int bytesToDump, JProgressBar progressBar)
	{
		this.address = address;
		this.length = length;
		this.memoryDumpingChunks = memoryDumpingChunks;
		this.bytesToDump = bytesToDump;
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

			try
			{
				long bytesDumped = 0;
				ByteBuffer dumpedBytesBuffer = ByteBuffer.allocate(length);

				for (SearchQueryOptimizer.MemoryDumpingChunk memoryDumpingChunk : memoryDumpingChunks)
				{
					byte[] dumped = memoryDumpingChunk.dump();
					int currentAddress = memoryDumpingChunk.getAddress();
					int bufferIndex = currentAddress - address;

					dumpedBytesBuffer.position(bufferIndex);
					dumpedBytesBuffer.put(dumped);

					bytesDumped += dumped.length;

					long progress = bytesDumped * 100 / bytesToDump;
					setProgress((int) progress);
					publish(bytesDumped);

					if (JGeckoUGUI.getInstance().isDumpingCanceled())
					{
						return null;
					}
				}

				dumpedBytes = dumpedBytesBuffer.array();

				return bytesDumped;
			} catch (Exception exception)
			{
				JRootPane rootPane = JGeckoUGUI.getInstance().getRootPane();
				StackTraceUtils.handleException(rootPane, exception);
			}

			return null;
		}
	}
}