package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.gui.utilities.ClasspathUtilities;

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
			String string = ClasspathUtilities.getClassPathString(fileName);
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

	public boolean contains(String functionName)
	{
		return functionNames.contains(functionName);
	}
}