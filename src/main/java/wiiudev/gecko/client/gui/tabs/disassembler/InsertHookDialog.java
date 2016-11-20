package wiiudev.gecko.client.gui.tabs.disassembler;

import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.gui.tabs.disassembler.assembler.Assembler;
import wiiudev.gecko.client.gui.tabs.disassembler.assembler.AssemblerException;
import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;

public class InsertHookDialog extends JDialog
{
	private JPanel contentPane;
	private JTextArea assemblyTextArea;
	private JButton confirmButton;

	private byte[] bytes;

	public InsertHookDialog()
	{
		setFrameProperties();

		confirmButton.addActionListener(actionEvent ->
		{
			String inputAssembly = assemblyTextArea.getText();

			try
			{
				bytes = Assembler.assembleBytes(inputAssembly);
				dispose();
			} catch (AssemblerException assemblerException)
			{
				JOptionPane.showMessageDialog(this,
						assemblerException.getMessage(),
						"Assembler Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception exception)
			{
				StackTraceUtils.handleException(rootPane, exception);
			}
		});
	}

	private void setFrameProperties()
	{
		setContentPane(contentPane);
		setModal(true);
		setSize(400, 300);
		WindowUtilities.setIconImage(this);
		setTitle("Insert Hook");
	}

	public byte[] getAssemblyBytes()
	{
		return bytes;
	}
}