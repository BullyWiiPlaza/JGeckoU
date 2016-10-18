import org.junit.Assert;
import org.junit.Test;
import wiiudev.gecko.client.codes.CodeListInformationReader;

import java.io.IOException;

public class TestCodeHandlerAddressesReading
{
	@Test
	public void testReadingCodeHandlerAddresses() throws IOException
	{
		CodeListInformationReader codeListInformationReader = new CodeListInformationReader();
		int startAddress = codeListInformationReader.getStartAddress();
		Assert.assertEquals(startAddress, 0x10015000);
		int endAddress = codeListInformationReader.getEndAddress();
		Assert.assertEquals(endAddress, 0x10017000);
		int codeHandlerStartAddress = codeListInformationReader.getCodeHandlerEnabledAddress();
		Assert.assertEquals(codeHandlerStartAddress, 0x10014CFC);
	}
}