package wiiudev.gecko.client.gui.utilities;

import wiiudev.gecko.client.gui.tabs.watch_list.HeaderRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

public class JTableUtilities
{
	public static void setCellsAlignment(JTable table, int alignment)
	{
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(alignment);

		TableModel tableModel = table.getModel();

		for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++)
		{
			table.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
		}
	}

	public static void setHeaderAlignment(JTable table)
	{
		JTableHeader header = table.getTableHeader();
		HeaderRenderer headerRenderer = new HeaderRenderer(table);
		header.setDefaultRenderer(headerRenderer);
	}

	public static void deleteSelectedRows(JTable table)
	{
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int[] rows = table.getSelectedRows();

		for (int rowIndex = 0; rowIndex < rows.length; rowIndex++)
		{
			model.removeRow(rows[rowIndex] - rowIndex);
		}
	}

	public static void deleteAllRows(JTable table)
	{
		DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();
		int rowCount = defaultTableModel.getRowCount();

		// Remove rows one by one from the end of the table
		for (int rowIndex = rowCount - 1; rowIndex >= 0; rowIndex--)
		{
			defaultTableModel.removeRow(rowIndex);
		}
	}

	public static void configureTable(JTable table, String[] columnHeaderNames)
	{
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		tableModel.setColumnCount(columnHeaderNames.length);
		tableModel.setColumnIdentifiers(columnHeaderNames);
		setHeaderAlignment(table);

		table.setModel(tableModel);
		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setReorderingAllowed(false);
		tableHeader.setResizingAllowed(false);
		tableHeader.setVisible(true);
		setCellsAlignment(table, SwingConstants.CENTER);
	}

	public static DefaultTableModel getDefaultTableModel()
	{
		return new DefaultTableModel()
		{
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
	}
}