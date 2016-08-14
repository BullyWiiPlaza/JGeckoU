package wiiudev.gecko.client.gui.tabs.memory_viewer;

import wiiudev.gecko.client.conversion.Conversions;
import wiiudev.gecko.client.conversion.SystemClipboard;
import wiiudev.gecko.client.gui.inputFilter.ValueSizes;
import wiiudev.gecko.client.gui.utilities.JTableUtilities;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class MemoryViewerTableManager
{
	private JTable table;
	private DefaultTableModel tableModel;
	private MemoryViews memoryView;
	private int[] cellValues;
	private static final int INTEGER_SIZE = ValueSizes.SIXTEEN_BIT.getSize();
	public static final int STARTING_ADDRESS = 0x10000000;

	public MemoryViewerTableManager(JTable table, MemoryViews memoryView)
	{
		this.table = table;
		setMemoryView(memoryView);
	}

	public void setMemoryView(MemoryViews memoryView)
	{
		this.memoryView = memoryView;
	}

	public void configureTable()
	{
		tableModel = new DefaultTableModel()
		{
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		Object[] columnHeaders = setupColumnHeaders(4);
		tableModel.setColumnCount(columnHeaders.length);
		tableModel.setColumnIdentifiers(columnHeaders);

		// This is chosen as multiples of the request limit
		tableModel.setNumRows(MemoryReader.MAXIMUM_MEMORY_CHUNK_SIZE * 5 / getRowBytes());

		table.setModel(tableModel);
		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setReorderingAllowed(false);
		table.setRowSelectionAllowed(false);
		tableHeader.setResizingAllowed(false);
		JTableUtilities.setCellsAlignment(table, SwingConstants.CENTER);
		selectFirstCell();

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
				MemoryViewerContextMenu contextMenu = new MemoryViewerContextMenu(table);
				contextMenu.addContextMenu();

				contextMenu.show(mouseEvent.getComponent(),
						mouseEvent.getX(),
						mouseEvent.getY());
			}
		}
	}

	private Object[] setupColumnHeaders(int addressColumnsCount)
	{
		Object[] columnHeaders = new Object[addressColumnsCount + 1];
		columnHeaders[0] = "";

		for (int columnHeaderIndex = 1; columnHeaderIndex < columnHeaders.length; columnHeaderIndex++)
		{
			String columnHeaderText = Conversions.toHexadecimalNoPadding(columnHeaderIndex * INTEGER_SIZE - INTEGER_SIZE);
			columnHeaders[columnHeaderIndex] = columnHeaderText;
		}

		return columnHeaders;
	}

	private void selectFirstCell()
	{
		setCellSelection(0, 1);
	}

	public void selectAddress(int address)
	{
		int rowCount = tableModel.getRowCount();
		int columnCount = tableModel.getColumnCount();
		int cellAddressOffset = 4;

		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
		{
			String memoryAddress = (String) tableModel.getValueAt(rowIndex, 0);
			int memoryViewerAddress = Integer.parseInt(memoryAddress, 16);
			int difference = address - memoryViewerAddress;

			if (difference <= cellAddressOffset * columnCount)
			{
				int columnIndex = difference / cellAddressOffset;
				setCellSelection(rowIndex, columnIndex + 1);

				break;
			}
		}
	}

	private void setCellSelection(int rowIndex, int columnIndex)
	{
		table.changeSelection(rowIndex, columnIndex, false, false);
		table.requestFocus();
	}

	/**
	 * Calculates the amount of bytes being displayed in the memory viewer at once
	 *
	 * @return The amount of displayed bytes
	 */
	public int getDisplayedBytes()
	{
		return getRowBytes() * tableModel.getRowCount();
	}

	public void updateCells(int memoryAddress, boolean resetSelection) throws IOException
	{
		MemoryReader memoryReader = new MemoryReader();
		int normalizedAddress = normalize(memoryAddress);
		byte[] readBytes = memoryReader.readBytes(normalizedAddress, getDisplayedBytes());
		cellValues = Conversions.toIntegerArray(readBytes);

		setAddressesRowLabels(normalizedAddress);
		setMemoryCellContents();

		if (resetSelection)
		{
			selectFirstCell();
		}
	}

	public String getSelectedValue()
	{
		int selectedRow = table.getSelectedRow();
		int selectedColumn = table.getSelectedColumn();

		if (noCellSelected() || cellValues == null)
		{
			return "00000000";
		}

		int index = 0;
		index += selectedColumn - 1;
		index += selectedRow * getRowBytes() / INTEGER_SIZE;

		return Conversions.toHexadecimal(cellValues[index]);
	}

	private int normalize(int memoryAddress)
	{
		return memoryAddress - memoryAddress % getRowBytes();
	}

	private int getRowBytes()
	{
		return (tableModel.getColumnCount() - 1) * INTEGER_SIZE;
	}

	public int getSelectedAddress()
	{
		if (noCellSelected())
		{
			return STARTING_ADDRESS;
		}

		int selectedColumn = table.getSelectedColumn();
		int selectedRow = table.getSelectedRow();

		return getValueAt(selectedColumn, selectedRow);
	}

	private int getValueAt(int selectedColumn, int selectedRow)
	{
		String rowStartingAddress = (String) tableModel.getValueAt(selectedRow, 0);
		long currentBaseAddress = Conversions.toDecimal(rowStartingAddress);
		currentBaseAddress += (selectedColumn - 1) * INTEGER_SIZE;

		return (int) currentBaseAddress;
	}

	private boolean noCellSelected()
	{
		int selectedColumn = table.getSelectedColumn();
		int selectedRow = table.getSelectedRow();

		return selectedRow == -1 || selectedColumn == -1;
	}

	public void setMemoryCellContents()
	{
		int rowIndex = 0;
		int startingColumnIndex = 1;
		int columnIndex = startingColumnIndex;

		for (int readInteger : cellValues)
		{
			String hexadecimalValue = Conversions.decimalToHexadecimalMemoryAddress(readInteger);

			switch (memoryView)
			{
				case ASCII:
					hexadecimalValue = Conversions.hexadecimalToASCII(hexadecimalValue);
					break;

				case Float:
					hexadecimalValue = Conversions.hexadecimalToFloatingPoint(hexadecimalValue);
					break;

				case Decimal:
					hexadecimalValue = Conversions.hexadecimalToDecimal(hexadecimalValue);
					break;

				case Unicode:
					hexadecimalValue = Conversions.toUnicode(readInteger);
					break;
			}

			tableModel.setValueAt(hexadecimalValue, rowIndex, columnIndex);

			columnIndex++;

			if (columnIndex == tableModel.getColumnCount())
			{
				columnIndex = startingColumnIndex;
				rowIndex++;
			}
		}
	}

	private void setAddressesRowLabels(int memoryAddress)
	{
		for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); rowIndex++)
		{
			int updatedMemoryAddress = memoryAddress + getRowBytes() * rowIndex;
			String hexadecimalAddress = Conversions.decimalToHexadecimalMemoryAddress(updatedMemoryAddress);
			tableModel.setValueAt(hexadecimalAddress, rowIndex, 0);
		}
	}

	public void copyCells()
	{
		StringBuilder copiedCellsBuilder = new StringBuilder();
		int columnCount = tableModel.getColumnCount();
		int rowCount = tableModel.getRowCount();

		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
		{
			for (int columnIndex = 0; columnIndex < columnCount; columnIndex++)
			{
				String cellValue = (String) tableModel.getValueAt(rowIndex, columnIndex);
				copiedCellsBuilder.append(cellValue);

				if (columnIndex != columnCount - 1)
				{
					copiedCellsBuilder.append(" ");
				}
			}

			if (rowIndex != rowCount - 1)
			{
				copiedCellsBuilder.append(System.lineSeparator());
			}
		}

		String copiedCells = copiedCellsBuilder.toString();
		SystemClipboard.copy(copiedCells);
	}

	public int getFirstMemoryAddress()
	{
		return getValueAt(0, 0) + INTEGER_SIZE;
	}
}