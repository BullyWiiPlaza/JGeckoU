package wiiudev.gecko.client.gui.tabs.search;

import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.utilities.PopupMenuUtilities;
import wiiudev.gecko.client.memory_search.SearchResult;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

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
		KeyStroke pokePreviousKeyStroke = PopupMenuUtilities.addOption(this, "Poke Previous", "control P", actionEvent -> pokePreviousValues());

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
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, pokePreviousKeyStroke))
					{
						pokePreviousValues();
					}
				}
			}
		});
	}

	private void pokePreviousValues()
	{
		List<SearchResult> searchResults = tableManager.getSelected();
		MemoryWriter memoryWriter = new MemoryWriter();

		for (SearchResult searchResult : searchResults)
		{
			try
			{
				int address = searchResult.getAddress();
				byte[] previousValue = searchResult.getPreviousValueBytes();
				memoryWriter.writeBytes(address, previousValue);
			} catch (IOException exception)
			{
				StackTraceUtils.handleException(getRootPane(), exception);
			}
		}
	}

	private void switchToMemoryViewer()
	{
		SearchResult searchResult = tableManager.getSelected().get(0);
		int address = searchResult.getAddress();
		JGeckoUGUI.selectInMemoryViewer(address);
	}
}