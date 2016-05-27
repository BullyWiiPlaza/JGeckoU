package wiiudev.gecko.client.gui.code_list;

import wiiudev.gecko.client.codes.CodeListEntry;
import wiiudev.gecko.client.codes.GeckoCode;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

public class CodesListManager
{
	private JList<JCheckBox> codesListBoxes;
	private DefaultListModel<JCheckBox> codeListModel;
	private List<CodeListEntry> codeListEntries;
	private JRootPane rootPane;

	@SuppressWarnings("unchecked")
	public CodesListManager(JList codesListBoxes, JRootPane rootPane)
	{
		codeListEntries = new LinkedList<>();
		this.codesListBoxes = codesListBoxes;
		codesListBoxes.setModel(new DefaultListModel<>());
		codesListBoxes.setCellRenderer(new CheckBoxListCellRenderer());
		codeListModel = (DefaultListModel<JCheckBox>) codesListBoxes.getModel();
		this.rootPane = rootPane;
	}

	public JList<JCheckBox> getCodesListBoxes()
	{
		return codesListBoxes;
	}

	public boolean isSelectedCodeListEntryTicked()
	{
		int selectedCodeListEntry = getSelectedCodeListIndex(false);

		return codeListModel.getElementAt(selectedCodeListEntry).isSelected();
	}

	public void addCodeListEntryClickedListener()
	{
		codesListBoxes.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent mouseEvent)
			{
				int index = codesListBoxes.locationToIndex(mouseEvent.getPoint());

				if (index != -1)
				{
					JCheckBox checkbox = codesListBoxes.getModel().getElementAt(index);
					checkbox.setSelected(!checkbox.isSelected());
					codesListBoxes.repaint();
				}
			}
		});
	}

	public CodeListEntry getSelectedCodeListEntry()
	{
		int selectedCodeListEntry = getSelectedCodeListIndex(false);

		if (selectedCodeListEntry != -1)
		{
			return codeListEntries.get(selectedCodeListEntry);
		} else
		{
			return null;
		}
	}

	public void clearCodeList()
	{
		codeListEntries.clear();
		codeListModel.removeAllElements();
	}

	public void addCode(GeckoCode geckoCode)
	{
		CodeListEntry codeListEntry = new CodeListEntry(geckoCode.getTitle(), geckoCode.getCode(), geckoCode.getComment());
		addCodeListEntry(codeListEntry);
	}

	public void addCodeListEntry(CodeListEntry codeListEntry, boolean selected)
	{
		codeListEntries.add(codeListEntry);
		JCheckBox codeListCheckBox = codeListEntry.getCodeListCheckBox();
		codeListCheckBox.setSelected(selected);
		codeListModel.addElement(codeListCheckBox);
		codesListBoxes.setSelectedIndex(codesListBoxes.getLastVisibleIndex());
	}

	public void addCodeListEntry(CodeListEntry codeListEntry)
	{
		addCodeListEntry(codeListEntry, false);
	}

	public void deleteSelectedCodeListEntry(boolean silent)
	{
		int selectedCodeListEntry = getSelectedCodeListIndex(false);

		if (selectedCodeListEntry != -1)
		{
			if (silent)
			{
				deleteSelectedCodeListEntry(selectedCodeListEntry);
			} else
			{
				int selectedAnswer = JOptionPane.showConfirmDialog(
						rootPane,
						"Do you really want to delete the selected code?",
						"Delete?",
						JOptionPane.YES_NO_OPTION);

				if (selectedAnswer == JOptionPane.YES_OPTION)
				{
					deleteSelectedCodeListEntry(selectedCodeListEntry);
				}
			}
		}
	}

	public List<CodeListEntry> getActiveCodes()
	{
		List<CodeListEntry> activeCodes = new LinkedList<>();

		for (int i = 0; i < codeListEntries.size(); i++)
		{
			boolean isSelected = codeListModel.getElementAt(i).isSelected();

			if (isSelected)
			{
				activeCodes.add(codeListEntries.get(i));
			}
		}

		return activeCodes;
	}

	private void deleteSelectedCodeListEntry(int selectedCodeListEntry)
	{
		codeListModel.remove(selectedCodeListEntry);
		codeListEntries.remove(selectedCodeListEntry);
		codesListBoxes.setSelectedIndex(codesListBoxes.getLastVisibleIndex());
	}

	public int getSelectedCodeListIndex(boolean silent)
	{
		int selectedCodeIndex = codesListBoxes.getSelectedIndex();
		if (selectedCodeIndex == -1)
		{
			if (!silent)
			{
				JOptionPane.showMessageDialog(rootPane,
						"No code has been selected!",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
			return -1;
		} else
		{
			return selectedCodeIndex;
		}
	}

	public boolean isSomeEntrySelected()
	{
		return codesListBoxes.getSelectedIndex() != -1;
	}

	public List<GeckoCode> getCodeListBackup()
	{
		List<GeckoCode> codeListBackup = new LinkedList<>();

		for (int codeListBackupIndex = 0; codeListBackupIndex < codeListEntries.size(); codeListBackupIndex++)
		{
			CodeListEntry codeListEntry = codeListEntries.get(codeListBackupIndex);
			GeckoCode codeListEntryState = new GeckoCode();
			codeListEntryState.setTitle(codeListEntry.getTitle());
			codeListEntryState.setCode(codeListEntry.getCode());
			codeListEntryState.setComment(codeListEntry.getComment());
			codeListEntryState.setEnabled(codeListModel.get(codeListBackupIndex).isSelected());
			codeListBackup.add(codeListEntryState);
		}

		return codeListBackup;
	}

	public void setCodesListBackup(List<GeckoCode> codesListBackup)
	{
		clearCodeList();

		for (GeckoCode codeListEntryState : codesListBackup)
		{
			CodeListEntry codeListEntry = new CodeListEntry(codeListEntryState.getTitle(), codeListEntryState.getCode(), codeListEntryState.getComment());
			addCodeListEntry(codeListEntry, codeListEntryState.isEnabled());
		}
	}
}