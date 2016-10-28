import wiiudev.gecko.client.tcpgecko.main.Connector;
import wiiudev.gecko.client.tcpgecko.main.MemoryReader;

public class Testing
{
	public static void main(String[] arguments) throws Exception
	{
		Connector.getInstance().connect("192.168.178.35");

		// RemoteDisassembler.disassembleRange(0x01000000, 0x8);

		MemoryReader memoryReader = new MemoryReader();
		int address = memoryReader.search(0x10000000, 0x1000000, new byte[]{0x73, 0x68, 0x6F, 0x75});
		System.out.println(Integer.toHexString(address));

		/*MemoryReader memoryReader = new MemoryReader();
		byte[] bytes = memoryReader.readBytes(0x10000000, 0x2000);
		Files.write(Paths.get("file4.bin"), bytes);*/



		/*MemoryWriter memoryWriter = new MemoryWriter();
		memoryWriter.kernelWriteInt(0x01100000, 0x60000000);
		MemoryWriter memoryWriter = new MemoryWriter();
		int address = 0x11000000;
		memoryWriter.writeBytes(address, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08});
		MemoryReader memoryReader = new MemoryReader();
		int i = memoryReader.readInt(address);
		System.out.println(Integer.toHexString(i).toUpperCase());
		address += 4;
		i = memoryReader.readInt(address);
		System.out.println(Integer.toHexString(i).toUpperCase());*/
		/*MemoryReader memoryReader = new MemoryReader();
		memoryReader.disassembleRange(0x02000000, 0x10);*/
		Connector.getInstance().closeConnection();
		// System.out.println(removeScientificNotation("3.0103E-7"));
	}
	// System.out.println(decimalToHex(-20f));
	//

		/*MemoryRange memoryRange = new MemoryRange(0x01800000, 0x10000000);
		memoryRange.updateMemoryRange(false);
		System.out.println(memoryRange.getEndingAddress());*/

		/*RemoteProcedureCall remoteProcedureCall = new RemoteProcedureCall();
		ExportedSymbol exportedSymbol = new ExportedSymbol(0x0249EEE0);
		int result = remoteProcedureCall.callInt(exportedSymbol);
		System.out.println(Conversions.toHexadecimal(result, 8));*/
		/*ExportedSymbol exportedSymbol = remoteProcedureCall.getSymbol("t6mp_cafef_rpl.rpl", "Party_IsPrivateOnlineGame");
		System.out.println(Conversions.toHexadecimal(exportedSymbol.getAddress(), 8));*/
		/*MemoryRange memoryRange = new MemoryRange(0xA0000000, 0xB0000000);
		memoryRange.updateMemoryRange(false);
		System.out.println(Conversions.toHexadecimal(memoryRange.getEndingAddress(), 8));*/
		/*byte[] bytes = RandomUtils.nextBytes(0x900);
		MemoryWriter memoryWriter = new MemoryWriter();
		memoryWriter.writeBytes(0x1FFFFFF0, bytes);*/

	// Connector.getInstance().closeConnection();
	// IDAProFunctionsDumpParser idaProFunctionsDumpParser = new IDAProFunctionsDumpParser();
	// Connector.getInstance().connect("192.168.178.35");
	// MemoryReader memoryReader = new MemoryReader();
		/*Connector.getInstance().connect("192.168.178.35");
		MemoryReader memoryReader = new MemoryReader();
		int physical = memoryReader.getEffectiveToPhysical(0x10000000);
		System.out.println(physical);
		Connector.getInstance().closeConnection();*/
		/*String rplName = "coreinit.rpl";
		int length = 8 + rplName.length() + 1;
		byte[] lengthBytes = ByteBuffer.allocate(4).putInt(length).array();
		System.out.println(length);
		System.out.println(Arrays.toString(lengthBytes));*/

		/*TitleDatabaseManager titleDatabaseManager = new TitleDatabaseManager();
		Title title = titleDatabaseManager.readTitle("00050000-10102000");
		System.out.println(title.getGameId());*/

		/*Connector.getInstance().connect("192.168.178.35");
		MemoryWriter memoryWriter = new MemoryWriter();
		memoryWriter.upload(0x3FD00000, Paths.get("binary.bin"));
		Connector.getInstance().closeConnection();*/

	// String waitingTime = MemoryReader.getExpectedWaitingTime(0x49000000 - 0x3A000000);
	// System.out.println(waitingTime);
		/*Field field = System.class.getDeclaredField("lineSeparator");
		field.setAccessible(true);
		field.set(System.class, "\n");
		String formatted = CheatCodeFormatter.format("12345678123456781234567812345678", false);
		System.out.println(formatted);
		formatted = CheatCodeFormatter.format("00220000 3FB467E0\n" +
				"10000000 50000000\n" +
				"00000024 00000000\n" +
				"10000000 50000000\n" +
				"000002E8 41000000", false);
		System.out.println(formatted);*/
	// String s = "FFFFF160";
	// System.out.println();
	// MemoryPointerExpression memoryPointerExpression = new MemoryPointerExpression("[0x4443BB4C]");
	// System.out.println();

		/*try
		{
			CodeDatabaseDownloader codeDatabaseDownloader = new CodeDatabaseDownloader("AGMP01");
			boolean codesExist = codeDatabaseDownloader.codesExist();

			if(codesExist)
			{
				List<GeckoCode> codes = codeDatabaseDownloader.parseCodes();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}*/

		/*TitleDatabaseManager titleDatabaseManager = new TitleDatabaseManager();
		titleDatabaseManager.restore();
		Title title = titleDatabaseManager.getTitleFromGameId("AGMP01");
		System.out.println(title.getTitleID().replace("-", ""));*/

	// System.out.println();

		/*Connector.getInstance().connect("192.168.178.35");
		int address = new MemoryReader().search_old(0x10000000, 0x2A0A0000, 0x1000);
		System.out.println(Integer.toHexString(address).toUpperCase());
		/*MemorySearch memorySearch = new MemorySearch(0x10000000, 0x1000);
		memorySearch.dump();
		memorySearch.dumpBytes(0x6C64206E, SearchConditions.EQUAL);
		memorySearch.dump();
		// memorySearch.dumpBytes(0x20202000, SearchConditions.NOT_EQUAL);
		// WordSearch_old wordSearchOld = new WordSearch_old(0x10000000, 0x1000);
		// wordSearchOld.dumpBytes(0x6C64206E);
		// wordSearchOld.dumpBytes(0x6C64206E);
		Connector.getInstance().closeConnection();*/
}