package wiiudev.gecko.client.gui.tabs.threads;

import wiiudev.gecko.client.gui.utilities.WindowUtilities;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThread;

import javax.swing.*;

public class ChangeThreadNameDialog extends JDialog
{
	private JPanel contentPane;
	private JButton setButton;
	private JTextField threadNameField;
	private boolean confirmed;

	public ChangeThreadNameDialog(OSThread thread)
	{
		setContentPane(contentPane);
		setModal(true);
		setTitle("Change Thread Name");
		WindowUtilities.setIconImage(this);
		pack();

		threadNameField.setText(thread.getName());

		setButton.addActionListener(actionEvent ->
		{
			confirmed = true;
			dispose();
		});
	}

	public boolean isConfirmed()
	{
		return confirmed;
	}

	public String getName()
	{
		return threadNameField.getText();
	}
}