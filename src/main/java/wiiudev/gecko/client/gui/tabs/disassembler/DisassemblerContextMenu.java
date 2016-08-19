package wiiudev.gecko.client.gui.tabs.disassembler;

import wiiudev.gecko.client.conversions.SystemClipboard;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.utilities.PopupMenuUtilities;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import javax.swing.*;
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
		KeyStroke memoryViewerKeyStroke = PopupMenuUtilities.addOption(this, "Memory Viewer", "control M", actionEvent -> switchToMemoryViewer());
		KeyStroke copyCellsKeyStroke = PopupMenuUtilities.addOption(this, "Copy Cells", "control C", actionEvent -> copyCells());

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
					if (PopupMenuUtilities.keyEventPressed(pressedEvent, memoryViewerKeyStroke))
					{
						switchToMemoryViewer();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, copyCellsKeyStroke))
					{
						copyCells();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, followBranchKeyStroke))
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

	private void switchToMemoryViewer()
	{
		DisassembledInstruction disassembledInstruction = tableManager.getSelectedInstruction();
		int address = disassembledInstruction.getAddress();
		JGeckoUGUI.selectInMemoryViewer(address);
	}
}