package wiiudev.gecko.client.gui.utilities;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class PopupMenuUtilities
{
	public static boolean keyEventPressed(KeyEvent event, KeyStroke keyStroke)
	{
		int targetKeyCode = keyStroke.getKeyCode();

		return event.getKeyCode() == targetKeyCode && (event.getModifiers() & KeyEvent.CTRL_MASK) != 0;
	}

	public static KeyStroke addOption(JPopupMenu popupMenu, String text, String key, ActionListener actionListener)
	{
		JMenuItem option = new JMenuItem(text);
		KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
		option.setAccelerator(keyStroke);
		option.addActionListener(actionListener);
		popupMenu.add(option);

		return keyStroke;
	}
}
