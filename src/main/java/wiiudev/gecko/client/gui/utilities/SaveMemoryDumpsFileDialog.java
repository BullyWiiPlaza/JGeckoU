package wiiudev.gecko.client.gui.utilities;

import wiiudev.gecko.client.gui.tabs.GraphicalMemoryDumper;
import wiiudev.gecko.client.tcpgecko.main.TCPGecko;
import wiiudev.gecko.client.titles.Title;
import wiiudev.gecko.client.titles.TitleDatabaseManager;

import javax.swing.*;

public class SaveMemoryDumpsFileDialog extends SaveFileDialog
{
	public SaveMemoryDumpsFileDialog(JFrame frame) throws Exception
	{
		super(frame);

		if (TCPGecko.isConnected())
		{
			TitleDatabaseManager titleDatabaseManager = new TitleDatabaseManager();
			Title title = titleDatabaseManager.readTitle();
			String dumpsDirectory = GraphicalMemoryDumper.getDirectory(title);
			setDirectory(dumpsDirectory);
		}

		setFileExtension("bin");
	}
}