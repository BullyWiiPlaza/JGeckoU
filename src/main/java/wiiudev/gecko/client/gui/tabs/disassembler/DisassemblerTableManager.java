package wiiudev.gecko.client.gui.tabs.disassembler;

import wiiudev.gecko.client.gui.tabs.disassembler.assembler.Disassembler;
import wiiudev.gecko.client.gui.utilities.JTableUtilities;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DisassemblerTableManager
{
	private JTable table;
	private int length;

	private List<DisassembledInstruction> disassembledInstructions;

	public DisassemblerTableManager(JTable table)
	{
		DefaultTableModel tableModel = JTableUtilities.getDefaultTableModel();
		table.setModel(tableModel);
		JTableUtilities.setSingleSelection(table);
		this.table = table;
		disassembledInstructions = new ArrayList<>();

		int rowsCount = 100;
		int integerBytesCount = 4;
		length = rowsCount * integerBytesCount;
	}

	public List<DisassembledInstruction> getDisassembledInstructions()
	{
		return disassembledInstructions;
	}

	public void configure()
	{
		String[] columnHeaderNames = new String[]{"Address", "Value", "Instruction"};
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
				DisassemblerContextMenu contextMenu = new DisassemblerContextMenu(this);
				contextMenu.addContextMenu();

				contextMenu.show(mouseEvent.getComponent(),
						mouseEvent.getX(),
						mouseEvent.getY());
			}
		}
	}

	public void updateRows(int address) throws Exception
	{
		// int selectedRow = table.getSelectedRow();
		disassembledInstructions.clear();

		MemoryReader memoryReader = new MemoryReader();
		byte[] values = memoryReader.readBytes(address, length);
		disassembledInstructions = Disassembler.disassemble(values, address);
		JTableUtilities.deleteAllRows(table);
		disassembledInstructions.forEach(this::addRow);

		/*if (selectedRow == -1)
		{
			selectedRow = 0;
		}

		table.setRowSelectionInterval(selectedRow, selectedRow);*/

		resetViewSelection();
	}

	private void addRow(DisassembledInstruction disassembledInstruction)
	{
		Object[] objects = new Object[]{new Hexadecimal(disassembledInstruction.getAddress(), 8),
				new Hexadecimal(disassembledInstruction.getValue(), 8),
				disassembledInstruction.getInstruction()};
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		tableModel.addRow(objects);
	}

	public int getDumpLength()
	{
		return length;
	}

	public DisassembledInstruction getSelectedInstruction()
	{
		int selectedRowIndex = table.getSelectedRow();

		if (selectedRowIndex == -1)
		{
			return null;
		}

		return disassembledInstructions.get(selectedRowIndex);
	}

	public JTable getTable()
	{
		return table;
	}

	public void resetViewSelection()
	{
		table.setRowSelectionInterval(0, 0);

		scroll();
	}

	public void scroll()
	{
		JViewport viewport = (JViewport) table.getParent();
		int rowIndex = table.getSelectedRow();
		Rectangle rectangle = table.getCellRect(rowIndex, 0, true);
		Point point = viewport.getViewPosition();

		rectangle.setLocation(rectangle.x - point.x, rectangle.y - point.y);
		table.scrollRectToVisible(rectangle);
	}
}