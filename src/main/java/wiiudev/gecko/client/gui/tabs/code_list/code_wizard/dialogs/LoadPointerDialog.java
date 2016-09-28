package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.DialogUtilities;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeTypes;

import javax.swing.*;
import java.text.ParseException;

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

		try
		{
			DialogUtilities.addHexadecimalFormatterTo(addressField);
			DialogUtilities.addHexadecimalFormatterTo(memoryRange1StartingAddress);
			DialogUtilities.addHexadecimalFormatterTo(memoryRange1EndAddress);
			DialogUtilities.addHexadecimalFormatterTo(memoryRange2StartingAddress);
			DialogUtilities.addHexadecimalFormatterTo(memoryRange2EndAddress);
			DialogUtilities.addHexadecimalFormatterTo(addOffsetField);
		} catch (ParseException parseException)
		{
			StackTraceUtils.handleException(rootPane, parseException);
		}

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
		codeGenerator.append(CodeTypes.LOAD_POINTER.getValue());
		codeGenerator.append("000000");
		codeGenerator.append(CodeWizardDialog.getPaddedValue(addressField));
		codeGenerator.append(CodeWizardDialog.getPaddedValue(memoryRange1StartingAddress));
		codeGenerator.append(CodeWizardDialog.getPaddedValue(memoryRange1EndAddress));

		if (pointerInPointerCheckBox.isSelected())
		{
			codeGenerator.append(PointerAddOffsetDialog.generatePointerAddOffsetCode(addOffsetField));
			codeGenerator.append(CodeTypes.LOAD_POINTER.getValue());
			codeGenerator.append("100000");
			codeGenerator.append("00000000");
			codeGenerator.append(CodeWizardDialog.getPaddedValue(memoryRange2StartingAddress));
			codeGenerator.append(CodeWizardDialog.getPaddedValue(memoryRange2EndAddress));
			codeGenerator.append(CodeWizardDialog.generateTerminatorLine());
		}

		return codeGenerator.toString();
	}
}