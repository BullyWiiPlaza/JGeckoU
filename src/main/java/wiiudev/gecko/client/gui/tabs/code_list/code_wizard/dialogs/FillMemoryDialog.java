package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.DialogUtilities;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeTypes;

import javax.swing.*;
import java.text.ParseException;

public class FillMemoryDialog extends JDialog
{
	private JPanel contentPane;
	private JButton generateButton;
	private JFormattedTextField addressField;
	private JFormattedTextField lengthField;
	private JFormattedTextField valueField;
	private JCheckBox pointerCheckBox;

	public FillMemoryDialog()
	{
		DialogUtilities.setCodeGeneratorDialogProperties(this,
				generateButton,
				contentPane,
				this::generateCode);

		try
		{
			DialogUtilities.addHexadecimalFormatterTo(addressField);
			DialogUtilities.addHexadecimalFormatterTo(valueField);
			DialogUtilities.addHexadecimalFormatterTo(lengthField);
		} catch (ParseException parseException)
		{
			StackTraceUtils.handleException(rootPane, parseException);
		}
	}

	private String generateCode()
	{
		return CodeTypes.FILL_MEMORY.getValue() + getPointerValue() + "00000"
				+ CodeWizardDialog.getPaddedValue(valueField)
				+ CodeWizardDialog.getPaddedValue(addressField)
				+ CodeWizardDialog.getPaddedValue(lengthField);
	}

	private int getPointerValue()
	{
		return pointerCheckBox.isSelected() ? 1 : 0;
	}
}