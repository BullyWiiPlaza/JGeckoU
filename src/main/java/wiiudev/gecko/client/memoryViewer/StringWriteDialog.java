package wiiudev.gecko.client.memoryViewer;

import wiiudev.gecko.client.conversion.Conversions;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.utilities.DefaultContextMenu;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class StringWriteDialog extends JDialog
{
	private JPanel contentPane;
	private JButton pokeButton;
	private JButton cancelButton;
	private JTextArea textArea;
	private JCheckBox unicodeCheckBox;
	private JLabel instructionsLabel;
	private int targetAddress;

	public StringWriteDialog(int targetAddress, String dialogTitle)
	{
		setDialogProperties(dialogTitle);

		this.targetAddress = targetAddress;
		instructionsLabel.setText("Please type the text to write to address " + Conversions.toHexadecimal(targetAddress) + " below:");

		pokeButton.addActionListener(actionEvent -> onOK());
		cancelButton.addActionListener(actionEvent -> onCancel());

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent windowEvent)
			{
				onCancel();
			}
		});

		new DefaultContextMenu().addTo(textArea);

		contentPane.registerKeyboardAction(actionEvent -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void setDialogProperties(String dialogTitle)
	{
		setTitle(dialogTitle);
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(pokeButton);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(400, 300);
		WindowUtilities.setIconImage(this);
	}

	private void writeText()
	{
		try
		{
			String text = textArea.getText();

			// By default, just convert UTF-8 to hexadecimal
			byte[] bytes = Conversions.getNullTerminatedBytes(text);

			// If unicode is desired, overwrite
			if (unicodeCheckBox.isSelected())
			{
				bytes = Conversions.toUnicode(bytes);
			}

			// Write the bytes and update the memory viewer as well
			MemoryWriter memoryWriter = new MemoryWriter();
			memoryWriter.writeBytes(targetAddress, bytes);
			JGeckoUGUI.getInstance().updateMemoryViewer();
		} catch (IOException exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	private void onOK()
	{
		writeText();
		dispose();
	}

	private void onCancel()
	{
		dispose();
	}
}