package wiiudev.gecko.client.titles;

import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;
import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;

import java.io.IOException;

/**
 * A class for reading the game's title id from the memory
 */
public class TitleIdentifierUtilities
{
	public static String readDashedTitleID() throws IOException
	{
		long titleID = CoreInit.getTitleID();
		String titleIDString = new Hexadecimal(titleID, 16).toString();
		titleIDString = titleIDString.toUpperCase();

		return getDashedTitleID(titleIDString);
	}

	/**
	 * This is needed for comparing with the title database entries
	 *
	 * @return The dashed title id
	 */
	private static String getDashedTitleID(String titleID)
	{
		int startingIndex = 0;
		int stepSize = 8;

		String firstPart = titleID.substring(startingIndex, stepSize);
		String secondPart = titleID.substring(stepSize, stepSize + stepSize);

		return firstPart + "-" + secondPart;
	}
}