package wiiudev.gecko.client.gui.watch_list;

import wiiudev.gecko.client.gui.MemoryPointerExpression;
import wiiudev.gecko.client.gui.code_list.code_wizard.selections.ValueSize;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AddWatchDialog extends JDialog
{
	private JPanel contentPane;
	private JButton confirmButton;
	private JButton cancelButton;
	private JTextField nameField;
	private JTextField addressField;
	private JComboBox<ValueSize> valueSizeComboBox;

	private boolean confirmed;
	private WatchListElement watchListElement;

	public AddWatchDialog(JRootPane rootPane, String title)
	{
		setContentPane(contentPane);
		setLocationRelativeTo(rootPane);
		setModal(true);
		getRootPane().setDefaultButton(confirmButton);
		setTitle(title);
		WindowUtilities.setIconImage(this);

		confirmButton.addActionListener(actionEvent -> onOK());
		cancelButton.addActionListener(actionEvent -> onCancel());

		DefaultComboBoxModel<ValueSize> defaultComboBoxModel = new DefaultComboBoxModel<>(ValueSize.values());
		valueSizeComboBox.setModel(defaultComboBoxModel);
		valueSizeComboBox.setSelectedIndex(valueSizeComboBox.getItemCount() - 1);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent windowEvent)
			{
				onCancel();
			}
		});

		contentPane.registerKeyboardAction(actionEvent -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		nameField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				setConfirmButtonAvailability();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				setConfirmButtonAvailability();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				setConfirmButtonAvailability();
			}
		});

		addressField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				setConfirmButtonAvailability();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				setConfirmButtonAvailability();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				setConfirmButtonAvailability();
			}
		});

		setConfirmButtonAvailability();
		pack();
	}

	public void setWatchListElement(WatchListElement watchListElement)
	{
		nameField.setText(watchListElement.getName());
		addressField.setText(watchListElement.getAddressExpression().toString());
		valueSizeComboBox.setSelectedItem(watchListElement.getValueSize());
	}

	private void setConfirmButtonAvailability()
	{
		boolean isValidInput = !nameField.getText().isEmpty() && !addressField.getText().isEmpty();
		confirmButton.setEnabled(isValidInput);
	}

	public WatchListElement getWatchListElement()
	{
		return watchListElement;
	}

	public boolean isConfirmed()
	{
		return confirmed;
	}

	private void onOK()
	{
		try
		{
			String name = nameField.getText();
			MemoryPointerExpression addressExpression = new MemoryPointerExpression(addressField.getText());
			ValueSize valueSize = (ValueSize) valueSizeComboBox.getSelectedItem();
			watchListElement = new WatchListElement(name, addressExpression, valueSize);
			confirmed = true;

			dispose();
		} catch (Exception exception)
		{
			JOptionPane.showMessageDialog(this,
					"Invalid address expression!",
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onCancel()
	{
		dispose();
	}
}