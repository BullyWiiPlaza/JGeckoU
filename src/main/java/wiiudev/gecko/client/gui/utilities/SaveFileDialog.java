package wiiudev.gecko.client.gui.utilities;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveFileDialog extends FileDialog
{
	protected String forcedExtension;

	public SaveFileDialog(JFrame frame)
	{
		super(frame, "Save", FileDialog.SAVE);

		String programDirectory = ProgramDirectoryUtilities.getProgramDirectory();
		setDirectory(programDirectory);
		WindowUtilities.setIconImage(this);
	}

	@Override
	public void setDirectory(String directory)
	{
		super.setDirectory(directory);

		if (directory != null)
		{
			Path directoryPath = Paths.get(directory);

			try
			{
				Files.createDirectories(directoryPath);
			} catch (IOException exception)
			{
				exception.printStackTrace();
			}
		}
	}

	public void setFileExtension(String extension)
	{
		setFile("*." + extension);
		forcedExtension = extension;
	}

	public Path getSelectedFilePath()
	{
		dispose();

		String selectedDirectory = getDirectory();

		if (selectedDirectory == null)
		{
			return null;
		}

		String selectedFile = selectedDirectory + getFile();

		if (!selectedFile.endsWith(forcedExtension))
		{
			selectedFile += "." + forcedExtension;
		}

		return Paths.get(selectedFile);
	}

	public void showDialog()
	{
		setVisible(true);
	}
}