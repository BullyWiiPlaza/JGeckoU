package wiiudev.gecko.client.gui.tabs.memory_viewer;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.dialogs.DialogUtilities;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.ValueSize;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;

import javax.swing.*;
import java.io.IOException;

public class SerialWriteDialog extends JDialog
{
	private JPanel contentPane;
	private JFormattedTextField addressField;
	private JFormattedTextField writesCountField;
	private JComboBox<ValueSize> valueSizeSelection;
	private JFormattedTextField valueField;
	private JButton writeButton;
	private JFormattedTextField valueIncrementField;

	private boolean written;

	public SerialWriteDialog(int address, String dialogTitle)
	{
		setDialogProperties(dialogTitle);

		DefaultComboBoxModel<ValueSize> comboBoxModel = new DefaultComboBoxModel<>(ValueSize.values());
		valueSizeSelection.setModel(comboBoxModel);
		valueSizeSelection.setSelectedItem(ValueSize.THIRTY_TWO_BIT);
		valueSizeSelection.setEnabled(false);

		DialogUtilities.setHexadecimalFormatter(addressField);
		DialogUtilities.setHexadecimalFormatter(valueField);
		DialogUtilities.setHexadecimalFormatter(writesCountField);

		addressField.setText(Conversions.toHexadecimal(address));
		valueIncrementField.setText(Conversions.toHexadecimal(0, 8));

		try
		{
			MemoryReader memoryReader = new MemoryReader();
			int value = memoryReader.readInt(address);
			valueField.setText(Conversions.toHexadecimal(value));
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}

		writesCountField.setText(Conversions.toHexadecimal(1, 8));

		writeButton.addActionListener(actionEvent ->
		{
			performSerialWrite();
			written = true;
			dispose();
		});
	}

	private void performSerialWrite()
	{
		try
		{
			MemoryWriter memoryWriter = new MemoryWriter();
			memoryWriter.serialWrite(Conversions.toDecimal(addressField.getText()),
					Conversions.toDecimal(valueField.getText()),
					Conversions.toDecimal(writesCountField.getText()),
					Conversions.toDecimal(valueIncrementField.getText()));
		} catch (IOException exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	private void setDialogProperties(String dialogTitle)
	{
		setContentPane(contentPane);
		setTitle(dialogTitle);
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(JGeckoUGUI.getInstance());
		WindowUtilities.setIconImage(this);
		pack();
	}

	public boolean hasWritten()
	{
		return written;
	}
}