package wiiudev.gecko.client.tcpgecko.main.threads;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * http://wiiubrew.org/wiki/Coreinit.rpl#Structures
 */
public class OSContext
{
	private int[] generalPurposeRegisters;
	private int conditionalRegister;
	private int linkRegister;
	private int counterRegister;
	private int exceptionRegister;
	private int restoreRegister0;
	private int restoreRegister1;
	private int floatingPointStatusHighRegister;
	private int getFloatingPointStatusLowRegister;
	private long[] floatingPointRegisters;
	private int[] graphicsQuantizationRegisters;
	private int pir;
	private long[] fprRegisters;

	public OSContext(int address) throws IOException
	{
		MemoryReader memoryReader = new MemoryReader();
		int osContextLength = 0x320;
		byte[] osContextBytes = memoryReader.readBytes(address, osContextLength);
		ByteBuffer byteBuffer = ByteBuffer.wrap(osContextBytes);

		// Skip the context tag
		byteBuffer.position(byteBuffer.position() + 0x8);

		generalPurposeRegisters = new int[32];

		for (int registersIndex = 0; registersIndex < generalPurposeRegisters.length; registersIndex++)
		{
			int register = byteBuffer.getInt();
			generalPurposeRegisters[registersIndex] = register;
		}

		conditionalRegister = byteBuffer.getInt();
		linkRegister = byteBuffer.getInt();
		counterRegister = byteBuffer.getInt();
		exceptionRegister = byteBuffer.getInt();
		restoreRegister0 = byteBuffer.getInt();
		restoreRegister1 = byteBuffer.getInt();

		// Skip over some unknown junk
		byteBuffer.position(byteBuffer.position() + 0x14);

		floatingPointStatusHighRegister = byteBuffer.getInt();
		getFloatingPointStatusLowRegister = byteBuffer.getInt();

		floatingPointRegisters = new long[32];

		for (int registersIndex = 0; registersIndex < floatingPointRegisters.length; registersIndex++)
		{
			long register = byteBuffer.getLong();
			floatingPointRegisters[registersIndex] = register;
		}

		// Skip over some more junk
		byteBuffer.position(byteBuffer.position() + 0x4);

		graphicsQuantizationRegisters = new int[8];

		for (int registersIndex = 0; registersIndex < graphicsQuantizationRegisters.length; registersIndex++)
		{
			int register = byteBuffer.getInt();
			graphicsQuantizationRegisters[registersIndex] = register;
		}

		pir = byteBuffer.getInt();

		fprRegisters = new long[32];

		for (int registersIndex = 0; registersIndex < fprRegisters.length; registersIndex++)
		{
			long register = byteBuffer.getLong();
			fprRegisters[registersIndex] = register;
		}
	}

	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();

		for (int registersIndex = 0; registersIndex < generalPurposeRegisters.length; registersIndex++)
		{
			int register = generalPurposeRegisters[registersIndex];

			stringBuilder.append("r");
			stringBuilder.append(registersIndex);
			stringBuilder.append(": ");
			stringBuilder.append(toHexadecimal(register));
			stringBuilder.append(System.lineSeparator());
		}

		stringBuilder.append("CR: ");
		stringBuilder.append(toHexadecimal(conditionalRegister));
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("LR: ");
		stringBuilder.append(toHexadecimal(linkRegister));
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("CTR: ");
		stringBuilder.append(toHexadecimal(counterRegister));
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("XER: ");
		stringBuilder.append(toHexadecimal(exceptionRegister));
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("SRR0: ");
		stringBuilder.append(toHexadecimal(restoreRegister0));
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("SRR1: ");
		stringBuilder.append(toHexadecimal(restoreRegister1));
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("FPSR_H: ");
		stringBuilder.append(toHexadecimal(floatingPointStatusHighRegister));
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("FPSR_L: ");
		stringBuilder.append(toHexadecimal(getFloatingPointStatusLowRegister));
		stringBuilder.append(System.lineSeparator());

		for (int registersIndex = 0; registersIndex < floatingPointRegisters.length; registersIndex++)
		{
			long register = floatingPointRegisters[registersIndex];

			stringBuilder.append("f");
			stringBuilder.append(registersIndex);
			stringBuilder.append(": ");
			stringBuilder.append(toHexadecimal(register));
			stringBuilder.append(System.lineSeparator());
		}

		for (int registersIndex = 0; registersIndex < graphicsQuantizationRegisters.length; registersIndex++)
		{
			int register = graphicsQuantizationRegisters[registersIndex];

			stringBuilder.append("g");
			stringBuilder.append(registersIndex);
			stringBuilder.append(": ");
			stringBuilder.append(toHexadecimal(register));
			stringBuilder.append(System.lineSeparator());
		}

		stringBuilder.append("PIR: ");
		stringBuilder.append(toHexadecimal(pir));
		stringBuilder.append(System.lineSeparator());

		for (int registersIndex = 0; registersIndex < fprRegisters.length; registersIndex++)
		{
			long register = fprRegisters[registersIndex];

			stringBuilder.append("p");
			stringBuilder.append(registersIndex);
			stringBuilder.append(": ");
			stringBuilder.append(toHexadecimal(register));
			stringBuilder.append(System.lineSeparator());
		}

		return stringBuilder.toString().trim();
	}

	private String toHexadecimal(int value)
	{
		return new Hexadecimal(value, 8).toString();
	}

	private String toHexadecimal(long value)
	{
		return new Hexadecimal(value, 16).toString();
	}
}