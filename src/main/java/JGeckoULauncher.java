import wiiudev.gecko.client.gui.JGeckoUGUI;

import javax.swing.*;

public class JGeckoULauncher
{
	// TODO Move up AND down buttons
	// TODO When code modified, put it back at its original spot
	// TODO Copy buttons for ADD AND modify code
	// TODO Watch list fix random deadlock
	// TODO Watch list ASSIGN [0xADDRESS] = 0xVALUE for the retrieved value
	// TODO Save watch list for each game in watches/GAME_ID.xml
	// TODO Finish code wizard for the new code types (float AND int operations)
	// TODO Fix bigger memory dumps getting stuck
	// TODO Only toggle checkbox when clicked on that checkbox
	public static void main(String[] arguments) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		// Runs the application
		JGeckoUGUI jGeckoUGUI = JGeckoUGUI.getInstance();
		jGeckoUGUI.setVisible(true);
	}
}