package wiiudev.gecko.client.gui.tabs.memory_viewer;

import wiiudev.gecko.client.gui.inputFilter.HexadecimalInputFilter;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class OffsetDialog extends JDialog
{
	private JPanel contentPane;
	private JButton okayButton;
	private JButton cancelButton;
	private JTextField offsetField;
	private boolean confirmed;

	public OffsetDialog(JRootPane rootPane)
	{
		setContentPane(contentPane);
		setModal(true);
		WindowUtilities.setIconImage(this);
		setLocationRelativeTo(rootPane);
		getRootPane().setDefaultButton(okayButton);
		setTitle("Add Offset");
		HexadecimalInputFilter.addHexadecimalInputFilter(offsetField);
		okayButton.addActionListener(actionEvent -> onOK());
		cancelButton.addActionListener(actionEvent -> onCancel());

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent windowEvent)
			{
				onCancel();
			}
		});

		contentPane.registerKeyboardAction(actionEvent -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		pack();
	}

	private void onOK()
	{
		confirmed = true;
		dispose();
	}

	public boolean isConfirmed()
	{
		return confirmed;
	}

	public int getOffset()
	{
		return (int) Long.parseLong(offsetField.getText(), 16);
	}

	private void onCancel()
	{
		dispose();
	}
}
