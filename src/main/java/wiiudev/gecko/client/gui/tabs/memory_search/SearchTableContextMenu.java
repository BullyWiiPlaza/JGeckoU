package wiiudev.gecko.client.gui.tabs.memory_search;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.conversions.SystemClipboard;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.utilities.PopupMenuUtilities;
import wiiudev.gecko.client.memory_search.SearchResult;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
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
		KeyStroke pokeKeyStroke = PopupMenuUtilities.addOption(this, "Poke", "control K", actionEvent -> pokeValues());
		KeyStroke pokePreviousKeyStroke = PopupMenuUtilities.addOption(this, "Poke Previous", "control P", actionEvent -> pokePreviousValues());
		KeyStroke pokeCurrentKeyStroke = PopupMenuUtilities.addOption(this, "Poke Current", "control U", actionEvent -> pokeCurrentValues());
		KeyStroke copyAddressKeyStroke = PopupMenuUtilities.addOption(this, "Copy Address", "control R", actionEvent -> copySelectedAddress());
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
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, pokeKeyStroke))
					{
						pokeValues();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, pokePreviousKeyStroke))
					{
						pokePreviousValues();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, pokeCurrentKeyStroke))
					{
						pokeCurrentValues();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, copyAddressKeyStroke))
					{
						copySelectedAddress();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, deleteSelectedKeyStroke))
					{
						deleteSelectedRows();
					}
				}
			}
		});
	}

	private void copySelectedAddress()
	{
		List<SearchResult> searchResults = getSelectedSearchResults();
		SearchResult firstSearchResult = searchResults.get(0);
		SystemClipboard.copy(Conversions.toHexadecimal(firstSearchResult.getAddress(), 8));
	}

	private void pokeValues()
	{
		List<SearchResult> searchResults = getSelectedSearchResults();
		SearchResult firstSearchResult = searchResults.get(0);
		PokeValueDialog pokeValueDialog = new PokeValueDialog(firstSearchResult.getCurrentValue(),
				firstSearchResult.getValueSize());
		pokeValueDialog.setLocationRelativeTo(JGeckoUGUI.getInstance());
		pokeValueDialog.setVisible(true);

		if (pokeValueDialog.shouldPoke())
		{
			byte[] valueBytes = pokeValueDialog.getValueBytes();

			try
			{
				for (int searchResultIndex = 0; searchResultIndex < searchResults.size(); searchResultIndex++)
				{
					SearchResult searchResult = searchResults.get(searchResultIndex);

					int address = searchResult.getAddress();
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					byteArrayOutputStream.write(valueBytes);

					if (searchResultIndex < searchResults.size() - 1)
					{
						SearchResult nextSearchResult = searchResults.get(searchResultIndex + 1);

						// Optimize poking by reducing the amount of queries sent
						while (searchResult.getAddress()
								+ searchResult.getValueSize().getBytesCount()
								== nextSearchResult.getAddress())
						{
							byteArrayOutputStream.write(valueBytes);
							searchResult = nextSearchResult;
							searchResultIndex++;

							if (searchResultIndex == searchResults.size() - 1)
							{
								break;
							}

							nextSearchResult = searchResults.get(searchResultIndex + 1);
						}
					}

					MemoryPoke memoryPoke = new MemoryPoke(address, byteArrayOutputStream);
					memoryPoke.poke();
				}
			} catch (IOException exception)
			{
				StackTraceUtils.handleException(getRootPane(), exception);
			}
		}
	}

	private void switchToDisassembler()
	{
		SearchResult searchResult = getSelectedSearchResults().get(0);
		int address = searchResult.getAddress();
		JGeckoUGUI.selectInDisassembler(address);
	}

	private void deleteSelectedRows()
	{
		tableManager.deleteSelectedRows();
	}

	private void pokeCurrentValues()
	{
		List<SearchResult> searchResults = getSelectedSearchResults();

		try
		{
			for (int searchResultIndex = 0; searchResultIndex < searchResults.size(); searchResultIndex++)
			{
				SearchResult searchResult = searchResults.get(searchResultIndex);

				int address = searchResult.getAddress();
				ByteArrayOutputStream valueBytes = new ByteArrayOutputStream();
				valueBytes.write(searchResult.getCurrentValueBytes());

				if (searchResultIndex < searchResults.size() - 1)
				{
					SearchResult nextSearchResult = searchResults.get(searchResultIndex + 1);

					// Optimize poking by reducing the amount of queries sent
					while (searchResult.getAddress()
							+ searchResult.getValueSize().getBytesCount()
							== nextSearchResult.getAddress())
					{
						valueBytes.write(nextSearchResult.getCurrentValueBytes());
						searchResult = nextSearchResult;
						searchResultIndex++;

						if (searchResultIndex == searchResults.size() - 1)
						{
							break;
						}

						nextSearchResult = searchResults.get(searchResultIndex + 1);
					}
				}

				MemoryPoke memoryPoke = new MemoryPoke(address, valueBytes);
				memoryPoke.poke();
			}
		} catch (IOException exception)
		{
			StackTraceUtils.handleException(getRootPane(), exception);
		}
	}

	private List<SearchResult> getSelectedSearchResults()
	{
		return tableManager.getSelected();
	}

	private void pokePreviousValues()
	{
		List<SearchResult> searchResults = getSelectedSearchResults();

		try
		{
			for (int searchResultIndex = 0; searchResultIndex < searchResults.size(); searchResultIndex++)
			{
				SearchResult searchResult = searchResults.get(searchResultIndex);

				int address = searchResult.getAddress();
				ByteArrayOutputStream valueBytes = new ByteArrayOutputStream();
				valueBytes.write(searchResult.getPreviousValueBytes());

				if (searchResultIndex < searchResults.size() - 1)
				{
					SearchResult nextSearchResult = searchResults.get(searchResultIndex + 1);

					// Optimize poking by reducing the amount of queries sent
					while (searchResult.getAddress()
							+ searchResult.getValueSize().getBytesCount()
							== nextSearchResult.getAddress())
					{
						valueBytes.write(nextSearchResult.getPreviousValueBytes());
						searchResult = nextSearchResult;
						searchResultIndex++;

						if (searchResultIndex == searchResults.size() - 1)
						{
							break;
						}

						nextSearchResult = searchResults.get(searchResultIndex + 1);
					}
				}

				MemoryPoke memoryPoke = new MemoryPoke(address, valueBytes);
				memoryPoke.poke();
			}
		} catch (IOException exception)
		{
			StackTraceUtils.handleException(getRootPane(), exception);
		}
	}

	private void switchToMemoryViewer()
	{
		SearchResult searchResult = getSelectedSearchResults().get(0);
		int address = searchResult.getAddress();
		JGeckoUGUI.selectInMemoryViewer(address);
	}

	private static class MemoryPoke
	{
		private int address;
		private byte[] bytes;

		MemoryPoke(int address, ByteArrayOutputStream byteArrayOutputStream)
		{
			this(address, byteArrayOutputStream.toByteArray());
		}

		MemoryPoke(int address, byte[] bytes)
		{
			this.address = address;
			this.bytes = bytes;
		}

		void poke() throws IOException
		{
			MemoryWriter memoryWriter = new MemoryWriter();
			memoryWriter.writeBytes(address, bytes);
		}
	}
}