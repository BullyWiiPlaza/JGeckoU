package wiiudev.gecko.client.gui;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import wiiudev.gecko.client.codes.*;
import wiiudev.gecko.client.conversions.ConversionType;
import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.conversions.Validation;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.dialogs.*;
import wiiudev.gecko.client.gui.input_filters.HexadecimalInputFilter;
import wiiudev.gecko.client.gui.input_filters.InputLengthFilter;
import wiiudev.gecko.client.gui.input_filters.ValueSizes;
import wiiudev.gecko.client.gui.tabs.GraphicalMemoryDumper;
import wiiudev.gecko.client.gui.tabs.code_list.AddCodeDialog;
import wiiudev.gecko.client.gui.tabs.code_list.CodesListManager;
import wiiudev.gecko.client.gui.tabs.disassembler.DisassembledInstruction;
import wiiudev.gecko.client.gui.tabs.disassembler.DisassemblerTableManager;
import wiiudev.gecko.client.gui.tabs.disassembler.assembler.Assembler;
import wiiudev.gecko.client.gui.tabs.disassembler.assembler.AssemblerException;
import wiiudev.gecko.client.gui.tabs.disassembler.assembler.AssemblerFilesException;
import wiiudev.gecko.client.gui.tabs.memory_search.SearchResultsTableManager;
import wiiudev.gecko.client.gui.tabs.memory_search.SearchValueConversionContextMenu;
import wiiudev.gecko.client.gui.tabs.memory_viewer.MemoryViewerTableManager;
import wiiudev.gecko.client.gui.tabs.memory_viewer.MemoryViews;
import wiiudev.gecko.client.gui.tabs.pointer_search.DownloadingUtilities;
import wiiudev.gecko.client.gui.tabs.pointer_search.ZipUtils;
import wiiudev.gecko.client.gui.tabs.threads.ThreadsTableManager;
import wiiudev.gecko.client.gui.tabs.watch_list.*;
import wiiudev.gecko.client.gui.utilities.DefaultContextMenu;
import wiiudev.gecko.client.gui.utilities.JFileChooserUtilities;
import wiiudev.gecko.client.gui.utilities.JTableUtilities;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;
import wiiudev.gecko.client.memory_search.*;
import wiiudev.gecko.client.memory_search.enumerations.SearchConditions;
import wiiudev.gecko.client.memory_search.enumerations.SearchMode;
import wiiudev.gecko.client.memory_search.enumerations.ValueSize;
import wiiudev.gecko.client.memory_viewer.ValueOperations;
import wiiudev.gecko.client.network_scanner.WiiUFinder;
import wiiudev.gecko.client.tcpgecko.main.Connector;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThread;
import wiiudev.gecko.client.tcpgecko.main.threads.OSThreadRPC;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.AddressRange;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.MemoryAccessLevel;
import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;
import wiiudev.gecko.client.tcpgecko.rpl.RemoteDisassembler;
import wiiudev.gecko.client.tcpgecko.rpl.structures.OSSystemInfo;
import wiiudev.gecko.client.titles.Title;
import wiiudev.gecko.client.titles.TitleDatabaseManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * A class defining the main frame of the application
 */
public class JGeckoUGUI extends JFrame
{
	private JPanel rootPanel;
	private JButton connectButton;
	private JButton disconnectButton;
	private JTextField searchStartingAddressField;
	private JTextField searchEndingAddressField;
	private JComboBox<SearchConditions> searchConditionComboBox;
	private JComboBox<SearchMode> searchModeComboBox;
	private JTextField searchValueField;
	private JButton searchButton;
	private JButton restartSearchButton;
	private JTable searchResultsTable;
	private JButton connectionHelpButton;
	private JTextField ipAddressField;
	private JCheckBox autoDetectCheckBox;
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
	private JButton pokeMemoryViewerValueButton;
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
	private JTabbedPane programTabs;
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
	private JButton hexEditorButton;
	private JPanel dumpingTab;
	private JCheckBox autoSaveCodeListCheckBox;
	private JProgressBar memoryDumpingProgressBar;
	private JPanel searchTab;
	private JCheckBox memoryAddressProtectionCheckBox;
	private JButton convertEffectiveToPhysicalButton;
	private JButton displayMessageButton;
	private JButton remoteProcedureCallButton;
	private JTree tree1;
	private JButton readFileSystemButton;
	private JTextArea textArea1;
	private JPanel fileSystemTab;
	private JTable threadsTable;
	private JButton readThreadsButton;
	private JCheckBox updateWatchlistCheckBox;
	private JPanel memoryViewerTab;
	private JTextArea registersArea;
	private JCheckBox autoUpdateRegistersCheckBox;
	private JTable disassemblerTable;
	private JTextField disassemblerAddressField;
	private JButton updateDisassemblerButton;
	private JPanel disassemblerTab;
	private JPanel watchListTab;
	private JTextField disassemblerInstructionField;
	private JButton assembleInstructionButton;
	private JButton powerPCAssemblyDocumentationButton;
	private JButton osTimeButton;
	private JButton processPFIDButton;
	private JButton remoteDisassemblerButton;
	private JButton titleIDButton;
	private JButton osIDButton;
	private JButton appFlagsButton;
	private JButton sdkVersionButton;
	private JButton shutdownButton;
	private JButton tcpGeckoThreadButton;
	private JButton systemInformationButton;
	private JComboBox<ValueSize> searchValueSizeComboBox;
	private JLabel resultsCountLabel;
	private JButton undoSearchButton;
	private JLabel searchIterationLabel;
	private JProgressBar searchProgressBar;
	private JButton saveSearchButton;
	private JButton loadSearchButton;
	private JButton memoryBoundsButton;
	private JLabel threadsCountLabel;
	private JButton speedCrunchButton;
	private JButton readKernelIntegerButton;
	private JTextField kernelReadAddressField;
	private JCheckBox kernelWriteCheckBox;
	private MemoryViewerTableManager memoryViewerTableManager;
	private CodesListManager codesListManager;
	private ListSelectionModel listSelectionModel;
	private boolean connecting;
	private String connectButtonText;
	private String connectedIPAddress;
	private CodeListSender codeListSender;
	private TitleDatabaseManager titleDatabaseManager;
	private boolean titlesInitialized;
	private String gameId;
	private String programName;
	private CodeListStorage codesStorage;
	private SimpleProperties simpleProperties;
	private WatchListManager watchListManager;
	private boolean readingThreads;
	private DisassemblerTableManager disassemblerTableManager;
	private ThreadsTableManager threadsTableManager;
	private boolean assembling;
	private MemorySearcher memorySearcher;
	private boolean searching;
	private boolean noResultsFound;
	private SearchResultsTableManager searchResultsTableManager;
	private static JGeckoUGUI instance;

	private JGeckoUGUI()
	{
		addFormDesignerPanel();
		setFrameProperties();
		addTabsChangedListener();

		configureConnectionTab();
		configureCodesTab();
		configureThreadsTab();
		configureDisassemblerTab();
		configureExternalToolsTab();
		configureMemoryViewerTab();
		configureConversionsTab();
		configureWatchListTab();
		configureMemoryDumpingTab();
		removeUnfinishedTabs();
		configureMiscellaneousTab();
		configureRemoteProcedureCallTab();
		configureAboutTab();
		configureSearchTab();

		restorePersistentSettings();
		addSettingsBackupShutdownHook();
	}

