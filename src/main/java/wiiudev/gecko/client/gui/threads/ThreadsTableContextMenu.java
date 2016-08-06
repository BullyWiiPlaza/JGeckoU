package wiiudev.gecko.client.gui.threads;

import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThread;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class ThreadsTableContextMenu extends JPopupMenu
{
	private ThreadsTableManager threadsTableManager;

	public ThreadsTableContextMenu(ThreadsTableManager threadsTableManager)
	{
		this.threadsTableManager = threadsTableManager;

		addToggleStateOption();
		addMemoryViewerOption();
	}

	private void addMemoryViewerOption()
	{
		JMenuItem memoryViewerOption = new JMenuItem("Memory Viewer");
		List<OSThread> osThreads = threadsTableManager.getSelectedItems();
		memoryViewerOption.setEnabled(osThreads.size() == 1);

		memoryViewerOption.addActionListener(actionEvent ->
				JGeckoUGUI.selectInMemoryViewer(osThreads.get(0).getAddress()));

		add(memoryViewerOption);
	}

	private void addToggleStateOption()
	{
		JMenuItem toggleThreadStateOption = new JMenuItem("Toggle State");

		toggleThreadStateOption.addActionListener(actionEvent ->
				// This can take longer so don't freeze the GUI
				new SwingWorker<String, String>()
				{
					@Override
					protected String doInBackground() throws Exception
					{
						try
						{
							List<OSThread> osThreads = threadsTableManager.getSelectedItems();
							int[] selectedRows = threadsTableManager.getSelectedRows();

							for (OSThread osThread : osThreads)
							{
								osThread.toggleState();
							}

							threadsTableManager.populateRows();
							threadsTableManager.setSelectedItems(selectedRows);
						} catch (IOException exception)
						{
							StackTraceUtils.handleException(null, exception);
						}

						return null;
					}
				}.execute());

		add(toggleThreadStateOption);
	}
}