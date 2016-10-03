package wiiudev.gecko.client.gui.tabs.threads;

import wiiudev.gecko.client.gui.utilities.WindowUtilities;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThread;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
		WindowUtilities.setIconImage(this);
		pack();

		threadNameField.setText(thread.getName());

		setButton.addActionListener(actionEvent -> set());

		threadNameField.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent keyEvent)
			{
			}

			public void keyTyped(KeyEvent keyEvent)
			{
			}

			public void keyPressed(KeyEvent keyEvent)
			{
				if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
				{
					set();
				}
			}
		});
	}

	private void set()
	{
		confirmed = true;
		dispose();
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