package wiiudev.gecko.client.gui.disassembler;

import wiiudev.gecko.client.gui.JTableUtilities;
import wiiudev.gecko.client.gui.disassembler.assembler.Disassembler;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
		table.setSelectionModel(new ForcedListSelectionModel());
		this.table = table;
		disassembledInstructions = new ArrayList<>();

		int rowsCount = 100;
		int integerBytesCount = 4;
		length = rowsCount * integerBytesCount;
	}

	public void configure()
	{
		String[] columnHeaderNames = new String[]{"Address", "Value", "Instruction"};
		JTableUtilities.configureTable(table, columnHeaderNames);
		// addContextMenuListener();
	}

	public void updateRows(int address) throws Exception
	{
		int selectedRow = table.getSelectedRow();
		JTableUtilities.deleteAllRows(table);
		disassembledInstructions.clear();

		MemoryReader memoryReader = new MemoryReader();
		byte[] values = memoryReader.readBytes(address, length);
		disassembledInstructions = Disassembler.disassemble(values, address);
		disassembledInstructions.forEach(this::addRow);

		if(selectedRow == -1)
		{
			selectedRow = 0;
		}

		table.setRowSelectionInterval(selectedRow, selectedRow);
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

	private class ForcedListSelectionModel extends DefaultListSelectionModel
	{
		public ForcedListSelectionModel()
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
}