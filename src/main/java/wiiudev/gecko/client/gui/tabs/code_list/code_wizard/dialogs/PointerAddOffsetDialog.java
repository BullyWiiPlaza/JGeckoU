package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeType;

import javax.swing.*;

public class PointerAddOffsetDialog extends JDialog
{
	private JPanel contentPane;
	private JFormattedTextField addOffsetField;
	private JButton generateButton;

	public PointerAddOffsetDialog()
	{
		DialogUtilities.setCodeGeneratorDialogProperties(this,
				generateButton,
				contentPane,
				this::generateCode);

		DialogUtilities.setHexadecimalFormatter(addOffsetField);
	}

	private String generateCode()
	{
		return generatePointerAddOffsetCode(addOffsetField);
	}

	public static String generatePointerAddOffsetCode(JFormattedTextField addOffsetField)
	{
		return CodeType.ADD_POINTER_OFFSET.getValue()
				+ "000000"
				+ CodeWizardDialog.getPaddedValue(addOffsetField);
	}
}