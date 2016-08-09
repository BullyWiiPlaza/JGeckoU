package wiiudev.gecko.client.gui.tabs.disassembler;

import wiiudev.gecko.client.conversion.SystemClipboard;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class DisassemblerContextMenu extends JPopupMenu
{
	private DisassemblerTableManager tableManager;

	public DisassemblerContextMenu(DisassemblerTableManager tableManager)
	{
		this.tableManager = tableManager;
	}

	public void addContextMenu()
	{
		KeyStroke memoryViewerKeyStroke = addOption("Memory Viewer", "control M", actionEvent -> switchToMemoryViewer());
		KeyStroke copyCellsKeyStroke = addOption("Copy Cells", "control C", actionEvent -> copyCells());

		JMenuItem option = new JMenuItem("Follow Branch");
		KeyStroke followBranchKeyStroke = KeyStroke.getKeyStroke("control F");
		option.setAccelerator(followBranchKeyStroke);
		option.addActionListener(actionEvent -> followBranch());
		DisassembledInstruction disassembledInstruction = tableManager.getSelectedInstruction();
		option.setEnabled(disassembledInstruction.isBranchWithDestination());
		add(option);

		JTable table = tableManager.getTable();
		table.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent pressedEvent)
			{
				if (TCPGecko.isConnected())
				{
					if (keyEventPressed(pressedEvent, memoryViewerKeyStroke.getKeyCode()))
					{
						switchToMemoryViewer();
					} else if (keyEventPressed(pressedEvent, copyCellsKeyStroke.getKeyCode()))
					{
						copyCells();
					} else if (keyEventPressed(pressedEvent, followBranchKeyStroke.getKeyCode()))
					{
						followBranch();
					}
				}
			}
		});
	}

	private void followBranch()
	{
		DisassembledInstruction disassembledInstruction = tableManager.getSelectedInstruction();

		if (disassembledInstruction.isBranchWithDestination())
		{
			int address = disassembledInstruction.getBranchDestination();
			JGeckoUGUI.getInstance().updateDisassembler(address);
		}
	}

	private KeyStroke addOption(String text, String key, ActionListener actionListener)
	{
		JMenuItem option = new JMenuItem(text);
		KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
		option.setAccelerator(keyStroke);
		option.addActionListener(actionListener);
		add(option);

		return keyStroke;
	}

	private void copyCells()
	{
		List<DisassembledInstruction> disassembledInstructions = tableManager.getDisassembledInstructions();
		StringBuilder copyCellsBuilder = new StringBuilder();

		for (DisassembledInstruction disassembledInstruction : disassembledInstructions)
		{
			copyCellsBuilder.append(disassembledInstruction);
			copyCellsBuilder.append(System.lineSeparator());
		}

		String copiedCells = copyCellsBuilder.toString().trim();
		SystemClipboard.copy(copiedCells);
	}

	private boolean keyEventPressed(KeyEvent event, int targetKeyCode)
	{
		return event.getKeyCode() == targetKeyCode && (event.getModifiers() & KeyEvent.CTRL_MASK) != 0;
	}

	private void switchToMemoryViewer()
	{
		DisassembledInstruction disassembledInstruction = tableManager.getSelectedInstruction();
		int address = disassembledInstruction.getAddress();
		JGeckoUGUI.selectInMemoryViewer(address);
	}
}