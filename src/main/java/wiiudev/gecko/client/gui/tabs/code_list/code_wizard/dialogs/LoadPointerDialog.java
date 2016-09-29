package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeType;

import javax.swing.*;

public class LoadPointerDialog extends JDialog
{
	private JPanel contentPane;
	private JButton generateButton;
	private JFormattedTextField addressField;
	private JFormattedTextField memoryRange1StartingAddress;
	private JFormattedTextField memoryRange1EndAddress;
	private JCheckBox pointerInPointerCheckBox;

	private JFormattedTextField memoryRange2StartingAddress;
	private JFormattedTextField memoryRange2EndAddress;
	private JFormattedTextField addOffsetField;

	public LoadPointerDialog()
	{
		DialogUtilities.setCodeGeneratorDialogProperties(this,
				generateButton,
				contentPane,
				this::generateCode);

		DialogUtilities.setHexadecimalFormatter(addressField);
		DialogUtilities.setHexadecimalFormatter(memoryRange1StartingAddress);
		DialogUtilities.setHexadecimalFormatter(memoryRange1EndAddress);
		DialogUtilities.setHexadecimalFormatter(memoryRange2StartingAddress);
		DialogUtilities.setHexadecimalFormatter(memoryRange2EndAddress);
		DialogUtilities.setHexadecimalFormatter(addOffsetField);

		pointerInPointerCheckBox.addItemListener(itemEvent -> setComponentsAvailability());

		setComponentsAvailability();
	}

	private void setComponentsAvailability()
	{
		boolean pointerInPointer = pointerInPointerCheckBox.isSelected();
		memoryRange2StartingAddress.setEnabled(pointerInPointer);
		memoryRange2EndAddress.setEnabled(pointerInPointer);
		addOffsetField.setEnabled(pointerInPointer);
	}

	private String generateCode()
	{
		StringBuilder codeGenerator = new StringBuilder();
		codeGenerator.append(CodeType.LOAD_POINTER.getValue());
		codeGenerator.append("000000");
		codeGenerator.append(CodeWizardDialog.getPaddedValue(addressField));
		codeGenerator.append(CodeWizardDialog.getPaddedValue(memoryRange1StartingAddress));
		codeGenerator.append(CodeWizardDialog.getPaddedValue(memoryRange1EndAddress));

		if (pointerInPointerCheckBox.isSelected())
		{
			codeGenerator.append(PointerAddOffsetDialog.generatePointerAddOffsetCode(addOffsetField));
			codeGenerator.append(CodeType.LOAD_POINTER.getValue());
			codeGenerator.append("100000");
			codeGenerator.append("00000000");
			codeGenerator.append(CodeWizardDialog.getPaddedValue(memoryRange2StartingAddress));
			codeGenerator.append(CodeWizardDialog.getPaddedValue(memoryRange2EndAddress));
			codeGenerator.append(CodeWizardDialog.generateTerminatorLine());
		}

		return codeGenerator.toString();
	}
}