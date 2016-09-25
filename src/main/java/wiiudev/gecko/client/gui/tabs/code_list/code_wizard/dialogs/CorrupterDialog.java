package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.DialogUtilities;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeTypes;

import javax.swing.*;
import java.text.ParseException;

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
		DialogUtilities.setFrameProperties(this, contentPane);
		DialogUtilities.addGenerateCodeButtonListener(this,
				generateButton,
				this::generateCode,
				true);

		try
		{
			DialogUtilities.addHexadecimalFormatterTo(startingAddressField);
			DialogUtilities.addHexadecimalFormatterTo(endAddressField);
			DialogUtilities.addHexadecimalFormatterTo(valueField);
			DialogUtilities.addHexadecimalFormatterTo(replacementField);
		} catch (ParseException parseException)
		{
			StackTraceUtils.handleException(rootPane, parseException);
		}
	}

	private String generateCode()
	{
		return String.valueOf(CodeTypes.CORRUPTER.getValue()) +
				"000000" +
				CodeWizardDialog.doPadding(startingAddressField.getText()) +
				CodeWizardDialog.doPadding(endAddressField.getText()) +
				CodeWizardDialog.doPadding(valueField.getText()) +
				CodeWizardDialog.doPadding(replacementField.getText());
	}
}