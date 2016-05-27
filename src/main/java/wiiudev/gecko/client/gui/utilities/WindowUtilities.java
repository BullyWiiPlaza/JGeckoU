package wiiudev.gecko.client.gui.utilities;

import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class WindowUtilities
{
	public static void setIconImage(Window window)
	{
		window.setIconImage(Toolkit.getDefaultToolkit().getImage(WindowUtilities.class.getResource("/Gecko.png")));
	}

	public static String resourceToString(String filePath) throws IOException, URISyntaxException
	{
		InputStream inputStream = WindowUtilities.class.getClassLoader().getResourceAsStream(filePath);
		return IOUtils.toString(inputStream, "UTF-8");
	}
}