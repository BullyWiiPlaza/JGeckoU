package wiiudev.gecko.client.gui;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class IDAProFunctionsDumpParser
{
	private List<String> functionNames;
	private String fileName;

	public IDAProFunctionsDumpParser(String fileName)
	{
		this.fileName = fileName;
		parseFunctionNames();
	}

	private void parseFunctionNames()
	{
		try
		{
			String string = getClassPathString(fileName);
			String[] lines = string.split(System.lineSeparator());
			functionNames = new ArrayList<>();

			for (String line : lines)
			{
				int spaceIndex = line.indexOf(" ");
				String functionName = line.substring(0, spaceIndex);
				functionNames.add(functionName);
			}
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	private String getClassPathString(String fileName) throws IOException
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = loader.getResourceAsStream(fileName);

		return IOUtils.toString(inputStream, Charset.defaultCharset());
	}

	public boolean contains(String functionName)
	{
		return functionNames.contains(functionName);
	}
}