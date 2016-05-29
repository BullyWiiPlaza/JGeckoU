package wiiudev.gecko.client.memoryViewer;

import wiiudev.gecko.client.connector.MemoryWriter;
import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.utilities.JFileChooserUtilities;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MemoryViewerContextMenu extends JPopupMenu
{
	private JMenuItem writeStringOption;
	private JMenuItem uploadFileOption;

	public MemoryViewerContextMenu()
	{
		JMenuItem offsetOption = new JMenuItem("Add Offset");
		offsetOption.setAccelerator(KeyStroke.getKeyStroke("control G"));
		offsetOption.addActionListener(actionEvent -> showOffsetDialog());
		add(offsetOption);

		JMenuItem copyAllCells = new JMenuItem("Copy Cells");
		copyAllCells.setAccelerator(KeyStroke.getKeyStroke("control L"));
		copyAllCells.addActionListener(actionEvent -> copyCells());
		add(copyAllCells);

		JMenuItem codeWizardOption = new JMenuItem("Code Wizard");
		codeWizardOption.setAccelerator(KeyStroke.getKeyStroke("control W"));
		codeWizardOption.addActionListener(actionEvent -> displayCodeWizard());
		add(codeWizardOption);

		writeStringOption = new JMenuItem("Write Text");
		writeStringOption.setAccelerator(KeyStroke.getKeyStroke("control T"));
		writeStringOption.addActionListener(actionEvent -> displayStringWriteDialog());
		add(writeStringOption);

		uploadFileOption = new JMenuItem("Upload File");
		uploadFileOption.setAccelerator(KeyStroke.getKeyStroke("control F"));
		uploadFileOption.addActionListener(actionEvent -> uploadFile());
		add(uploadFileOption);
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
				File selectedFile = fileChooser.getSelectedFile();
				byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
				MemoryWriter memoryWriter = new MemoryWriter();
				int selectedAddress = JGeckoUGUI.getInstance().getSelectedMemoryViewerAddress();
				memoryWriter.writeBytes(selectedAddress, fileBytes);
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

	public void addListeners(JTable table)
	{
		addKeyShortcuts(table);
		addRightClickListener(table);
	}

	private void addRightClickListener(JTable table)
	{
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent releasedEvent)
			{
				if (JGeckoUGUI.getInstance().isConnected())
				{
					if (releasedEvent.getButton() == MouseEvent.BUTTON3)
					{
						processClick(releasedEvent);
					}
				}
			}
		});
	}

	private void addKeyShortcuts(JTable table)
	{
		table.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent pressedEvent)
			{
				if (JGeckoUGUI.getInstance().isConnected())
				{
					if (keyEventPressed(pressedEvent, KeyEvent.VK_G))
					{
						showOffsetDialog();
					}

					if (keyEventPressed(pressedEvent, KeyEvent.VK_L))
					{
						copyCells();
					}

					if (keyEventPressed(pressedEvent, KeyEvent.VK_W))
					{
						displayCodeWizard();
					}

					if (keyEventPressed(pressedEvent, KeyEvent.VK_T))
					{
						displayStringWriteDialog();
					}

					if (keyEventPressed(pressedEvent, KeyEvent.VK_F))
					{
						uploadFile();
					}
				}
			}
		});
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

	private void processClick(MouseEvent event)
	{
		JTable table = (JTable) event.getSource();
		table.requestFocus();
		show(table, event.getX(), event.getY());
	}
}
