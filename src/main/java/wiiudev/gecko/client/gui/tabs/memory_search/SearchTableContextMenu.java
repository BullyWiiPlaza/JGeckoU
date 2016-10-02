package wiiudev.gecko.client.gui.tabs.memory_search;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.conversions.SystemClipboard;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.utilities.JTableUtilities;
import wiiudev.gecko.client.gui.utilities.PopupMenuUtilities;
import wiiudev.gecko.client.memory_search.SearchResult;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
		JTableUtilities.removeAllKeyListeners(table);
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
			pokeValues(valueBytes);
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
		pokeValues(SearchResult::getCurrentValueBytes);
	}

	private List<SearchResult> getSelectedSearchResults()
	{
		return tableManager.getSelectedSearchResults();
	}

	private void pokePreviousValues()
	{
		pokeValues(SearchResult::getPreviousValueBytes);
	}

	private void pokeValues(byte[] valueBytes)
	{
		new SwingWorker<String, String>()
		{
			@Override
			protected String doInBackground() throws Exception
			{
				List<SearchResult> searchResults = getSelectedSearchResults();

				try
				{
					List<MemoryPoke> memoryPokes = new ArrayList<>();
					int searchResultsCount = searchResults.size();

					for (int searchResultIndex = 0; searchResultIndex < searchResultsCount; searchResultIndex++)
					{
						SearchResult searchResult = searchResults.get(searchResultIndex);

						int address = searchResult.getAddress();
						ByteArrayOutputStream valueBytesStream = new ByteArrayOutputStream();
						valueBytesStream.write(valueBytes);

						if (searchResultIndex < searchResults.size() - 1)
						{
							SearchResult nextSearchResult = searchResults.get(searchResultIndex + 1);

							// Optimize poking by reducing the amount of queries sent
							while (searchResult.getAddress()
									+ searchResult.getValueSize().getBytesCount()
									== nextSearchResult.getAddress())
							{
								valueBytesStream.write(valueBytes);
								searchResult = nextSearchResult;
								searchResultIndex++;

								if (searchResultIndex == searchResults.size() - 1)
								{
									break;
								}

								nextSearchResult = searchResults.get(searchResultIndex + 1);
							}
						}

						MemoryPoke memoryPoke = new MemoryPoke(address, valueBytesStream);
						memoryPokes.add(memoryPoke);
						updateProgress("Evaluated", searchResultIndex, searchResultsCount);
					}

					int totalPokesCount = memoryPokes.size();
					int memoryPokeIndex = 0;

					for (MemoryPoke memoryPoke : memoryPokes)
					{
						memoryPoke.poke();
						memoryPokeIndex++;

						updateProgress("Poked", memoryPokeIndex, totalPokesCount);
					}
				} catch (Exception exception)
				{
					StackTraceUtils.handleException(getRootPane(), exception);
				}

				return null;
			}
		}.execute();
	}

	private void updateProgress(String labelName, int memoryPokeIndex, int totalPokesCount)
	{
		updateProgressBar(memoryPokeIndex, totalPokesCount);
		updateLabel(labelName, memoryPokeIndex, totalPokesCount);
	}

	private void updateProgressBar(int memoryPokeIndex, int totalPokesCount)
	{
		JProgressBar progressBar = JGeckoUGUI.getInstance().getSearchProgressBar();
		int progress = memoryPokeIndex * 100 / totalPokesCount;
		progressBar.setValue(progress);
	}

	private void updateLabel(String labelName, int memoryPokeIndex, int totalPokesCount)
	{
		JLabel addressProgressLabel = JGeckoUGUI.getInstance().getAddressProgressLabel();

		if (memoryPokeIndex == totalPokesCount)
		{
			addressProgressLabel.setText("");
		}
		else
		{
			addressProgressLabel.setText(labelName + ": " + memoryPokeIndex + "/" + totalPokesCount);
		}
	}

	private void pokeValues(ValueBytesGetter valueBytesGetter)
	{
		List<SearchResult> searchResults = getSelectedSearchResults();

		try
		{
			for (int searchResultIndex = 0; searchResultIndex < searchResults.size(); searchResultIndex++)
			{
				SearchResult searchResult = searchResults.get(searchResultIndex);

				int address = searchResult.getAddress();
				ByteArrayOutputStream valueBytes = new ByteArrayOutputStream();
				valueBytes.write(valueBytesGetter.getValueBytes(searchResult));

				if (searchResultIndex < searchResults.size() - 1)
				{
					SearchResult nextSearchResult = searchResults.get(searchResultIndex + 1);

					// Optimize poking by reducing the amount of queries sent
					while (searchResult.getAddress()
							+ searchResult.getValueSize().getBytesCount()
							== nextSearchResult.getAddress())
					{
						valueBytes.write(valueBytesGetter.getValueBytes(searchResult));
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
		private MemoryWriter memoryWriter;

		private int address;
		private byte[] bytes;

		MemoryPoke(int address, ByteArrayOutputStream valueBytesStream)
		{
			this(address, valueBytesStream.toByteArray());
		}

		MemoryPoke(int address, byte[] bytes)
		{
			memoryWriter = new MemoryWriter();
			this.address = address;
			this.bytes = bytes;
		}

		void poke() throws IOException
		{
			memoryWriter.writeBytes(address, bytes);
		}
	}

	@FunctionalInterface
	private interface ValueBytesGetter
	{
		byte[] getValueBytes(SearchResult searchResult) throws IOException;
	}
}