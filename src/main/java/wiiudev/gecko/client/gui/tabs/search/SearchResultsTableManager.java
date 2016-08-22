package wiiudev.gecko.client.gui.tabs.search;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.gui.utilities.JTableUtilities;
import wiiudev.gecko.client.memory_search.SearchResult;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

public class SearchResultsTableManager
{
	private JTable table;
	private List<SearchResult> searchResults;

	public SearchResultsTableManager(JTable table)
	{
		DefaultTableModel tableModel = JTableUtilities.getDefaultTableModel();
		table.setModel(tableModel);
		this.table = table;
	}

	public void configure()
	{
		String[] columnHeaderNames = new String[]{"Address", "Previous", "Current", "Difference"};
		JTableUtilities.configureTable(table, columnHeaderNames);
		addContextMenuListener();
	}

	private void addContextMenuListener()
	{
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				handleContextMenu(mouseEvent);
			}
		});
	}

	private void handleContextMenu(MouseEvent mouseEvent)
	{
		int rowIndex = table.getSelectedRow();

		if (rowIndex >= 0)
		{
			if (mouseEvent.isPopupTrigger() &&
					mouseEvent.getComponent() instanceof JTable
					&& TCPGecko.isConnected())
			{
				SearchTableContextMenu contextMenu = new SearchTableContextMenu(this);
				contextMenu.addContextMenu();

				contextMenu.show(mouseEvent.getComponent(),
						mouseEvent.getX(),
						mouseEvent.getY());
			}
		}
	}

	public void populateSearchResults(List<SearchResult> searchResults)
	{
		this.searchResults = searchResults;
		removeAllRows();

		// Do not populate the table when results are numerous
		if (searchResults.size() < 99999)
		{
			searchResults.forEach(this::addRow);
		}
	}

	private void addRow(SearchResult searchResult)
	{

		Object[] objects = new Object[]{new Hexadecimal(searchResult.getAddress(), 8),
				Conversions.toHexadecimal(searchResult.getPreviousValue(), searchResult.getValueSize()),
				Conversions.toHexadecimal(searchResult.getCurrentValue(), searchResult.getValueSize()),
				Conversions.toHexadecimal(searchResult.getValueDifference(), searchResult.getValueSize())};
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		tableModel.addRow(objects);
	}

	public void removeAllRows()
	{
		JTableUtilities.deleteAllRows(table);
	}

	public boolean areSearchResultsEmpty()
	{
		return searchResults.isEmpty();
	}

	public List<SearchResult> getSelected()
	{
		int[] selectedRows = table.getSelectedRows();

		List<SearchResult> selectedSearchResults = new LinkedList<>();

		for (int selectedRow : selectedRows)
		{
			SearchResult selectedSearchResult = searchResults.get(selectedRow);
			selectedSearchResults.add(selectedSearchResult);
		}

		return selectedSearchResults;
	}

	public JTable getTable()
	{
		return table;
	}

	public List<SearchResult> getSearchResults()
	{
		return searchResults;
	}

	public void clearSearchResults()
	{
		searchResults.clear();
	}
}