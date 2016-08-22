package wiiudev.gecko.client.gui.dialogs;

import wiiudev.gecko.client.gui.input_filters.HexadecimalInputFilter;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;

public class RemoteDisassemblerDialog extends JDialog
{
	private JPanel contentPane;
	private JButton disassembleButton;
	private JTextField valueField;
	private boolean confirmed;

	public RemoteDisassemblerDialog()
	{
		setFrameProperties();

		disassembleButton.addActionListener(actionEvent ->
		{
			confirmed = true;
			dispose();
		});

		HexadecimalInputFilter.setHexadecimalInputFilter(valueField);
	}

	public int getEnteredValue()
	{
		String value = valueField.getText();

		return Integer.parseUnsignedInt(value, 16);
	}

	private void setFrameProperties()
	{
		setContentPane(contentPane);
		setModal(true);
		WindowUtilities.setIconImage(this);
		pack();
	}

	public boolean isConfirmed()
	{
		return confirmed;
	}
}