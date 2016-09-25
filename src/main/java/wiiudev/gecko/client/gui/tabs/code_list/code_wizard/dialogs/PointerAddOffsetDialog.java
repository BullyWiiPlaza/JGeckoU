package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.DialogUtilities;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeTypes;

import javax.swing.*;
import java.text.ParseException;

public class PointerAddOffsetDialog extends JDialog
{
	private JPanel contentPane;
	private JFormattedTextField addOffsetField;
	private JButton generateButton;

	public PointerAddOffsetDialog()
	{
		DialogUtilities.setFrameProperties(this, contentPane);
		DialogUtilities.addGenerateCodeButtonListener(this,
				generateButton,
				this::generateCode,
				false);

		try
		{
			DialogUtilities.addHexadecimalFormatterTo(addOffsetField);
		} catch (ParseException parseException)
		{
			parseException.printStackTrace();
		}
	}

	private String generateCode()
	{
		return generatePointerAddOffsetCode(addOffsetField);
	}

	public static String generatePointerAddOffsetCode(JFormattedTextField addOffsetField)
	{
		return CodeTypes.ADD_POINTER_OFFSET.getValue()
				+ "000000"
				+ CodeWizardDialog.doPadding(addOffsetField.getText());
	}
}