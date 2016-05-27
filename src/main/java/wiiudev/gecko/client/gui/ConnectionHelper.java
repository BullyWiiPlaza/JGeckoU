package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.gui.utilities.WindowUtilities;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class ConnectionHelper
{
	public static void displayConnectionHelperMessage(JRootPane rootPane) throws IOException, URISyntaxException
	{
		String helpHTML = WindowUtilities.resourceToString("Connector.html");

		JEditorPane aboutPane = new JEditorPane();
		aboutPane.setContentType("text/html");
		aboutPane.setText(helpHTML);

		aboutPane.setEditable(false);
		aboutPane.setOpaque(false);

		addHyperLinkListener(aboutPane);

		JOptionPane.showMessageDialog(rootPane, aboutPane, "Connector Help", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void addHyperLinkListener(JEditorPane editorPane)
	{
		editorPane.addHyperlinkListener(hyperlinkListener ->
		{
			if (HyperlinkEvent.EventType.ACTIVATED.equals(hyperlinkListener.getEventType()))
			{
				Desktop desktop = Desktop.getDesktop();

				try
				{
					desktop.browse(hyperlinkListener.getURL().toURI());
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
	}
}