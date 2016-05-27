package wiiudev.gecko.client.titles;

import org.apache.commons.codec.binary.Hex;
import wiiudev.gecko.client.connector.MemoryReader;

import java.io.IOException;

/**
 * A class for reading the game's title id from the memory
 */
class TitleIdentifierUtilities
{
	/**
	 * This is needed for comparing with the title database entries
	 *
	 * @return The dashed title id
	 */
	private static String getDashedTitleId(String titleId)
	{
		int startingIndex = 0;
		int stepSize = 8;

		String firstPart = titleId.substring(startingIndex, stepSize);
		String secondPart = titleId.substring(stepSize, stepSize + stepSize);

		return firstPart + "-" + secondPart;
	}

	static String readDashedTitleId() throws IOException
	{
		byte[] titleIdBytes = readTitleIdBytes();
		String titleId = Hex.encodeHexString(titleIdBytes);
		titleId = titleId.toUpperCase();

		return getDashedTitleId(titleId);
	}

	/**
	 * @return The title id of the currently running Wii U game
	 * @throws IOException
	 */
	private static byte[] readTitleIdBytes() throws IOException
	{
		MemoryReader memoryReader = new MemoryReader();
		int firmwareVersion = memoryReader.readFirmwareVersion();
		int titleIdAddress;

		if(firmwareVersion == 532)
		{
			titleIdAddress = 0x100136D0;
		}
		else if(firmwareVersion == 550 || firmwareVersion == 551)
		{
			titleIdAddress = 0x10013C10;
		}
		else
		{
			throw new FirmwareNotImplementedException("Automatic title detection is not implemented for your firmware version " + firmwareVersion);
		}

		return memoryReader.readBytes(titleIdAddress, 0x8);
	}
}