import org.junit.Assert;
import org.junit.Test;
import wiiudev.gecko.client.conversions.Conversions;

public class TestConversions
{
	@Test
	public void testConversions()
	{
		String floatValue = Conversions.hexadecimalToFloatingPoint("40000000");
		Assert.assertEquals(floatValue, "2.0");
		floatValue = Conversions.hexadecimalToFloatingPoint("50000000");
		Assert.assertEquals(floatValue, "8589934592"); // TODO
		floatValue = Conversions.hexadecimalToFloatingPoint("C0000000");
		Assert.assertEquals(floatValue, "-2.0");
	}
}