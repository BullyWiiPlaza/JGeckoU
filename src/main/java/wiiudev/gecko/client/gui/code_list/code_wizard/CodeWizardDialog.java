package wiiudev.gecko.client.gui.code_list.code_wizard;

import wiiudev.gecko.client.conversion.Conversions;
import wiiudev.gecko.client.conversion.SystemClipboard;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.MemoryPointerExpression;
import wiiudev.gecko.client.gui.code_list.code_wizard.selections.CodeTypes;
import wiiudev.gecko.client.gui.code_list.code_wizard.selections.IntegerOperations;
import wiiudev.gecko.client.gui.code_list.code_wizard.selections.Pointer;
import wiiudev.gecko.client.gui.code_list.code_wizard.selections.ValueSize;
import wiiudev.gecko.client.gui.inputFilter.HexadecimalInputFilter;
import wiiudev.gecko.client.gui.inputFilter.InputLengthFilter;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CodeWizardDialog extends JDialog
{
	private JPanel contentPane;
	private JButton generateButton;
	private JButton cancelButton;
	private JComboBox<CodeTypes> codeTypeSelection;
	private JComboBox<ValueSize> valueSizeSelection;
	private JComboBox<Pointer> pointerSelection;
	private JTextField addressField;
	private JTextField valueField;
	private JTextField pointerOffsetField;
	private JTextArea generatedArea;
	private JButton copyButton;
	private JTextField pointerRangeEndAddressField;
	private JTextField pointerRangeStartAddressField;
	private JTextField pointerOffset2Field;
	private JTextField pointerRangeStartAddress2Field;
	private JTextField pointerRangeEndAddress2Field;
	private JTextArea stringWriteTextArea;
	private JTextField writesCountField;
	private JTextField writesOffsetField;
	private JTextField valueIncrementField;
	private JButton parsePointerExpressionButton;
	private JTextField registerTextField;
	private JComboBox<IntegerOperations> registerOperationsComboBox;
	public static String NOP_CODE = "D1000000 DEADC0DE";

	public CodeWizardDialog()
	{
		setLocationRelativeTo(JGeckoUGUI.getInstance());
		defineDialogProperties();

		DefaultComboBoxModel<CodeTypes> codeTypesDefaultComboBoxModel = new DefaultComboBoxModel<>(CodeTypes.values());
		codeTypeSelection.setModel(codeTypesDefaultComboBoxModel);

		DefaultComboBoxModel<Pointer> pointerDefaultComboBoxModel = new DefaultComboBoxModel<>(Pointer.values());
		pointerSelection.setModel(pointerDefaultComboBoxModel);

		DefaultComboBoxModel<IntegerOperations> integerComboBoxModel = new DefaultComboBoxModel<>(IntegerOperations.values());
		registerOperationsComboBox.setModel(integerComboBoxModel);

		DefaultComboBoxModel<ValueSize> valueSizeDefaultComboBoxModel = new DefaultComboBoxModel<>(ValueSize.values());
		valueSizeSelection.setModel(valueSizeDefaultComboBoxModel);
		valueSizeSelection.setSelectedItem(ValueSize.thirty_two_bit);

		HexadecimalInputFilter.addHexadecimalInputFilter(addressField);

		valueSizeSelection.addItemListener(itemEvent ->
		{
			if (itemEvent.getStateChange() == ItemEvent.SELECTED)
			{
				setValueFieldInputFilter();
			}
		});

		registerTextField.setDocument(new RegisterLimit());

		setValueFieldInputFilter();

		codeTypeSelection.addItemListener(itemEvent ->
		{
			if (itemEvent.getStateChange() == ItemEvent.SELECTED)
			{
				setCodeTypesAvailability();
			}
		});

		setCodeTypesAvailability();

		pointerSelection.addItemListener(itemEvent ->
		{
			if (itemEvent.getStateChange() == ItemEvent.SELECTED)
			{
				setPointerOffsetAvailability();
			}
		});

		pointerOffsetField.setDocument(new InputLengthFilter(8));
		pointerOffset2Field.setDocument(new InputLengthFilter(8));

		setPointerOffsetAvailability();

		generateButton.addActionListener(actionEvent -> generatedArea.setText(generateCode()));
		cancelButton.addActionListener(actionEvent -> cancelDialog());
		copyButton.addActionListener(actionEvent -> copyGeneratedCode());

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent actionEvent)
			{
				cancelDialog();
			}
		});

		contentPane.registerKeyboardAction(actionEvent -> cancelDialog(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		pointerRangeStartAddressField.setDocument(new InputLengthFilter(8));
		pointerRangeEndAddressField.setDocument(new InputLengthFilter(8));
		pointerRangeStartAddress2Field.setDocument(new InputLengthFilter(8));
		pointerRangeEndAddress2Field.setDocument(new InputLengthFilter(8));
		InputLengthFilter inputLengthFilter = new InputLengthFilter(4);
		writesCountField.setDocument(inputLengthFilter);
		HexadecimalInputFilter.addHexadecimalInputFilter(writesCountField, 4);
		writesOffsetField.setDocument(new InputLengthFilter(8));
		valueIncrementField.setDocument(new InputLengthFilter(8));
		addParsePointerExpressionButtonListener();
	}

	public void setAddress(int address)
	{
		addressField.setText(Integer.toHexString(address).toUpperCase());
	}

	private void addParsePointerExpressionButtonListener()
	{
		parsePointerExpressionButton.addActionListener(actionEvent ->
		{
			String pointerExpression = JOptionPane.showInputDialog(rootPane, "Please enter the pointer expression!", "Pointer Expression?", JOptionPane.INFORMATION_MESSAGE);

			try
			{
				if (pointerExpression != null)
				{
					MemoryPointerExpression memoryPointerExpression = new MemoryPointerExpression(pointerExpression);
					int[] offsets = memoryPointerExpression.getOffsets();
					int offsetsCount = offsets.length;
					int maximumDepth = 2;

					if (offsetsCount > maximumDepth)
					{
						JOptionPane.showMessageDialog(rootPane,
								"Only pointers up to depth " + maximumDepth + " are currently supported!",
								"Not possible",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					codeTypeSelection.setSelectedItem(CodeTypes.ram_write);
					pointerSelection.setSelectedItem(offsetsCount == 1 ? Pointer.pointer : Pointer.pointer_in_pointer);
					valueSizeSelection.setSelectedItem(ValueSize.thirty_two_bit);
					pointerOffsetField.setText(Integer.toHexString(offsets[0]).toUpperCase());

					if (offsetsCount == 2)
					{
						pointerOffset2Field.setText(Integer.toHexString(offsets[1]).toUpperCase());
					}

					long value = memoryPointerExpression.getValue();
					valueField.setText(Long.toHexString(value).toUpperCase());
					long baseAddress = memoryPointerExpression.getBaseAddress();
					addressField.setText(Long.toHexString(baseAddress).toUpperCase());
					// double roundingPercentage = 0.05;
					long addressRoundedDown = 0x10000000; // AddressRounding.roundDown(baseAddress, roundingPercentage);
					long addressRoundedUp = 0x50000000; // AddressRounding.roundUp(baseAddress, roundingPercentage);
					pointerRangeStartAddressField.setText(Long.toHexString(addressRoundedDown).toUpperCase());
					pointerRangeEndAddressField.setText(Long.toHexString(addressRoundedUp).toUpperCase());

					if(offsetsCount == 2)
					{
						pointerRangeStartAddress2Field.setText(pointerRangeStartAddressField.getText());
						pointerRangeEndAddress2Field.setText(pointerRangeEndAddressField.getText());
					}

					generatedArea.setText(generateCode());
				}
			}
			catch(Exception exception)
			{
				JOptionPane.showMessageDialog(rootPane,
						"Failed to parse pointer expression!",
						"Parsing Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	private void setCodeTypesAvailability()
	{
		CodeTypes codeType = (CodeTypes) codeTypeSelection.getSelectedItem();
		boolean registerOperation = isRegisterOperation(codeType);
		boolean dataOperation = isDataOperation(codeType);
		boolean valueFieldEnabled = codeType != CodeTypes.string_write && !registerOperation;
		boolean stringWriteTextFieldEnabled = codeType == CodeTypes.string_write;
		boolean isIndependentCodeType = codeType == CodeTypes.no_operation || codeType == CodeTypes.termination;

		registerTextField.setEnabled(registerOperation);
		registerOperationsComboBox.setEnabled(dataOperation);
		valueField.setEnabled(valueFieldEnabled && !isIndependentCodeType);
		stringWriteTextArea.setEnabled(stringWriteTextFieldEnabled);
		pointerSelection.setEnabled(!isIndependentCodeType);
		valueSizeSelection.setEnabled(!isIndependentCodeType);
		addressField.setEnabled(!isIndependentCodeType);

		boolean isSkipWrite = codeType == CodeTypes.skip_write;
		valueIncrementField.setEnabled(isSkipWrite);
		writesOffsetField.setEnabled(isSkipWrite);
		writesCountField.setEnabled(isSkipWrite);
	}

	private boolean isDataOperation(CodeTypes codeType)
	{
		return codeType == CodeTypes.integer_operations || codeType == CodeTypes.float_operations;
	}

	private boolean isRegisterOperation(CodeTypes codeType)
	{
		return codeType == CodeTypes.load_int || codeType == CodeTypes.store_int || codeType == CodeTypes.integer_operations || codeType == CodeTypes.load_float || codeType == CodeTypes.store_float || codeType == CodeTypes.float_operations;
	}

	private void setPointerOffsetAvailability()
	{
		Pointer selectedPointerType = (Pointer) pointerSelection.getSelectedItem();

		switch (selectedPointerType)
		{
			case pointer:
				pointerOffsetField.setEnabled(true);
				pointerRangeStartAddressField.setEnabled(true);
				pointerRangeEndAddressField.setEnabled(true);
				pointerOffset2Field.setEnabled(false);
				pointerRangeStartAddress2Field.setEnabled(false);
				pointerRangeEndAddress2Field.setEnabled(false);
				break;

			case pointer_in_pointer:
				pointerOffsetField.setEnabled(true);
				pointerRangeStartAddressField.setEnabled(true);
				pointerRangeEndAddressField.setEnabled(true);
				pointerOffset2Field.setEnabled(true);
				pointerRangeStartAddress2Field.setEnabled(true);
				pointerRangeEndAddress2Field.setEnabled(true);
				break;

			case no_pointer:
				pointerOffsetField.setEnabled(false);
				pointerRangeStartAddressField.setEnabled(false);
				pointerRangeEndAddressField.setEnabled(false);
				pointerOffset2Field.setEnabled(false);
				pointerRangeStartAddress2Field.setEnabled(false);
				pointerRangeEndAddress2Field.setEnabled(false);
				break;
		}
	}

	private void setValueFieldInputFilter()
	{
		int valueSize;

		switch ((ValueSize) valueSizeSelection.getSelectedItem())
		{
			case eight_bit:
				valueSize = 2;
				break;

			case sixteen_bit:
				valueSize = 4;
				break;

			case thirty_two_bit:
				valueSize = 8;
				break;

			default:
				throw new IllegalArgumentException("Invalid value size selection");
		}

		InputLengthFilter inputLengthFilter = new InputLengthFilter(valueSize);
		valueField.setDocument(inputLengthFilter);
	}

	private void defineDialogProperties()
	{
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(generateButton);
		setTitle("Code Wizard");
		WindowUtilities.setIconImage(this);
		setSize(700, 650);
	}

	private void copyGeneratedCode()
	{
		String text = generatedArea.getText();
		SystemClipboard.copy(text);
		dispose();
	}

	private String generateCode()
	{
		StringBuilder codeBuilder = new StringBuilder();
		CodeTypes selectedCodeType = (CodeTypes) codeTypeSelection.getSelectedItem();
		String codeType = selectedCodeType.getValue();
		codeBuilder.append(codeType);

		if (selectedCodeType == CodeTypes.termination)
		{
			String TERMINATION_CODE = "00000000 DEADCAFE";
			codeBuilder.append(TERMINATION_CODE.replace(" ", "").substring(2));
			return CheatCodeFormatter.format(codeBuilder.toString(), false);
		}

		if (selectedCodeType == CodeTypes.no_operation)
		{
			codeBuilder.append(NOP_CODE.replace(" ", "").substring(2));
			return CheatCodeFormatter.format(codeBuilder.toString(), false);
		}

		Pointer selectedPointerType = (Pointer) pointerSelection.getSelectedItem();
		String pointer = selectedPointerType.getValue();
		codeBuilder.append(pointer);
		ValueSize valueSize = (ValueSize) valueSizeSelection.getSelectedItem();
		String size = valueSize.getValue();
		codeBuilder.append(size);

		String stringWriteText = stringWriteTextArea.getText();
		String stringWriteHexadecimal = Conversions.asciiToHexadecimal(stringWriteText);

		if (selectedCodeType == CodeTypes.string_write)
		{
			int bytesCount = stringWriteHexadecimal.length() / 2;
			String paddedSize = Conversions.toHexadecimal(bytesCount, 4);
			codeBuilder.append(paddedSize);
		} else if (selectedCodeType == CodeTypes.skip_write)
		{
			String writesCount = doPadding(writesCountField.getText(), 4);
			codeBuilder.append(writesCount);
		} else
		{
			codeBuilder.append("000");

			if(isRegisterOperation(selectedCodeType))
			{
				String register = registerTextField.getText();

				if(register.equals(""))
				{
					register = "0";
				}

				codeBuilder.append(register);
			}
			else
			{
				codeBuilder.append("0");
			}
		}

		String address = doPadding(addressField.getText());
		codeBuilder.append(address);

		if (selectedPointerType == Pointer.pointer || selectedPointerType == Pointer.pointer_in_pointer)
		{
			String rangeStart = doPadding(pointerRangeStartAddressField.getText());
			codeBuilder.append(rangeStart);
			String rangeEnd = doPadding(pointerRangeEndAddressField.getText());
			codeBuilder.append(rangeEnd);
			String pointerOffset = doPadding(pointerOffsetField.getText());
			codeBuilder.append(pointerOffset);

			if (selectedPointerType == Pointer.pointer_in_pointer)
			{
				codeBuilder.append("00000000");
				String range2Start = doPadding(pointerRangeStartAddress2Field.getText());
				codeBuilder.append(range2Start);
				String range2End = doPadding(pointerRangeEndAddress2Field.getText());
				codeBuilder.append(range2End);
				String pointer2Offset = doPadding(pointerOffset2Field.getText());
				codeBuilder.append(pointer2Offset);
			}

			if(isRegisterOperation(selectedCodeType))
			{
				codeBuilder.append("00000000");
			}
		}

		if (selectedCodeType == CodeTypes.string_write)
		{
			codeBuilder.append(stringWriteHexadecimal);
			return CheatCodeFormatter.formatWithPadding(codeBuilder.toString(), "FF");
		}

		if(valueField.isEnabled())
		{
			String value = doPadding(valueField.getText());
			codeBuilder.append(value);
		}

		if (selectedCodeType == CodeTypes.skip_write)
		{
			String offset = doPadding(writesOffsetField.getText(), 8);
			codeBuilder.append(offset);

			String increment = doPadding(valueIncrementField.getText(), 8);
			codeBuilder.append(increment);
		}

		if (selectedPointerType != Pointer.pointer && selectedPointerType != Pointer.pointer_in_pointer && !(isRegisterOperation(selectedCodeType)))
		{
			codeBuilder.append("00000000");
		}

		return CheatCodeFormatter.format(codeBuilder.toString(), false);
	}

	private String doPadding(String value, int targetLength)
	{
		while (value.length() < targetLength)
		{
			value = "0" + value;
		}

		return value;
	}

	private String doPadding(String value)
	{
		return doPadding(value, 8);
	}

	private void cancelDialog()
	{
		dispose();
	}

	public void setValue(int value)
	{
		valueField.setText(Integer.toHexString(value).toUpperCase());
	}

	public static void main(String[] arguments)
	{
		new CodeWizardDialog().setVisible(true);
	}
}