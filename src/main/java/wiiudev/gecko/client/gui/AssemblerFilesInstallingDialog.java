package wiiudev.gecko.client.gui;

import javax.swing.*;

public class AssemblerFilesInstallingDialog extends JDialog
{
	private JPanel contentPane;

	public AssemblerFilesInstallingDialog()
	{
		setContentPane(contentPane);
		setModal(true);
		setTitle("Downloading...");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
	}
}