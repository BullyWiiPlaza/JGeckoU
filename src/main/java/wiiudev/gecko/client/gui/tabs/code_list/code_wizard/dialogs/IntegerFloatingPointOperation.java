package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.DialogUtilities;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.RegisterOperation;

import javax.swing.*;
import java.text.ParseException;

public class IntegerFloatingPointOperation extends JDialog
{
	private JPanel contentPane;
	private JComboBox<Integer> destinationRegisterSelection;
	private JComboBox<Integer> sourceRegisterSelection;
	private JComboBox<RegisterOperation> registerOperationSelection;
	private JComboBox<RegisterDataType> dataTypeSelection;
	private JFormattedTextField valueField;
	private JCheckBox directValueCheckBox;
	private JButton generateButton;

	public IntegerFloatingPointOperation()
	{
		DialogUtilities.setCodeGeneratorDialogProperties(this,
				generateButton,
				contentPane,
				this::generateCode);

		try
		{
			DialogUtilities.addHexadecimalFormatterTo(valueField);
		} catch (ParseException parseException)
		{
			StackTraceUtils.handleException(rootPane, parseException);
		}

		addRegisterOperations();
		addDataTypes();

		dataTypeSelection.addItemListener(itemEvent -> setComponentsAvailability());

		addItems(destinationRegisterSelection);
		addItems(sourceRegisterSelection);

		directValueCheckBox.addItemListener(itemEvent -> setComponentsAvailability());

		setComponentsAvailability();
	}

	private void manageApplicableOperations()
	{
		RegisterOperation selectedRegisterOperation = registerOperationSelection.getItemAt(registerOperationSelection.getSelectedIndex());

		// Restore all operations
		addRegisterOperations();

		RegisterDataType registerDataType = dataTypeSelection.getItemAt(dataTypeSelection.getSelectedIndex());

		if (registerDataType == RegisterDataType.INTEGER)
		{
			registerOperationSelection.removeItem(RegisterOperation.FLOAT_TO_INT);
		}

		if (directValueCheckBox.isSelected())
		{
			// Remove the register operations
			registerOperationSelection.removeItem(RegisterOperation.ADD_REGISTER);
			registerOperationSelection.removeItem(RegisterOperation.SUBTRACT_REGISTER);
			registerOperationSelection.removeItem(RegisterOperation.MULTIPLY_REGISTER);
			registerOperationSelection.removeItem(RegisterOperation.DIVIDE_REGISTER);
			registerOperationSelection.removeItem(RegisterOperation.FLOAT_TO_INT);
		} else
		{
			// Remove the value operations
			registerOperationSelection.removeItem(RegisterOperation.ADD_VALUE);
			registerOperationSelection.removeItem(RegisterOperation.SUBTRACT_VALUE);
			registerOperationSelection.removeItem(RegisterOperation.MULTIPLY_VALUE);
			registerOperationSelection.removeItem(RegisterOperation.DIVIDE_VALUE);
		}

		registerOperationSelection.setSelectedItem(selectedRegisterOperation);
	}

	private void addRegisterOperations()
	{
		DefaultComboBoxModel<RegisterOperation> comboBoxModel = new DefaultComboBoxModel<>(RegisterOperation.values());
		registerOperationSelection.setModel(comboBoxModel);
	}

	private void addDataTypes()
	{
		DefaultComboBoxModel<RegisterDataType> comboBoxModel = new DefaultComboBoxModel<>(RegisterDataType.values());
		dataTypeSelection.setModel(comboBoxModel);
	}

	private void setComponentsAvailability()
	{
		boolean usingDirectValue = directValueCheckBox.isSelected();
		valueField.setEnabled(usingDirectValue);
		sourceRegisterSelection.setEnabled(!usingDirectValue);

		manageApplicableOperations();
	}

	private void addItems(JComboBox<Integer> registerComboBox)
	{
		for (int registerIndex = 0; registerIndex < 8; registerIndex++)
		{
			registerComboBox.addItem(registerIndex);
		}
	}

	private String generateCode()
	{
		RegisterDataType registerDataType = dataTypeSelection.getItemAt(dataTypeSelection.getSelectedIndex());
		String codeType = registerDataType.getCodeType();
		RegisterOperation registerOperation = registerOperationSelection.getItemAt(registerOperationSelection.getSelectedIndex());
		String value = "00000000";

		if (directValueCheckBox.isSelected())
		{
			value = CodeWizardDialog.getPaddedValue(valueField);
		}

		String sourceRegister = sourceRegisterSelection.getSelectedItem().toString();

		if (directValueCheckBox.isSelected())
		{
			sourceRegister = "0";
		}

		return codeType +
				registerOperation.getValue() + "0"
				+ destinationRegisterSelection.getSelectedItem() + "0"
				+ sourceRegister
				+ value;
	}
}