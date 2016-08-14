package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;

public class MessageTextDialog extends JDialog
{
	private JPanel contentPane;
	private JButton okayButton;
	private JTextArea messageTextArea;
	private JCheckBox haltSystemCheckBox;
	private boolean confirmed;

	public MessageTextDialog()
	{
		setFrameProperties();

		okayButton.addActionListener(actionEvent ->
		{
			confirmed = true;
			dispose();
		});

		// Always halting for now
		haltSystemCheckBox.setVisible(false);
	}

	private void setFrameProperties()
	{
		setContentPane(contentPane);
		setModal(true);
		WindowUtilities.setIconImage(this);
		setSize(400, 400);
	}

	public boolean isConfirmed()
	{
		return confirmed;
	}

	public String getMessageText()
	{
		return messageTextArea.getText();
	}

	public boolean shouldHaltSystem()
	{
		return haltSystemCheckBox.isSelected();
	}
}