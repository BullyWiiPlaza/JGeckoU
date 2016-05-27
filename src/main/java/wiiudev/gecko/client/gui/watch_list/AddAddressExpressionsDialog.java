package wiiudev.gecko.client.gui.watch_list;

import wiiudev.gecko.client.gui.MemoryPointerExpression;
import wiiudev.gecko.client.gui.code_list.code_wizard.selections.ValueSize;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class AddAddressExpressionsDialog extends JDialog
{
	private JPanel contentPane;
	private JButton confirmButton;
	private JButton cancelButton;
	private JTextArea addressExpressionsArea;
	private List<WatchListElement> watchListElements;
	private boolean confirmed;

	public AddAddressExpressionsDialog(JRootPane rootPane, String title)
	{
		setContentPane(contentPane);
		setModal(true);
		setTitle(title);
		getRootPane().setDefaultButton(confirmButton);
		setLocationRelativeTo(rootPane);
		WindowUtilities.setIconImage(this);

		confirmButton.addActionListener(actionEvent -> onOK());

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

	public boolean isConfirmed()
	{
		return confirmed;
	}

	public List<WatchListElement> getWatchListElements()
	{
		return watchListElements;
	}

	private void onOK()
	{
		try
		{
			watchListElements = new ArrayList<>();
			String addressExpressions = addressExpressionsArea.getText();
			String[] addressExpressionsArray = addressExpressions.split("\n");

			for (int addressExpressionsIndex = 0; addressExpressionsIndex < addressExpressionsArray.length; addressExpressionsIndex++)
			{
				String addressExpression = addressExpressionsArray[addressExpressionsIndex];
				WatchListElement watchListElement = new WatchListElement("#" + (addressExpressionsIndex + 1), new MemoryPointerExpression(addressExpression), ValueSize.thirty_two_bit);
				watchListElements.add(watchListElement);
			}

			confirmed = true;
			dispose();
		}
		catch(Exception exception)
		{
			JOptionPane.showMessageDialog(this,
					exception.getMessage(),
					"Invalid Input",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onCancel()
	{
		dispose();
	}
}