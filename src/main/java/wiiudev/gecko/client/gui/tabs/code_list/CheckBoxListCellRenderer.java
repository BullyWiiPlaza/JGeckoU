package wiiudev.gecko.client.gui.tabs.code_list;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class CheckBoxListCellRenderer implements ListCellRenderer
{
	public JCheckBox getListCellRendererComponent(
			JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus)
	{
		JCheckBox checkbox = (JCheckBox) value;
		checkbox.setBackground(isSelected ?
				list.getSelectionBackground() : list.getBackground());
		checkbox.setForeground(isSelected ?
				list.getSelectionForeground() : list.getForeground());
		checkbox.setEnabled(list.isEnabled());
		checkbox.setFont(list.getFont());
		checkbox.setFocusPainted(false);
		checkbox.setBorderPainted(true);
		Border noFocusBorder =
				new EmptyBorder(1, 1, 1, 1);
		checkbox.setBorder(isSelected ?
				UIManager.getBorder(
						"List.focusCellHighlightBorder") : noFocusBorder);

		return checkbox;
	}
}