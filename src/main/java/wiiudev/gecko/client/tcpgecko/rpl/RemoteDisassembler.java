package wiiudev.gecko.client.tcpgecko.rpl;

import wiiudev.gecko.client.tcpgecko.main.MemoryReader;
import wiiudev.gecko.client.tcpgecko.main.MemoryWriter;
import wiiudev.gecko.client.tcpgecko.rpl.structures.AllocatedMemory;

import java.io.IOException;

public class RemoteDisassembler
{
	// TODO Still crashes
	/*
		typedef void (*DisasmReport)(char* outputBuffer, ...);
		typedef void* (*DisasmGetSym) (unsigned long addr, unsigned char *symbolName, unsigned long nameBufSize);
		void DisassemblePPCRange(void *rangeStart, void *rangeEnd, DisasmReport disasmReport, DisasmGetSym disasmGetSym, u32 disasmOptions);
	*/
	public static String disassembleRange(int address, int length) throws IOException
	{
		int end = address + length;

		RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol disassembleRangeFunction = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "DisassemblePPCRange");
		ExportedSymbol printingFunction = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "__os_snprintf");
		ExportedSymbol getSymbolName = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "__OSGetSymbolName");
		remoteProcedureCall.call(disassembleRangeFunction,
				address,
				end,
				printingFunction.getAddress(),
				getSymbolName.getAddress(),
				DisassemblerOptions.SIMPLIFY.getValue());

			/*MemoryReader memoryReader = new MemoryReader();
			byte[] disassembledBytes = memoryReader.readBytes(printingFunction.getAddress(), length);
			String disassembled = new String(disassembledBytes);
			System.out.println(disassembled);

			disassembledBytes = memoryReader.readBytes(getSymbol.getAddress(), length);
			String disassembled2 = new String(disassembledBytes);
			System.out.println(disassembled2);*/

		return null;
	}

	public static String disassembleValue(int value) throws IOException
	{
		try (AllocatedMemory allocatedMemory = new AllocatedMemory(4, 4))
		{
			int address = allocatedMemory.getAddress();

			// Write the value into the allocated memory address
			MemoryWriter memoryWriter = new MemoryWriter();
			memoryWriter.writeInt(address, value);

			// Disassemble and return the result
			return disassembleAddress(address);
		}
	}

	// typedef void* (*DisasmGetSym) (unsigned long addr, unsigned char *symbolName, unsigned long nameBufSize);
	// BOOL DisassemblePPCOpcode(u32 *opcode, char *outputBuffer, u32 bufferSize, DisasmGetSym disasmGetSym, u32 disasmOptions);
	public static String disassembleAddress(int address) throws IOException
	{
		int instructionBufferLength = 0x40;

		try (AllocatedMemory outputBuffer = new AllocatedMemory(instructionBufferLength, 0x20))
		{
			RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
			ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "DisassemblePPCOpcode");
			ExportedSymbol getSymbolName = remoteProcedureCall.getSymbol(RPL.CORE_INIT.toString(), "__OSGetSymbolName");
			boolean validOpCodeFound = remoteProcedureCall.callBoolean(exportedSymbol,
					address,
					outputBuffer.getAddress(),
					instructionBufferLength,
					getSymbolName.getAddress(),
					DisassemblerOptions.SIMPLIFY.getValue());

			if (validOpCodeFound)
			{
				// Read the output buffer's contents
				MemoryReader memoryReader = new MemoryReader();
				String disassembled = memoryReader.readString(outputBuffer.getAddress());

				// Clean up the disassembled String
				disassembled = disassembled.replaceAll("\\(null\\)", " ");
				disassembled = disassembled.replaceAll("\\s+", " ");
				disassembled = disassembled.trim();

				return disassembled;
			} else
			{
				return "invalid";
			}
		}
	}

	// cafe_sdk\system\include\cafe\ppc_disasm.h
	private enum DisassemblerOptions
	{
		DEFAULT(0),
		SIMPLIFY(1),
		EMIT_SPACES(0x20),
		EMIT_DISASSEMBLY(0x40),
		EMIT_ADDRESS(0x80),
		EMIT_FUNCTIONS(0x100);

		private int value;

		DisassemblerOptions(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}
	}
}