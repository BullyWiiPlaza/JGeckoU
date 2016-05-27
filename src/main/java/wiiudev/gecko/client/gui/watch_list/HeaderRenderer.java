package wiiudev.gecko.client.gui.watch_list;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

class HeaderRenderer implements TableCellRenderer
{
	private DefaultTableCellRenderer renderer;

	public HeaderRenderer(JTable table)
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