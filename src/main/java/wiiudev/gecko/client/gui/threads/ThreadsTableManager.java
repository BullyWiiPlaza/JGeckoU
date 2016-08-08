package wiiudev.gecko.client.gui.threads;

import wiiudev.gecko.client.gui.JTableUtilities;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThread;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ThreadsTableManager
{
	private JTable table;
	private List<OSThread> osThreads;

	public ThreadsTableManager(JTable table)
	{
		DefaultTableModel tableModel = JTableUtilities.getDefaultTableModel();
		table.setModel(tableModel);
		this.table = table;
		osThreads = new ArrayList<>();
	}

	public void configure()
	{
		String[] columnHeaderNames = new String[]{"Name", "Address", "State"};
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
				JPopupMenu contextMenu = new ThreadsTableContextMenu(ThreadsTableManager.this);
				contextMenu.show(mouseEvent.getComponent(),
						mouseEvent.getX(),
						mouseEvent.getY());
			}
		}
	}

	public int[] getSelectedRows()
	{
		return table.getSelectedRows();
	}

	public List<OSThread> getSelectedItems()
	{
		int[] selectedRows = table.getSelectedRows();
		List<OSThread> selectedThreads = new ArrayList<>();

		if (selectedRows == null)
		{
			throw new IllegalStateException("Nothing selected!");
		} else
		{
			for (int selectedRowIndex : selectedRows)
			{
				OSThread osThread = osThreads.get(selectedRowIndex);
				selectedThreads.add(osThread);
			}
		}

		return selectedThreads;
	}

	public void addRow(OSThread osThread)
	{
		Object[] objects = new Object[]{osThread.geName(),
				new Hexadecimal(osThread.getAddress()),
				osThread.getState()};
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		tableModel.addRow(objects);
	}

	public void updateRows() throws IOException
	{
		osThreads = OSThread.readThreads();
		populateRows();
	}

	public void populateRows()
	{
		JTableUtilities.deleteAllRows(table);
		osThreads.forEach(this::addRow);
	}

	public void setSelectedItems(int[] selectedRows)
	{
		table.setRowSelectionInterval(selectedRows[0], selectedRows[selectedRows.length - 1]);
	}

	public String getSelectedThreadRegisters() throws IOException
	{
		// The first one only
		List<OSThread> selectedItems = getSelectedItems();

		if (selectedItems.size() > 0)
		{
			OSThread osThread = selectedItems.get(0);

			return osThread.readRegisters();
		}

		return "";
	}
}