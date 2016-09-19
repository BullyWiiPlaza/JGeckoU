import org.junit.Assert;
import org.junit.Test;
import wiiudev.gecko.client.conversions.Conversions;

public class TestConversions
{
	@Test
	public void testFloatingPoint()
	{
		String floatValue = Conversions.hexadecimalToFloatingPoint("40000000");
		Assert.assertEquals(floatValue, "2");
		floatValue = Conversions.hexadecimalToFloatingPoint("50000000");
		Assert.assertEquals(floatValue, "8589934592");
		floatValue = Conversions.hexadecimalToFloatingPoint("C0000000");
		Assert.assertEquals(floatValue, "-2");
		floatValue = Conversions.hexadecimalToFloatingPoint("D0000000");
		Assert.assertEquals(floatValue, "-8589934592");

		String hexadecimal = Conversions.floatingPointToHexadecimal("10");
		Assert.assertEquals(hexadecimal, "41200000");
		hexadecimal = Conversions.floatingPointToHexadecimal("-10");
		Assert.assertEquals(hexadecimal, "C1200000");
		hexadecimal = Conversions.floatingPointToHexadecimal("598340580934850348503");
		Assert.assertEquals(hexadecimal, "6201BE93");
		hexadecimal = Conversions.floatingPointToHexadecimal("-485093484850348503");
		Assert.assertEquals(hexadecimal, "DCD76CBF");
	}

	@Test
	public void testDecimal()
	{
		String decimal = Conversions.hexadecimalToDecimal("3E8");
		Assert.assertEquals(decimal, "1000");
	}
}