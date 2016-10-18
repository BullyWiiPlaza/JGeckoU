package wiiudev.gecko.client.gui.utilities;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ClasspathUtilities
{
	public static String getClassPathString(String fileName) throws IOException
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = loader.getResourceAsStream(fileName);

		return IOUtils.toString(inputStream, Charset.defaultCharset());
	}
}