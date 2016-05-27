package wiiudev.gecko.client.debugging;

import javax.swing.*;

public class StackTraceUtils
{
	public static ErrorLogger errorLogger = new ErrorLogger();

	private static String toString(Exception exception)
	{
		StringBuilder stringBuilder = new StringBuilder(exception.toString());
		for (StackTraceElement stackTraceElement : exception.getStackTrace())
		{
			stringBuilder.append("\n\tat ");
			stringBuilder.append(stackTraceElement);
		}

		return stringBuilder.toString();
	}

	public static void handleException(JRootPane rootPane, Exception exception)
	{
		errorLogger.log(exception);

		JOptionPane.showMessageDialog(rootPane,
				StackTraceUtils.toString(exception),
				"Error",
				JOptionPane.ERROR_MESSAGE);
	}
}