package wiiudev.gecko.client.gui.tabs.code_list;

import wiiudev.gecko.client.codes.CheatCodeFormatting;
import wiiudev.gecko.client.codes.CodeListEntry;
import wiiudev.gecko.client.gui.JGeckoUGUI;
import wiiudev.gecko.client.gui.input_filters.InputCapitalization;
import wiiudev.gecko.client.gui.tabs.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.gui.utilities.DefaultContextMenu;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class CodeInputDialog extends JDialog
{
	private JPanel contentPane;
	private JButton saveCodeButton;
	private JTextArea codeArea;
	private JTextField codeTitleField;
	private JLabel statusLabel;
	private JTextArea codeCommentField;
	private JButton codeWizardButton;
	private JScrollPane codeAreaScroller;

	private String codeTitle;
	private String cheatCode;
	private String comment;

	public CodeInputDialog(CodeListEntry codeListEntry)
	{
		codeArea.setDocument(new InputCapitalization());
		codeTitleField.setText(codeListEntry.getTitle());
		codeArea.setText(codeListEntry.getCode());
		codeCommentField.setText(codeListEntry.getComment());
		new DefaultContextMenu().addTo(codeCommentField);
		new DefaultContextMenu().addTo(codeTitleField);

		saveCodeButton.addActionListener(actionEvent -> onSave());

		// Keep the area big enough for a code
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

	public CodeInputDialog()
	{
		this(new CodeListEntry("", "", ""));
	}

	private void validateCode()
	{
		String code = codeArea.getText();

		try
		{
			new CheatCodeFormatting(code);
			codeArea.setBackground(Color.GREEN);
			saveCodeButton.setEnabled(true);
			statusLabel.setText("Status: OK!");
		} catch (CheatCodeFormatting.InvalidCheatCodeException invalidCheatCodeException)
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
		setContentPane(contentPane);
		setModal(true);
		setLocationRelativeTo(JGeckoUGUI.getInstance());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(650, 500);
		WindowUtilities.setIconImage(this);
	}
}