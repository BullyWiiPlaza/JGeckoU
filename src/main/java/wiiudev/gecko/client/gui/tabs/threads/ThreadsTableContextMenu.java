package wiiudev.gecko.client.gui.tabs.threads;

import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThread;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

public class ThreadsTableContextMenu extends JPopupMenu
{
	private ThreadsTableManager threadsTableManager;

	public ThreadsTableContextMenu(ThreadsTableManager threadsTableManager)
	{
		this.threadsTableManager = threadsTableManager;
	}

	public void addContextMenu()
	{
		JMenuItem toggleThreadStateOption = new JMenuItem("Toggle State");
		KeyStroke toggleStateKeyStroke = KeyStroke.getKeyStroke("control S");
		toggleThreadStateOption.setAccelerator(toggleStateKeyStroke);
		toggleThreadStateOption.addActionListener(actionEvent ->
				toggleThreadStateConcurrently());
		add(toggleThreadStateOption);

		JMenuItem memoryViewerOption = new JMenuItem("Memory Viewer");
		List<OSThread> osThreads = threadsTableManager.getSelectedItems();
		memoryViewerOption.setEnabled(osThreads.size() == 1);
		KeyStroke memoryViewerKeyStroke = KeyStroke.getKeyStroke("control M");
		memoryViewerOption.setAccelerator(memoryViewerKeyStroke);
		memoryViewerOption.addActionListener(actionEvent ->
				switchToMemoryViewer());
		add(memoryViewerOption);

		JMenuItem setNameOption = new JMenuItem("Change Name");
		KeyStroke setNameKeyStroke = KeyStroke.getKeyStroke("control N");
		setNameOption.setAccelerator(setNameKeyStroke);
		setNameOption.addActionListener(actionEvent -> displayChangeThreadNameDialog());
		add(setNameOption);

		JTable table = threadsTableManager.getTable();
		table.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent pressedEvent)
			{
				if (TCPGecko.isConnected())
				{
					if (keyEventPressed(pressedEvent, memoryViewerKeyStroke.getKeyCode()))
					{
						switchToMemoryViewer();
					}
					else if(keyEventPressed(pressedEvent, toggleStateKeyStroke.getKeyCode()))
					{
						toggleThreadStateConcurrently();
					}
					else if(keyEventPressed(pressedEvent, setNameKeyStroke.getKeyCode()))
					{
						displayChangeThreadNameDialog();
					}
				}
			}
		});
	}

	private void displayChangeThreadNameDialog()
	{
		JGeckoUGUI jGeckoUGUI = JGeckoUGUI.getInstance();
		OSThread thread = jGeckoUGUI.getSelectedThread();
		ChangeThreadNameDialog changeThreadNameDialog = new ChangeThreadNameDialog(thread);
		changeThreadNameDialog.setLocationRelativeTo(jGeckoUGUI.getRootPane());
		changeThreadNameDialog.setVisible(true);

		if(changeThreadNameDialog.isConfirmed())
		{
			String name = changeThreadNameDialog.getName();

			try
			{
				thread.setName(name);
				jGeckoUGUI.updateThreads(false);
			} catch (Exception exception)
			{
				exception.printStackTrace();
			}
		}
	}

	private void toggleThreadStateConcurrently()
	{
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
		}.execute();
	}

	private void switchToMemoryViewer()
	{
		List<OSThread> osThreads = threadsTableManager.getSelectedItems();
		JGeckoUGUI.selectInMemoryViewer(osThreads.get(0).getAddress());
	}

	private boolean keyEventPressed(KeyEvent event, int targetKeyCode)
	{
		return event.getKeyCode() == targetKeyCode && (event.getModifiers() & KeyEvent.CTRL_MASK) != 0;
	}
}