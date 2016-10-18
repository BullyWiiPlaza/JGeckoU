package wiiudev.gecko.client.codes;

import wiiudev.gecko.client.gui.utilities.ClasspathUtilities;

import java.io.IOException;

public class CodeListInformationReader
{
	private int[] components;

	public CodeListInformationReader() throws IOException
	{
		String codeListInformation = ClasspathUtilities.getClassPathString("Code List.txt");
		String[] codeListInformationLines = codeListInformation.split(System.lineSeparator());
		components = new int[codeListInformationLines.length];
		int componentIndex = 0;

		for(String codeListInformationLine : codeListInformationLines)
		{
			String[] lineComponents = codeListInformationLine.split("= ");
			String address = lineComponents[1];
			components[componentIndex] = Integer.parseUnsignedInt(address, 16);
			componentIndex++;
		}
	}

	public int getStartAddress()
	{
		return components[0];
	}

	public int getEndAddress()
	{
		return components[1];
	}

	public int getCodeHandlerEnabledAddress()
	{
		return components[2];
	}
}