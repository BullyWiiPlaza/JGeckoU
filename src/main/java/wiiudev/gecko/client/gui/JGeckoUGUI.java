package wiiudev.gecko.client.gui;

import org.apache.commons.io.FilenameUtils;
import wiiudev.gecko.client.ValueOperations;
import wiiudev.gecko.client.codes.*;
import wiiudev.gecko.client.connector.Connector;
import wiiudev.gecko.client.connector.IPAddressValidator;
import wiiudev.gecko.client.connector.MemoryReader;
import wiiudev.gecko.client.connector.MemoryWriter;
import wiiudev.gecko.client.connector.utilities.AddressRange;
import wiiudev.gecko.client.connector.utilities.MemoryAccessLevel;
import wiiudev.gecko.client.conversion.ConversionType;
import wiiudev.gecko.client.conversion.Conversions;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.code_list.AddCodeDialog;
import wiiudev.gecko.client.gui.code_list.CodesListManager;
import wiiudev.gecko.client.gui.inputFilter.HexadecimalInputFilter;
import wiiudev.gecko.client.gui.inputFilter.InputLengthFilter;
import wiiudev.gecko.client.gui.inputFilter.ValueSizes;
import wiiudev.gecko.client.gui.utilities.Benchmark;
import wiiudev.gecko.client.gui.utilities.DefaultContextMenu;
import wiiudev.gecko.client.gui.utilities.JFileChooserUtilities;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;
import wiiudev.gecko.client.gui.watch_list.*;
import wiiudev.gecko.client.memoryViewer.MemoryViewerTableManager;
import wiiudev.gecko.client.memoryViewer.MemoryViews;
import wiiudev.gecko.client.pointer_search.DownloadingUtilities;
import wiiudev.gecko.client.scanner.WiiUFinder;
import wiiudev.gecko.client.titles.FirmwareNotImplementedException;
import wiiudev.gecko.client.titles.Title;
import wiiudev.gecko.client.titles.TitleDatabaseManager;
import wiiudev.gecko.client.titles.TitleNotFoundException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * A class defining the main frame of the application
 */
public class JGeckoUGUI extends JFrame
{
	private JPanel rootPanel;
	private JButton connectButton;
	private JButton disconnectButton;
	private JTextField startingAddressField;
	private JTextField lastAddressField;
	private JComboBox comparisonOperationComboBox;
	private JComboBox searchTypeComboBox;
	private JTextField valueField;
	private JButton searchButton;
	private JButton restartButton;
	private JTable resultsTable;
	private JButton connectionHelpButton;
	private JTextField ipAddressField;
	private JCheckBox autoDetectCheckBox;
	private JPanel codesPanel;
	private JButton addCodeButton;
	private JButton deleteCodeButton;
	private JButton sendCodesButton;
	private JButton disableCodesButton;
	private JButton editCodeButton;
	private JButton codesHelpButton;
	private JButton updateGameTitlesButton;
	private JList<JCheckBox> codesListBoxes;
	private JButton storeCodeListButton;
	private JButton reconnectButton;
	private JButton loadCodeListButton;
	private JButton hexadecimalUTF8Button;
	private JButton UTF8HexadecimalButton;
	private JButton hexadecimalFloatingPointButton;
	private JButton floatingPointHexadecimalButton;
	private JButton hexadecimalIntegerButton;
	private JButton integerHexadecimalButton;
	private JTextField memoryViewerAddressField;
	private JTable memoryViewerTable;
	private JButton updateMemoryViewerButton;
	private JCheckBox memoryViewerAutoUpdateCheckBox;
	private JComboBox<MemoryViews> memoryViewerViews;
	private JButton firmwareVersionButton;
	private JButton searchMemoryViewerButton;
	private JTextField searchMemoryViewerValueField;
	private JTextField searchLengthField;
	private JButton pokeValueButton;
	private JTextField memoryViewerValueField;
	private JComboBox<ValueSizes> valueSizePokeComboBox;
	private JButton hexadecimalUnicodeButton;
	private JButton unicodeHexadecimalButton;
	private JButton dumpMemoryButton;
	private JTextField dumpFilePathField;
	private JTextField dumpStartingAddressField;
	private JTextField dumpEndingAddressField;
	private JButton chooseFilePathButton;
	private JButton downloadCodeDatabaseButton;
	private JButton followPointerButton;
	private JTabbedPane tabs;
	private JButton pointerSearchApplicationButton;
	private JTextPane aboutTextPane;
	private JButton powerPCAssemblyCompilerButton;
	private JTable watchListTable;
	private JButton addWatchButton;
	private JButton deleteWatchButton;
	private JButton modifyWatchButton;
	private JButton addAddressExpressionsButton;
	private JButton clearWatchListButton;
	private JComboBox<ValueOperations> pokeOperationsComboBox;
	private JButton exportCodeListButton;
	private JButton saveWatchListButton;
	private JButton exportWatchListButton;
	private JSpinner watchListUpdateDelaySpinner;
	private MemoryViewerTableManager memoryViewerTableManager;
	private CodesListManager codesListManager;
	private ListSelectionModel listSelectionModel;
	private boolean connected;
	private boolean connecting;
	private String connectButtonText;
	private String connectedIPAddress;
	private CodeListSender codeListSender;
	private TitleDatabaseManager titleDatabaseManager;
	private int firmwareVersion = -1;
	private boolean titlesInitialized;
	private String gameId;
	private String programName;
	private CodeListStorage codesStorage;
	private SimpleProperties simpleProperties;
	private WatchListManager watchListManager;
	private static JGeckoUGUI instance;

	private JGeckoUGUI()
	{
		addFormDesignerPanel();
		setFrameProperties();
		configureConnectionTab();
		configureCodesTab();
		configureExternalToolsTab();
		configureMemoryViewerTab();
		configureConversionsTab();
		configureWatchListTab();
		configureMemoryDumpingTab();
		configureAboutTab();
		restorePersistentSettings();
		addSettingsBackupShutdownHook();
	}

	private void configureWatchListTab()
	{
		int updateDelayMinimum = 10;
		watchListUpdateDelaySpinner.setValue(updateDelayMinimum);
		((SpinnerNumberModel) watchListUpdateDelaySpinner.getModel()).setMinimum(updateDelayMinimum);
		watchListManager = new WatchListManager(watchListTable);
		addWatchListDeleteRowsDeleteKey();
		watchListManager.configure(watchListTable);
		addWatchListTableListener();
		addWatchButton.addActionListener(actionEvent -> displayAddWatchDialog(false, addWatchButton.getText()));
		deleteWatchButton.addActionListener(actionEvent -> deleteSelectedWatchListRows());
		modifyWatchButton.addActionListener(actionEvent -> displayAddWatchDialog(true, modifyWatchButton.getText()));
		keepUpdatingWatchList();
		addAddAddressExpressionsButtonListener();
		addClearWatchListActionListener();
		setWatchButtonsAvailability();
		saveWatchListButton.addActionListener(actionEvent -> storeWatchList());
		exportWatchListButton.addActionListener(actionEvent -> exportWatchList());
	}

