package wiiudev.gecko.client.tcpgecko.rpl.structures;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class OSSystemInfo
{
	private int busClockSpeed;
	private int coreClockSpeed;
	private int timeBase;
	private int[] L2Size;
	private int cpuRatio;

	public OSSystemInfo(int address) throws IOException
	{
		MemoryReader memoryReader = new MemoryReader();
		byte[] bytes = memoryReader.readBytes(address, 0x1C);
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

		busClockSpeed = byteBuffer.getInt();
		coreClockSpeed = byteBuffer.getInt();
		timeBase = byteBuffer.getInt();
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
		return "Bus Clock Speed: " + busClockSpeed + System.lineSeparator()
				+ "Core Clock Speed: " + coreClockSpeed + System.lineSeparator()
				+ "Time Base: " + timeBase + System.lineSeparator()
				+ "L2 Size: " + Arrays.toString(L2Size) + System.lineSeparator()
				+ "CPU Ratio: " + cpuRatio;
	}
}