package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.codes.CodeListStorage;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class CodeListChooser extends JFileChooser
{
	public CodeListChooser()
	{
		setFileSelectionMode(JFileChooser.FILES_ONLY);
		setCurrentDirectory(new File(CodeListStorage.codesDirectory));

		setFileFilter(new FileFilter()
		{
			public String getDescription()
			{
				return "XML Code Lists (*." + CodeListStorage.fileExtension + ")";
			}

			public boolean accept(File file)
			{
				String filename = file.getName().toLowerCase();
				return filename.endsWith("." + CodeListStorage.fileExtension);
			}
		});
	}
}
