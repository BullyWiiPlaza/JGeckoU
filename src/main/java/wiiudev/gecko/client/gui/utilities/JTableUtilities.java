package wiiudev.gecko.client.gui.utilities;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyListener;

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

	private static void setHeaderAlignment(JTable table)
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
		defaultTableModel.setRowCount(0);
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

	public static void setSingleSelection(JTable table)
	{
		table.setSelectionModel(new ForcedListSelectionModel());
	}

	public static void removeAllKeyListeners(JComponent component)
	{
		KeyListener[] keyListeners = component.getKeyListeners();

		for (KeyListener keyListener : keyListeners)
		{
			component.removeKeyListener(keyListener);
		}
	}

	private static class ForcedListSelectionModel extends DefaultListSelectionModel
	{
		ForcedListSelectionModel()
		{
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}

		@Override
		public void clearSelection()
		{
		}

		@Override
		public void removeSelectionInterval(int start, int end)
		{
		}
	}

	private static class HeaderRenderer implements TableCellRenderer
	{
		private DefaultTableCellRenderer renderer;

		HeaderRenderer(JTable table)
		{
			renderer = (DefaultTableCellRenderer)
					table.getTableHeader().getDefaultRenderer();
			renderer.setHorizontalAlignment(JLabel.CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(
				JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int col)
		{
			return renderer.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, col);
		}
	}

	public static void setSelectedRow(JTable table, int rowIndex, int columnIndex)
	{
		table.setRowSelectionInterval(rowIndex, columnIndex);

		try
		{
			Thread.sleep(10);
		} catch (InterruptedException exception)
		{
			exception.printStackTrace();
		}

		scrollToSelectedRow(table);
	}

	private static void scrollToSelectedRow(JTable table)
	{
		JViewport viewport = (JViewport) table.getParent();
		Rectangle cellRectangle = table.getCellRect(table.getSelectedRow(), 0, true);
		Rectangle visibleRectangle = viewport.getVisibleRect();
		table.scrollRectToVisible(new Rectangle(cellRectangle.x, cellRectangle.y, (int) visibleRectangle.getWidth(), (int) visibleRectangle.getHeight()));
	}
}