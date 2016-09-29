package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeType;

import javax.swing.*;

public class CorrupterDialog extends JDialog
{
	protected JPanel contentPane;
	private JFormattedTextField startingAddressField;
	private JFormattedTextField endAddressField;
	private JFormattedTextField valueField;
	private JFormattedTextField replacementField;
	private JButton generateButton;

	public CorrupterDialog()
	{
		DialogUtilities.setCodeGeneratorDialogProperties(this,
				generateButton,
				contentPane,
				this::generateCode);

		DialogUtilities.setHexadecimalFormatter(startingAddressField);
		DialogUtilities.setHexadecimalFormatter(endAddressField);
		DialogUtilities.setHexadecimalFormatter(valueField);
		DialogUtilities.setHexadecimalFormatter(replacementField);
	}

	private String generateCode()
	{
		return String.valueOf(CodeType.CORRUPTER.getValue()) +
				"000000" +
				CodeWizardDialog.doPadding(startingAddressField.getText()) +
				CodeWizardDialog.doPadding(endAddressField.getText()) +
				CodeWizardDialog.doPadding(valueField.getText()) +
				CodeWizardDialog.doPadding(replacementField.getText());
	}
}