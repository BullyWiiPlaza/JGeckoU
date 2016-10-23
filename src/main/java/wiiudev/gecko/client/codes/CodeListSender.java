package wiiudev.gecko.client.codes;

import wiiudev.gecko.client.debugging.StackTraceUtils;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * A class for sending and disabling the cheat code list
 */
public class CodeListSender
{
	private CodeListInformationReader codeListInformationReader;

	private List<CodeListEntry> codeListEntries;
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
	 * Clears all codes from the memory and optionally sends the fresh list
	 *
	 * @param sendCodes Whether to send codes or not
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

	public void setCodeListEntries(List<CodeListEntry> codeListEntries)
	{
		this.codeListEntries = codeListEntries;
	}

	/**
	 * Writes the <code>codeList</code> to the memory
	 */
	private void sendCodes() throws IOException
	{
		memoryWriter = new MemoryWriter();

		ByteArrayOutputStream codeBytesBuffer = new ByteArrayOutputStream();

		for (CodeListEntry codeListEntry : codeListEntries)
		{
			byte[] cheatCodeBytes = codeListEntry.getCheatCodeBytes();
			codeBytesBuffer.write(cheatCodeBytes);
		}

		// Send all cheat code bytes at once
		byte[] codeBytes = codeBytesBuffer.toByteArray();
		memoryWriter.writeBytes(codeListStartingAddress, codeBytes);

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