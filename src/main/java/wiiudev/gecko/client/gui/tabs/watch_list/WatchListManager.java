package wiiudev.gecko.client.gui.tabs.watch_list;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.conversions.SystemClipboard;
import wiiudev.gecko.client.gui.MemoryPointerExpression;
import wiiudev.gecko.client.gui.utilities.JTableUtilities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WatchListManager
{
	public static String assertionError = "ASSERTION ERROR";

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

	public synchronized void updateValues(JPanel watchListTab, String assertedValue) throws Exception
	{
		int rowCount = tableModel.getRowCount();

		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
		{
			if (watchListTab.isShowing()
					&& rowIndex < tableModel.getDataVector().size())
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

				if (!assertedValue.equals("")
						&& !readValue.equals(assertedValue))
				{
					valueFieldText = assertionError;
				}

				int index = watchListRow.size() - 1;

				// Prevent index errors
				if (table.getRowCount() > index - 1)
				{
					tableModel.setValueAt(valueFieldText, rowIndex, index);
				} else
				{
					break;
				}
			} else
			{
				break;
			}
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

	public WatchListElement getSelectedElement()
	{
		int selectedRow = table.getSelectedRow();
		Vector watchListRow = (Vector) tableModel.getDataVector().elementAt(selectedRow);

		return new WatchListElement(watchListRow);
	}

	public int copyAssertedMemoryPointerExpressions()
	{
		StringBuilder stringBuilder = new StringBuilder();
		int addressExpressionsAddedCount = 0;

		for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++)
		{
			Vector watchListRow = (Vector) tableModel.getDataVector().elementAt(rowIndex);
			MemoryPointerExpression memoryPointerExpression = (MemoryPointerExpression) watchListRow.get(1);
			String value = (String) watchListRow.get(3);

			if (!value.equals(assertionError))
			{
				stringBuilder.append(memoryPointerExpression.toString());
				stringBuilder.append(System.lineSeparator());

				addressExpressionsAddedCount++;
			}
		}

		SystemClipboard.copy(stringBuilder.toString().trim());

		return addressExpressionsAddedCount;
	}

	public List<WatchListElement> getElements()
	{
		List<WatchListElement> elements = new ArrayList<>();

		for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++)
		{
			Vector watchListRow = (Vector) tableModel.getDataVector().elementAt(rowIndex);
			WatchListElement watchListElement = new WatchListElement(watchListRow);
			elements.add(watchListElement);
		}

		return elements;
	}

	public void setRows(List<WatchListElement> elements)
	{
		JTableUtilities.deleteAllRows(table);
		elements.forEach(this::addRow);
	}
}