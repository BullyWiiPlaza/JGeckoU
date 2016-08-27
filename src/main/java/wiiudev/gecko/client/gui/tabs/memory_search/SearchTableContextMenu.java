package wiiudev.gecko.client.gui.tabs.memory_search;

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
		KeyStroke disassemblerKeyStroke = PopupMenuUtilities.addOption(this, "Disassembler", "control D", actionEvent -> switchToDisassembler());
		KeyStroke pokePreviousKeyStroke = PopupMenuUtilities.addOption(this, "Poke Previous", "control P", actionEvent -> pokePreviousValues());
		KeyStroke pokeCurrentKeyStroke = PopupMenuUtilities.addOption(this, "Poke Current", "control U", actionEvent -> pokeCurrentValues());
		KeyStroke deleteSelectedKeyStroke = PopupMenuUtilities.addOption(this, "Delete", "control D", actionEvent -> deleteSelectedRows());

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
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, disassemblerKeyStroke))
					{
						switchToDisassembler();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, pokePreviousKeyStroke))
					{
						pokePreviousValues();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, pokeCurrentKeyStroke))
					{
						pokeCurrentValues();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, deleteSelectedKeyStroke))
					{
						deleteSelectedRows();
					}
				}
			}
		});
	}

	private void switchToDisassembler()
	{
		SearchResult searchResult = tableManager.getSelected().get(0);
		int address = searchResult.getAddress();
		JGeckoUGUI.selectInDisassembler(address);
	}

	private void deleteSelectedRows()
	{
		tableManager.deleteSelectedRows();
	}

	private void pokeCurrentValues()
	{
		List<SearchResult> searchResults = tableManager.getSelected();

		try
		{
			for (SearchResult searchResult : searchResults)
			{
				poke(searchResult, searchResult.getCurrentValueBytes());
			}
		} catch (IOException exception)
		{
			StackTraceUtils.handleException(getRootPane(), exception);
		}
	}

	private void pokePreviousValues()
	{
		List<SearchResult> searchResults = tableManager.getSelected();

		try
		{
			for (SearchResult searchResult : searchResults)
			{
				poke(searchResult, searchResult.getPreviousValueBytes());
			}
		} catch (IOException exception)
		{
			StackTraceUtils.handleException(getRootPane(), exception);
		}
	}

	private void poke(SearchResult searchResult, byte[] value) throws IOException
	{
		MemoryWriter memoryWriter = new MemoryWriter();
		int address = searchResult.getAddress();
		memoryWriter.writeBytes(address, value);
	}

	private void switchToMemoryViewer()
	{
		SearchResult searchResult = tableManager.getSelected().get(0);
		int address = searchResult.getAddress();
		JGeckoUGUI.selectInMemoryViewer(address);
	}
}