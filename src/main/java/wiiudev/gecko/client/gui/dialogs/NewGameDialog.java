package wiiudev.gecko.client.gui.dialogs;

import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NewGameDialog extends JDialog
{
	private JPanel contentPane;
	private JButton okayButton;
	private JButton cancelButton;
	private JTextField gameNameField;
	private JTextField gameIdField;
	private String gameName;
	private String gameId;
	private boolean confirmed;

	public NewGameDialog(JFrame frame)
	{
		setLocationRelativeTo(frame);
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(okayButton);

		okayButton.addActionListener(e -> onConfirmed());

		cancelButton.addActionListener(e -> onCancel());

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				onCancel();
			}
		});

		contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void onConfirmed()
	{
		confirmed = true;
		gameName = gameNameField.getText();
		gameId = gameIdField.getText();
		dispose();
	}

	private void onCancel()
	{
		dispose();
	}

	public void display()
	{
		WindowUtilities.setIconImage(this);
		setTitle("Please enter!");
		pack();
		setVisible(true);
	}

	public String getGameName()
	{
		return gameName;
	}

	public String getGameId()
	{
		return gameId;
	}

	public boolean confirmed()
	{
		return confirmed;
	}

	public static void main(String[] args)
	{
		NewGameDialog newGameDialog = new NewGameDialog(null);
		newGameDialog.display();
	}
}