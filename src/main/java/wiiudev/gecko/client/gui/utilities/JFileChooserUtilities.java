package wiiudev.gecko.client.gui.utilities;

import wiiudev.gecko.client.debugging.StackTraceUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;

public class JFileChooserUtilities
{
	public static void registerDeleteAction(JFileChooser fileChooser)
	{
		AbstractAction abstractAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				JFileChooser jFileChooser = (JFileChooser) actionEvent.getSource();

				try
				{
					File selectedFile = jFileChooser.getSelectedFile();

					if (selectedFile != null)
					{
						int selectedAnswer = JOptionPane.showConfirmDialog(null, "Are you sure want to permanently delete this file?", "Confirm", JOptionPane.YES_NO_OPTION);

						if (selectedAnswer == JOptionPane.YES_OPTION)
						{
							Files.delete(selectedFile.toPath());
							jFileChooser.rescanCurrentDirectory();
						}
					}
				} catch (Exception exception)
				{
					StackTraceUtils.handleException(null, exception);
				}
			}
		};

		fileChooser.getActionMap().put("delAction", abstractAction);
		fileChooser.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "delAction");
	}

	public static void setZippedXMLFileChooser(JFileChooser fileChooser)
	{
		setFileFilter(fileChooser, "Zipped XML", "xml.zip");
	}

	private static void setFileFilter(JFileChooser fileChooser, String description, String extension)
	{
		fileChooser.setFileFilter(new FileFilter()
		{
			public String getDescription()
			{
				return description + " (*." + extension + ")";
			}

			public boolean accept(File file)
			{
				String filename = file.getName().toLowerCase();
				return filename.endsWith("." + extension);
			}
		});
	}

	public static void setXMLFileChooser(JFileChooser fileChooser)
	{
		setFileFilter(fileChooser, "XML", "xml");
	}
}