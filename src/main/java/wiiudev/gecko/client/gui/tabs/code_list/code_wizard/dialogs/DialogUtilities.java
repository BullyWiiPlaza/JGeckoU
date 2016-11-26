package wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs;

import wiiudev.gecko.client.conversions.SystemClipboard;
import wiiudev.gecko.client.gui.input_filters.HexadecimalInputFilter;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CheatCodeFormatter;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class DialogUtilities
{
	public static void setHexadecimalFormatter(JFormattedTextField formattedTextField)
	{
		setHexadecimalFormatter(formattedTextField, 8);
	}

	public static void setHexadecimalFormatter(JFormattedTextField formattedTextField, int maximumLength)
	{
		HexadecimalInputFilter.setHexadecimalInputFilter(formattedTextField, maximumLength);
	}

	private static void setFrameProperties(JDialog dialog, JPanel contentPane)
	{
		dialog.setContentPane(contentPane);
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		WindowUtilities.setIconImage(dialog);
		dialog.pack();
	}

	public static void setCodeGeneratorDialogProperties(JDialog dialog,
	                                                    JButton generateButton,
	                                                    JPanel contentPane,
	                                                    Supplier<String> generateCode)
	{
		DialogUtilities.setFrameProperties(dialog, contentPane);
		DialogUtilities.addGenerateCodeButtonListener(dialog,
				generateButton,
				generateCode
		);
	}

	public static void addGenerateCodeButtonListener(JButton button,
	                                                 Supplier<String> supplier)
	{
		addGenerateCodeButtonListener(null, button, supplier);
	}

	private static void addGenerateCodeButtonListener(Window frame,
	                                                  JButton button,
	                                                  Supplier<String> supplier)
	{
		button.addActionListener(actionEvent ->
		{
			String generatedCode = supplier.get();
			generatedCode = CheatCodeFormatter.format(generatedCode, false);
			SystemClipboard.copy(generatedCode);
			JOptionPane.showMessageDialog(frame,
					"Code copied to the clipboard!",
					"Success",
					JOptionPane.INFORMATION_MESSAGE);

			if (frame != null)
			{
				frame.dispose();
			}
		});
	}

	static int getBooleanInt(JCheckBox checkBox)
	{
		return checkBox.isSelected() ? 1 : 0;
	}

	static void addRegisterItems(JComboBox<Integer> registerComboBox)
	{
		for (int registerIndex = 0; registerIndex < 8; registerIndex++)
		{
			registerComboBox.addItem(registerIndex);
		}
	}
}