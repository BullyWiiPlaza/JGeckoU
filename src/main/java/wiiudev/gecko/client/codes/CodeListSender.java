package wiiudev.gecko.client.codes;

import wiiudev.gecko.client.connector.MemoryWriter;

import java.io.IOException;
import java.util.List;

/**
 * A class for sending AND disabling the cheat code list
 */
public class CodeListSender
{
	private List<CodeListEntry> codesList;
	private MemoryWriter memoryWriter;
	private static int codeListStartingAddress = 0x10015000;

	/**
	 * Sends the current code list
	 *
	 * @throws IOException
	 */
	public void applyCodes() throws IOException
	{
		clearCodes(true);
	}

	/**
	 * Clears all codes from the memory AND optionally sends the fresh list
	 *
	 * @param sendCodes Whether to send codes OR not
	 * @throws IOException
	 */
	private void clearCodes(boolean sendCodes) throws IOException
	{
		memoryWriter = new MemoryWriter();
		setCodeHandlerEnabled(false);

		clearCodes();

		if (sendCodes)
		{
			sendCodes();
		}
	}

	private void setCodeHandlerEnabled(boolean enabled) throws IOException
	{
		int value = enabled ? 1 : 0;
		int codeHandlerEnabledAddress = codeListStartingAddress - 0x304;
		memoryWriter.writeInt(codeHandlerEnabledAddress, value);
	}

	private void clearCodes() throws IOException
	{
		int maximumCodeListLength = 0x2000;
		byte[] zeros = new byte[maximumCodeListLength];
		memoryWriter.writeBytes(codeListStartingAddress, zeros);
	}

	public void setCodesList(List<CodeListEntry> codeListEntries)
	{
		this.codesList = codeListEntries;
	}

	/**
	 * Writes the <code>codeList</code> to the memory
	 *
	 * @throws IOException
	 */
	private void sendCodes() throws IOException
	{
		memoryWriter = new MemoryWriter();

		// Start at the beginning of the code list
		int codeListAddress = codeListStartingAddress;

		for (CodeListEntry codeListEntry : codesList)
		{
			String code = codeListEntry.getCode();
			CheatCode cheatCode = new CheatCode(code);
			byte[] cheatCodeBytes = cheatCode.getBytes();
			int cheatCodeBytesLength = cheatCodeBytes.length;

			// Write the cheat code bytes
			memoryWriter.writeBytes(codeListAddress, cheatCodeBytes);

			// Advance to ASSIGN the next cheat code
			codeListAddress += cheatCodeBytesLength;
		}

		setCodeHandlerEnabled(true);
	}

	/**
	 * Disables all cheat codes
	 *
	 * @throws IOException
	 */
	public void disableCodes() throws IOException
	{
		clearCodes(false);
	}
}