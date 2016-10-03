package wiiudev.gecko.client.gui.tabs.threads;

import wiiudev.gecko.client.gui.utilities.JTableUtilities;
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

	public JTable getTable()
	{
		return table;
	}

	public void configure()
	{
		String[] columnHeaderNames = new String[]{"Name", "Address", "State", "CPU Affinity", "Priority"};
		JTableUtilities.configureTable(table, columnHeaderNames);
		addContextMenuListener();
	}

	public List<OSThread> getThreads()
	{
		return osThreads;
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
				ThreadsTableContextMenu contextMenu = new ThreadsTableContextMenu(ThreadsTableManager.this);
				contextMenu.addContextMenu();

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

		for (int selectedRowIndex : selectedRows)
		{
			OSThread osThread = osThreads.get(selectedRowIndex);
			selectedThreads.add(osThread);
		}

		return selectedThreads;
	}

	private void addRow(OSThread osThread)
	{
		Object[] objects = new Object[]{osThread.getName(),
				new Hexadecimal(osThread.getAddress()),
				osThread.getState(),
		osThread.getAffinity(),
		osThread.getPriority()};
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		tableModel.addRow(objects);
	}

	public void updateRows(boolean fetch) throws Exception
	{
		JTableUtilities.deleteAllRows(table);

		if (fetch)
		{
			osThreads = OSThread.readThreads();
		}

		osThreads.forEach(this::addRow);
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

	public String readSelectedThreadRegisters() throws IOException
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