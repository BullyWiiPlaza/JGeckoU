package wiiudev.gecko.client.tcpgecko.rpl;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.rpl.structures.AllocatedMemory;
import wiiudev.gecko.client.tcpgecko.rpl.structures.FindSymbol;
import wiiudev.gecko.client.tcpgecko.rpl.structures.RemoteString;

import java.io.IOException;

public class RemoteDisassembler
{
	public static String disassembleRange(int start, int length) throws IOException
	{
		int end = start + length;

		try (FindSymbol findSymbol = new FindSymbol();
		     RemoteString remoteString = new RemoteString("%s"))
		{
			RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
			ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "DisassemblePPCRange");
			remoteProcedureCall.call(exportedSymbol, start, end,
					remoteString.getAddress(), findSymbol.getAddress(), 0);

			MemoryReader memoryReader = new MemoryReader();
			byte[] disassembledBytes = memoryReader.readBytes(remoteString.getAddress(), length);
			String disassembled = new String(disassembledBytes);
			System.out.println(disassembled);

			disassembledBytes = memoryReader.readBytes(findSymbol.getAddress(), length);
			String disassembled2 = new String(disassembledBytes);
			System.out.println(disassembled2);

			return disassembled;
		}
	}

	public static String disassembleValue(int value) throws IOException
	{
		int address = CoreInit.allocateDefaultHeapMemory(4, 4);
		MemoryWriter memoryWriter = new MemoryWriter();
		memoryWriter.writeInt(address, value);
		String disassembled = disassembleAddress(address);
		CoreInit.freeDefaultHeapMemory(address);

		return disassembled;
	}

	public static String disassembleAddress(int address) throws IOException
	{
		int instructionBufferLength = 0x40;

		try (FindSymbol findSymbol = new FindSymbol();
		     AllocatedMemory instructionBuffer = new AllocatedMemory(instructionBufferLength, 0x20))
		{
			RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
			ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("coreinit.rpl", "DisassemblePPCOpcode");
			remoteProcedureCall.call(exportedSymbol, address, instructionBuffer.getAddress(), instructionBufferLength, findSymbol.getAddress(), 0);

			MemoryReader memoryReader = new MemoryReader();
			byte[] disassembledBytes = memoryReader.readBytes(instructionBuffer.getAddress(), instructionBufferLength);

			String disassembled = new String(disassembledBytes);
			disassembled = disassembled.replaceAll("\\(null\\)", " ");
			disassembled = disassembled.replaceAll("\\s+", " ");
			disassembled = disassembled.trim();

			return disassembled;
		}
	}
}