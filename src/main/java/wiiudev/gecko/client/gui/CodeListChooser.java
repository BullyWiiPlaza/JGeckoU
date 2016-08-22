package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.codes.CodeListStorage;
import wiiudev.gecko.client.gui.utilities.JFileChooserUtilities;

import javax.swing.*;
import java.io.File;

public class CodeListChooser extends JFileChooser
{
	public CodeListChooser()
	{
		setFileSelectionMode(JFileChooser.FILES_ONLY);
		setCurrentDirectory(new File(CodeListStorage.codesDirectory));

		JFileChooserUtilities.setXMLFileChooser(this);
	}

}
