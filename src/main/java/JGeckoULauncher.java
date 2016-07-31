import wiiudev.gecko.client.gui.JGeckoUGUI;

import javax.swing.*;

public class JGeckoULauncher
{
	// TODO Get symbol and call symbol
	// TODO File system access (extract and replace)
	// TODO Search tab
	// TODO Move up and down buttons for codes and watch list
	// TODO When code or watch modified, put it back at its original spot
	// TODO Copy buttons for add and modify code to reduce redundancy
	// TODO Watch list "random" deadlock when updating?
	/*
		// Process returned data
		Status status = readStatus();
		switch (status)
		{
			// The memory is not all 0, read the data
			case OK:
				readData(buffer, 0, readLength);
				output.write(buffer, 0, readLength);
				break;
			// The memory was all 0, no nead to read the data
			case OK_EMPTY:
				clearBuffer(buffer);
				output.write(buffer, 0, readLength);
				break;
			// Something went wrong
			default:
				throw new WiiUException("Got an unknown status while reading memory!");
		}
	*/
	// TODO Watch list ASSIGN [0xADDRESS] = 0xVALUE for the retrieved value
	// TODO Finish code wizard fix input availability for new code types fully
	// TODO Only toggle cheat code checkbox when clicked on that checkbox
	// TODO Keep checking if the server is still alive and disconnect if not
	// TODO Combine connect and disconnect button into one
	public static void main(String[] arguments) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		// Runs the application
		JGeckoUGUI jGeckoUGUI = JGeckoUGUI.getInstance();
		jGeckoUGUI.setVisible(true);
	}
}