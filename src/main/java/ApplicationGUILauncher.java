import wiiudev.gecko.client.gui.JGeckoUGUI;

import javax.swing.*;

public class ApplicationGUILauncher
{
	// TODO Fully optimize memory dumping logic for the search to save time
	// TODO File system access (extract and replace files)
	// TODO Move up and down buttons for codes and watch list
	// TODO When code or watch modified, put it back at its original spot
	// TODO Copy buttons for add and modify code
	// TODO Watch list use [0xADDRESS] = 0xVALUE for the retrieved value
	// TODO Finish code wizard fix input availability for new/changed code types (pointers)
	// TODO Only toggle cheat code checkbox when clicked on that checkbox
	// TODO Combine connect and disconnect button into one
	public static void main(String[] arguments) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		// Runs the application
		JGeckoUGUI jGeckoUGUI = JGeckoUGUI.getInstance();
		jGeckoUGUI.setVisible(true);
	}
}