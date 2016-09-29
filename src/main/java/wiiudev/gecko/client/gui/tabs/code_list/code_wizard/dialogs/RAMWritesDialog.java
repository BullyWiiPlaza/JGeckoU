package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CheatCodeFormatter;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeType;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.ValueSize;

import javax.swing.*;

public class RAMWritesDialog extends JDialog
{
	private JPanel contentPane;
	private JComboBox<ValueSize> valueSizeSelection;
	private JCheckBox pointerCheckBox;
	private JFormattedTextField addressField;
	private JFormattedTextField valueField;
	private JButton generateButton;

	public RAMWritesDialog()
	{
		DialogUtilities.setCodeGeneratorDialogProperties(this,
				generateButton,
				contentPane,
				this::generateCode);

		DefaultComboBoxModel<ValueSize> comboBoxModel = new DefaultComboBoxModel<>(ValueSize.values());
		valueSizeSelection.setModel(comboBoxModel);

		valueSizeSelection.addItemListener(itemEvent -> setValueFieldLengthLimit());
		pointerCheckBox.addItemListener(itemEvent -> setAddressFieldLengthLimit());

		setValueFieldLengthLimit();
		setAddressFieldLengthLimit();
	}

	private void setAddressFieldLengthLimit()
	{
		boolean isPointer = pointerCheckBox.isSelected();
		DialogUtilities.setHexadecimalFormatter(addressField, isPointer ? 4 : 8);
	}

	private void setValueFieldLengthLimit()
	{
		ValueSize valueSize = valueSizeSelection.getItemAt(valueSizeSelection.getSelectedIndex());
		DialogUtilities.setHexadecimalFormatter(valueField, valueSize.getSize());
	}

	private String getOffset()
	{
		String offset = "0000";

		if (pointerCheckBox.isSelected())
		{
			offset = CodeWizardDialog.getPaddedValue(addressField, 4);
		}

		return offset;
	}

	private String generateCode()
	{
		StringBuilder codeBuilder = new StringBuilder();
		codeBuilder.append(CodeType.RAM_WRITE.getValue());
		codeBuilder.append(DialogUtilities.getBooleanInt(pointerCheckBox));
		codeBuilder.append(valueSizeSelection.getItemAt(valueSizeSelection.getSelectedIndex()).getValue());
		codeBuilder.append(getOffset());

		if (!pointerCheckBox.isSelected())
		{
			codeBuilder.append(CodeWizardDialog.getPaddedValue(addressField));
		}

		codeBuilder.append(CodeWizardDialog.getPaddedValue(valueField));

		return CheatCodeFormatter.format(codeBuilder.toString(), !pointerCheckBox.isSelected());
	}
}