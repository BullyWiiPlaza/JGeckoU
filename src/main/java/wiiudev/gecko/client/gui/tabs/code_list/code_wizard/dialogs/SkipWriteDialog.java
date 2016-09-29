package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CheatCodeFormatter;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeType;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.ValueSize;

import javax.swing.*;

public class SkipWriteDialog extends JDialog
{
	private JPanel contentPane;
	private JButton generateButton;
	private JComboBox<ValueSize> valueSizeSelection;
	private JFormattedTextField writesCountField;
	private JFormattedTextField skipBytesField;
	private JFormattedTextField valueIncrementField;
	private JFormattedTextField valueField;
	private JFormattedTextField addressField;
	private JCheckBox pointerCheckBox;

	public SkipWriteDialog()
	{
		DialogUtilities.setCodeGeneratorDialogProperties(this,
				generateButton,
				contentPane,
				this::generateCode);

		DefaultComboBoxModel<ValueSize> comboBoxModel = new DefaultComboBoxModel<>(ValueSize.values());
		valueSizeSelection.setModel(comboBoxModel);

		valueSizeSelection.addItemListener(itemEvent -> setValueSizeLimit());

		DialogUtilities.setHexadecimalFormatter(addressField);
		DialogUtilities.setHexadecimalFormatter(writesCountField, 4);
		DialogUtilities.setHexadecimalFormatter(skipBytesField);

		setValueSizeLimit();
	}

	private void setValueSizeLimit()
	{
		ValueSize valueSize = valueSizeSelection.getItemAt(valueSizeSelection.getSelectedIndex());
		DialogUtilities.setHexadecimalFormatter(valueField, valueSize.getSize());
		DialogUtilities.setHexadecimalFormatter(valueIncrementField, valueSize.getSize());
	}

	private String generateCode()
	{
		String generatedCode = CodeType.SKIP_WRITE.getValue()
				+ DialogUtilities.getBooleanInt(pointerCheckBox)
				+ valueSizeSelection.getItemAt(valueSizeSelection.getSelectedIndex()).getValue()
				+ CodeWizardDialog.getPaddedValue(writesCountField, 4)
				+ CodeWizardDialog.getPaddedValue(addressField)
				+ CodeWizardDialog.getPaddedValue(valueField)
				+ CodeWizardDialog.getPaddedValue(skipBytesField)
				+ CodeWizardDialog.getPaddedValue(valueIncrementField);

		return CheatCodeFormatter.format(generatedCode, true);
	}
}