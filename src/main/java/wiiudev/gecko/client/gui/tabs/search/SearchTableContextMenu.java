package wiiudev.gecko.client.gui.tabs.search;

import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.utilities.PopupMenuUtilities;
import wiiudev.gecko.client.search.SearchResult;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SearchTableContextMenu extends JPopupMenu
{
	private SearchResultsTableManager tableManager;

	public SearchTableContextMenu(SearchResultsTableManager tableManager)
	{
		this.tableManager = tableManager;
	}

	public void addContextMenu()
	{
		KeyStroke memoryViewerKeyStroke = PopupMenuUtilities.addOption(this, "Memory Viewer", "control M", actionEvent -> switchToMemoryViewer());

		JTable table = tableManager.getTable();
		table.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent pressedEvent)
			{
				if (TCPGecko.isConnected())
				{
					if (PopupMenuUtilities.keyEventPressed(pressedEvent, memoryViewerKeyStroke))
					{
						switchToMemoryViewer();
					}
				}
			}
		});
	}

	private void switchToMemoryViewer()
	{
		SearchResult searchResult = tableManager.getSelected();
		int address = searchResult.getAddress();
		JGeckoUGUI.selectInMemoryViewer(address);
	}
}