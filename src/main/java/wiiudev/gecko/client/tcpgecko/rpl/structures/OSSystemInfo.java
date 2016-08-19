package wiiudev.gecko.client.tcpgecko.rpl.structures;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;

import java.io.IOException;
import java.nio.ByteBuffer;

public class OSSystemInfo
{
	private int busClockSpeed;
	private int coreClockSpeed;
	private long timeBase;
	private int[] L2Size;
	private int cpuRatio;

	public OSSystemInfo(int address) throws IOException
	{
		MemoryReader memoryReader = new MemoryReader();
		byte[] bytes = memoryReader.readBytes(address, 6 * 4 + 8);
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

		busClockSpeed = byteBuffer.getInt();
		coreClockSpeed = byteBuffer.getInt();
		timeBase = byteBuffer.getLong();
		L2Size = new int[3];

		for (int arrayIndex = 0; arrayIndex < L2Size.length; arrayIndex++)
		{
			L2Size[arrayIndex] = byteBuffer.getInt();
		}

		cpuRatio = byteBuffer.getInt();
	}

	@Override
	public String toString()
	{
		return "Bus Clock Speed: " + new Hexadecimal(busClockSpeed, 8) + System.lineSeparator()
				+ "Core Clock Speed: " + new Hexadecimal(coreClockSpeed, 8) + System.lineSeparator()
				+ "Time Base: " + new Hexadecimal(timeBase, 16) + System.lineSeparator()
				+ "L2 Size: " + Hexadecimal.toHexadecimal(L2Size) + System.lineSeparator()
				+ "CPU Ratio: " + new Hexadecimal(cpuRatio, 8);
	}
}