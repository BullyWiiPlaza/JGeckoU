package wiiudev.gecko.client.gui.tabs.memory_search;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.conversions.Validation;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;
import wiiudev.gecko.client.memory_search.enumerations.ValueSize;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigInteger;

public class PokeValueDialog extends JDialog
{
	private JPanel contentPane;
	private JTextField pokeValueField;
	private JButton pokeButton;
	private boolean shouldPoke;
	private ValueSize valueSize;

	public PokeValueDialog(BigInteger defaultValue, ValueSize valueSize)
	{
		this.valueSize = valueSize;
		setFrameProperties(Conversions.toHexadecimal(defaultValue, valueSize));

		pokeButton.addActionListener(actionEvent -> confirmPoke());

		ValueConversionContextMenu valueConversionContextMenu = new ValueConversionContextMenu(pokeValueField);
		valueConversionContextMenu.addContextMenu();

		pokeValueField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				setPokeValueAvailability();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				setPokeValueAvailability();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				setPokeValueAvailability();
			}
		});

		setPokeValueAvailability();

		pokeValueField.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent keyEvent)
			{
			}

			public void keyTyped(KeyEvent keyEvent)
			{
			}

			public void keyPressed(KeyEvent keyEvent)
			{
				if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER
						&& pokeButton.isEnabled())
				{
					confirmPoke();
				}
			}
		});
	}

	private void setPokeValueAvailability()
	{
		boolean valueOkay = isValueOkay();
		pokeValueField.setBackground(valueOkay ? Color.GREEN : Color.RED);
		pokeButton.setEnabled(valueOkay);
	}

	private boolean isValueOkay()
	{
		String value = pokeValueField.getText();
		boolean isHexadecimal = Validation.isHexadecimal(value);
		boolean isLengthOkay = value.length() <= valueSize.getBytesCount() * 2;

		return isHexadecimal && isLengthOkay;
	}

	private void confirmPoke()
	{
		shouldPoke = true;
		dispose();
	}

	private void setFrameProperties(String defaultValue)
	{
		setContentPane(contentPane);
		setModal(true);
		WindowUtilities.setIconImage(this);
		setTitle("Poke");
		pokeValueField.setText(defaultValue);
		pack();
	}

	public boolean shouldPoke()
	{
		return shouldPoke;
	}

	public byte[] getValueBytes()
	{
		String value = pokeValueField.getText();
		value = Conversions.prependPadding(value, valueSize.getBytesCount() * 2);
		return Conversions.hexStringToByteArray(value);
	}
}