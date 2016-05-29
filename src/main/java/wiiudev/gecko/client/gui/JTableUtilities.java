package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.gui.watch_list.HeaderRenderer;

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
}