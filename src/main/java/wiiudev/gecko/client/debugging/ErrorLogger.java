package wiiudev.gecko.client.debugging;

import wiiudev.gecko.client.gui.utilities.ProgramDirectoryUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class ErrorLogger
{
	private Logger logger;

	public ErrorLogger()
	{
		logger = Logger.getAnonymousLogger();

		configure();
	}

	private void configure()
	{
		try
		{
			String logsDirectoryFolder = ProgramDirectoryUtilities.getProgramDirectory() + File.separator + "logs";
			Files.createDirectories(Paths.get(logsDirectoryFolder));
			FileHandler fileHandler = new FileHandler(logsDirectoryFolder + File.separator + getCurrentTimeString() + ".log");
			logger.addHandler(fileHandler);
			SimpleFormatter formatter = new SimpleFormatter();
			fileHandler.setFormatter(formatter);
		} catch (IOException exception)
		{
			exception.printStackTrace();
		}

		addCloseHandlersShutdownHook();
	}

	private void addCloseHandlersShutdownHook()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			// Close all handlers to get rid of empty .LCK files
			for (Handler handler : logger.getHandlers())
			{
				handler.close();
			}
		}));
	}

	private String getCurrentTimeString()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return dateFormat.format(new Date());
	}

	public void log(Exception exception)
	{
		logger.log(Level.SEVERE, "", exception);
	}
}