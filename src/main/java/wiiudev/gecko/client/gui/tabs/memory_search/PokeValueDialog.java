package wiiudev.gecko.client.gui.tabs.memory_search;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.gui.input_filters.HexadecimalInputFilter;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;
import wiiudev.gecko.client.memory_search.enumerations.ValueSize;

import javax.swing.*;
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

		pokeButton.addActionListener(actionEvent ->
		{
			shouldPoke = true;
			dispose();
		});

		HexadecimalInputFilter.setHexadecimalInputFilter(pokeValueField, valueSize.getBytesCount() * 2);
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