package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeType;

import javax.swing.*;

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

		DialogUtilities.setHexadecimalFormatter(addressField);
		DialogUtilities.setHexadecimalFormatter(valueField);
		DialogUtilities.setHexadecimalFormatter(lengthField);
	}

	private String generateCode()
	{
		return CodeType.FILL_MEMORY.getValue() +
				DialogUtilities.getBooleanInt(pointerCheckBox)
				+ "00000"
				+ CodeWizardDialog.getPaddedValue(valueField)
				+ CodeWizardDialog.getPaddedValue(addressField)
				+ CodeWizardDialog.getPaddedValue(lengthField);
	}
}