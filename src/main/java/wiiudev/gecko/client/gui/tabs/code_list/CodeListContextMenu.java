package wiiudev.gecko.client.gui.tabs.code_list;

import wiiudev.gecko.client.conversions.SystemClipboard;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.tabs.PopupMenuUtilities;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CodeListContextMenu extends JPopupMenu
{
	private CodesListManager codesListManager;

	public CodeListContextMenu(CodesListManager codesListManager)
	{
		this.codesListManager = codesListManager;
	}

	public void addContextMenu()
	{
		KeyStroke editCodeKeyStroke = PopupMenuUtilities.addOption(this, "Edit Code", "control E", actionEvent -> editSelectedCode());
		KeyStroke copyCodeKeyStroke = PopupMenuUtilities.addOption(this, "Copy Code", "control O", actionEvent -> copySelectedCode());
		KeyStroke deleteCodeKeyStroke = PopupMenuUtilities.addOption(this, "Delete Code", "control L", actionEvent -> deleteSelectedCode());
		KeyStroke moveUpwardsKeyStroke = PopupMenuUtilities.addOption(this, "Move Upwards", "control U", actionEvent -> moveUpwards());
		KeyStroke moveDownwardsKeyStroke = PopupMenuUtilities.addOption(this, "Move Downwards", "control D", actionEvent -> moveDownwards());

		JList<JCheckBox> checkBoxList = codesListManager.getCheckBoxList();

		checkBoxList.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent pressedEvent)
			{
				if (PopupMenuUtilities.keyEventPressed(pressedEvent, moveUpwardsKeyStroke))
				{
					moveUpwards();
				} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, copyCodeKeyStroke))
				{
					copySelectedCode();
				} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, moveDownwardsKeyStroke))
				{
					moveDownwards();
				} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, editCodeKeyStroke))
				{
					editSelectedCode();
				} else if (PopupMenuUtilities.keyEventPressed(pressedEvent, deleteCodeKeyStroke))
				{
					deleteSelectedCode();
				}
			}
		});
	}

	private void copySelectedCode()
	{
		String codeToCopy = codesListManager.getSelectedCode();
		SystemClipboard.copy(codeToCopy);
	}

	private void moveUpwards()
	{
		JGeckoUGUI geckoUGUI = JGeckoUGUI.getInstance();
		geckoUGUI.moveCodeListEntryUpwards();
	}

	private void moveDownwards()
	{
		JGeckoUGUI geckoUGUI = JGeckoUGUI.getInstance();
		geckoUGUI.moveCodeListEntryDownwards();
	}

	private void deleteSelectedCode()
	{
		JGeckoUGUI geckoUGUI = JGeckoUGUI.getInstance();
		geckoUGUI.deleteSelectedCodeListEntry();
	}

	private void editSelectedCode()
	{
		JGeckoUGUI geckoUGUI = JGeckoUGUI.getInstance();
		geckoUGUI.editSelectedCode();
	}
}