	private void configureSearchTab()
	{
		populateSearchModes();
		populateSearchConditions();
		populateSearchValueSizes();

		searchResultsTableManager = new SearchResultsTableManager(searchResultsTable);
		searchResultsTableManager.configure();

		searchProgressBar.setStringPainted(true);

		SearchValueConversionContextMenu searchValueConversionContextMenu = new SearchValueConversionContextMenu(searchValueField);
		searchValueConversionContextMenu.addMouseListener();

		searchValueField.addKeyListener(new KeyAdapter()
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
						&& searchButton.isEnabled())
				{
					performSearchConcurrently();
				}
			}
		});

		searchValueField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				setSearchValueBackgroundColor();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				setSearchValueBackgroundColor();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				setSearchValueBackgroundColor();
			}
		});

		setSearchValueBackgroundColor();
		setSearchInputFilter();

		HexadecimalInputFilter.setHexadecimalInputFilter(searchStartingAddressField);
		HexadecimalInputFilter.setHexadecimalInputFilter(searchEndingAddressField);

		searchEndingAddressField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				setConnectionButtonsAvailability();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				setConnectionButtonsAvailability();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				setConnectionButtonsAvailability();
			}
		});

		searchStartingAddressField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				setConnectionButtonsAvailability();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				setConnectionButtonsAvailability();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				setConnectionButtonsAvailability();
			}
		});

		searchModeComboBox.addItemListener(itemEvent ->
		{
			if (itemEvent.getStateChange() == ItemEvent.SELECTED)
			{
				setConnectionButtonsAvailability();
			}
		});

		loadSearchButton.addActionListener(actionEvent ->
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			JFileChooserUtilities.registerDeleteAction(fileChooser);
			JFileChooserUtilities.setXMLFileChooser(fileChooser);

			try
			{
				setSearchResultsCurrentDirectory(fileChooser);
				int selectedOption = fileChooser.showOpenDialog(this);

				if (selectedOption == JOptionPane.OK_OPTION)
				{
					File selectedFile = fileChooser.getSelectedFile();
					ResultsStorage resultsStorage = new ResultsStorage(selectedFile.getAbsolutePath());
					SearchBackup searchBackup = resultsStorage.readResults();
					List<SearchResult> searchResults = searchBackup.getSearchResults();
					populateSearchResults(searchResults);
					SearchBounds searchBounds = searchBackup.getSearchBounds();
					searchStartingAddressField.setText(Conversions.toHexadecimal(searchBounds.getAddress()));
					searchEndingAddressField.setText(Conversions.toHexadecimal(searchBounds.getAddress() + searchBounds.getLength()));
					memorySearcher = new MemorySearcher(searchBounds);
					searchValueSizeComboBox.setSelectedItem(searchBackup.getValueSize());
					memorySearcher.setSetResults(searchResults);
					updateSearchIterationsLabel();
					setSearchButtonsAvailability();
				}
			} catch (Exception exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		});

		saveSearchButton.addActionListener(actionEvent ->
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			JFileChooserUtilities.registerDeleteAction(fileChooser);
			JFileChooserUtilities.setXMLFileChooser(fileChooser);

			try
			{
				setSearchResultsCurrentDirectory(fileChooser);
				int selectedOption = fileChooser.showSaveDialog(this);

				if (selectedOption == JOptionPane.OK_OPTION)
				{
					File selectedFile = fileChooser.getSelectedFile();
					ResultsStorage resultsStorage = new ResultsStorage(selectedFile.getAbsolutePath());
					List<SearchResult> searchResults = searchResultsTableManager.getSearchResults();
					SearchBounds searchBounds = new SearchBounds(Conversions.toDecimal(searchStartingAddressField.getText()),
							Conversions.toDecimal(searchEndingAddressField.getText())
									- Conversions.toDecimal(searchStartingAddressField.getText()));
					resultsStorage.writeResults(searchResults, searchBounds);

					JOptionPane.showMessageDialog(this,
							"Search results written to\n"
									+ resultsStorage.getFilePath(),
							"Success",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (Exception exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		});

		updateSearchResultsCountLabel();
		updateSearchIterationsLabel();
		setConnectionButtonsAvailability();

		addSearchButtonListener();
		addNewSearchButtonListener();
		addUndoButtonListener();
	}

	private void setSearchValueBackgroundColor()
	{
		boolean valueOkay = isSearchValueOkay();
		searchValueField.setBackground(valueOkay ? Color.GREEN : Color.RED);
		setSearchButtonAvailability();
	}

	private boolean isSearchValueOkay()
	{
		String value = searchValueField.getText();
		boolean isHexadecimal = Validation.isHexadecimal(value);
		int valueSizeBytesCount = searchValueSizeComboBox.getItemAt(searchValueSizeComboBox.getSelectedIndex()).getBytesCount() * 2;
		boolean isLengthOkay = value.length() <= valueSizeBytesCount;
		return isHexadecimal && isLengthOkay;
	}

	private Path setSearchResultsCurrentDirectory(JFileChooser fileChooser) throws Exception
	{
		String targetDirectory = "searches";

		if (TCPGecko.isConnected())
		{
			TitleDatabaseManager titleDatabaseManager = new TitleDatabaseManager();
			Title title = titleDatabaseManager.readTitle();
			String parentFolderName = title.getGameId();
			targetDirectory += File.separator + parentFolderName;
		}

		Path targetPath = Paths.get(targetDirectory);
		Files.createDirectories(targetPath);

		fileChooser.setCurrentDirectory(targetPath.toFile());

		return targetPath;
	}

	private void addUndoButtonListener()
	{
		undoSearchButton.addActionListener(actionEvent ->
		{
			Object[] options = {"Yes", "No"};

			int selectedAnswer = JOptionPane.showOptionDialog(rootPane,
					"Would you really like to undo the last memory search?",
					undoSearchButton.getText(),
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					null);

			if (selectedAnswer == JOptionPane.YES_OPTION)
			{
				List<SearchResult> searchResults = memorySearcher.undoSearchResults();
				populateSearchResults(searchResults);

				noResultsFound = false;
				setConnectionButtonsAvailability();
				updateSearchIterationsLabel();
			}
		});
	}

	private void addNewSearchButtonListener()
	{
		restartSearchButton.addActionListener(actionEvent ->
		{
			Object[] options = {"Yes", "No"};

			int selectedAnswer = JOptionPane.showOptionDialog(rootPane,
					"Would you really like to start a new memory search?",
					restartSearchButton.getText(),
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					null);

			if (selectedAnswer == JOptionPane.YES_OPTION)
			{
				startNewSearch();
			}
		});
	}

	private void addSearchButtonListener()
	{
		searchButton.addActionListener(actionEvent -> performSearchConcurrently());
	}

	private void performSearchConcurrently()
	{
		new SwingWorker<String, String>()
		{
			@Override
			protected String doInBackground() throws Exception
			{
				String searchButtonText = searchButton.getText();
				searching = true;
				searchButton.setText("Dumping...");
				setConnectionButtonsAvailability();

				try
				{
					if (memorySearcher == null)
					{
						defineMemorySearcher();
					}

					SearchRefinement searchRefinement = getSearchRefinement();
					List<SearchResult> searchResults = memorySearcher.search(searchRefinement);
					populateSearchResults(searchResults);
					updateSearchIterationsLabel();
					setConnectionButtonsAvailability();
				} catch (Exception exception)
				{
					StackTraceUtils.handleException(rootPane, exception);
				} finally
				{
					searchButton.setText(searchButtonText);
					searching = false;

					if (searchResultsTableManager.areSearchResultsEmpty())
					{
						noResultsFound = true;

						Object[] options = {"Yes", "No"};

						int selectedAnswer = JOptionPane.showOptionDialog(rootPane,
								"Would you like to start a new memory search?",
								"No results found",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null,
								options,
								null);

						if (selectedAnswer == JOptionPane.YES_OPTION)
						{
							startNewSearch();
						}
					}

					setConnectionButtonsAvailability();
				}

				return null;
			}
		}.execute();
	}

	private void defineMemorySearcher()
	{
		// This can only be defined at the start of a new memory search
		int startingAddress = Conversions.toDecimal(searchStartingAddressField.getText());
		int endAddress = Conversions.toDecimal(searchEndingAddressField.getText());
		memorySearcher = new MemorySearcher(startingAddress, endAddress - startingAddress);
		setConnectionButtonsAvailability();
	}

	private SearchRefinement getSearchRefinement()
	{
		// This can be changed during each memory search step
		SearchConditions searchConditions = searchConditionComboBox.getItemAt(searchConditionComboBox.getSelectedIndex());
		ValueSize valueSize = searchValueSizeComboBox.getItemAt(searchValueSizeComboBox.getSelectedIndex());
		SearchMode searchMode = searchModeComboBox.getItemAt(searchModeComboBox.getSelectedIndex());

		switch (searchMode)
		{
			case SPECIFIC:
				BigInteger value = new BigInteger(searchValueField.getText(), 16);
				return new SearchRefinement(searchConditions, valueSize, value);

			case UNKNOWN:
				if (memorySearcher.isFirstSearch())
				{
					return new SearchRefinement(valueSize);
				}

				return new SearchRefinement(searchConditions, valueSize);

			default:
				throw new IllegalStateException("Unhandled memory search mode");
		}
	}

	private void updateSearchIterationsLabel()
	{
		int iteration = memorySearcher == null ? 0 : memorySearcher.getSearchIterationsCount();
		searchIterationLabel.setText("Iteration: " + iteration);
	}

	private void populateSearchResults(List<SearchResult> searchResults)
	{
		searchResultsTableManager.populateSearchResults(searchResults);
		updateSearchResultsCountLabel();
	}

	private void startNewSearch()
	{
		searchResultsTableManager.removeAllRows();
		searchResultsTableManager.clearSearchResults();
		memorySearcher = null;

		updateSearchResultsCountLabel();
		updateSearchIterationsLabel();
		noResultsFound = false;
		setConnectionButtonsAvailability();
	}

	private void setSearchInputFilter()
	{
		/*ValueSize valueSize = searchValueSizeComboBox.getItemAt(searchValueSizeComboBox.getSelectedIndex());
		int charactersLength = valueSize.getBytesCount() * 2;
		HexadecimalInputFilter.setHexadecimalInputFilter(searchValueField, charactersLength);*/
	}

	private void populateSearchValueSizes()
	{
		DefaultComboBoxModel<ValueSize> defaultComboBoxModel = new DefaultComboBoxModel<>(ValueSize.values());
		searchValueSizeComboBox.setModel(defaultComboBoxModel);
		searchValueSizeComboBox.setSelectedItem(ValueSize.THIRTY_TWO_BIT);

		searchValueSizeComboBox.addItemListener(itemEvent ->
		{
			if (itemEvent.getStateChange() == ItemEvent.SELECTED)
			{
				setConnectionButtonsAvailability();
				setSearchInputFilter();
				setSearchValueBackgroundColor();
			}
		});
	}

	private void populateSearchConditions()
	{
		DefaultComboBoxModel<SearchConditions> defaultComboBoxModel2 = new DefaultComboBoxModel<>(SearchConditions.values());
		searchConditionComboBox.setModel(defaultComboBoxModel2);
	}

	private void populateSearchModes()
	{
		DefaultComboBoxModel<SearchMode> defaultComboBoxModel = new DefaultComboBoxModel<>(SearchMode.values());
		searchModeComboBox.setModel(defaultComboBoxModel);
		searchModeComboBox.setSelectedItem(SearchMode.SPECIFIC);
	}

	private void setSearchButtonsAvailability()
	{
		boolean searchStarted = memorySearcher != null;
		restartSearchButton.setEnabled(searchStarted && !searching);
		searchValueSizeComboBox.setEnabled(!searchStarted);
		searchStartingAddressField.setEnabled(!searchStarted);
		searchEndingAddressField.setEnabled(!searchStarted);
		undoSearchButton.setEnabled(searchStarted && memorySearcher.canUndoSearch() && !searching);
		loadSearchButton.setEnabled(!searching);
		saveSearchButton.setEnabled(searchResultsTableManager != null
				&& searchResultsTableManager.getSearchResults() != null
				&& !searchResultsTableManager.getSearchResults().isEmpty()
				&& searchResultsTableManager.getSearchResults().size() < 99999);
		SearchMode searchMode = searchModeComboBox.getItemAt(searchModeComboBox.getSelectedIndex());
		if (searchMode == SearchMode.UNKNOWN)
		{
			searchValueField.setEnabled(false);

			// First memory search?
			if (memorySearcher == null)
			{
				searchConditionComboBox.setEnabled(false);
			} else
			{
				searchConditionComboBox.setEnabled(true);
			}
		} else
		{
			searchValueField.setEnabled(true);
			searchConditionComboBox.setEnabled(true);
		}

		setSearchButtonAvailability();
	}

	public void updateSearchResultsCountLabel()
	{
		int count = (searchResultsTableManager == null || searchResultsTableManager.getSearchResults() == null)
				? 0 : searchResultsTableManager.getSearchResults().size();
		resultsCountLabel.setText("Results: " + count);
	}

	private void addTabsChangedListener()
	{
		programTabs.addChangeListener(changeEvent ->
				considerUpdatingTabs());
	}

	private void configureDisassemblerTab()
	{
		disassemblerTableManager = new DisassemblerTableManager(disassemblerTable);
		disassemblerTableManager.configure();

		assembleInstructionButton.addActionListener(actionEvent ->
				assembleInstruction());

		powerPCAssemblyDocumentationButton.addActionListener(actionEvent ->
				openURL("http://www.ds.ewi.tudelft.nl/vakken/in1006/instruction-set/"));

		disassemblerTable.getSelectionModel().addListSelectionListener(event ->
		{
			DisassembledInstruction disassembledInstruction = disassemblerTableManager.getSelectedInstruction();

			if (disassembledInstruction != null)
			{
				String instruction = disassembledInstruction.getInstruction();
				disassemblerInstructionField.setText(instruction);
			}
		});

		disassemblerInstructionField.addKeyListener(new KeyAdapter()
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
						&& assembleInstructionButton.isEnabled())
				{
					int cursorPosition = disassemblerInstructionField.getCaretPosition();
					assembleInstruction();
					disassemblerInstructionField.requestFocus();

					// This can happen if the assembled instruction is shorter than the input
					if (cursorPosition < disassemblerInstructionField.getText().length())
					{
						disassemblerInstructionField.setCaretPosition(cursorPosition);
					}
				}
			}
		});

		disassemblerAddressField.addKeyListener(new KeyAdapter()
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
						&& updateDisassemblerButton.isEnabled())
				{
					int cursorPosition = disassemblerAddressField.getCaretPosition();
					updateDisassembler();
					disassemblerAddressField.requestFocus();
					disassemblerAddressField.setCaretPosition(cursorPosition);
				}
			}
		});

		configureDisassemblerAddressField();
	}

	private void assembleInstruction()
	{
		new SwingWorker<String, String>()
		{
			@Override
			protected String doInBackground() throws Exception
			{
				String buttonText = assembleInstructionButton.getText();
				assembleInstructionButton.setText("Assembling...");
				assembling = true;
				setConnectionButtonsAvailability();

				String instruction = disassemblerInstructionField.getText();

				try
				{
					String assembled = Assembler.assemble(instruction);
					DisassembledInstruction disassembledInstruction = disassemblerTableManager.getSelectedInstruction();
					MemoryWriter memoryWriter = new MemoryWriter();
					int address = disassembledInstruction.getAddress();
					int value = Integer.parseUnsignedInt(assembled, 16);
					memoryWriter.writeKernelInt(address, value);

					updateDisassembler();
				} catch (AssemblerFilesException | AssemblerException exception)
				{
					JOptionPane.showMessageDialog(JGeckoUGUI.this,
							exception.getMessage(),
							"Assembler Error",
							JOptionPane.ERROR_MESSAGE);
				} catch (Exception exception)
				{
					StackTraceUtils.handleException(rootPane, exception);
				} finally
				{
					assembling = false;
					assembleInstructionButton.setText(buttonText);
					setConnectionButtonsAvailability();
				}

				return null;
			}
		}.execute();
	}

	private void configureDisassemblerAddressField()
	{
		updateDisassemblerButton.addActionListener(actionEvent ->
				updateDisassembler());

		HexadecimalInputFilter.setHexadecimalInputFilter(disassemblerAddressField);

		disassemblerAddressField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				setDisassemblerUpdateButtonAvailability();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				setDisassemblerUpdateButtonAvailability();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				setDisassemblerUpdateButtonAvailability();
			}
		});

		disassemblerAddressField.setText("01000000");
		setDisassemblerUpdateButtonAvailability();
	}

	private void updateDisassembler()
	{
		if (isDisassemblerAddressValid())
		{
			int address = Integer.parseUnsignedInt(disassemblerAddressField.getText(), 16);

			try
			{
				disassemblerTableManager.updateRows(address);
			} catch (AssemblerFilesException filesException)
			{
				JOptionPane.showMessageDialog(this,
						filesException.getMessage(),
						"Disassembler Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		}
	}

	private void setDisassemblerUpdateButtonAvailability()
	{
		boolean valid = isDisassemblerAddressValid();
		disassemblerAddressField.setBackground(valid ? Color.GREEN : Color.RED);
	}

	private boolean isDisassemblerAddressValid()
	{
		String address = disassemblerAddressField.getText();
		int length = disassemblerTableManager.getDumpLength();
		boolean rangeValid = isAddressInputValid(address, length);

		boolean is32Bit = false;

		try
		{
			is32Bit = Integer.parseUnsignedInt(address, 16) % 4 == 0;
		} catch (NumberFormatException ignored)
		{

		}

		return rangeValid && is32Bit;
	}

	private void configureThreadsTab()
	{
		threadsTableManager = new ThreadsTableManager(threadsTable);
		threadsTableManager.configure();

		updateThreadsCountLabel();

		DefaultCaret caret = (DefaultCaret) registersArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE); // Do not scroll when setting text

		autoUpdateRegistersCheckBox.addItemListener(itemEvent ->
				new SwingWorker<String, String>()
				{
					@Override
					protected String doInBackground() throws Exception
					{
						while (autoUpdateRegistersCheckBox.isSelected())
						{
							considerUpdatingRegisters();

							try
							{
								Thread.sleep(100);
							} catch (InterruptedException exception)
							{
								exception.printStackTrace();
							}
						}

						return null;
					}
				}.execute());

		threadsTable.getSelectionModel().addListSelectionListener(event ->
				considerUpdatingRegisters());

		readThreadsButton.addActionListener(actionEvent ->
				new SwingWorker<String, String>()
				{
					@Override
					protected String doInBackground() throws Exception
					{
						String buttonText = readThreadsButton.getText();
						readingThreads = true;
						setConnectionButtonsAvailability();
						readThreadsButton.setText("Retrieving...");

						try
						{
							threadsTableManager.updateRows(true);
							updateThreadsCountLabel();
						} catch (Exception exception)
						{
							StackTraceUtils.handleException(rootPane, exception);
						} finally
						{
							readThreadsButton.setText(buttonText);
							readingThreads = false;
							setConnectionButtonsAvailability();
						}

						return null;
					}
				}.execute());
	}

	private void updateThreadsCountLabel()
	{
		int amount = (threadsTableManager == null) ? 0 : threadsTableManager.getThreads().size();
		threadsCountLabel.setText("Threads: " + amount);
	}

	private void considerUpdatingRegisters()
	{
		if (registersArea.isShowing() && TCPGecko.isConnected())
		{
			updateRegisters();
		}
	}

	private void updateRegisters()
	{
		try
		{
			String registers = threadsTableManager.readSelectedThreadRegisters();
			registersArea.setText(registers);
		} catch (IOException exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	public static void setVisible(JDialog dialog, JRootPane rootPane, ActionEvent actionEvent)
	{
		dialog.setLocationRelativeTo(rootPane);
		JButton button = (JButton) actionEvent.getSource();
		dialog.setTitle(button.getText());
		dialog.setVisible(true);
	}

	private void configureRemoteProcedureCallTab()
	{
		displayMessageButton.addActionListener(actionEvent ->
		{
			MessageTextDialog textDialog = new MessageTextDialog();
			setVisible(textDialog, rootPane, actionEvent);

			if (textDialog.isConfirmed())
			{
				boolean shouldHaltSystem = textDialog.shouldHaltSystem();
				String message = textDialog.getMessageText();

				try
				{
					CoreInit.displayMessage(message, shouldHaltSystem);
				} catch (IOException ignored)
				{
					// This crash is to be expected so no handling it
				}
			}
		});

		systemInformationButton.addActionListener(actionEvent ->
		{
			try
			{
				OSSystemInfo systemInfo = CoreInit.getSystemInformation();
				JOptionPane.showMessageDialog(this,
						systemInfo.toString(),
						systemInformationButton.getText(),
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException exception)
			{
				exception.printStackTrace();
			}
		});

		remoteDisassemblerButton.addActionListener(actionEvent ->
		{
			RemoteDisassemblerDialog remoteDisassemblerDialog = new RemoteDisassemblerDialog();
			remoteDisassemblerDialog.setTitle(remoteDisassemblerButton.getText());
			remoteDisassemblerDialog.setLocationRelativeTo(this);
			remoteDisassemblerDialog.setVisible(true);

			if (remoteDisassemblerDialog.isConfirmed())
			{
				int value = remoteDisassemblerDialog.getEnteredValue();

				try
				{
					String disassembled = RemoteDisassembler.disassembleValue(value);
					JOptionPane.showMessageDialog(this,
							disassembled,
							remoteDisassemblerButton.getText(),
							JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException exception)
				{
					StackTraceUtils.handleException(rootPane, exception);
				}
			}
		});

		convertEffectiveToPhysicalButton.addActionListener(actionEvent ->
		{
			String suppliedInput = JOptionPane.showInputDialog(this,
					"Please enter the effective hexadecimal address to convert:",
					convertEffectiveToPhysicalButton.getText(),
					JOptionPane.INFORMATION_MESSAGE);

			if (suppliedInput != null)
			{
				try
				{
					int address = Integer.parseUnsignedInt(suppliedInput, 16);
					int physical = CoreInit.getEffectiveToPhysical(address);

					JOptionPane.showMessageDialog(this,
							new Hexadecimal(physical, 8),
							convertEffectiveToPhysicalButton.getText(),
							JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception exception)
				{
					StackTraceUtils.handleException(rootPane, exception);
				}
			}
		});

		shutdownButton.addActionListener(actionEvent ->
		{
			try
			{
				Object[] options = {"Yes",
						"No"};

				int selectedAnswer = JOptionPane.showOptionDialog(rootPane,
						"Would you really like to shutdown your Wii U?",
						shutdownButton.getText(),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						null);

				if (selectedAnswer == JOptionPane.YES_OPTION)
				{
					CoreInit.shutdown();
				}
			} catch (IOException exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		});

		appFlagsButton.addActionListener(actionEvent ->
		{
			try
			{
				long appFlags = CoreInit.getAppFlags();
				JOptionPane.showMessageDialog(this,
						new Hexadecimal(appFlags, 8),
						appFlagsButton.getText(),
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		});

		tcpGeckoThreadButton.addActionListener(e ->
		{
			try
			{
				OSThread osThread = OSThreadRPC.getCurrentThread();
				JOptionPane.showMessageDialog(this,
						osThread.toString(),
						tcpGeckoThreadButton.getText(),
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		});

		sdkVersionButton.addActionListener(actionEvent ->
		{
			try
			{
				long sdkVersion = CoreInit.getSDKVersion();
				JOptionPane.showMessageDialog(this,
						new Hexadecimal(sdkVersion, 8),
						sdkVersionButton.getText(),
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		});

		osIDButton.addActionListener(actionEvent ->
		{
			try
			{
				long osID = CoreInit.getOSIdentifier();
				JOptionPane.showMessageDialog(this,
						new Hexadecimal(osID, 16),
						osIDButton.getText(),
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		});

		titleIDButton.addActionListener(actionEvent ->
		{
			try
			{
				long titleID = CoreInit.getTitleID();
				JOptionPane.showMessageDialog(this,
						new Hexadecimal(titleID, 16),
						titleIDButton.getText(),
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		});

		osTimeButton.addActionListener(actionEvent ->
		{
			try
			{
				long osTime = CoreInit.getOSTime();
				JOptionPane.showMessageDialog(this,
						new Hexadecimal(osTime, 16),
						osTimeButton.getText(),
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		});

		processPFIDButton.addActionListener(actionEvent ->
		{
			try
			{
				long processPFID = CoreInit.getProcessPFID();
				JOptionPane.showMessageDialog(this,
						new Hexadecimal(processPFID, 8),
						processPFIDButton.getText(),
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		});

		remoteProcedureCallButton.addActionListener(actionEvent ->
				setVisible(new RemoteProcedureCallDialog(), rootPane, actionEvent));

		memoryBoundsButton.addActionListener(actionEvent ->
				JOptionPane.showMessageDialog(this,
						"App/Executable: " + AddressRange.appExecutableLibraries
								+ System.lineSeparator()
								+ "MEM2 Region: " + AddressRange.mem2Region,
						memoryBoundsButton.getText(),
						JOptionPane.INFORMATION_MESSAGE));
	}

	private void configureMiscellaneousTab()
	{
		addFirmwareVersionButtonListener();
		updateGameTitlesButton.addActionListener(actionEvent -> updateGameTitles());

		memoryAddressProtectionCheckBox.addChangeListener(changeEvent ->
				TCPGecko.enforceMemoryAccessProtection = memoryAddressProtectionCheckBox.isSelected());
	}

	private void removeUnfinishedTabs()
	{
		programTabs.remove(fileSystemTab);
	}

	private void monitorGeckoServerHealthConcurrently()
	{
		Thread monitor = new Thread(() ->
		{
			while (TCPGecko.isConnected())
			{
				try
				{
					if (!TCPGecko.hasRequestedBytes
							&& !searching && !readingThreads)
					{
						MemoryReader memoryReader = new MemoryReader();
						boolean isRunning = memoryReader.isRunning();

						if (!isRunning)
						{
							connectionReset();
						}
					}

					Thread.sleep(100);
				} catch (Exception exception)
				{
					exception.printStackTrace();

					if (TCPGecko.isConnected())
					{
						connectionReset();
					}
				}
			}
		});

		monitor.start();
	}

	private void connectionReset()
	{
		disconnect();
		closeAllDialogs();

		JOptionPane.showMessageDialog(rootPane,
				"Please reconnect manually when ready.",
				"Connection Lost",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void closeAllDialogs()
	{
		Window[] windows = getWindows();

		for (Window window : windows)
		{
			if (window instanceof JDialog)
			{
				window.dispose();
			}
		}
	}

	public void selectDumpingTab()
	{
		int selectedAddress = getSelectedMemoryViewerAddress();
		dumpStartingAddressField.setText(Conversions.toHexadecimal(selectedAddress, 8));
		dumpEndingAddressField.setText(Conversions.toHexadecimal(selectedAddress + 0x10));
		dumpFilePathField.setText("dumped.bin");
		programTabs.setSelectedComponent(dumpingTab);

		handleDumpMemoryButtonAvailability();
	}

	private void configureWatchListTab()
	{
		int updateDelayMinimum = 10;
		watchListUpdateDelaySpinner.setValue(updateDelayMinimum);
		((SpinnerNumberModel) watchListUpdateDelaySpinner.getModel()).setMinimum(updateDelayMinimum);
		watchListManager = new WatchListManager(watchListTable);
		addWatchListDeleteRowsDeleteKey();
		watchListManager.configure();
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
		updateWatchlistCheckBox.addItemListener(itemEvent -> keepUpdatingWatchList());
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
				JTableUtilities.deleteAllRows(watchListTable);
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
				while (updateWatchlistCheckBox.isSelected())
				{
					if (watchListTable.isShowing()
							&& TCPGecko.isConnected())
					{
						updateWatchList();
					}

					String stringUpdateDelay = watchListUpdateDelaySpinner.getValue().toString();
					int updateDelay = Integer.parseUnsignedInt(stringUpdateDelay);
					Thread.sleep(updateDelay);
				}

				return null;
			}
		}.execute();
	}

	private void updateWatchList()
	{
		try
		{
			watchListManager.updateValues();
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
			updateWatchlistCheckBox.setSelected(false);
		}
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
			simpleProperties.put("SELECTED_TAB_INDEX", programTabs.getSelectedIndex() + "");
			simpleProperties.put("IP_ADDRESS_AUTO_DETECT", String.valueOf(autoDetectCheckBox.isSelected()));
			simpleProperties.put("AUTO_SAVE_CODES", String.valueOf(autoSaveCodeListCheckBox.isSelected()));
			simpleProperties.put("MEMORY_VIEWER_ADDRESS", memoryViewerAddressField.getText());
			simpleProperties.put("IP_ADDRESS", ipAddressField.getText());
			simpleProperties.put("WATCH_LIST_UPDATE_DELAY_SECONDS", watchListUpdateDelaySpinner.getValue().toString());
			simpleProperties.put("DUMPING_START_ADDRESS", dumpStartingAddressField.getText());
			simpleProperties.put("DUMPING_END_ADDRESS", dumpEndingAddressField.getText());
			simpleProperties.put("UPDATE_WATCH_LIST", String.valueOf(updateWatchlistCheckBox.isSelected()));
			simpleProperties.put("DUMPING_FILE_PATH", dumpFilePathField.getText());
			simpleProperties.put("DISASSEMBLER_ADDRESS", disassemblerAddressField.getText());
			simpleProperties.put("SEARCH_RANGE_START", searchStartingAddressField.getText());
			simpleProperties.put("SEARCH_RANGE_END", searchEndingAddressField.getText());
			simpleProperties.put("SEARCH_VALUE", searchValueField.getText());
			simpleProperties.put("SEARCH_VALUE_SIZE", searchValueSizeComboBox.getSelectedItem().toString());
			simpleProperties.put("SEARCH_MODE", searchModeComboBox.getSelectedItem().toString());
			simpleProperties.put("SEARCH_CONDITION", searchConditionComboBox.getSelectedItem().toString());

			simpleProperties.writeToFile();
		}));
	}

	private void restorePersistentSettings()
	{
		simpleProperties = new SimpleProperties();

		String tab = simpleProperties.get("SELECTED_TAB_INDEX");

		if (tab != null)
		{
			int tabIndex = Integer.parseUnsignedInt(tab);
			if (tabIndex > 0 && tabIndex < programTabs.getComponents().length)
			{
				programTabs.setSelectedIndex(tabIndex);
			}
		}

		String autoDetectString = simpleProperties.get("IP_ADDRESS_AUTO_DETECT");
		if (autoDetectString != null)
		{
			boolean autoDetect = Boolean.parseBoolean(autoDetectString);
			autoDetectCheckBox.setSelected(autoDetect);
		}

		String autoSaveString = simpleProperties.get("AUTO_SAVE_CODES");
		if (autoSaveString != null)
		{
			boolean autoSave = Boolean.parseBoolean(autoSaveString);
			autoSaveCodeListCheckBox.setSelected(autoSave);
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

		String updateDelay = simpleProperties.get("WATCH_LIST_UPDATE_DELAY_SECONDS");
		if (updateDelay != null)
		{
			watchListUpdateDelaySpinner.setValue(Integer.parseUnsignedInt(updateDelay));
		}

		String dumpStartingAddress = simpleProperties.get("DUMPING_START_ADDRESS");
		if (dumpStartingAddress != null)
		{
			dumpStartingAddressField.setText(dumpStartingAddress);
		}

		String dumpEndAddress = simpleProperties.get("DUMPING_END_ADDRESS");
		if (dumpEndAddress != null)
		{
			dumpEndingAddressField.setText(dumpEndAddress);
		}

		String dumpFilePath = simpleProperties.get("DUMPING_FILE_PATH");
		if (dumpFilePath != null)
		{
			dumpFilePathField.setText(dumpFilePath);
		}

		String updateWatchList = simpleProperties.get("UPDATE_WATCH_LIST");
		if (updateWatchList != null)
		{
			boolean selected = Boolean.parseBoolean(updateWatchList);
			updateWatchlistCheckBox.setSelected(selected);
		}

		String disassemblerAddress = simpleProperties.get("DISASSEMBLER_ADDRESS");
		if (disassemblerAddress != null)
		{
			disassemblerAddressField.setText(disassemblerAddress);
		}

		String searchStartingAddress = simpleProperties.get("SEARCH_RANGE_START");
		if (searchStartingAddress != null)
		{
			searchStartingAddressField.setText(searchStartingAddress);
		}

		String searchEndingAddress = simpleProperties.get("SEARCH_RANGE_END");
		if (searchEndingAddress != null)
		{
			searchEndingAddressField.setText(searchEndingAddress);
		}

		String searchValueSize = simpleProperties.get("SEARCH_VALUE_SIZE");
		if (searchValueSize != null)
		{
			ValueSize valueSize = ValueSize.parse(searchValueSize);
			searchValueSizeComboBox.setSelectedItem(valueSize);
		}

		String searchValue = simpleProperties.get("SEARCH_VALUE");
		if (searchValue != null)
		{
			searchValueField.setText(searchValue);
		}

		String searchMode = simpleProperties.get("SEARCH_MODE");
		if (searchMode != null)
		{
			SearchMode mode = SearchMode.parse(searchMode);
			searchModeComboBox.setSelectedItem(mode);
		}

		String searchCondition = simpleProperties.get("SEARCH_CONDITION");
		if (searchCondition != null)
		{
			SearchConditions condition = SearchConditions.parse(searchCondition);
			searchConditionComboBox.setSelectedItem(condition);
		}
	}

	private void configureExternalToolsTab()
	{
		powerPCAssemblyCompilerButton.addActionListener(actionEvent -> downloadAndLaunch("https://github.com/BullyWiiPlaza/PowerPC-Assembly-Compiler/blob/master/PowerPC-Assembly-Compiler.jar?raw=true", actionEvent));
		pointerSearchApplicationButton.addActionListener(actionEvent -> downloadAndLaunch("https://github.com/BullyWiiPlaza/Universal-Pointer-Searcher/blob/master/Universal-Pointer-Searcher.jar?raw=true", actionEvent));
		addStartHexEditorButtonListener();
		addStartScientificCalculatorListener();
	}

	private void addStartScientificCalculatorListener()
	{
		ApplicationLauncher applicationLauncher = new ApplicationLauncher("C:\\Program Files (x86)\\SpeedCrunch\\SpeedCrunch.exe",
				"https://bitbucket.org/heldercorreia/speedcrunch/downloads/SpeedCrunch-0.11.exe",
				"SpeedCrunch", false);
		speedCrunchButton.addActionListener(actionEvent ->
				startApplication(actionEvent, applicationLauncher));
		speedCrunchButton.setVisible(SystemUtils.IS_OS_WINDOWS);
	}

	private void addStartHexEditorButtonListener()
	{
		ApplicationLauncher applicationLauncher = new ApplicationLauncher("C:\\Program Files (x86)\\HxD\\HxD.exe",
				"https://mh-nexus.de/downloads/HxDSetupEN.zip", "HxD", true);
		hexEditorButton.addActionListener(actionEvent -> startApplication(actionEvent, applicationLauncher));
		hexEditorButton.setVisible(SystemUtils.IS_OS_WINDOWS);
	}

	private void startApplication(ActionEvent actionEvent, ApplicationLauncher applicationLauncher)
	{
		JButton startButton = (JButton) actionEvent.getSource();
		Path installedExecutablePath = applicationLauncher.getInstalledExecutablePath();
		String fileName = applicationLauncher.getSetupFileName();

		try
		{
			if (isRunning(applicationLauncher.getName()))
			{
				return;
			}

			if (isRunning(fileName))
			{
				return;
			}

			boolean installed = Files.exists(installedExecutablePath);
			String messageText = installed ? "Would you like to start " + applicationLauncher.getName() + "?"
					: "Would you like to download and run the " + applicationLauncher.getName() + " installation setup?";
			String[] options = {"Yes", "No"};
			int selectedAnswer = JOptionPane.showOptionDialog(rootPane,
					messageText,
					startButton.getText(),
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					null);

			if (selectedAnswer == JOptionPane.YES_OPTION)
			{
				String buttonText = startButton.getText();

				if (installed)
				{
					startButton.setText("Starting...");
					Desktop.getDesktop().open(installedExecutablePath.toFile());
					startButton.setText(buttonText);
				} else
				{
					new SwingWorker<String, String>()
					{
						@Override
						protected String doInBackground() throws Exception
						{
							try
							{
								startButton.setEnabled(false);
								startButton.setText("Downloading...");
								DownloadingUtilities.trustAllCertificates();
								DownloadingUtilities.download(applicationLauncher.getDownloadURL());

								File executeFile;

								if (applicationLauncher.shouldUnZip())
								{
									startButton.setText("Unzipping...");
									File unzippedFile = ZipUtils.unZipFile(fileName);
									Files.delete(Paths.get(fileName));
									executeFile = unzippedFile;
								} else
								{
									executeFile = new File(fileName);
								}

								startButton.setText("Executing...");
								Desktop.getDesktop().open(executeFile);
							} catch (Exception exception)
							{
								StackTraceUtils.handleException(rootPane, exception);
							}

							return null;
						}

						@Override
						protected void done()
						{
							startButton.setEnabled(true);
							startButton.setText(buttonText);
						}
					}.execute();
				}
			}
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	private boolean isRunning(String applicationName) throws IOException
	{
		String forcedExtension = ".exe";

		if (!applicationName.endsWith(forcedExtension))
		{
			applicationName += forcedExtension;
		}

		if (ApplicationUtilities.isProcessRunning(applicationName))
		{
			JOptionPane.showMessageDialog(rootPane,
					applicationName + " is running already",
					"Error",
					JOptionPane.ERROR_MESSAGE);

			return true;
		}

		return false;
	}

	private boolean isFileLocked(String fileName)
	{
		if (!DownloadingUtilities.canDownload(fileName))
		{
			JOptionPane.showMessageDialog(rootPane,
					"The setup seems to be running already.",
					"Error",
					JOptionPane.ERROR_MESSAGE);

			return true;
		}

		return false;
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

		if (isFileLocked(downloadedFileName))
		{
			return;
		}

		int selectedAnswer = JOptionPane.showConfirmDialog(
				rootPane,
				"Would you like to download and start " + downloadButton.getText() + "?",
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

		memoryDumpingProgressBar.setStringPainted(true);

		dumpMemoryButton.addActionListener(actionEvent ->
		{
			int startingAddress = Integer.parseUnsignedInt(dumpStartingAddressField.getText(), 16);
			int endingAddress = Integer.parseUnsignedInt(dumpEndingAddressField.getText(), 16);
			int length = endingAddress - startingAddress;

			String targetFilePath = dumpFilePathField.getText();
			File targetFile = new File(targetFilePath);

			GraphicalMemoryDumper graphicalMemoryDumper = new GraphicalMemoryDumper(startingAddress,
					length, targetFile, memoryDumpingProgressBar, dumpMemoryButton);
			graphicalMemoryDumper.dumpMemory();
		});

		chooseFilePathButton.addActionListener(actionEvent ->
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			String applicationDirectory = System.getProperty("user.dir");
			fileChooser.setCurrentDirectory(new File(applicationDirectory));

			String chosenFile = dumpFilePathField.getText();

			if (!chosenFile.equals(""))
			{
				File chosen = new File(chosenFile);
				fileChooser.setCurrentDirectory(chosen.getParentFile());
			}

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
			int startingAddress = Integer.parseUnsignedInt(dumpStartingAddressField.getText(), 16);
			int endingAddress = Integer.parseUnsignedInt(dumpEndingAddressField.getText(), 16);
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
		dumpMemoryButton.setEnabled(TCPGecko.isConnected() && validMemoryAddresses && validStartingAddress && validEndingAddress && validTargetFile);
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

		HexadecimalInputFilter.setHexadecimalInputFilter(memoryViewerAddressField);
		addMemoryViewerAddressChangedListener();
		new DefaultContextMenu().addTo(memoryViewerAddressField);

		handleUpdateMemoryViewerButton();

		memoryViewerAutoUpdateCheckBox.addActionListener(actionEvent -> tryRunningMemoryViewerUpdater());

		memoryViewerAddressField.setText(Conversions.toHexadecimal(MemoryViewerTableManager.STARTING_ADDRESS));

		memoryViewerAddressField.addKeyListener(new KeyAdapter()
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
						&& updateMemoryViewerButton.isEnabled())
				{
					int cursorPosition = memoryViewerAddressField.getCaretPosition();
					updateMemoryViewer();
					memoryViewerAddressField.requestFocus();
					memoryViewerAddressField.setCaretPosition(cursorPosition);
				}
			}
		});

		memoryViewerValueField.setDocument(new InputLengthFilter(ValueSizes.THIRTY_TWO_BIT.getSize()));
		new DefaultContextMenu().addTo(memoryViewerValueField);

		memoryViewerValueField.addKeyListener(new KeyAdapter()
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
						&& updateMemoryViewerButton.isEnabled())
				{
					pokeMemoryViewerMemory();
				}
			}
		});

		valueSizePokeComboBox.addItemListener(itemEvent ->
		{
			if (itemEvent.getStateChange() == ItemEvent.SELECTED)
			{
				ValueSizes valueSize = valueSizePokeComboBox.getItemAt(valueSizePokeComboBox.getSelectedIndex());
				changePokeValueSize(valueSize.getSize());
				kernelWriteCheckBox.setEnabled(valueSize == ValueSizes.THIRTY_TWO_BIT);
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

		readKernelIntegerButton.addActionListener(actionEvent ->
		{
			int address = Conversions.toDecimal(kernelReadAddressField.getText());

			try
			{
				MemoryReader memoryReader = new MemoryReader();
				int value = memoryReader.readKernelInt(address);
				JOptionPane.showMessageDialog(rootPane,
						Conversions.toHexadecimal(value, 8),
						readKernelIntegerButton.getText(),
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		});

		HexadecimalInputFilter.setHexadecimalInputFilter(kernelReadAddressField);
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
		searchMemoryViewerButton.setEnabled(validLength && searchValueValid && TCPGecko.isConnected());
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

		int searchLengthInteger = Integer.parseUnsignedInt(searchLength, 16);
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

		codesListManager.getCodesJList().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (autoSaveCodeListCheckBox.isSelected())
				{
					storeCurrentCodeList(true);
				}
			}
		});

		autoSaveCodeListCheckBox.addItemListener(actionEvent -> setCodeListButtonsAvailability());
		addCodeButton.addActionListener(actionEvent -> addCode());
		deleteCodeButton.addActionListener(actionEvent -> deleteSelectedCodeListEntry());
		sendCodesButton.addActionListener(actionEvent -> sendCodes());
		disableCodesButton.addActionListener(actionEvent -> disableCodes());
		codesHelpButton.addActionListener(actionEvent -> visitCodeHandlerGBATempThread());
		editCodeButton.addActionListener(actionEvent -> editSelectedCode());
		storeCodeListButton.addActionListener(actionEvent -> storeCurrentCodeList(false));
		downloadCodeDatabaseButton.addActionListener(actionEvent -> handleDownloadingCodeDatabase());
		exportCodeListButton.addActionListener(actionEvent -> exportCodeList());
		loadCodeListButton.addActionListener(actionEvent -> loadCodeList());

		addCodeListBoxesMouseListener();
		addCodeListChangedListener();

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
		new SwingWorker<String, String>()
		{
			@Override
			protected String doInBackground() throws Exception
			{
				String buttonText = downloadCodeDatabaseButton.getText();

				try
				{
					downloadCodeDatabaseButton.setText("Downloading...");
					downloadCodeDatabaseButton.setEnabled(false);
					CodeDatabaseDownloader codeDatabaseDownloader = new CodeDatabaseDownloader(gameId);
					List<GeckoCode> downloadedCodes = codeDatabaseDownloader.getCodes();
					int codesCount = downloadedCodes.size();
					boolean codesExist = codesCount > 0;
					Title title = titleDatabaseManager.getTitleFromGameId(gameId);
					String gameName = title.getGameName();

					if (codesExist)
					{
						Object[] options = {"Yes", "No"};

						int selectAnswer = JOptionPane.showOptionDialog(rootPane,
								codesCount + " code(s) found for " + gameName + "." + System.lineSeparator()
										+ "Would you like to add them to your current code list?"
										+ System.lineSeparator() + "Duplicates will not be added!",
								"Download Codes?",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null,
								options, null);

						if (selectAnswer == JOptionPane.YES_OPTION)
						{
							// Add non-duplicate codes
							downloadedCodes.stream().filter(code -> !codesListManager.containsCode(code))
									.forEach(code -> codesListManager.addCode(code));
						}
					} else
					{
						JOptionPane.showMessageDialog(rootPane,
								"No codes found for " + gameName + "!",
								":(",
								JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception exception)
				{
					StackTraceUtils.handleException(rootPane, exception);
				} finally
				{
					downloadCodeDatabaseButton.setText(buttonText);
					downloadCodeDatabaseButton.setEnabled(true);
				}

				return null;
			}
		}.execute();
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
		autoDetectCheckBox.addActionListener(actionEvent -> setConnectionButtonsAvailability());
		reconnectButton.addActionListener(actionEvent -> reconnect());
		disconnectButton.addActionListener(actionEvent -> disconnect());
		connectionHelpButton.addActionListener(actionEvent -> displayConnectionHelperMessage());
		codesListManager = new CodesListManager(codesListBoxes, rootPane);
		codesListManager.addCodeListEntryClickedListener();

		setConnectionButtonsAvailability();
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
				setConnectionButtonsAvailability();

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

			if (TCPGecko.isConnected())
			{
				String memoryAddress = Conversions.toHexadecimal(memoryViewerTableManager.getSelectedAddress());
				memoryViewerAddressField.setText(memoryAddress);
			}
		}
	}

	private void addPokeButtonListener()
	{
		pokeMemoryViewerValueButton.addActionListener(actionEvent ->
				pokeMemoryViewerMemory());
	}

	private void pokeMemoryViewerMemory()
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
					if (kernelWriteCheckBox.isSelected())
					{
						memoryWriter.writeKernelInt(targetAddress, newValue);
					} else
					{
						memoryWriter.writeInt(targetAddress, newValue);
					}
			}

			pokeMemoryViewerUpdate();
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
		}
	}

	private void pokeMemoryViewerUpdate()
	{
		// A "hacky" way to avoid the memory viewer to move after poking
		int selectedAddress = memoryViewerTableManager.getSelectedAddress();
		int firstAddress = memoryViewerTableManager.getFirstMemoryAddress();
		memoryViewerAddressField.setText(new Hexadecimal(firstAddress, 8).toString());
		updateMemoryViewer(false, false);
		memoryViewerTableManager.selectAddress(selectedAddress);
		memoryViewerAddressField.setText(new Hexadecimal(selectedAddress, 8).toString());
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
			MemoryReader memoryReader = new MemoryReader();
			String firmwareVersion = "";

			try
			{
				firmwareVersion = Integer.toString(memoryReader.readFirmwareVersion());

				// A "hack" to not confuse 551 users by a "wrong" firmware output
				if (firmwareVersion.equals("550"))
				{
					firmwareVersion = "55X";
				}
			} catch (IOException exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}

			// Display the message to the user
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
		sendCodesButton.setEnabled(TCPGecko.isConnected() && codesSelected);
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
					while (memoryViewerAutoUpdateCheckBox.isSelected()
							&& TCPGecko.isConnected())
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
			boolean shouldEnable = isValid && TCPGecko.isConnected()
					&& !memoryViewerAutoUpdateCheckBox.isSelected();
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
		boolean isValid;

		try
		{
			int address = Conversions.toDecimal(input);
			isValid = AddressRange.isValidAccess(address, length, MemoryAccessLevel.READ);
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
		codesListManager.getCodesJList().addListSelectionListener(selectionEvent -> handleCodeListButtonAvailability());
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

			if (autoSaveCodeListCheckBox.isSelected())
			{
				storeCurrentCodeList(true);
			}
		}
	}

	private void deleteSelectedCodeListEntry()
	{
		codesListManager.deleteSelectedCodeListEntry(false);

		if (autoSaveCodeListCheckBox.isSelected())
		{
			storeCurrentCodeList(true);
		}
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
			openURL("http://gbatemp.net/threads/post-your-wiiu-cheat-codes-here.395443/");
		}
	}

	private void openURL(String link)
	{
		try
		{
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(new URI(link));
		} catch (Exception exception)
		{
			StackTraceUtils.handleException(rootPane, exception);
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
			AddCodeDialog addCodeDialog = new AddCodeDialog(codeListEntry, true);
			addCodeDialog.display();

			if (addCodeDialog.isConfirmed())
			{
				codesListManager.deleteSelectedCodeListEntry(true);
				codeListEntry = addCodeDialog.getCodeListEntry();
				codesListManager.addCodeListEntry(codeListEntry, selected);

				if (autoSaveCodeListCheckBox.isSelected())
				{
					storeCurrentCodeList(true);
				}
			}
		}
	}

	private void storeCurrentCodeList(boolean silent)
	{
		try
		{
			List<GeckoCode> codes = codesListManager.getCodeListBackup();
			String codeListFilePath = codesStorage.writeCodeList(codes);

			if (!silent)
			{
				JOptionPane.showMessageDialog(rootPane,
						"Code list stored to " + codeListFilePath,
						"Stored",
						JOptionPane.INFORMATION_MESSAGE);
			}
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
			connectButton.setText(connectButtonText);
			setTitle(programName);
			setConnectionButtonsAvailability();
			// codesListManager.clearCodeList();
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
				programTabs.setEnabled(false);
				connectButton.setText("Connecting...");
				connecting = true;
				setConnectionButtonsAvailability();

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

					considerUpdatingTabs();
				} catch (Exception exception)
				{
					connectButton.setText(connectButtonText);
					StackTraceUtils.handleException(rootPane, exception);
				} finally
				{
					programTabs.setEnabled(true);
					connecting = false;
					setConnectionButtonsAvailability();
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

	private void considerUpdatingTabs()
	{
		if (TCPGecko.isConnected())
		{
			if (memoryViewerTab.isShowing())
			{
				updateMemoryViewer(true, false);
			}

			if (disassemblerTab.isShowing())
			{
				updateDisassembler();
			}

			if (watchListTab.isShowing())
			{
				updateWatchList();
			}
		}

		considerUpdatingRegisters();
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

		MemoryRangeAdjustment memoryRangeAdjustment = new MemoryRangeAdjustment(titleDatabaseManager);
		memoryRangeAdjustment.setAdjustedMemoryRanges();
		monitorGeckoServerHealthConcurrently();
		String ipAddressAddition = (autoDetectCheckBox.isSelected() ? (" [" + ipAddress + "]") : "");
		connectButton.setText(connectButtonText + "ed" + ipAddressAddition);
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
				title = titleDatabaseManager.readTitle();
				gameId = title.getGameId();
				updateCodeDatabaseDownloadButtonAvailability();
			} else
			{
				title = titleDatabaseManager.getTitleFromGameId(gameId);
			}

			gameName = title.getGameName();
		} catch (TitleDatabaseManager.TitleNotFoundException exception)
		{
			// Let the user input the data then
			NewGameDialog newGameDialog = new NewGameDialog(this);
			newGameDialog.display();

			if (newGameDialog.confirmed())
			{
				gameId = newGameDialog.getGameId();
				gameName = newGameDialog.getGameName();

				Title title = titleDatabaseManager.getTitleFromGameId(gameId);

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
				setConnectionButtonsAvailability();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				setConnectionButtonsAvailability();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				setConnectionButtonsAvailability();
			}
		});
	}

	/**
	 * Contains the logic for the availability of buttons and field on the connect tab
	 */
	private void setConnectionButtonsAvailability()
	{
		boolean connected = TCPGecko.isConnected();
		String inputtedIPAddress = ipAddressField.getText();
		boolean isValidIPAddress = IPAddressValidator.validateIPv4Address(inputtedIPAddress);
		boolean isAutoDetect = autoDetectCheckBox.isSelected();
		boolean mayConnect = (isValidIPAddress || isAutoDetect);
		boolean shouldEnableConnectButton = !connected
				&& mayConnect && !connecting && titlesInitialized;
		connectButton.setEnabled(shouldEnableConnectButton);
		memoryBoundsButton.setEnabled(connected);
		processPFIDButton.setEnabled(connected);
		readKernelIntegerButton.setEnabled(connected);
		systemInformationButton.setEnabled(connected);
		setSearchButtonsAvailability();
		tcpGeckoThreadButton.setEnabled(connected);
		sdkVersionButton.setEnabled(connected);
		shutdownButton.setEnabled(connected);
		osIDButton.setEnabled(connected);
		appFlagsButton.setEnabled(connected);
		titleIDButton.setEnabled(connected);
		remoteDisassemblerButton.setEnabled(connected);
		displayMessageButton.setEnabled(connected);
		disconnectButton.setEnabled(connected);
		osTimeButton.setEnabled(connected);
		assembleInstructionButton.setEnabled(connected && !assembling);
		readThreadsButton.setEnabled(connected && !readingThreads);
		reconnectButton.setEnabled(!connectButton.isEnabled()
				&& disconnectButton.isEnabled());
		ipAddressField.setEnabled(!isAutoDetect);
		updateDisassemblerButton.setEnabled(connected);
		setSendCodesButtonAvailability();
		disableCodesButton.setEnabled(connected);
		ipAddressField.setBackground(isValidIPAddress ? Color.GREEN : Color.RED);
		loadCodeListButton.setEnabled(!connected);
		memoryViewerAutoUpdateCheckBox.setEnabled(connected);
		firmwareVersionButton.setEnabled(connected);
		memoryViewerViews.setEnabled(connected);
		pokeMemoryViewerValueButton.setEnabled(connected);
		followPointerButton.setEnabled(connected);
		addWatchButton.setEnabled(connected);
		addAddressExpressionsButton.setEnabled(connected);
		exportWatchListButton.setEnabled(connected);
		saveWatchListButton.setEnabled(connected);
		remoteProcedureCallButton.setEnabled(connected);
		convertEffectiveToPhysicalButton.setEnabled(connected);

		setMemoryViewerSearchButtonAvailability();

		if (isAutoDetect)
		{
			ipAddressField.setBackground(Color.GREEN);
		}

		handleUpdateMemoryViewerButton();
		handleDumpMemoryButtonAvailability();
	}

	private void setSearchButtonAvailability()
	{
		boolean isRangePositive = Conversions.toDecimal(searchEndingAddressField.getText())
				- Conversions.toDecimal(searchStartingAddressField.getText()) > 0;
		ValueSize valueSize = searchValueSizeComboBox.getItemAt(searchValueSizeComboBox.getSelectedIndex());
		boolean isRangeAlignedCorrectly = valueSize != null
				&& (Conversions.toDecimal(searchEndingAddressField.getText()) - Conversions.toDecimal(searchStartingAddressField.getText())) % valueSize.getBytesCount() == 0;
		boolean areAddressesValid = AddressRange.isValidAccess(Conversions.toDecimal(searchStartingAddressField.getText()), 1, MemoryAccessLevel.READ) && AddressRange.isValidAccess(Conversions.toDecimal(searchEndingAddressField.getText()), 1, MemoryAccessLevel.READ);
		searchButton.setEnabled(TCPGecko.isConnected() && !searching
				&& !noResultsFound && isRangePositive
				&& isRangeAlignedCorrectly && areAddressesValid
				&& isSearchValueOkay());
	}

	private void setCodeListButtonsAvailability()
	{
		storeCodeListButton.setEnabled(codesStorage != null &&
				!autoSaveCodeListCheckBox.isSelected());
		exportCodeListButton.setEnabled(codesStorage != null);
	}

	public void addMemoryViewerOffset(int offset)
	{
		int selectedAddress = getSelectedMemoryViewerAddress();
		selectedAddress += offset;
		String destinationAddressHexadecimal = Conversions.toHexadecimal(selectedAddress, 8);
		memoryViewerAddressField.setText(destinationAddressHexadecimal);
		updateMemoryViewer();
	}

	/**
	 * Defines properties for the main window
	 */
	private void setFrameProperties()
	{
		programName = "JGecko U";
		setTitle(programName);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(900, 450);
		setLocationRelativeTo(null);
		WindowUtilities.setIconImage(this);
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

	public void setMemoryViewerAddress(int address)
	{
		memoryViewerAddressField.setText(new Hexadecimal(address, 8).toString());
	}

	private void switchToMemoryViewer()
	{
		programTabs.setSelectedComponent(memoryViewerTab);
	}

	public static void selectInMemoryViewer(int address)
	{
		JGeckoUGUI geckoUGUI = getInstance();
		geckoUGUI.setMemoryViewerAddress(address);
		geckoUGUI.switchToMemoryViewer();
		geckoUGUI.selectMemoryViewerCell(address);
	}

	private void selectMemoryViewerCell(int address)
	{
		memoryViewerTableManager.selectAddress(address);
	}

	public void selectDisassemblerTab()
	{
		String address = memoryViewerAddressField.getText();
		disassemblerAddressField.setText(address);
		programTabs.setSelectedComponent(disassemblerTab);
	}

	public static void selectInDisassembler(int address)
	{
		JGeckoUGUI jGeckoUGUI = JGeckoUGUI.getInstance();
		jGeckoUGUI.selectDisassemblerTab();
		jGeckoUGUI.updateDisassembler(address);
	}

	public void updateDisassembler(int address)
	{
		disassemblerAddressField.setText(Conversions.toHexadecimal(address, 8));
		updateDisassembler();
	}

	public OSThread getSelectedThread()
	{
		return threadsTableManager.getSelectedItems().get(0);
	}

	public void updateThreads(boolean fetch) throws Exception
	{
		threadsTableManager.updateRows(fetch);
	}

	public JButton getSearchButton()
	{
		return searchButton;
	}

	public JProgressBar getSearchProgressBar()
	{
		return searchProgressBar;
	}

	public void setupSearch(DisassembledInstruction disassembledInstruction)
	{
		searchStartingAddressField.setText(Conversions.toHexadecimal(disassembledInstruction.getAddress(), 8));
		searchValueField.setText(Conversions.toHexadecimal(disassembledInstruction.getValue()));
		searchModeComboBox.setSelectedItem(SearchMode.SPECIFIC);
		switchToSearchTab();
	}

	private void switchToSearchTab()
	{
		programTabs.setSelectedComponent(searchTab);
	}
}