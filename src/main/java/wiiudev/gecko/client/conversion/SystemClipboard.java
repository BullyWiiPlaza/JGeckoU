package wiiudev.gecko.client.conversion;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class SystemClipboard
{
	public static void copy(String text)
	{
		Clipboard clipboard = getSystemClipboard();
		clipboard.setContents(new StringSelection(text), null);
	}

	private static Clipboard getSystemClipboard()
	{
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		return defaultToolkit.getSystemClipboard();
	}
}