	private void addWatchListDeleteRowsDeleteKey()
	{
		int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap inputMap = watchListTable.getInputMap(condition);
		ActionMap actionMap = watchListTable.getActionMap();
		String action = "delete";
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), action);

		actionMap.put(action, new AbstractAction()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				deleteSelectedWatchListRows();
			}
		});
	}

	private void exportWatchList()
	{
		try
		{
			WatchListStorage watchListStorage = new WatchListStorage(gameId);
			String filePath = watchListStorage.export(watchListManager.getWatchListElements());

			askForOpeningFile(filePath);
		} catch (IOException exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	private void storeWatchList()
	{
		try
		{
			WatchListStorage watchListStorage = new WatchListStorage(gameId);
			String filePath = watchListStorage.store(watchListManager.getWatchListElements());

			JOptionPane.showMessageDialog(rootPane,
					"Watch list stored to " + filePath,
					"Success",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	private void askForOpeningFile(String filePath) throws IOException
	{
		Object[] options = {"OK",
				"Cancel"};

		int selectedAnswer = JOptionPane.showOptionDialog(rootPane,
				"Would you like to open the exported file?",
				filePath,
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				null);

		if (selectedAnswer == JOptionPane.YES_OPTION)
		{
			Desktop.getDesktop().open(new File(filePath));
		}
	}

	private void addWatchListTableListener()
	{
		watchListTable.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent mouseEvent)
			{
				setWatchButtonsAvailability();
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				setWatchButtonsAvailability();
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				setWatchButtonsAvailability();
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{

			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{

			}
		});
	}

	private void deleteSelectedWatchListRows()
	{
		String[] answers = {"Yes", "No"};
		int selectedAnswer = JOptionPane.showOptionDialog(this,
				"Do you really want to delete the selected row(s)?",
				"Delete?",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				answers,
				null);

		if (selectedAnswer == JOptionPane.YES_OPTION)
		{
			JTableUtilities.deleteSelectedRows(watchListTable);
			setWatchButtonsAvailability();
		}
	}

	private void addAddAddressExpressionsButtonListener()
	{
		addAddressExpressionsButton.addActionListener(actionEvent ->
		{
			AddAddressExpressionsDialog addAddressExpressionsDialog = new AddAddressExpressionsDialog(rootPane, addAddressExpressionsButton.getText());
			addAddressExpressionsDialog.setVisible(true);

			if (addAddressExpressionsDialog.isConfirmed())
			{
				List<WatchListElement> watchListElements = addAddressExpressionsDialog.getWatchListElements();

				for (WatchListElement watchListElement : watchListElements)
				{
					watchListManager.addRow(watchListElement);
				}
			}

			setWatchButtonsAvailability();
		});
	}

	private void addClearWatchListActionListener()
	{
		clearWatchListButton.addActionListener(actionEvent ->
		{
			Object[] options = {"Yes",
					"No"};

			int selectedAnswer = JOptionPane.showOptionDialog(rootPane,
					"Do you really want to delete all watch list items?",
					"Delete All?",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					null);

			if (selectedAnswer == JOptionPane.YES_OPTION)
			{
				watchListManager.deleteAllRows();
			}

			setWatchButtonsAvailability();
		});
	}

	private void keepUpdatingWatchList()
	{
		new SwingWorker<String, String>()
		{
			@Override
			protected String doInBackground() throws Exception
			{
				while (true)
				{
					try
					{
						if (watchListTable.isShowing() && connected)
						{
							watchListManager.updateValues();
						}

						int updateDelay = Integer.parseInt(watchListUpdateDelaySpinner.getValue().toString());
						Thread.sleep(updateDelay);
					} catch (ArrayIndexOutOfBoundsException ignored)
					{

					} catch (Exception exception)
					{
						StackTraceUtils.handleException(rootPane, exception);
						break;
					}
				}

				return null;
			}
		}.execute();
	}

	private void displayAddWatchDialog(boolean modify, String title)
	{
		AddWatchDialog addWatchDialog = new AddWatchDialog(rootPane, title);

		if (modify)
		{
			addWatchDialog.setWatchListElement(watchListManager.getSelectedWatchListElement());
		}

		addWatchDialog.setVisible(true);

		if (addWatchDialog.isConfirmed())
		{
			WatchListElement watchListElement = addWatchDialog.getWatchListElement();

			if (modify)
			{
				JTableUtilities.deleteSelectedRows(watchListTable);
			}

			watchListManager.addRow(watchListElement);
		}

		setWatchButtonsAvailability();
	}

	private void setWatchButtonsAvailability()
	{
		boolean isRowSelected = watchListManager.isRowSelected();
		deleteWatchButton.setEnabled(isRowSelected);
		modifyWatchButton.setEnabled(isRowSelected && !watchListManager.areMultipleRowsSelected());
		boolean rowExists = watchListManager.rowExists();
		clearWatchListButton.setEnabled(rowExists);
	}

	private void addSettingsBackupShutdownHook()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			boolean autoDetect = autoDetectCheckBox.isSelected();
			simpleProperties.put("AUTO_DETECT", String.valueOf(autoDetect));
			simpleProperties.put("MEMORY_VIEWER_ADDRESS", memoryViewerAddressField.getText());
			simpleProperties.put("IP_ADDRESS", ipAddressField.getText());
			simpleProperties.put("WATCH_LIST_UPDATE_DELAY", watchListUpdateDelaySpinner.getValue().toString());
			simpleProperties.writeToFile();
		}));
	}

	private void restorePersistentSettings()
	{
		simpleProperties = new SimpleProperties();

		String autoDetectString = simpleProperties.get("AUTO_DETECT");
		if (autoDetectString != null)
		{
			boolean autoDetect = Boolean.parseBoolean(autoDetectString);
			autoDetectCheckBox.setSelected(autoDetect);
		}

		String ipAddress = simpleProperties.get("IP_ADDRESS");
		if (ipAddress != null)
		{
			ipAddressField.setText(ipAddress);
		}

		String memoryViewerAddress = simpleProperties.get("MEMORY_VIEWER_ADDRESS");
		if (memoryViewerAddress != null)
		{
			memoryViewerAddressField.setText(memoryViewerAddress);
		}

		String updateDelay = simpleProperties.get("WATCH_LIST_UPDATE_DELAY");
		if (updateDelay != null)
		{
			watchListUpdateDelaySpinner.setValue(Integer.parseInt(updateDelay));
		}
	}

	private void configureExternalToolsTab()
	{
		powerPCAssemblyCompilerButton.addActionListener(actionEvent -> downloadAndLaunch("https://github.com/BullyWiiPlaza/PowerPC-Assembly-Compiler/blob/master/PowerPC-Assembly-Compiler.jar?raw=true", actionEvent));
		pointerSearchApplicationButton.addActionListener(actionEvent -> downloadAndLaunch("https://github.com/BullyWiiPlaza/Universal-Pointer-Searcher/blob/master/Universal-Pointer-Searcher.jar?raw=true", actionEvent));
	}

	private void configureAboutTab()
	{
		try
		{
			ConnectionHelper.addHyperLinkListener(aboutTextPane);
			String aboutText = WindowUtilities.resourceToString("About.html");
			aboutTextPane.setText(aboutText);
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	private void downloadAndLaunch(String downloadURL, ActionEvent actionEvent)
	{
		JButton downloadButton = (JButton) actionEvent.getSource();
		String downloadedFileName = DownloadingUtilities.getFileName(downloadURL);

		if (!DownloadingUtilities.canDownload(downloadedFileName))
		{
			JOptionPane.showMessageDialog(rootPane,
					"The application seems to be running already.",
					"Error",
					JOptionPane.ERROR_MESSAGE);

			return;
		}

		int selectedAnswer = JOptionPane.showConfirmDialog(
				rootPane,
				"Would you like to download and start this application?",
				"Download and launch?",
				JOptionPane.YES_NO_OPTION);

		if (selectedAnswer == JOptionPane.YES_OPTION)
		{
			new SwingWorker<String, String>()
			{
				@Override
				protected String doInBackground() throws Exception
				{
					String currentText = downloadButton.getText();

					try
					{
						downloadButton.setEnabled(false);
						downloadButton.setText("Downloading...");
						DownloadingUtilities.downloadAndExecute(downloadURL);
					} catch (Exception exception)
					{
						StackTraceUtils.handleException(rootPane, exception);
					} finally
					{
						downloadButton.setEnabled(true);
						downloadButton.setText(currentText);
					}

					return null;
				}
			}.execute();
		}
	}

	private void configureMemoryDumpingTab()
	{
		dumpStartingAddressField.setDocument(new InputLengthFilter(8));
		dumpEndingAddressField.setDocument(new InputLengthFilter(8));
		new DefaultContextMenu().addTo(dumpStartingAddressField);
		new DefaultContextMenu().addTo(dumpEndingAddressField);

		dumpStartingAddressField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				handleDumpMemoryButtonAvailability();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				handleDumpMemoryButtonAvailability();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				handleDumpMemoryButtonAvailability();
			}
		});

		dumpEndingAddressField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				handleDumpMemoryButtonAvailability();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				handleDumpMemoryButtonAvailability();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				handleDumpMemoryButtonAvailability();
			}
		});

		dumpMemoryButton.addActionListener(actionEvent ->
		{
			int startingAddress = Integer.parseInt(dumpStartingAddressField.getText(), 16);
			int endingAddress = Integer.parseInt(dumpEndingAddressField.getText(), 16);
			int length = endingAddress - startingAddress;
			String formattedWaitingTime = MemoryReader.getExpectedWaitingTime(length);

			int selectedAnswer = JOptionPane.showConfirmDialog(
					rootPane,
					"Memory dumping is currently slow. Do you really want to dump " + length + " bytes?\nThe expected waiting time is approximately " + formattedWaitingTime + "!",
					"Dump?",
					JOptionPane.YES_NO_OPTION);

			if (selectedAnswer == JOptionPane.YES_OPTION)
			{
				new SwingWorker<String, String>()
				{
					@Override
					protected String doInBackground() throws Exception
					{
						String dumpText = dumpMemoryButton.getText();
						String targetFilePath = dumpFilePathField.getText();
						dumpMemoryButton.setText("Dumping...");
						dumpMemoryButton.setEnabled(false);

						try
						{
							File targetFile = new File(targetFilePath);
							MemoryReader memoryReader = new MemoryReader();
							Benchmark benchmark = new Benchmark();
							benchmark.start();
							memoryReader.dump(startingAddress, length, targetFile);
							double elapsedSeconds = benchmark.getElapsedTime();
							Toolkit.getDefaultToolkit().beep();
							JOptionPane.showMessageDialog(rootPane,
									"Dumped " + length + " bytes after " + elapsedSeconds + " second(s)",
									"Success",
									JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception exception)
						{
							StackTraceUtils.handleException(rootPane, exception);
						} finally
						{
							dumpMemoryButton.setText(dumpText);
							dumpMemoryButton.setEnabled(true);
						}

						return null;
					}
				}.execute();
			}
		});

		chooseFilePathButton.addActionListener(actionEvent ->
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			String applicationDirectory = System.getProperty("user.dir");
			fileChooser.setCurrentDirectory(new File(applicationDirectory));
			JFileChooserUtilities.registerDeleteAction(fileChooser);
			int selectedOption = fileChooser.showSaveDialog(rootPane);

			if (selectedOption == JOptionPane.YES_OPTION)
			{
				File selectedFile = fileChooser.getSelectedFile();
				String selectedFilePath = selectedFile.getAbsolutePath();
				String forcedExtension = ".bin";
				if (!selectedFilePath.endsWith(forcedExtension))
				{
					selectedFilePath += forcedExtension;
				}

				dumpFilePathField.setText(selectedFilePath);
				handleDumpMemoryButtonAvailability();
			}
		});
	}

	private void handleDumpMemoryButtonAvailability()
	{
		boolean validMemoryAddresses = false;

		try
		{
			int startingAddress = Integer.parseInt(dumpStartingAddressField.getText(), 16);
			int endingAddress = Integer.parseInt(dumpEndingAddressField.getText(), 16);
			validMemoryAddresses = startingAddress < endingAddress;
		} catch (NumberFormatException ignored)
		{
			// Happens when the field is empty
		}

		String targetFilePath = dumpFilePathField.getText();
		boolean validTargetFile = !targetFilePath.equals("");
		boolean validStartingAddress = isAddressInputValid(dumpStartingAddressField.getText());
		boolean validEndingAddress = isAddressInputValid(dumpEndingAddressField.getText());
		dumpStartingAddressField.setBackground(validStartingAddress ? Color.GREEN : Color.RED);
		dumpEndingAddressField.setBackground(validEndingAddress ? Color.GREEN : Color.RED);
		dumpMemoryButton.setEnabled(connected && validMemoryAddresses && validStartingAddress && validEndingAddress && validTargetFile);
	}

	private void followPointer()
	{
		new SwingWorker<String, String>()
		{
			@Override
			protected String doInBackground() throws Exception
			{
				String input = JOptionPane.showInputDialog(rootPane, "Please enter the pointer notation:", followPointerButton.getText(), JOptionPane.INFORMATION_MESSAGE);

				if (input != null)
				{
					try
					{
						MemoryPointerExpression memoryPointer = new MemoryPointerExpression(input);
						long destinationAddress = memoryPointer.getDestinationAddress();
						String destinationAddressHexadecimal = Long.toHexString(destinationAddress).toUpperCase();
						memoryViewerAddressField.setText(destinationAddressHexadecimal);

						if (destinationAddress != MemoryPointerExpression.INVALID_POINTER)
						{
							updateMemoryViewer(true, true);
						} else
						{
							JOptionPane.showMessageDialog(rootPane,
									"Could not follow " + input + ":\nInvalid destination address",
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
					} catch (StringIndexOutOfBoundsException invalidInput)
					{
						JOptionPane.showMessageDialog(rootPane,
								"Invalid pointer expression!",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					} catch (Exception exception)
					{
						StackTraceUtils.handleException(rootPane, exception);
					}
				}

				return null;
			}
		}.execute();
	}

	// Singleton class to access it from anywhere
	public static JGeckoUGUI getInstance()
	{
		if (instance == null)
		{
			instance = new JGeckoUGUI();
		}

		return instance;
	}

	private void addFormDesignerPanel()
	{
		add(rootPanel);
	}

	private void configureMemoryViewerTab()
	{
		configureMemoryViewerViewsComboBox();

		DefaultComboBoxModel<ValueOperations> defaultComboBoxModel = new DefaultComboBoxModel<>(ValueOperations.values());
		pokeOperationsComboBox.setModel(defaultComboBoxModel);
		pokeOperationsComboBox.setSelectedItem(ValueOperations.ASSIGN);

		memoryViewerTableManager = new MemoryViewerTableManager(memoryViewerTable, (MemoryViews) memoryViewerViews.getSelectedItem());
		memoryViewerTableManager.configureTable();

		updateMemoryViewerButton.addActionListener(actionEvent -> updateMemoryViewer(true, true));

		HexadecimalInputFilter.addHexadecimalInputFilter(memoryViewerAddressField);
		addMemoryViewerAddressChangedListener();
		new DefaultContextMenu().addTo(memoryViewerAddressField);

		handleUpdateMemoryViewerButton();

		memoryViewerAutoUpdateCheckBox.addActionListener(actionEvent -> tryRunningMemoryViewerUpdater());

		memoryViewerAddressField.setText(Conversions.toHexadecimal(MemoryViewerTableManager.STARTING_ADDRESS));
		memoryViewerValueField.setDocument(new InputLengthFilter(ValueSizes.THIRTY_TWO_BIT.getSize()));
		new DefaultContextMenu().addTo(memoryViewerValueField);

		valueSizePokeComboBox.addItemListener(itemEvent ->
		{
			if (itemEvent.getStateChange() == ItemEvent.SELECTED)
			{
				ValueSizes valueSize = (ValueSizes) valueSizePokeComboBox.getSelectedItem();

				switch (valueSize)
				{
					case EIGHT_BIT:
						changePokeValueSize(ValueSizes.EIGHT_BIT.getSize());
						break;

					case SIXTEEN_BIT:
						changePokeValueSize(ValueSizes.SIXTEEN_BIT.getSize());
						break;

					case THIRTY_TWO_BIT:
						changePokeValueSize(ValueSizes.THIRTY_TWO_BIT.getSize());
						break;
				}
			}
		});

		pokeOperationsComboBox.addItemListener(itemEvent ->
		{
			if (itemEvent.getStateChange() == ItemEvent.SELECTED)
			{
				ValueOperations selectedValueOperation = (ValueOperations) pokeOperationsComboBox.getSelectedItem();
				boolean enableValueField = selectedValueOperation != ValueOperations.COMPLIMENT;
				memoryViewerValueField.setEnabled(enableValueField);
			}
		});

		DefaultComboBoxModel<ValueSizes> defaultComboBoxModel2 = new DefaultComboBoxModel<>(ValueSizes.values());
		valueSizePokeComboBox.setModel(defaultComboBoxModel2);
		valueSizePokeComboBox.setSelectedItem(ValueSizes.THIRTY_TWO_BIT);
		valueField.setText("");

		addPokeButtonListener();
		updateValueAndAddressField();

		listSelectionModel = memoryViewerTable.getColumnModel().getSelectionModel();

		// Listen for column selection changes
		listSelectionModel.addListSelectionListener(listSelectionEvent -> memoryViewerCellChanged(true));

		// Listen for row selection changes
		memoryViewerTable.getSelectionModel().addListSelectionListener(listSelectionEvent -> memoryViewerCellChanged(true));
		memoryViewerCellChanged(true);

		configureMemoryViewerSearchListeners();
		followPointerButton.addActionListener(actionEvent -> followPointer());
	}

	private void configureMemoryViewerSearchListeners()
	{
		searchMemoryViewerButton.addActionListener(actionEvent -> performMemoryViewerSearch());

		memoryViewerValueField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				setMemoryViewerSearchButtonAvailability();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				setMemoryViewerSearchButtonAvailability();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				setMemoryViewerSearchButtonAvailability();
			}
		});

		new DefaultContextMenu().addTo(searchLengthField);

		searchLengthField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				setMemoryViewerSearchButtonAvailability();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				setMemoryViewerSearchButtonAvailability();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				setMemoryViewerSearchButtonAvailability();
			}
		});

		searchLengthField.setText("1000");
	}

	private void setMemoryViewerSearchButtonAvailability()
	{
		boolean validLength = hasValidSearchLength();
		boolean searchValueValid = isValidSearchValue();
		searchMemoryViewerButton.setEnabled(validLength && searchValueValid && connected);
	}

	private boolean isValidSearchValue()
	{
		String searchValue = memoryViewerValueField.getText();

		return !searchValue.equals("");
	}

	private boolean hasValidSearchLength()
	{
		String searchLength = searchLengthField.getText();

		if (searchLength.equals(""))
		{
			return false;
		}

		int searchLengthInteger = Integer.parseInt(searchLength, 16);
		return searchLengthInteger >= 4;
	}

	private void changePokeValueSize(int byteValueSize)
	{
		String currentValue = memoryViewerValueField.getText();
		memoryViewerValueField.setDocument(new InputLengthFilter(byteValueSize));

		// Cut the value if it is too big
		if (currentValue.length() > byteValueSize)
		{
			currentValue = currentValue.substring(0, byteValueSize);
		} else
		{
			// Fill up the value with leading zeros
			while (currentValue.length() < byteValueSize)
			{
				currentValue = "0" + currentValue;
			}
		}

		memoryViewerValueField.setText(currentValue);
	}

	private void performMemoryViewerSearch()
	{
		int selectedAddress = Conversions.toDecimal(memoryViewerAddressField.getText());
		int searchValue = Conversions.toDecimal(memoryViewerValueField.getText());
		int length = Conversions.toDecimal(searchLengthField.getText());
		MemoryReader memoryReader = new MemoryReader();

		try
		{
			int foundAddress = memoryReader.search(selectedAddress, searchValue, length);

			// Value found in the given range?
			if (foundAddress != 0)
			{
				// Yes, update the cells
				memoryViewerTableManager.updateCells(foundAddress, false);
				memoryViewerAddressField.setText(Conversions.toHexadecimal(foundAddress));
			} else
			{
				// No, tell the user
				JOptionPane.showMessageDialog(rootPane,
						"Value " + Conversions.toHexadecimal(searchValue) + " has not been found between address " + Conversions.toHexadecimal(selectedAddress) + " and " + Conversions.toHexadecimal(selectedAddress + length) + "!",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (IOException exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	private void configureCodesTab()
	{
		codeListSender = new CodeListSender();

		addCodeButton.addActionListener(actionEvent -> addCode());
		deleteCodeButton.addActionListener(actionEvent -> deleteSelectedCodeListEntry());
		sendCodesButton.addActionListener(actionEvent -> sendCodes());
		disableCodesButton.addActionListener(actionEvent -> disableCodes());
		codesHelpButton.addActionListener(actionEvent -> visitCodeHandlerGBATempThread());
		editCodeButton.addActionListener(actionEvent -> editSelectedCode());
		storeCodeListButton.addActionListener(actionEvent -> storeCurrentCodeList());
		downloadCodeDatabaseButton.addActionListener(actionEvent -> handleDownloadingCodeDatabase());
		exportCodeListButton.addActionListener(actionEvent -> exportCodeList());

		addCodeListBoxesMouseListener();
		addCodeListChangedListener();
		loadCodeListButton.addActionListener(actionEvent -> loadCodeList());

		updateCodeDatabaseDownloadButtonAvailability();
		handleCodeListButtonAvailability();
		setCodeListButtonsAvailability();
	}

	private void exportCodeList()
	{
		try
		{
			List<GeckoCode> codes = codesListManager.getCodeListBackup();
			String targetPath = codesStorage.exportCodeList(codes, gameId);
			askForOpeningFile(targetPath);
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	private void addCodeListBoxesMouseListener()
	{
		codesListBoxes.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent mouseEvent)
			{
				setSendCodesButtonAvailability();
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				setSendCodesButtonAvailability();
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				setSendCodesButtonAvailability();
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				setSendCodesButtonAvailability();
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				setSendCodesButtonAvailability();
			}
		});
	}

	private void handleDownloadingCodeDatabase()
	{
		try
		{
			CodeDatabaseDownloader codeDatabaseDownloader = new CodeDatabaseDownloader(gameId);
			boolean codesExist = codeDatabaseDownloader.codesExist();

			if (codesExist)
			{
				int availableCodes = codeDatabaseDownloader.getAvailableCodesCount();

				Object[] options = {"Yes", "No"};

				int selectAnswer = JOptionPane.showOptionDialog(this,
						availableCodes + " codes found. Would you like to add them to your current code list?",
						"Download codes?",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options, null);

				if (selectAnswer == JOptionPane.YES_OPTION)
				{
					List<GeckoCode> downloadedCodes = codeDatabaseDownloader.downloadCodes();

					for (GeckoCode code : downloadedCodes)
					{
						codesListManager.addCode(code);
					}
				}
			} else
			{
				JOptionPane.showMessageDialog(this,
						"No codes found for " + gameId + " on " + codeDatabaseDownloader.getCodeDatabaseURL(),
						"No codes",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	private void configureConnectionTab()
	{
		connectButtonText = connectButton.getText();

		try
		{
			titleDatabaseManager = new TitleDatabaseManager();
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}

		initializeGameTitlesDatabaseConcurrently();
		addIPAddressDocumentListener();
		new DefaultContextMenu().addTo(ipAddressField);
		connectButton.addActionListener(actionEvent -> connect());
		autoDetectCheckBox.addActionListener(actionEvent -> handleConnectionButtonsAvailability());
		reconnectButton.addActionListener(actionEvent -> reconnect());
		disconnectButton.addActionListener(actionEvent -> disconnect());
		updateGameTitlesButton.addActionListener(actionEvent -> updateGameTitles());
		connectionHelpButton.addActionListener(actionEvent -> displayConnectionHelperMessage());
		addFirmwareVersionButtonListener();
		codesListManager = new CodesListManager(codesListBoxes, rootPane);
		codesListManager.addCodeListEntryClickedListener();
		handleConnectionButtonsAvailability();
	}

	private void initializeGameTitlesDatabaseConcurrently()
	{
		new SwingWorker<String, String>()
		{
			@Override
			protected String doInBackground() throws Exception
			{
				// titleDatabaseManager.populateTitles();
				titlesInitialized = true;
				handleConnectionButtonsAvailability();

				return null;
			}
		}.execute();
	}

	private void addMemoryViewerAddressChangedListener()
	{
		memoryViewerAddressField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				handleUpdateMemoryViewerButton();
				updateValueAndAddressField();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				handleUpdateMemoryViewerButton();
				updateValueAndAddressField();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				handleUpdateMemoryViewerButton();
				updateValueAndAddressField();
			}
		});
	}

	private void memoryViewerCellChanged(boolean copyAddressValue)
	{
		if (listSelectionModel.isSelectedIndex(0))
		{
			listSelectionModel.setSelectionInterval(1, 1);
		}

		if (copyAddressValue)
		{
			updateValueAndAddressField();

			if (connected)
			{
				String memoryAddress = Conversions.decimalToHexadecimal(Integer.toString(memoryViewerTableManager.getSelectedAddress()));
				memoryViewerAddressField.setText(memoryAddress);
			}
		}
	}

	private void addPokeButtonListener()
	{
		pokeValueButton.addActionListener(actionEvent ->
		{
			try
			{
				ValueSizes valueSize = (ValueSizes) valueSizePokeComboBox.getSelectedItem();
				int targetAddress = Conversions.toDecimal(memoryViewerAddressField.getText());
				int currentValue = new MemoryReader().readValue(targetAddress, valueSize);
				int newValue = applySelectedPokeOperation(currentValue);
				MemoryWriter memoryWriter = new MemoryWriter();

				switch (valueSize)
				{
					case EIGHT_BIT:
						memoryWriter.write(targetAddress, (byte) newValue);
						break;

					case SIXTEEN_BIT:
						memoryWriter.writeShort(targetAddress, (short) newValue);
						break;

					case THIRTY_TWO_BIT:
						memoryWriter.writeInt(targetAddress, newValue);
				}

				updateMemoryViewer(false, false);
			} catch (Exception exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		});
	}

	private int applySelectedPokeOperation(int currentValue)
	{
		int pokeValue = Conversions.toDecimal(memoryViewerValueField.getText());
		int newValue = -1;
		ValueOperations pokeOperation = (ValueOperations) pokeOperationsComboBox.getSelectedItem();

		switch (pokeOperation)
		{
			case ASSIGN:
				newValue = pokeValue;
				break;

			case ADD:
				newValue = currentValue + pokeValue;
				break;

			case SUBTRACT:
				newValue = currentValue - pokeValue;
				break;

			case MULTIPLY:
				newValue = currentValue * pokeValue;
				break;

			case DIVIDE:
				newValue = currentValue / pokeValue;
				break;

			case MODULUS:
				newValue = currentValue % pokeValue;
				break;

			case AND:
				newValue = currentValue & pokeValue;
				break;

			case OR:
				newValue = currentValue | pokeValue;
				break;

			case XOR:
				newValue = currentValue ^ pokeValue;
				break;

			case COMPLIMENT:
				newValue = ~currentValue;
				break;

			case LEFT_SHIFT:
				newValue = currentValue << pokeValue;
				break;

			case RIGHT_SHIFT:
				newValue = currentValue >> pokeValue;
				break;

			case ZERO_FILL_RIGHT_SHIFT:
				newValue = currentValue >>> pokeValue;
				break;
		}

		return newValue;
	}

	private void updateValueAndAddressField()
	{
		if (isMemoryViewerAddressValid())
		{
			String selectedValue = memoryViewerTableManager.getSelectedValue();
			memoryViewerValueField.setText(selectedValue);
		}
	}

	private void addFirmwareVersionButtonListener()
	{
		firmwareVersionButton.addActionListener(actionEvent ->
		{
			// Retrieve the firmware version the first time it is requested only
			if (firmwareVersion == -1)
			{
				MemoryReader memoryReader = new MemoryReader();

				try
				{
					firmwareVersion = memoryReader.readFirmwareVersion();
				} catch (IOException exception)
				{
					StackTraceUtils.handleException(rootPane, exception);
				}
			}

			// Display it to the user
			JOptionPane.showMessageDialog(rootPane,
					"Wii U Firmware Version: " + firmwareVersion,
					"Information",
					JOptionPane.INFORMATION_MESSAGE);
		});
	}

	private void configureMemoryViewerViewsComboBox()
	{
		addMemoryViewerViews();
		addMemoryViewerViewChangedListener();
	}

	private void addMemoryViewerViewChangedListener()
	{
		memoryViewerViews.addItemListener(itemEvent ->
		{
			if (itemEvent.getStateChange() == ItemEvent.SELECTED)
			{
				MemoryViews memoryView = (MemoryViews) itemEvent.getItem();
				memoryViewerTableManager.setMemoryView(memoryView);
				memoryViewerTableManager.setMemoryCellContents();
			}
		});
	}

	private void addMemoryViewerViews()
	{
		DefaultComboBoxModel<MemoryViews> defaultComboBoxModel = new DefaultComboBoxModel<>(MemoryViews.values());
		memoryViewerViews.setModel(defaultComboBoxModel);
	}

	private void setSendCodesButtonAvailability()
	{
		boolean codesSelected = areCodesSelected();
		sendCodesButton.setEnabled(connected && codesSelected);
	}

	private boolean areCodesSelected()
	{
		List<CodeListEntry> codeListEntries = codesListManager.getActiveCodes();

		return codeListEntries.size() > 0;
	}

	private void tryRunningMemoryViewerUpdater()
	{
		handleUpdateMemoryViewerButton();

		if (memoryViewerAutoUpdateCheckBox.isSelected())
		{
			new SwingWorker<String, String>()
			{
				@Override
				protected String doInBackground() throws Exception
				{
					while (memoryViewerAutoUpdateCheckBox.isSelected() && connected)
					{
						// Only update when the user is viewing the memory viewer table
						if (memoryViewerTable.isShowing())
						{
							// If exception thrown, stop the auto update
							if (!updateMemoryViewer(false, false))
							{
								autoDetectCheckBox.setSelected(false);
								break;
							}
						}

						try
						{
							Thread.sleep(200);
						} catch (InterruptedException ignored)
						{

						}
					}

					return null;
				}
			}.execute();
		}
	}

	/**
	 * Updates the memory viewer cells as long as the entered memory address is valid
	 */
	private boolean updateMemoryViewer(boolean copyAddressValue, boolean selectMemoryAddress)
	{
		int memoryAddress = Conversions.toDecimal(memoryViewerAddressField.getText());
		int length = memoryViewerTableManager.getDisplayedBytes();
		boolean validAccess = AddressRange.isValidAccess(memoryAddress, length, MemoryAccessLevel.READ);

		if (!validAccess || !isMemoryViewerAddressValid())
		{
			return false;
		}

		try
		{
			memoryViewerTableManager.updateCells(memoryAddress, false);

			if (selectMemoryAddress)
			{
				memoryViewerTableManager.selectAddress(memoryAddress);
			}
		} catch (IOException exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
			return false;
		}

		memoryViewerCellChanged(copyAddressValue);
		return true;
	}

	private void handleUpdateMemoryViewerButton()
	{
		if (memoryViewerTableManager != null)
		{
			boolean isValid = isMemoryViewerAddressValid();
			boolean shouldEnable = isValid && connected && !memoryViewerAutoUpdateCheckBox.isSelected();
			updateMemoryViewerButton.setEnabled(shouldEnable);
			memoryViewerAddressField.setBackground(isValid ? Color.GREEN : Color.RED);
		}
	}

	private boolean isMemoryViewerAddressValid()
	{
		String memoryViewerAddress = memoryViewerAddressField.getText();
		int length = memoryViewerTableManager.getDisplayedBytes();

		return isAddressInputValid(memoryViewerAddress, length);
	}

	private boolean isAddressInputValid(String input, int length)
	{
		boolean isValid = false;

		try
		{
			if (input.length() == 8)
			{
				int address = Conversions.toDecimal(input);
				isValid = AddressRange.isValidAccess(address, length, MemoryAccessLevel.READ);
			}
		} catch (NumberFormatException exception)
		{
			isValid = false;
		}

		return isValid;
	}

	private boolean isAddressInputValid(String input)
	{
		return isAddressInputValid(input, 4);
	}

	private void configureConversionsTab()
	{
		hexadecimalUTF8Button.addActionListener(actionEvent -> displayConversionDialog(actionEvent, ConversionType.hexadecimalToUTF8));
		UTF8HexadecimalButton.addActionListener(actionEvent -> displayConversionDialog(actionEvent, ConversionType.utf8ToHexadecimal));
		hexadecimalFloatingPointButton.addActionListener(actionEvent -> displayConversionDialog(actionEvent, ConversionType.hexadecimalToFloatingPoint));
		floatingPointHexadecimalButton.addActionListener(actionEvent -> displayConversionDialog(actionEvent, ConversionType.floatingPointToHexadecimal));
		hexadecimalIntegerButton.addActionListener(actionEvent -> displayConversionDialog(actionEvent, ConversionType.hexadecimalToDecimal));
		integerHexadecimalButton.addActionListener(actionEvent -> displayConversionDialog(actionEvent, ConversionType.decimalToHexadecimal));
		hexadecimalUnicodeButton.addActionListener(actionEvent -> displayConversionDialog(actionEvent, ConversionType.hexadecimalToUnicode));
		unicodeHexadecimalButton.addActionListener(actionEvent -> displayConversionDialog(actionEvent, ConversionType.unicodeToHexadecimal));
	}

	private void displayConversionDialog(ActionEvent actionEvent, ConversionType conversionType)
	{
		String dialogTitle = ((JButton) actionEvent.getSource()).getText();
		ConversionDialog conversionDialog = new ConversionDialog(this, dialogTitle, conversionType);
		conversionDialog.display();
	}

	private void addCodeListChangedListener()
	{
		codesListManager.getCodesListBoxes().addListSelectionListener(selectionEvent -> handleCodeListButtonAvailability());
	}

	private void handleCodeListButtonAvailability()
	{
		editCodeButton.setEnabled(codesListManager.isSomeEntrySelected());
		deleteCodeButton.setEnabled(codesListManager.isSomeEntrySelected());
	}

	private void loadCodeList()
	{
		CodeListChooser codeListChooser = new CodeListChooser();

		JFileChooserUtilities.registerDeleteAction(codeListChooser);
		int selectedOption = codeListChooser.showOpenDialog(rootPane);
		if (selectedOption == JFileChooser.APPROVE_OPTION)
		{
			File chosenFile = codeListChooser.getSelectedFile();
			String fileName = chosenFile.getName();
			String baseFileName = FilenameUtils.getBaseName(fileName);
			gameId = baseFileName;

			try
			{
				restoreCodesList(baseFileName);
				setSendCodesButtonAvailability();
			} catch (Exception exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}

			updateCodeDatabaseDownloadButtonAvailability();
		}
	}

	private void updateCodeDatabaseDownloadButtonAvailability()
	{
		downloadCodeDatabaseButton.setEnabled(gameId != null);
	}

	private void reconnect()
	{
		disconnect();
		connect();
	}

	private void displayConnectionHelperMessage()
	{
		try
		{
			ConnectionHelper.displayConnectionHelperMessage(rootPane);
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	private void addCode()
	{
		AddCodeDialog addCodeDialog = new AddCodeDialog();
		addCodeDialog.display();

		if (addCodeDialog.isConfirmed())
		{
			CodeListEntry codeListEntry = addCodeDialog.getCodeListEntry();
			codesListManager.addCodeListEntry(codeListEntry);
		}
	}

	private void deleteSelectedCodeListEntry()
	{
		codesListManager.deleteSelectedCodeListEntry(false);
	}

	private void sendCodes()
	{
		try
		{
			List<CodeListEntry> codeListEntries = codesListManager.getActiveCodes();
			codeListSender.setCodesList(codeListEntries);
			codeListSender.applyCodes();

			JOptionPane.showMessageDialog(rootPane,
					"Codes have been sent successfully!",
					"Codes sent",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	private void disableCodes()
	{
		try
		{
			codeListSender.disableCodes();
			JOptionPane.showMessageDialog(rootPane,
					"Codes disabled!",
					"Success",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	private void visitCodeHandlerGBATempThread()
	{
		int selectedAnswer = JOptionPane.showConfirmDialog(
				rootPane,
				"Do you want to view the code types documentation?",
				"Code Types Documentation?",
				JOptionPane.YES_NO_OPTION);

		if (selectedAnswer == JOptionPane.YES_OPTION)
		{
			try
			{
				URI link = new URI("http://cosmocortney.ddns.net/enzy/cafe_code_types_en.php");
				Desktop.getDesktop().browse(link);
			} catch (Exception exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		}
	}

	private void updateGameTitles()
	{
		int selectedAnswer = JOptionPane.showConfirmDialog(
				rootPane,
				"Do you want to update the game titles database?\n(This can help if your game is not detected automatically on connect)",
				updateGameTitlesButton.getText(),
				JOptionPane.YES_NO_OPTION);

		if (selectedAnswer == JOptionPane.YES_OPTION)
		{
			new SwingWorker<String, String>()
			{
				@Override
				protected String doInBackground() throws Exception
				{
					String currentText = updateGameTitlesButton.getText();
					updateGameTitlesButton.setEnabled(false);
					updateGameTitlesButton.setText("Updating...");

					try
					{
						titleDatabaseManager.update();
						JOptionPane.showMessageDialog(rootPane,
								"Update complete",
								"Success",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception exception)
					{
						StackTraceUtils.handleException(rootPane, exception);
					} finally
					{
						updateGameTitlesButton.setEnabled(true);
						updateGameTitlesButton.setText(currentText);
					}

					return null;
				}
			}.execute();
		}
	}

	private void editSelectedCode()
	{
		CodeListEntry codeListEntry = codesListManager.getSelectedCodeListEntry();

		if (codeListEntry != null)
		{
			boolean selected = codesListManager.isSelectedCodeListEntryTicked();
			AddCodeDialog addCodeDialog = new AddCodeDialog(codeListEntry);
			addCodeDialog.display();

			if (addCodeDialog.isConfirmed())
			{
				codesListManager.deleteSelectedCodeListEntry(true);
				codeListEntry = addCodeDialog.getCodeListEntry();
				codesListManager.addCodeListEntry(codeListEntry, selected);
			}
		}
	}

	private void storeCurrentCodeList()
	{
		try
		{
			List<GeckoCode> codes = codesListManager.getCodeListBackup();
			String codeListFilePath = codesStorage.writeCodeList(codes);

			JOptionPane.showMessageDialog(rootPane,
					"Code list stored to " + codeListFilePath,
					"Stored",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	/**
	 * Disconnects a connection with the Wii U
	 */
	private void disconnect()
	{
		try
		{
			Connector.getInstance().closeConnection();
			connected = false;
			connectButton.setText(connectButtonText);
			setTitle(programName);
			handleConnectionButtonsAvailability();
			codesListManager.clearCodeList();
		} catch (Exception ioException)
		{
			StackTraceUtils.handleException(rootPane, ioException);
		}
	}

	/**
	 * Method for connecting to the Wii U either via auto detection or IP address
	 */
	private void connect()
	{
		new SwingWorker<String, String>()
		{
			@Override
			protected String doInBackground() throws Exception
			{
				tabs.setEnabled(false);
				connectButton.setText("Connecting...");
				connecting = true;
				handleConnectionButtonsAvailability();

				try
				{
					String detectedIPAddress;

					if (autoDetectCheckBox.isSelected())
					{
						if (connectedIPAddress == null)
						{
							// Find the Wii U in the network and connect to it
							detectedIPAddress = WiiUFinder.getNintendoWiiUInternetProtocolAddress();
							connect(detectedIPAddress, true);
						} else
						{
							// Since we were connected already, we can use that IP address instead of scanning the network
							detectedIPAddress = connectedIPAddress;
							connect(detectedIPAddress, false);
						}
					} else
					{
						// Use the given IP address
						detectedIPAddress = ipAddressField.getText();
						connect(detectedIPAddress, false);
					}

					updateMemoryViewer(true, false);
				} catch (Exception exception)
				{
					connectButton.setText(connectButtonText);
					StackTraceUtils.handleException(rootPane, exception);
				} finally
				{
					tabs.setEnabled(true);
					connecting = false;
					handleConnectionButtonsAvailability();
				}

				return null;
			}

			@Override
			public void done()
			{
				tryRunningMemoryViewerUpdater();
			}
		}.execute();
	}

	/**
	 * Connects to an IP address of a Wii U console
	 *
	 * @param ipAddress          The IP address to connect to
	 * @param skipRealConnecting Skips the actual connection step
	 */
	private void connect(String ipAddress, boolean skipRealConnecting) throws Exception
	{
		if (!skipRealConnecting)
		{
			Connector.getInstance().connect(ipAddress);
		}

		connected = true;
		connectButton.setText(connectButtonText + "ed [" + ipAddress + ":" + Connector.PORT + "]");
		connectedIPAddress = ipAddress;

		setGameSpecificTitle();
		restoreCodesList();
		restoreWatchList();
	}

	private void restoreWatchList()
	{
		try
		{
			WatchListStorage watchListStorage = new WatchListStorage(gameId);
			List<WatchListElement> watchListElements = watchListStorage.restore();

			if (watchListElements != null)
			{
				watchListManager.setRows(watchListElements);
			}
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	private void restoreCodesList(String fileName) throws Exception
	{
		codesStorage = new CodeListStorage(fileName);
		setCodeListButtonsAvailability();
		List<GeckoCode> backedUpCodes = codesStorage.getCodeList();
		codesListManager.setCodesListBackup(backedUpCodes);
	}

	private void restoreCodesList() throws Exception
	{
		restoreCodesList(gameId);
	}

	private void setGameSpecificTitle() throws Exception
	{
		String gameTitle = getGameSpecificTitle();
		setTitle(gameTitle);
	}

	private String getGameSpecificTitle() throws Exception
	{
		String gameName = null;

		try
		{
			TitleDatabaseManager titleDatabaseManager = new TitleDatabaseManager();

			Title title;

			if (gameId == null)
			{
				title = titleDatabaseManager.getTitle();
				gameId = title.getGameId();
				updateCodeDatabaseDownloadButtonAvailability();
			} else
			{
				title = titleDatabaseManager.getTitle(gameId);
			}

			gameName = title.getGameName();
		} catch (TitleNotFoundException | FirmwareNotImplementedException exception)
		{
			if (exception instanceof FirmwareNotImplementedException)
			{
				JOptionPane.showMessageDialog(rootPane,
						exception.getMessage(),
						"Warning",
						JOptionPane.WARNING_MESSAGE);
			}

			// Let the user input the data then
			NewGameDialog newGameDialog = new NewGameDialog(this);
			newGameDialog.display();

			if (newGameDialog.confirmed())
			{
				gameId = newGameDialog.getGameId();
				gameName = newGameDialog.getGameName();

				Title title = titleDatabaseManager.getTitle(gameId);

				if (title != null)
				{
					gameName = title.getGameName();
				}
			}
		}

		// Still not defined? Use dummy values
		if (gameName == null)
		{
			gameName = "Unknown";
			gameId = "######";
		}

		return gameName + " [" + gameId + "]";
	}

	private void addIPAddressDocumentListener()
	{
		ipAddressField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				handleConnectionButtonsAvailability();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				handleConnectionButtonsAvailability();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				handleConnectionButtonsAvailability();
			}
		});
	}

	/**
	 * Contains the logic for the availability of buttons and field on the connect tab
	 */
	private void handleConnectionButtonsAvailability()
	{
		String inputtedIPAddress = ipAddressField.getText();
		boolean isValidIPAddress = IPAddressValidator.validateIPv4Address(inputtedIPAddress);
		boolean isAutoDetect = autoDetectCheckBox.isSelected();
		boolean mayConnect = (isValidIPAddress || isAutoDetect);
		boolean shouldEnableConnectButton = !connected && mayConnect && !connecting && titlesInitialized;
		connectButton.setEnabled(shouldEnableConnectButton);
		disconnectButton.setEnabled(connected);
		reconnectButton.setEnabled(!connectButton.isEnabled() && disconnectButton.isEnabled());
		ipAddressField.setEnabled(!isAutoDetect);
		setSendCodesButtonAvailability();
		disableCodesButton.setEnabled(connected);
		ipAddressField.setBackground(isValidIPAddress ? Color.GREEN : Color.RED);
		loadCodeListButton.setEnabled(!connected);
		memoryViewerAutoUpdateCheckBox.setEnabled(connected);
		firmwareVersionButton.setEnabled(connected);
		memoryViewerViews.setEnabled(connected);
		pokeValueButton.setEnabled(connected);
		followPointerButton.setEnabled(connected);
		addWatchButton.setEnabled(connected);
		addAddressExpressionsButton.setEnabled(connected);
		exportWatchListButton.setEnabled(connected);
		saveWatchListButton.setEnabled(connected);
		setMemoryViewerSearchButtonAvailability();

		if (isAutoDetect)
		{
			ipAddressField.setBackground(Color.GREEN);
		}

		handleUpdateMemoryViewerButton();
		handleDumpMemoryButtonAvailability();
	}

	private void setCodeListButtonsAvailability()
	{
		storeCodeListButton.setEnabled(codesStorage != null);
		exportCodeListButton.setEnabled(codesStorage != null);
	}

	public void addMemoryViewerOffset(int offset)
	{
		int selectedAddress = getSelectedMemoryViewerAddress();
		selectedAddress += offset;
		String destinationAddressHexadecimal = Long.toHexString(selectedAddress).toUpperCase();
		memoryViewerAddressField.setText(destinationAddressHexadecimal);
		updateMemoryViewer(true, true);
	}

	/**
	 * Defines properties for the main window
	 */
	private void setFrameProperties()
	{
		programName = "JGecko U";
		setTitle(programName);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(780, 350);
		setLocationRelativeTo(null);
		WindowUtilities.setIconImage(this);
	}

	public boolean isConnected()
	{
		return connected;
	}

	public void copyMemoryViewerCells()
	{
		memoryViewerTableManager.copyCells();
	}

	public int getSelectedMemoryViewerAddress()
	{
		return memoryViewerTableManager.getSelectedAddress();
	}

	public int getSelectedMemoryViewerValue()
	{
		return (int) Long.parseLong(memoryViewerTableManager.getSelectedValue(), 16);
	}

	public void updateMemoryViewer()
	{
		updateMemoryViewer(true, true);
	}
}