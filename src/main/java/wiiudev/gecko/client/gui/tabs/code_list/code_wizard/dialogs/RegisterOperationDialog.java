package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.RegisterOperation;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.ValueSize;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class RegisterOperationDialog extends JDialog
{
	private JPanel contentPane;
	private JComboBox<Integer> destinationRegisterSelection;
	private JComboBox<Integer> sourceRegisterSelection;
	private JComboBox<RegisterOperation> operationSelection;
	private JComboBox<RegisterDataType> dataTypeSelection;
	private JFormattedTextField valueField;
	private JCheckBox directValueCheckBox;
	private JButton generateButton;
	private JComboBox<OperationType> operationTypeSelection;
	private JComboBox<ValueSize> valueSizeSelection;
	private JCheckBox pointerCheckBox;

	public RegisterOperationDialog()
	{
		DialogUtilities.setCodeGeneratorDialogProperties(this,
				generateButton,
				contentPane,
				this::generateCode);

		addRegisterOperations();
		addDataTypes();

		populateOperationTypes();
		populateValueSizes();
		valueSizeSelection.addItemListener(itemEvent -> setValueSizeLengthLimit());

		setValueSizeLengthLimit();

		dataTypeSelection.addItemListener(itemEvent -> setComponentsAvailability());

		DialogUtilities.addRegisterItems(destinationRegisterSelection);
		DialogUtilities.addRegisterItems(sourceRegisterSelection);

		directValueCheckBox.addItemListener(itemEvent -> setComponentsAvailability());

		setComponentsAvailability();

		operationTypeSelection.addItemListener(itemEvent ->
		{
			if (itemEvent.getStateChange() == ItemEvent.SELECTED)
			{
				setComponentsAvailability();
			}
		});
	}

	private void handleOperationTypeSelectionAvailability()
	{
		OperationType operationType = operationTypeSelection.getItemAt(operationTypeSelection.getSelectedIndex());
		boolean usingOperation = operationType == OperationType.OPERATION;

		pointerCheckBox.setEnabled(!usingOperation);

		if (usingOperation)
		{
			pointerCheckBox.setSelected(false);
			valueSizeSelection.setSelectedItem(ValueSize.THIRTY_TWO_BIT);
		} else
		{
			directValueCheckBox.setSelected(false);
		}

		boolean usingFloat = dataTypeSelection.getItemAt(dataTypeSelection.getSelectedIndex()) == RegisterDataType.FLOAT;

		if(usingFloat)
		{
			valueSizeSelection.setSelectedItem(ValueSize.THIRTY_TWO_BIT);
		}

		valueSizeSelection.setEnabled(!usingOperation && !usingFloat);
		operationSelection.setEnabled(usingOperation);
		directValueCheckBox.setEnabled(usingOperation);
	}

	private void setValueSizeLengthLimit()
	{
		ValueSize valueSize = valueSizeSelection.getItemAt(valueSizeSelection.getSelectedIndex());
		DialogUtilities.setHexadecimalFormatter(valueField, valueSize.getSize());
	}

	private void populateOperationTypes()
	{
		DefaultComboBoxModel<OperationType> comboBoxModel = new DefaultComboBoxModel<>(OperationType.values());
		operationTypeSelection.setModel(comboBoxModel);
	}

	private void populateValueSizes()
	{
		DefaultComboBoxModel<ValueSize> comboBoxModel = new DefaultComboBoxModel<>(ValueSize.values());
		valueSizeSelection.setModel(comboBoxModel);
	}

	private void manageApplicableOperations()
	{
		RegisterOperation selectedRegisterOperation = operationSelection.getItemAt(operationSelection.getSelectedIndex());

		// Restore all operations
		addRegisterOperations();

		RegisterDataType registerDataType = dataTypeSelection.getItemAt(dataTypeSelection.getSelectedIndex());

		if (registerDataType == RegisterDataType.INTEGER)
		{
			operationSelection.removeItem(RegisterOperation.FLOAT_TO_INT);
		}

		if (directValueCheckBox.isSelected())
		{
			// Remove the register operations
			operationSelection.removeItem(RegisterOperation.ADD_REGISTER);
			operationSelection.removeItem(RegisterOperation.SUBTRACT_REGISTER);
			operationSelection.removeItem(RegisterOperation.MULTIPLY_REGISTER);
			operationSelection.removeItem(RegisterOperation.DIVIDE_REGISTER);
			operationSelection.removeItem(RegisterOperation.FLOAT_TO_INT);
		} else
		{
			// Remove the value operations
			operationSelection.removeItem(RegisterOperation.ADD_VALUE);
			operationSelection.removeItem(RegisterOperation.SUBTRACT_VALUE);
			operationSelection.removeItem(RegisterOperation.MULTIPLY_VALUE);
			operationSelection.removeItem(RegisterOperation.DIVIDE_VALUE);
		}

		operationSelection.setSelectedItem(selectedRegisterOperation);
	}

	private void addRegisterOperations()
	{
		DefaultComboBoxModel<RegisterOperation> comboBoxModel = new DefaultComboBoxModel<>(RegisterOperation.values());
		operationSelection.setModel(comboBoxModel);
	}

	private void addDataTypes()
	{
		DefaultComboBoxModel<RegisterDataType> comboBoxModel = new DefaultComboBoxModel<>(RegisterDataType.values());
		dataTypeSelection.setModel(comboBoxModel);
	}

	private void setComponentsAvailability()
	{
		OperationType operationType = operationTypeSelection.getItemAt(operationTypeSelection.getSelectedIndex());
		boolean usingOperation = operationType == OperationType.OPERATION;
		boolean usingDirectValue = directValueCheckBox.isSelected();
		valueField.setEnabled((usingOperation && usingDirectValue) | !usingOperation);
		sourceRegisterSelection.setEnabled(usingOperation && !usingDirectValue);

		manageApplicableOperations();
		handleOperationTypeSelectionAvailability();
	}

	private String getCodeType()
	{
		RegisterDataType registerDataType = dataTypeSelection.getItemAt(dataTypeSelection.getSelectedIndex());
		OperationType operationType = operationTypeSelection.getItemAt(operationTypeSelection.getSelectedIndex());

		switch (registerDataType)
		{
			case INTEGER:
				switch (operationType)
				{
					case LOAD:
						return "10";

					case STORE:
						return "11";

					case OPERATION:
						return "14";
				}

			case FLOAT:
				switch (operationType)
				{
					case LOAD:
						return "12";

					case STORE:
						return "13";

					case OPERATION:
						return "15";
				}
		}

		throw new IllegalArgumentException("Code type matching failed!");
	}

	private String getRegistersValue()
	{
		String sourceRegister = "0";

		if (sourceRegisterSelection.isEnabled())
		{
			sourceRegister = sourceRegisterSelection.getSelectedItem().toString();
		}

		String registers = "0" + destinationRegisterSelection.getSelectedItem() + "0" + sourceRegister;

		OperationType operationType = operationTypeSelection.getItemAt(operationTypeSelection.getSelectedIndex());

		if (operationType == OperationType.STORE || operationType == OperationType.LOAD)
		{
			registers = "000" + destinationRegisterSelection.getSelectedItem();
		}

		return registers;
	}

	private String generateCode()
	{
		String codeType = getCodeType();
		RegisterOperation registerOperation = operationSelection.getItemAt(operationSelection.getSelectedIndex());
		String value = "00000000";

		if (valueField.isEnabled())
		{
			value = CodeWizardDialog.getPaddedValue(valueField);
		}

		int pointer = 0;

		if (pointerCheckBox.isSelected())
		{
			pointer = 1;
		}

		String registers = getRegistersValue();

		return codeType
				+ pointer
				+ registerOperation.getValue()
				+ registers
				+ value;
	}
}