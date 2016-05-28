package wiiudev.gecko.client.memoryViewer;

import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.code_list.code_wizard.CodeWizardDialog;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MemoryViewerContextMenu extends JPopupMenu
{
	public MemoryViewerContextMenu()
	{
		JMenuItem offsetOption = new JMenuItem("Offset");
		offsetOption.setAccelerator(KeyStroke.getKeyStroke("control G"));
		offsetOption.addActionListener(actionEvent -> showOffsetDialog());
		add(offsetOption);

		JMenuItem copyAllCells = new JMenuItem("Copy Cells");
		copyAllCells.setAccelerator(KeyStroke.getKeyStroke("control L"));
		copyAllCells.addActionListener(actionEvent -> copyCells());
		add(copyAllCells);

		JMenuItem codeWizardOption = new JMenuItem("Code Wizard");
		codeWizardOption.setAccelerator(KeyStroke.getKeyStroke("control W"));
		codeWizardOption.addActionListener(actionEvent -> displayCodeWizard());
		add(codeWizardOption);
	}

	private void displayCodeWizard()
	{
		CodeWizardDialog codeWizardDialog = new CodeWizardDialog();
		JGeckoUGUI jGeckoUGUI = JGeckoUGUI.getInstance();
		int selectedMemoryViewerAddress = jGeckoUGUI.getSelectedMemoryViewerAddress();
		codeWizardDialog.setAddress(selectedMemoryViewerAddress);
		int value = jGeckoUGUI.getSelectedMemoryViewerValue();
		codeWizardDialog.setValue(value);
		codeWizardDialog.generateCode();
		codeWizardDialog.setVisible(true);
	}

	public void attachTo(JTable table)
	{
		addKeyShortcuts(table);
		addRightClickListener(table);
	}

	private void addRightClickListener(JTable table)
	{
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent releasedEvent)
			{
				if (JGeckoUGUI.getInstance().isConnected())
				{
					if (releasedEvent.getButton() == MouseEvent.BUTTON3)
					{
						processClick(releasedEvent);
					}
				}
			}
		});
	}

	private void addKeyShortcuts(JTable table)
	{
		table.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent pressedEvent)
			{
				if (JGeckoUGUI.getInstance().isConnected())
				{
					if (keyEventPressed(pressedEvent, KeyEvent.VK_G))
					{
						showOffsetDialog();
					}

					if (keyEventPressed(pressedEvent, KeyEvent.VK_L))
					{
						copyCells();
					}

					if (keyEventPressed(pressedEvent, KeyEvent.VK_W))
					{
						displayCodeWizard();
					}
				}
			}
		});
	}

	private boolean keyEventPressed(KeyEvent event, int targetKeyCode)
	{
		return event.getKeyCode() == targetKeyCode && (event.getModifiers() & KeyEvent.CTRL_MASK) != 0;
	}

	private void copyCells()
	{
		JGeckoUGUI.getInstance().copyMemoryViewerCells();
	}

	private void showOffsetDialog()
	{
		OffsetDialog goToOffsetDialog = new OffsetDialog(null);
		goToOffsetDialog.setVisible(true);

		if (goToOffsetDialog.isConfirmed())
		{
			int offset = goToOffsetDialog.getOffset();
			JGeckoUGUI.getInstance().addMemoryViewerOffset(offset);
		}
	}

	private void processClick(MouseEvent event)
	{
		JTable table = (JTable) event.getSource();
		table.requestFocus();
		show(table, event.getX(), event.getY());
	}
}
