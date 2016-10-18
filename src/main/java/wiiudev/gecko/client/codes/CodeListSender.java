package wiiudev.gecko.client.codes;

import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;

import java.io.IOException;
import java.util.List;

/**
 * A class for sending and disabling the cheat code list
 */
public class CodeListSender
{
	private CodeListInformationReader codeListInformationReader;

	private List<CodeListEntry> codesList;
	private MemoryWriter memoryWriter;
	private int codeListStartingAddress;

	public CodeListSender()
	{
		try
		{
			codeListInformationReader = new CodeListInformationReader();
		} catch (IOException exception)
		{
			StackTraceUtils.handleException(null, exception);
		}

		codeListStartingAddress = codeListInformationReader.getStartAddress();
	}

	/**
	 * Sends the current code list
	 */
	public void applyCodes() throws IOException
	{
		clearCodes(true);
	}

	/**
	 * Clears all codes from the memory AND optionally sends the fresh list
	 *
	 * @param sendCodes Whether to send codes OR not
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
		int codeHandlerEnabledAddress = codeListInformationReader.getCodeHandlerEnabledAddress();
		memoryWriter.writeInt(codeHandlerEnabledAddress, value);
	}

	private void clearCodes() throws IOException
	{
		int maximumCodeListLength = codeListInformationReader.getEndAddress() - codeListStartingAddress;
		byte[] zeros = new byte[maximumCodeListLength];
		memoryWriter.writeBytes(codeListStartingAddress, zeros);
	}

	public void setCodesList(List<CodeListEntry> codeListEntries)
	{
		this.codesList = codeListEntries;
	}

	/**
	 * Writes the <code>codeList</code> to the memory
	 */
	private void sendCodes() throws IOException
	{
		memoryWriter = new MemoryWriter();

		// Start at the beginning of the code list
		int codeListAddress = codeListStartingAddress;

		for (CodeListEntry codeListEntry : codesList)
		{
			String code = codeListEntry.getCode();
			CheatCodeFormatting cheatCodeFormatting = new CheatCodeFormatting(code);
			byte[] cheatCodeBytes = cheatCodeFormatting.getBytes();
			int cheatCodeBytesLength = cheatCodeBytes.length;

			// Write the cheat code bytes
			memoryWriter.writeBytes(codeListAddress, cheatCodeBytes);

			// Advance to write the next cheat code
			codeListAddress += cheatCodeBytesLength;
		}

		setCodeHandlerEnabled(true);
	}

	/**
	 * Disables all cheat codes
	 */
	public void disableCodes() throws IOException
	{
		clearCodes(false);
	}
}