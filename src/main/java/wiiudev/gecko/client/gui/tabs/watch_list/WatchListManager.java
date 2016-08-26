package wiiudev.gecko.client.gui.tabs.watch_list;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.gui.MemoryPointerExpression;
import wiiudev.gecko.client.gui.utilities.JTableUtilities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WatchListManager
{
	private JTable table;
	private DefaultTableModel tableModel;

	public WatchListManager(JTable table)
	{
		tableModel = JTableUtilities.getDefaultTableModel();
		table.setModel(tableModel);
		this.table = table;
	}

	public void configure()
	{
		String[] columnHeaderNames = new String[]{"Name", "Address Expression", "Value Size", "Value"};
		JTableUtilities.configureTable(table, columnHeaderNames);
	}

	public void addRow(WatchListElement watchListElement)
	{
		tableModel.addRow(new Object[]{watchListElement.getName(), watchListElement.getAddressExpression(), watchListElement.getValueSize(), ""});
	}

	public boolean areMultipleRowsSelected()
	{
		return table.getSelectedRows().length > 1;
	}

	public void updateValues() throws Exception
	{
		int rowCount = tableModel.getRowCount();

		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
		{
			Vector watchListRow = (Vector) tableModel.getDataVector().elementAt(rowIndex);
			WatchListElement watchListElement = new WatchListElement(watchListRow);
			String readValue = watchListElement.readValue();
			MemoryPointerExpression addressExpression = watchListElement.getAddressExpression();
			String address = Conversions.toHexadecimal((int) addressExpression.getDestinationAddress(), 8);

			String valueFieldText = readValue;
			if (!addressExpression.isAddress())
			{
				valueFieldText = "[" + address + "] = " + valueFieldText;
			}

			tableModel.setValueAt(valueFieldText, rowIndex, watchListRow.size() - 1);
		}
	}

	public boolean isRowSelected()
	{
		return table.getSelectedRow() != -1;
	}

	public boolean rowExists()
	{
		return tableModel.getRowCount() > 0;
	}

	public WatchListElement getSelectedWatchListElement()
	{
		int selectedRow = table.getSelectedRow();
		Vector watchListRow = (Vector) tableModel.getDataVector().elementAt(selectedRow);

		return new WatchListElement(watchListRow);
	}

	public List<WatchListElement> getWatchListElements()
	{
		List<WatchListElement> watchListElements = new ArrayList<>();

		for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++)
		{
			Vector watchListRow = (Vector) tableModel.getDataVector().elementAt(rowIndex);
			WatchListElement watchListElement = new WatchListElement(watchListRow);
			watchListElements.add(watchListElement);
		}

		return watchListElements;
	}

	public void setRows(List<WatchListElement> watchListElements)
	{
		JTableUtilities.deleteAllRows(table);
		watchListElements.forEach(this::addRow);
	}
}