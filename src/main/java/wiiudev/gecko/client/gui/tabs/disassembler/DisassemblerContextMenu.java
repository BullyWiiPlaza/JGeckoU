package wiiudev.gecko.client.gui.tabs.disassembler;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.conversions.SystemClipboard;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.tabs.disassembler.assembler.Disassembler;
import wiiudev.gecko.client.gui.utilities.JTableUtilities;
import wiiudev.gecko.client.gui.utilities.PopupMenuUtilities;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
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
		KeyStroke searchKeyStroke = PopupMenuUtilities.addOption(this, "Search", "control S", actionEvent -> switchToSearchTab());
		KeyStroke copyCellsKeyStroke = PopupMenuUtilities.addOption(this, "Copy Cells", "control C", actionEvent -> copyCells());
		KeyStroke functionStartKeyStroke = PopupMenuUtilities.addOption(this, "Function Start", "control T", actionEvent -> selectFunctionStart());
		KeyStroke copyFunctionKeyStroke = PopupMenuUtilities.addOption(this, "Copy Function", "control F", actionEvent -> copyFunction());

		DisassembledInstruction disassembledInstruction = tableManager.getSelectedInstruction();
		KeyStroke parseImmediateKeyStroke = KeyStroke.getKeyStroke("control D");
		if (disassembledInstruction.isLoadImmediateShifted())
		{
			JMenuItem option = new JMenuItem("Parse Immediate");
			option.setAccelerator(parseImmediateKeyStroke);
			option.addActionListener(actionEvent -> parseImmediate(tableManager));
			add(option);
		}

		KeyStroke followBranchKeyStroke = KeyStroke.getKeyStroke("control F");

		if (disassembledInstruction.isBranchWithDestination())
		{
			JMenuItem option = new JMenuItem("Follow Branch");
			option.setAccelerator(followBranchKeyStroke);
			option.addActionListener(actionEvent -> DisassemblerTableManager.followBranch(tableManager));
			add(option);
		}

		// Only possible on non-branch instructions
		KeyStroke hookKeyStroke = KeyStroke.getKeyStroke("control H");
		if (!disassembledInstruction.isBranchWithDestination())
		{
			JMenuItem option = new JMenuItem("Insert Hook");
			option.setAccelerator(hookKeyStroke);
			option.addActionListener(actionEvent -> hookAddress());
			add(option);
		}

		// Only applicable on always branches
		KeyStroke unHookKeyStroke = KeyStroke.getKeyStroke("control U");
		if (disassembledInstruction.isUnconditionalBranch())
		{
			JMenuItem option = new JMenuItem("Delete Hook");
			option.setAccelerator(unHookKeyStroke);
			option.addActionListener(actionEvent -> unHookAddress());
			add(option);
		}

		JTable table = tableManager.getTable();
		JTableUtilities.removeAllKeyListeners(table);
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
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, searchKeyStroke))
					{
						switchToSearchTab();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, copyCellsKeyStroke))
					{
						copyCells();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, followBranchKeyStroke))
					{
						DisassemblerTableManager.followBranch(tableManager);
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, parseImmediateKeyStroke))
					{
						parseImmediate(tableManager);
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, functionStartKeyStroke))
					{
						selectFunctionStart();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, copyFunctionKeyStroke))
					{
						copyFunction();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, hookKeyStroke))
					{
						hookAddress();
					} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, unHookKeyStroke))
					{
						unHookAddress();
					}
				}
			}
		});
	}

	private void unHookAddress()
	{
		try
		{
			int address = tableManager.getSelectedInstruction().getAddress();
			MemoryWriter memoryWriter = new MemoryWriter();
			memoryWriter.unHook(address);
			JGeckoUGUI.getInstance().updateDisassembler(address);
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(getRootPane(), exception);
		}
	}

	private void hookAddress()
	{
		InsertHookDialog insertHookDialog = new InsertHookDialog();
		insertHookDialog.setLocationRelativeTo(this);
		insertHookDialog.setVisible(true);

		byte[] assemblyBytes = insertHookDialog.getAssemblyBytes();

		if (assemblyBytes != null)
		{
			try
			{
				int address = tableManager.getSelectedInstruction().getAddress();
				MemoryWriter memoryWriter = new MemoryWriter();
				memoryWriter.hook(address, assemblyBytes);
				JGeckoUGUI.getInstance().updateDisassembler(address);
			} catch (Exception exception)
			{
				StackTraceUtils.handleException(getRootPane(), exception);
			}
		}
	}

	private void copyFunction()
	{
		List<DisassembledInstruction> disassembledInstructions = new ArrayList<>();
		int address = tableManager.getSelectedInstruction().getAddress();
		int length = MemoryReader.MAXIMUM_MEMORY_CHUNK_SIZE;

		try
		{
			doneCollectingInstructions:
			{
				while (true)
				{
					// Prepend the new elements
					List<DisassembledInstruction> newlyDisassembledInstructions = Disassembler.disassemble(address, length);
					for (int newlyDisassembledInstructionsIndex = 0; newlyDisassembledInstructionsIndex < newlyDisassembledInstructions.size(); newlyDisassembledInstructionsIndex++)
					{
						DisassembledInstruction disassembledInstruction = newlyDisassembledInstructions.get(newlyDisassembledInstructionsIndex);
						disassembledInstructions.add(newlyDisassembledInstructionsIndex, disassembledInstruction);

						// Function end reached?
						String instruction = disassembledInstruction.getInstruction();
						if (instruction.equals("blr"))
						{
							break doneCollectingInstructions;
						}
					}

					address += length;
				}
			}
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(getRootPane(), exception);
		}

		copyDisassembledInstructions(disassembledInstructions);
	}

	private void selectFunctionStart()
	{
		List<DisassembledInstruction> disassembledInstructions = new ArrayList<>();
		int address = tableManager.getSelectedInstruction().getAddress();
		int length = MemoryReader.MAXIMUM_MEMORY_CHUNK_SIZE;

		try
		{
			while (true)
			{
				// Keep looking in reverse
				address -= length;

				// Prepend the new elements
				List<DisassembledInstruction> newlyDisassembledInstructions = Disassembler.disassemble(address, length);
				for (int newlyDisassembledInstructionsIndex = 0; newlyDisassembledInstructionsIndex < newlyDisassembledInstructions.size(); newlyDisassembledInstructionsIndex++)
				{
					disassembledInstructions.add(newlyDisassembledInstructionsIndex, newlyDisassembledInstructions.get(newlyDisassembledInstructionsIndex));
				}

				// Check the newly prepended instructions
				for (int currentInstructionIndex = newlyDisassembledInstructions.size() - 1;
				     currentInstructionIndex >= 0; currentInstructionIndex--)
				{
					DisassembledInstruction currentInstruction = disassembledInstructions.get(currentInstructionIndex);
					String instruction = currentInstruction.getInstruction();

					// Function end reached?
					if (instruction.equals("blr"))
					{
						// The follow-on instruction is the start of the function
						JGeckoUGUI.selectInDisassembler(currentInstruction.getAddress() + 4);

						// Done, We found the function start
						return;
					}
				}
			}
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(getRootPane(), exception);
		}
	}

	private void parseImmediate(DisassemblerTableManager tableManager)
	{
		DisassembledInstruction disassembledInstruction = tableManager.getSelectedInstruction();
		String instruction = disassembledInstruction.getInstruction();
		LoadImmediateShifted loadImmediateShifted = new LoadImmediateShifted(instruction);
		int targetRegister = loadImmediateShifted.getRegister();
		int higherBits = Integer.parseInt(Integer.toHexString(loadImmediateShifted.getValue()) + "0000", 16);
		String addiOriSearchTarget = "r" + targetRegister + ",r" + targetRegister + ",";
		String lwzLfsSearchTarget = "(r" + targetRegister + ")";

		List<DisassembledInstruction> disassembledInstructions = tableManager.getDisassembledInstructions();
		int startingIndex = disassembledInstructions.indexOf(disassembledInstruction) + 1;
		boolean lowerAddressBitsFound = false;

		for (int disassembledInstructionIndex = startingIndex;
		     disassembledInstructionIndex < disassembledInstructions.size();
		     disassembledInstructionIndex++)
		{
			DisassembledInstruction currentDisassembledInstruction = disassembledInstructions.get(disassembledInstructionIndex);
			String selectedInstruction = currentDisassembledInstruction.getInstruction();

			if (selectedInstruction.contains(addiOriSearchTarget)
					&& (selectedInstruction.startsWith("addi")
					|| selectedInstruction.startsWith("ori")
					|| selectedInstruction.startsWith("subi")))
			{
				int searchTargetIndex = selectedInstruction.indexOf(addiOriSearchTarget)
						+ addiOriSearchTarget.length();
				selectedInstruction = selectedInstruction.substring(searchTargetIndex);

				lowerAddressBitsFound = true;
			} else if (selectedInstruction.endsWith(lwzLfsSearchTarget))
			{
				int searchTargetIndex = selectedInstruction.indexOf(lwzLfsSearchTarget);
				selectedInstruction = selectedInstruction.substring(0, searchTargetIndex);
				int lastCommaIndex = selectedInstruction.lastIndexOf(",");
				selectedInstruction = selectedInstruction.substring(lastCommaIndex + 1);

				lowerAddressBitsFound = true;
			}

			if (lowerAddressBitsFound)
			{
				int lowerBits = Integer.parseInt(selectedInstruction);

				if (currentDisassembledInstruction.getInstruction().startsWith("subi"))
				{
					higherBits -= lowerBits;
				} else
				{
					higherBits += lowerBits;
				}

				String value = Conversions.toHexadecimal(higherBits, 8);
				SystemClipboard.copy(value);
				JOptionPane.showMessageDialog(this,
						"Value " + value + " copied to the clipboard!\n\n"
								+ "Matching instruction pair:\n"
								+ disassembledInstruction
								+ "\n" + currentDisassembledInstruction,
						"Success",
						JOptionPane.INFORMATION_MESSAGE);

				break;
			}
		}
	}

	private void switchToSearchTab()
	{
		JGeckoUGUI jGeckoUGUI = JGeckoUGUI.getInstance();
		DisassembledInstruction disassembledInstruction = tableManager.getSelectedInstruction();
		jGeckoUGUI.setupSearch(disassembledInstruction);
	}

	private void copyCells()
	{
		List<DisassembledInstruction> disassembledInstructions = tableManager.getDisassembledInstructions();
		copyDisassembledInstructions(disassembledInstructions);
	}

	private void copyDisassembledInstructions(List<DisassembledInstruction> disassembledInstructions)
	{
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

	private static class LoadImmediateShifted
	{
		private int register;
		private int value;

		LoadImmediateShifted(String instruction)
		{
			instruction = instruction.replaceAll("lis r", "");
			int commaIndex = instruction.indexOf(",");
			register = Integer.parseInt(instruction.substring(0, commaIndex));
			instruction = instruction.substring(commaIndex + 1);
			value = Integer.parseInt(instruction);
		}

		int getRegister()
		{
			return register;
		}

		public int getValue()
		{
			return value;
		}
	}
}