package wiiudev.gecko.client.gui.code_list;

import wiiudev.gecko.client.codes.CheatCode;
import wiiudev.gecko.client.codes.CodeListEntry;
import wiiudev.gecko.client.codes.InvalidCheatCodeException;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.inputFilter.InputCapitalizer;
import wiiudev.gecko.client.gui.utilities.DefaultContextMenu;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AddCodeDialog extends JDialog
{
	private JPanel contentPane;
	private JButton saveCodeButton;
	private JButton cancelButton;
	private JTextArea codeArea;
	private JTextField codeTitleField;
	private JLabel statusLabel;
	private JTextArea codeCommentField;
	private JButton codeWizardButton;
	private JScrollPane codeAreaScroller;
	private boolean editMode;

	private String codeTitle;
	private String cheatCode;
	private String comment;

	public AddCodeDialog(CodeListEntry codeListEntry, boolean editMode)
	{
		this.editMode = editMode;
		codeArea.setDocument(new InputCapitalizer());
		codeTitleField.setText(codeListEntry.getTitle());
		codeArea.setText(codeListEntry.getCode());
		codeCommentField.setText(codeListEntry.getComment());
		new DefaultContextMenu().addTo(codeCommentField);
		new DefaultContextMenu().addTo(codeTitleField);

		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(saveCodeButton);

		saveCodeButton.addActionListener(actionEvent -> onSave());

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

		codeAreaScroller.setPreferredSize(new Dimension(300, 200));

		codeArea.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				validateCode();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				validateCode();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				validateCode();
			}
		});

		new DefaultContextMenu().addTo(codeArea);

		validateCode();
		codeWizardButton.addActionListener(actionEvent -> new CodeWizardDialog().setVisible(true));
	}

	public AddCodeDialog()
	{
		this(new CodeListEntry("", "", ""), true);
	}

	private void validateCode()
	{
		String code = codeArea.getText();

		try
		{
			new CheatCode(code);
			codeArea.setBackground(Color.GREEN);
			saveCodeButton.setEnabled(true);
			statusLabel.setText("Status: OK!");
		} catch (InvalidCheatCodeException invalidCheatCodeException)
		{
			codeArea.setBackground(Color.RED);
			saveCodeButton.setEnabled(false);
			statusLabel.setText("Status: " + invalidCheatCodeException.getMessage());
		}
	}

	private void onSave()
	{
		cheatCode = codeArea.getText();
		codeTitle = codeTitleField.getText();
		comment = codeCommentField.getText();

		dispose();
	}

	private void onCancel()
	{
		dispose();
	}

	public boolean isConfirmed()
	{
		return cheatCode != null;
	}

	public CodeListEntry getCodeListEntry()
	{
		return new CodeListEntry(codeTitle, cheatCode, comment);
	}

	public void display()
	{
		setDialogProperties();
		setVisible(true);
	}

	private void setDialogProperties()
	{
		setLocationRelativeTo(JGeckoUGUI.getInstance());

		if(editMode)
		{
			setTitle("Edit Code");
		}
		else
		{
			setTitle("Add Code");
		}

		setSize(650, 500);
		WindowUtilities.setIconImage(this);
	}
}