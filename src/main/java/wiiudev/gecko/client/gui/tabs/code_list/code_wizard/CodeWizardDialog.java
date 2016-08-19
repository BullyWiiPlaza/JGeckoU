package wiiudev.gecko.client.gui.tabs.code_list.code_wizard;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.conversions.SystemClipboard;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.MemoryPointerExpression;
import wiiudev.gecko.client.gui.input_filters.HexadecimalInputFilter;
import wiiudev.gecko.client.gui.input_filters.InputLengthFilter;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.CodeTypes;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.Pointer;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.RegisterOperations;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.selections.ValueSize;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.AddressRange;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

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
	private JTextField targetRegisterTextField;
	private JComboBox<RegisterOperations> registerOperationsComboBox;
	private JTextField sourceRegisterTextField;
	public static String NOP_CODE = "D1000000 DEADC0DE";

	public CodeWizardDialog()
	{
		setLocationRelativeTo(JGeckoUGUI.getInstance());
		defineDialogProperties();

		DefaultComboBoxModel<CodeTypes> codeTypesDefaultComboBoxModel = new DefaultComboBoxModel<>(CodeTypes.values());
		codeTypeSelection.setModel(codeTypesDefaultComboBoxModel);

		DefaultComboBoxModel<Pointer> pointerDefaultComboBoxModel = new DefaultComboBoxModel<>(Pointer.values());
		pointerSelection.setModel(pointerDefaultComboBoxModel);

		addRegisterComboBoxItems();

		DefaultComboBoxModel<ValueSize> valueSizeDefaultComboBoxModel = new DefaultComboBoxModel<>(ValueSize.values());
		valueSizeSelection.setModel(valueSizeDefaultComboBoxModel);
		valueSizeSelection.setSelectedItem(ValueSize.THIRTY_TWO_BIT);

		HexadecimalInputFilter.setHexadecimalInputFilter(addressField);

		valueSizeSelection.addItemListener(itemEvent ->
		{
			if (itemEvent.getStateChange() == ItemEvent.SELECTED)
			{
				setValueFieldInputFilter();
			}
		});

		sourceRegisterTextField.setDocument(new RegisterLimit());
		targetRegisterTextField.setDocument(new RegisterLimit());

		setValueFieldInputFilter();

		codeTypeSelection.addItemListener(itemEvent ->
		{
			if (itemEvent.getStateChange() == ItemEvent.SELECTED)
			{
				setCodeTypesAvailability();
				setPossiblePointerSelections();
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

		generateButton.addActionListener(actionEvent -> generatedArea.setText(getGeneratedCode()));
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
		HexadecimalInputFilter.setHexadecimalInputFilter(writesCountField, 4);
		writesOffsetField.setDocument(new InputLengthFilter(8));
		valueIncrementField.setDocument(new InputLengthFilter(8));
		addParsePointerExpressionButtonListener();
	}

	private void setPossiblePointerSelections()
	{
		CodeTypes codeType = (CodeTypes) codeTypeSelection.getSelectedItem();

		if (codeType == CodeTypes.LOAD_POINTER)
		{
			pointerSelection.removeAllItems();

			pointerSelection.addItem(Pointer.NO_POINTER);
			pointerSelection.addItem(Pointer.POINTER);
		} else
		{
			DefaultComboBoxModel<Pointer> pointerDefaultComboBoxModel = new DefaultComboBoxModel<>(Pointer.values());
			pointerSelection.setModel(pointerDefaultComboBoxModel);
		}
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

					codeTypeSelection.setSelectedItem(CodeTypes.RAM_WRITE);
					pointerSelection.setSelectedItem(offsetsCount == 1 ? Pointer.POINTER : Pointer.POINTER_IN_POINTER);
					valueSizeSelection.setSelectedItem(ValueSize.THIRTY_TWO_BIT);
					pointerOffsetField.setText(Integer.toHexString(offsets[0]).toUpperCase());

					if (offsetsCount == 2)
					{
						pointerOffset2Field.setText(Integer.toHexString(offsets[1]).toUpperCase());
					}

					long value = memoryPointerExpression.getValue();
					valueField.setText(Long.toHexString(value).toUpperCase());
					long baseAddress = memoryPointerExpression.getBaseAddress();
					addressField.setText(Long.toHexString(baseAddress).toUpperCase());
					setPointerOffsetRanges(memoryPointerExpression);

					generatedArea.setText(getGeneratedCode());
				}
			} catch (Exception exception)
			{
				exception.printStackTrace();
				JOptionPane.showMessageDialog(rootPane,
						"Failed to parse pointer expression!",
						"Parsing Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	private void setPointerOffsetRanges(MemoryPointerExpression memoryPointerExpression) throws IOException
	{
		int[] offsets = memoryPointerExpression.getOffsets();
		long baseAddress = memoryPointerExpression.getBaseAddress();
		AddressRounding addressRounding = new AddressRounding(0.007);

		if (TCPGecko.isConnected())
		{
			long destinationValue = new MemoryReader().readInt((int) baseAddress);

			if (AddressRange.isInRange((int) destinationValue))
			{
				long addressRoundedDown = addressRounding.roundDown(destinationValue);
				long addressRoundedUp = addressRounding.roundUp(destinationValue);
				pointerRangeStartAddressField.setText(Long.toHexString(addressRoundedDown).toUpperCase());
				pointerRangeEndAddressField.setText(Long.toHexString(addressRoundedUp).toUpperCase());
			} else
			{
				pointerRangeStartAddressField.setText(Long.toHexString(0x10000000).toUpperCase());
				pointerRangeEndAddressField.setText(Long.toHexString(0x50000000).toUpperCase());
			}
		} else
		{
			pointerRangeStartAddressField.setText(Long.toHexString(0x10000000).toUpperCase());
			pointerRangeEndAddressField.setText(Long.toHexString(0x50000000).toUpperCase());
		}

		if (offsets.length == 2)
		{
			if (TCPGecko.isConnected())
			{
				long destinationAddress = new MemoryPointerExpression(baseAddress, new int[]{offsets[0]}).getDestinationAddress();

				if (destinationAddress != MemoryPointerExpression.INVALID_POINTER)
				{
					if (AddressRange.isInRange((int) destinationAddress))
					{
						String startingAddress = Conversions.toHexadecimal((int) (addressRounding.roundDown(destinationAddress)));
						pointerRangeStartAddress2Field.setText(startingAddress);
						String endAddress = Conversions.toHexadecimal((int) (addressRounding.roundUp(destinationAddress)));
						pointerRangeEndAddress2Field.setText(endAddress);
					} else
					{
						pointerRangeStartAddress2Field.setText(pointerRangeStartAddress2Field.getText());
						pointerRangeEndAddress2Field.setText(pointerRangeEndAddressField.getText());
					}
				}
			} else
			{
				pointerRangeStartAddress2Field.setText(pointerRangeEndAddressField.getText());
				pointerRangeEndAddress2Field.setText(pointerRangeEndAddressField.getText());
			}
		}
	}

	private void setCodeTypesAvailability()
	{
		CodeTypes codeType = (CodeTypes) codeTypeSelection.getSelectedItem();
		boolean registerOperation = isRegisterOperation(codeType);
		boolean twoRegistersOperation = isTwoRegistersOperation(codeType);
		boolean valueFieldEnabled = (codeType != CodeTypes.STRING_WRITE) && !registerOperation || twoRegistersOperation;
		boolean stringWriteTextFieldEnabled = codeType == CodeTypes.STRING_WRITE;
		boolean isIndependentCodeType = codeType == CodeTypes.NO_OPERATION || codeType == CodeTypes.TERMINATION;
		boolean modifyRegisterOperation = codeType == CodeTypes.INTEGER_OPERATIONS || codeType == CodeTypes.FLOAT_OPERATIONS;

		setPointerRangeAvailability();
		sourceRegisterTextField.setEnabled(modifyRegisterOperation);
		targetRegisterTextField.setEnabled(registerOperation);
		registerOperationsComboBox.setEnabled(twoRegistersOperation);
		valueField.setEnabled(valueFieldEnabled && !isIndependentCodeType && codeType != CodeTypes.LOAD_POINTER);
		stringWriteTextArea.setEnabled(stringWriteTextFieldEnabled);
		pointerSelection.setEnabled(!isIndependentCodeType && !twoRegistersOperation && codeType != CodeTypes.ADD_POINTER_OFFSET);
		valueSizeSelection.setEnabled(!isIndependentCodeType && !twoRegistersOperation && codeType != CodeTypes.LOAD_POINTER && codeType != CodeTypes.ADD_POINTER_OFFSET);

		if (codeType != CodeTypes.ADD_POINTER_OFFSET)
		{
			valueSizeSelection.setSelectedItem(ValueSize.THIRTY_TWO_BIT);
		}

		addressField.setEnabled(!isIndependentCodeType && !twoRegistersOperation && codeType != CodeTypes.ADD_POINTER_OFFSET);
		boolean isSkipWrite = codeType == CodeTypes.SKIP_WRITE;
		boolean isFillMemory = codeType == CodeTypes.FILL_MEMORY;
		valueIncrementField.setEnabled(isSkipWrite || isFillMemory);
		writesOffsetField.setEnabled(isSkipWrite);
		writesCountField.setEnabled(isSkipWrite || isFillMemory);

		addRegisterComboBoxItems();

		if (codeType == CodeTypes.INTEGER_OPERATIONS)
		{
			registerOperationsComboBox.removeItemAt(registerOperationsComboBox.getItemCount() - 1);
		}
	}

	private void addRegisterComboBoxItems()
	{
		int selectedIndex = registerOperationsComboBox.getSelectedIndex();
		registerOperationsComboBox.removeAllItems();
		DefaultComboBoxModel<RegisterOperations> comboBoxModel = new DefaultComboBoxModel<>(RegisterOperations.values());
		registerOperationsComboBox.setModel(comboBoxModel);

		// Restore previous selection and choose the last element if the elements count decreased
		int itemsCount = registerOperationsComboBox.getItemCount();
		if (selectedIndex >= itemsCount)
		{
			selectedIndex = itemsCount - 1;
		}

		// Do not make the selection blank
		if (selectedIndex >= 0)
		{
			registerOperationsComboBox.setSelectedIndex(selectedIndex);
		}
	}

	private boolean isTwoRegistersOperation(CodeTypes codeType)
	{
		return codeType == CodeTypes.INTEGER_OPERATIONS || codeType == CodeTypes.FLOAT_OPERATIONS;
	}

	private boolean isRegisterOperation(CodeTypes codeType)
	{
		return codeType == CodeTypes.LOAD_INT || codeType == CodeTypes.STORE_INT || codeType == CodeTypes.INTEGER_OPERATIONS || codeType == CodeTypes.LOAD_FLOAT || codeType == CodeTypes.STORE_FLOAT || codeType == CodeTypes.FLOAT_OPERATIONS;
	}

	private void setPointerOffsetAvailability()
	{
		Pointer selectedPointerType = (Pointer) pointerSelection.getSelectedItem();

		switch (selectedPointerType)
		{
			case POINTER:
				CodeTypes codeType = (CodeTypes) codeTypeSelection.getSelectedItem();
				pointerOffsetField.setEnabled(codeType != CodeTypes.LOAD_POINTER);
				pointerRangeStartAddressField.setEnabled(true);
				pointerRangeEndAddressField.setEnabled(true);
				pointerOffset2Field.setEnabled(false);
				pointerRangeStartAddress2Field.setEnabled(false);
				pointerRangeEndAddress2Field.setEnabled(false);
				break;

			case POINTER_IN_POINTER:
				pointerOffsetField.setEnabled(true);
				pointerRangeStartAddressField.setEnabled(true);
				pointerRangeEndAddressField.setEnabled(true);
				pointerOffset2Field.setEnabled(true);
				pointerRangeStartAddress2Field.setEnabled(true);
				pointerRangeEndAddress2Field.setEnabled(true);
				break;

			case NO_POINTER:
				pointerOffsetField.setEnabled(false);
				setPointerRangeAvailability();
				pointerOffset2Field.setEnabled(false);
				pointerRangeStartAddress2Field.setEnabled(false);
				pointerRangeEndAddress2Field.setEnabled(false);
				break;
		}
	}

	private void setPointerRangeAvailability()
	{
		CodeTypes codeType = (CodeTypes) codeTypeSelection.getSelectedItem();
		pointerRangeStartAddressField.setEnabled(codeType == CodeTypes.LOAD_POINTER);
		pointerRangeEndAddressField.setEnabled(codeType == CodeTypes.LOAD_POINTER);
	}

	private void setValueFieldInputFilter()
	{
		int valueSize;

		switch ((ValueSize) valueSizeSelection.getSelectedItem())
		{
			case EIGHT_BIT:
				valueSize = 2;
				break;

			case SIXTEEN_BIT:
				valueSize = 4;
				break;

			case THIRTY_TWO_BIT:
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

	private String getGeneratedCode()
	{
		StringBuilder codeBuilder = new StringBuilder();
		CodeTypes selectedCodeType = (CodeTypes) codeTypeSelection.getSelectedItem();
		String codeType = selectedCodeType.getValue();
		codeBuilder.append(codeType);

		if (selectedCodeType == CodeTypes.ADD_POINTER_OFFSET)
		{
			codeBuilder.append("000000");
			String value = doPadding(valueField.getText());
			codeBuilder.append(value);
		} else
		{
			if (selectedCodeType == CodeTypes.TERMINATION)
			{
				String TERMINATION_CODE = "00000000 DEADCAFE";
				codeBuilder.append(TERMINATION_CODE.replace(" ", "").substring(2));
				return CheatCodeFormatter.format(codeBuilder.toString(), false);
			}

			if (selectedCodeType == CodeTypes.NO_OPERATION)
			{
				codeBuilder.append(NOP_CODE.replace(" ", "").substring(2));
				return CheatCodeFormatter.format(codeBuilder.toString(), false);
			}

			Pointer selectedPointerType = (Pointer) pointerSelection.getSelectedItem();

			if (isTwoRegistersOperation(selectedCodeType))
			{
				RegisterOperations integerOperation = (RegisterOperations) registerOperationsComboBox.getSelectedItem();
				String operation = integerOperation.getValue();
				codeBuilder.append(operation);
			} else
			{
				String pointer = selectedPointerType.getValue();
				codeBuilder.append(pointer);

				if (selectedCodeType == CodeTypes.LOAD_POINTER)
				{
					codeBuilder.append("0");
				} else
				{
					ValueSize valueSize = (ValueSize) valueSizeSelection.getSelectedItem();
					String size = valueSize.getValue();
					codeBuilder.append(size);
				}
			}

			String stringWriteText = stringWriteTextArea.getText();
			String stringWriteHexadecimal = Conversions.asciiToHexadecimal(stringWriteText);

			if (selectedCodeType == CodeTypes.STRING_WRITE)
			{
				int bytesCount = stringWriteHexadecimal.length() / 2;
				String paddedSize = Conversions.toHexadecimal(bytesCount, 4);
				codeBuilder.append(paddedSize);
			} else if (selectedCodeType == CodeTypes.SKIP_WRITE)
			{
				String writesCount = doPadding(writesCountField.getText(), 4);
				codeBuilder.append(writesCount);
			} else
			{
				codeBuilder.append("0");

				if (isTwoRegistersOperation(selectedCodeType))
				{
					String sourceRegister = sourceRegisterTextField.getText();

					if (sourceRegister.equals(""))
					{
						sourceRegister = "0";
					}

					codeBuilder.append(sourceRegister);
				} else
				{
					codeBuilder.append("0");
				}

				codeBuilder.append("0");

				if (isRegisterOperation(selectedCodeType))
				{
					String register = targetRegisterTextField.getText();

					if (register.equals(""))
					{
						register = "0";
					}

					codeBuilder.append(register);
				} else
				{
					codeBuilder.append("0");
				}
			}

			if (isTwoRegistersOperation(selectedCodeType))
			{
				String value = doPadding(valueField.getText());
				codeBuilder.append(value);
				return CheatCodeFormatter.format(codeBuilder.toString(), false);
			} else
			{
				String address = doPadding(addressField.getText());
				codeBuilder.append(address);
			}

			if (selectedPointerType == Pointer.POINTER || selectedPointerType == Pointer.POINTER_IN_POINTER || selectedCodeType == CodeTypes.LOAD_POINTER)
			{
				String rangeStart = doPadding(pointerRangeStartAddressField.getText());
				codeBuilder.append(rangeStart);
				String rangeEnd = doPadding(pointerRangeEndAddressField.getText());
				codeBuilder.append(rangeEnd);

				if (selectedCodeType != CodeTypes.LOAD_POINTER)
				{
					String pointerOffset = doPadding(pointerOffsetField.getText());
					codeBuilder.append(pointerOffset);

					if (selectedPointerType == Pointer.POINTER_IN_POINTER)
					{
						codeBuilder.append("00000000");
						String range2Start = doPadding(pointerRangeStartAddress2Field.getText());
						codeBuilder.append(range2Start);
						String range2End = doPadding(pointerRangeEndAddress2Field.getText());
						codeBuilder.append(range2End);
						String pointer2Offset = doPadding(pointerOffset2Field.getText());
						codeBuilder.append(pointer2Offset);
					}

					if (isRegisterOperation(selectedCodeType))
					{
						codeBuilder.append("00000000");
					}
				}
			}

			if (selectedCodeType == CodeTypes.STRING_WRITE)
			{
				codeBuilder.append(stringWriteHexadecimal);
				return CheatCodeFormatter.formatWithPadding(codeBuilder.toString(), "FF");
			}

			if (valueField.isEnabled())
			{
				String value = doPadding(valueField.getText());
				codeBuilder.append(value);
			}

			if (selectedCodeType == CodeTypes.SKIP_WRITE)
			{
				String offset = doPadding(writesOffsetField.getText(), 8);
				codeBuilder.append(offset);

				String increment = doPadding(valueIncrementField.getText(), 8);
				codeBuilder.append(increment);
			}

			if (selectedPointerType != Pointer.POINTER && selectedPointerType != Pointer.POINTER_IN_POINTER && !(isRegisterOperation(selectedCodeType)) && selectedCodeType != CodeTypes.LOAD_POINTER)
			{
				codeBuilder.append("00000000");
			}
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

	public void generateCode()
	{
		String generatedCode = getGeneratedCode();
		generatedArea.setText(generatedCode);
	}

	public void setValue(int value)
	{
		valueField.setText(Integer.toHexString(value).toUpperCase());
	}

	public static void main(String[] arguments) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		new CodeWizardDialog().setVisible(true);
	}
}