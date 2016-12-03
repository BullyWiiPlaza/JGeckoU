import wiiudev.gecko.client.gui.JGeckoUGUI;

import javax.swing.*;

public class ApplicationGUILauncher
{
	// TODO Refine search button when not first search
	// TODO Read compressed bytes from server?
	// TODO Watch list implement context menu (like for the codes tab)
	// TODO File system access tab (extract and replace files)
	// TODO Memory viewer fast bytes search (implement on TCP Gecko server)
	// TODO Disassembler Regex Search (+ Cancel Button)
	// TODO Remote range disassembler (implement on TCP Gecko server?)
	// TODO Only toggle cheat code checkbox when clicked on that checkbox directly, not the row
	public static void main(String[] arguments) throws Exception
	{
		// Sets the system look and feel
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		// Runs the application
		SwingUtilities.invokeLater(() ->
		{
			JGeckoUGUI mainFrame = JGeckoUGUI.getInstance();
			mainFrame.setVisible(true);
		});
	}
}