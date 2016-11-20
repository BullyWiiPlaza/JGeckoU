package wiiudev.gecko.client.gui.tabs.disassembler;

import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.tabs.disassembler.assembler.Disassembler;
import wiiudev.gecko.client.gui.utilities.JTableUtilities;
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

		addDoubleClickListener();

		length = TCPGecko.MAXIMUM_MEMORY_CHUNK_SIZE;
	}

	private void addDoubleClickListener()
	{
		table.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent mouseEvent)
			{
				JTable table = (JTable) mouseEvent.getSource();
				Point point = mouseEvent.getPoint();
				int rowIndex = table.rowAtPoint(point);

				if (rowIndex != -1 && mouseEvent.getClickCount() == 2)
				{
					followBranch(DisassemblerTableManager.this);
				}
			}
		});
	}

	public static void followBranch(DisassemblerTableManager tableManager)
	{
		DisassembledInstruction disassembledInstruction = tableManager.getSelectedInstruction();

		if (disassembledInstruction.isBranchWithDestination())
		{
			int address = disassembledInstruction.getBranchDestination();
			JGeckoUGUI.getInstance().updateDisassembler(address);
		}
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
		disassembledInstructions.clear();

		disassembledInstructions = Disassembler.disassemble(address - length / 2, length);
		JTableUtilities.deleteAllRows(table);
		disassembledInstructions.forEach(this::addRow);

		int index = (length / 4) / 2;
		JTableUtilities.setSelectedRow(table, index, index);
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
}