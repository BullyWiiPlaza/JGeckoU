package wiiudev.gecko.client.gui.watch_list;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WatchListManager
{
	private JTable table;
	private DefaultTableModel tableModel;

	public WatchListManager(JTable table)
	{
		tableModel = new DefaultTableModel()
		{
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		this.table = table;
	}

	public void configure(JTable table)
	{
		Object[] columnHeaders = new Object[]{"Name", "Address Expression", "Value Size", "Value"};
		tableModel.setColumnCount(columnHeaders.length);
		tableModel.setColumnIdentifiers(columnHeaders);
		setHeaderAlignment();

		table.setModel(tableModel);
		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setReorderingAllowed(false);
		tableHeader.setResizingAllowed(false);
		tableHeader.setVisible(true);
		setCellsAlignment();
	}

	private void setHeaderAlignment()
	{
		JTableHeader header = table.getTableHeader();
		header.setDefaultRenderer(new HeaderRenderer(table));
	}

	public void addRow(WatchListElement watchListElement)
	{
		tableModel.addRow(new Object[]{watchListElement.getName(), watchListElement.getAddressExpression(), watchListElement.getValueSize(), ""});
	}

	public void deleteSelectedRows()
	{
		DefaultTableModel model = (DefaultTableModel) this.table.getModel();
		int[] rows = table.getSelectedRows();

		for (int rowIndex = 0; rowIndex < rows.length; rowIndex++)
		{
			model.removeRow(rows[rowIndex] - rowIndex);
		}
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
			Thread.sleep(10);
			tableModel.setValueAt(readValue, rowIndex, watchListRow.size() - 1);
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

	private void setCellsAlignment()
	{
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.CENTER);

		for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++)
		{
			table.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
		}
	}

	private void setSelected(int rowIndex)
	{
		table.setRowSelectionInterval(rowIndex, rowIndex);
	}

	public WatchListElement getSelectedWatchListElement()
	{
		int selectedRow = table.getSelectedRow();
		Vector watchListRow = (Vector) tableModel.getDataVector().elementAt(selectedRow);

		return new WatchListElement(watchListRow);
	}

	public void deleteAllRows()
	{
		DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();
		int rowCount = defaultTableModel.getRowCount();

		// Remove rows one by one from the end of the table
		for (int rowIndex = rowCount - 1; rowIndex >= 0; rowIndex--)
		{
			defaultTableModel.removeRow(rowIndex);
		}
	}

	public List<WatchListElement> getWatchListElements()
	{
		List<WatchListElement> watchListElements = new ArrayList<>();

		for (int i = 0; i < table.getRowCount(); i++)
		{
			Vector watchListRow = (Vector) tableModel.getDataVector().elementAt(i);
			WatchListElement watchListElement = new WatchListElement(watchListRow);
			watchListElements.add(watchListElement);
		}

		return watchListElements;
	}

	public void setRows(List<WatchListElement> watchListElements)
	{
		deleteAllRows();
		watchListElements.forEach(this::addRow);
	}
}