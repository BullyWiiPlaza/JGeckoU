package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.gui.utilities.ProgramDirectoryUtilities;

import java.io.*;
import java.util.Properties;

public class SimpleProperties
{
	private Properties properties;
	private String propertiesFilePath;

	public SimpleProperties()
	{
		propertiesFilePath = ProgramDirectoryUtilities.getProgramDirectory() + File.separator + "config.properties";
		properties = new Properties();

		try
		{
			if (new File(propertiesFilePath).exists())
			{
				InputStream propertiesReader = new FileInputStream(propertiesFilePath);
				properties.load(propertiesReader);
			}
		} catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}

	public void put(String key, String value)
	{
		properties.setProperty(key, value);
	}

	public void writeToFile()
	{
		try
		{
			OutputStream propertiesWriter = new FileOutputStream(propertiesFilePath);
			properties.store(propertiesWriter, null);
		} catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}

	public String get(String key)
	{
		return (String) properties.get(key);
	}
}