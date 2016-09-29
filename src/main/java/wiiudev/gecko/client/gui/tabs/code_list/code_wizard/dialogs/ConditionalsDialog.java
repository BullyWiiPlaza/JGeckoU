package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CheatCodeFormatter;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeType;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.ValueSize;

import javax.swing.*;

public class ConditionalsDialog extends JDialog
{
	private JPanel contentPane;
	private JButton generateButton;
	private JFormattedTextField addressField;
	private JComboBox<ValueSize> valueSizeSelection;
	private JFormattedTextField value1Field;
	private JComboBox<CodeType> conditionSelection;
	private JCheckBox pointerCheckBox;
	private JFormattedTextField value2Field;

	public ConditionalsDialog()
	{
		DialogUtilities.setCodeGeneratorDialogProperties(this,
				generateButton,
				contentPane,
				this::generateCode);

		conditionSelection.addItem(CodeType.EQUAL);
		conditionSelection.addItem(CodeType.NOT_EQUAL);
		conditionSelection.addItem(CodeType.GREATER);
		conditionSelection.addItem(CodeType.LESS);
		conditionSelection.addItem(CodeType.GREATER_OR_EQUAL);
		conditionSelection.addItem(CodeType.LESS_OR_EQUAL);
		conditionSelection.addItem(CodeType.AND);
		conditionSelection.addItem(CodeType.OR);
		conditionSelection.addItem(CodeType.BETWEEN);

		conditionSelection.addItemListener(itemEvent -> setBetweenValueAvailability());

		DialogUtilities.setHexadecimalFormatter(addressField);

		DefaultComboBoxModel<ValueSize> comboBoxModel = new DefaultComboBoxModel<>(ValueSize.values());
		valueSizeSelection.setModel(comboBoxModel);

		valueSizeSelection.addItemListener(itemEvent -> setValueSizeLimit());

		setValueSizeLimit();
		setBetweenValueAvailability();
	}

	private void setBetweenValueAvailability()
	{
		value2Field.setEnabled(usingBetweenCodeType());
	}

	private boolean usingBetweenCodeType()
	{
		CodeType codeType = conditionSelection.getItemAt(conditionSelection.getSelectedIndex());

		return codeType == CodeType.BETWEEN;
	}

	private void setValueSizeLimit()
	{
		ValueSize valueSize = valueSizeSelection.getItemAt(valueSizeSelection.getSelectedIndex());
		DialogUtilities.setHexadecimalFormatter(value1Field, valueSize.getSize());
		DialogUtilities.setHexadecimalFormatter(value2Field, valueSize.getSize());
	}

	private String generateCode()
	{
		String value2 = "00000000";

		if (usingBetweenCodeType())
		{
			value2 = CodeWizardDialog.getPaddedValue(value2Field);
		}

		String generatedCode = conditionSelection.getItemAt(conditionSelection.getSelectedIndex()).getValue()
				+ DialogUtilities.getBooleanInt(pointerCheckBox)
				+ valueSizeSelection.getItemAt(valueSizeSelection.getSelectedIndex()).getValue()
				+ "0000"
				+ CodeWizardDialog.getPaddedValue(addressField)
				+ CodeWizardDialog.getPaddedValue(value1Field)
				+ value2;

		return CheatCodeFormatter.format(generatedCode);
	}
}