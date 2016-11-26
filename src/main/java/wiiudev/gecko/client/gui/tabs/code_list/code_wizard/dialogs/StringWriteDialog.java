package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CheatCodeFormatter;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeType;

import javax.swing.*;

public class StringWriteDialog extends JDialog
{
	private JPanel contentPane;
	private JButton generateButton;
	private JFormattedTextField addressField;
	private JTextArea stringArea;
	private JCheckBox pointerCheckBox;

	public StringWriteDialog()
	{
		DialogUtilities.setCodeGeneratorDialogProperties(this,
				generateButton,
				contentPane,
				this::generateCode);
		setSize(300, 300);

		DialogUtilities.setHexadecimalFormatter(addressField);
	}

	private String generateCode()
	{
		String stringWriteHexadecimal = stringArea.getText();
		int bytesCount = stringWriteHexadecimal.length();
		String paddedSize = Conversions.toHexadecimal(bytesCount, 4);

		String generatedCode = CodeType.STRING_WRITE.getValue()
				+ DialogUtilities.getBooleanInt(pointerCheckBox)
				+ "0"
				+ paddedSize
				+ CodeWizardDialog.getPaddedValue(addressField)
				+ Conversions.asciiToHexadecimal(stringWriteHexadecimal);

		return CheatCodeFormatter.formatWithPadding(generatedCode);
	}
}