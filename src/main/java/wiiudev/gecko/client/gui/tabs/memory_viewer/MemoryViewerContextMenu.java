package wiiudev.gecko.client.gui.tabs.memory_viewer;

import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.utilities.JFileChooserUtilities;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class MemoryViewerContextMenu extends JPopupMenu
{
	private JTable table;

	private JMenuItem writeStringOption;
	private JMenuItem uploadFileOption;

	public MemoryViewerContextMenu(JTable table)
	{
		this.table = table;
	}

	public void addContextMenu()
	{
		JMenuItem offsetOption = new JMenuItem("Add Offset");
		KeyStroke offsetKeyStroke = KeyStroke.getKeyStroke("control G");
		offsetOption.setAccelerator(offsetKeyStroke);
		offsetOption.addActionListener(actionEvent -> showOffsetDialog());
		add(offsetOption);

		JMenuItem copyAllCells = new JMenuItem("Copy Cells");
		KeyStroke copyCellsKeyStroke = KeyStroke.getKeyStroke("control L");
		copyAllCells.setAccelerator(copyCellsKeyStroke);
		copyAllCells.addActionListener(actionEvent -> copyCells());
		add(copyAllCells);

		JMenuItem codeWizardOption = new JMenuItem("Code Wizard");
		KeyStroke codeWizardKeyStroke = KeyStroke.getKeyStroke("control W");
		codeWizardOption.setAccelerator(codeWizardKeyStroke);
		codeWizardOption.addActionListener(actionEvent -> displayCodeWizard());
		add(codeWizardOption);

		writeStringOption = new JMenuItem("Write Text");
		KeyStroke writeTextKeyStroke = KeyStroke.getKeyStroke("control T");
		writeStringOption.setAccelerator(writeTextKeyStroke);
		writeStringOption.addActionListener(actionEvent -> displayStringWriteDialog());
		add(writeStringOption);

		uploadFileOption = new JMenuItem("Upload File");
		KeyStroke uploadFileKeyStroke = KeyStroke.getKeyStroke("control U");
		uploadFileOption.setAccelerator(uploadFileKeyStroke);
		uploadFileOption.addActionListener(actionEvent -> uploadFile());
		add(uploadFileOption);

		JMenuItem dumpFileOption = new JMenuItem("Dump File");
		KeyStroke dumpFileKeyStroke = KeyStroke.getKeyStroke("control F");
		dumpFileOption.setAccelerator(dumpFileKeyStroke);
		dumpFileOption.addActionListener(actionEvent -> switchToDumpingTab());
		add(dumpFileOption);

		JMenuItem disassemblerOption = new JMenuItem("Disassembler");
		KeyStroke disassemblerKeyStroke = KeyStroke.getKeyStroke("control D");
		disassemblerOption.setAccelerator(disassemblerKeyStroke);
		disassemblerOption.addActionListener(actionEvent -> switchToDisassemblerTab());
		add(disassemblerOption);

		table.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent pressedEvent)
			{
				if (TCPGecko.isConnected())
				{
					if (keyEventPressed(pressedEvent, offsetKeyStroke.getKeyCode()))
					{
						showOffsetDialog();
					}

					if (keyEventPressed(pressedEvent, copyCellsKeyStroke.getKeyCode()))
					{
						copyCells();
					}

					if (keyEventPressed(pressedEvent, codeWizardKeyStroke.getKeyCode()))
					{
						displayCodeWizard();
					}

					if (keyEventPressed(pressedEvent, writeTextKeyStroke.getKeyCode()))
					{
						displayStringWriteDialog();
					}

					if (keyEventPressed(pressedEvent, uploadFileKeyStroke.getKeyCode()))
					{
						uploadFile();
					}

					if (keyEventPressed(pressedEvent, dumpFileKeyStroke.getKeyCode()))
					{
						switchToDumpingTab();
					}

					if (keyEventPressed(pressedEvent, disassemblerKeyStroke.getKeyCode()))
					{
						switchToDisassemblerTab();
					}
				}
			}
		});
	}

	private void switchToDisassemblerTab()
	{
		JGeckoUGUI jGeckoUGUI = JGeckoUGUI.getInstance();
		jGeckoUGUI.selectDisassemblerTab();
	}

	private void switchToDumpingTab()
	{
		JGeckoUGUI jGeckoUGUI = JGeckoUGUI.getInstance();
		jGeckoUGUI.selectDumpingTab();
	}

	private void uploadFile()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(uploadFileOption.getText());
		JFileChooserUtilities.registerDeleteAction(fileChooser);
		File programDirectory = new File(System.getProperty("user.dir"));
		fileChooser.setCurrentDirectory(programDirectory);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Binary Files", "bin");
		fileChooser.setFileFilter(filter);
		int selectedAnswer = fileChooser.showOpenDialog(JGeckoUGUI.getInstance());

		try
		{
			if (selectedAnswer == JFileChooser.APPROVE_OPTION)
			{
				Path selectedFile = fileChooser.getSelectedFile().toPath();
				MemoryWriter memoryWriter = new MemoryWriter();
				int selectedAddress = JGeckoUGUI.getInstance().getSelectedMemoryViewerAddress();
				memoryWriter.upload(selectedAddress, selectedFile);
				JGeckoUGUI.getInstance().updateMemoryViewer();
			}
		} catch (IOException exception)
		{
			StackTraceUtils.handleException(null, exception);
		}
	}

	private void displayCodeWizard()
	{
		CodeWizardDialog codeWizardDialog = new CodeWizardDialog();
		JGeckoUGUI jGeckoUGUI = JGeckoUGUI.getInstance();
		int selectedMemoryViewerAddress = jGeckoUGUI.getSelectedMemoryViewerAddress();
		codeWizardDialog.setAddress(selectedMemoryViewerAddress);
		int value = jGeckoUGUI.getSelectedMemoryViewerValue();
		codeWizardDialog.setValue(value);
		codeWizardDialog.generateCode();
		codeWizardDialog.setVisible(true);
	}

	private void displayStringWriteDialog()
	{
		int selectedAddress = JGeckoUGUI.getInstance().getSelectedMemoryViewerAddress();
		StringWriteDialog stringWriteDialog = new StringWriteDialog(selectedAddress, writeStringOption.getText());
		stringWriteDialog.setVisible(true);
	}

	private boolean keyEventPressed(KeyEvent event, int targetKeyCode)
	{
		return event.getKeyCode() == targetKeyCode && (event.getModifiers() & KeyEvent.CTRL_MASK) != 0;
	}

	private void copyCells()
	{
		JGeckoUGUI.getInstance().copyMemoryViewerCells();
	}

	private void showOffsetDialog()
	{
		OffsetDialog goToOffsetDialog = new OffsetDialog(null);
		goToOffsetDialog.setVisible(true);

		if (goToOffsetDialog.isConfirmed())
		{
			int offset = goToOffsetDialog.getOffset();
			JGeckoUGUI.getInstance().addMemoryViewerOffset(offset);
		}
	}
}
