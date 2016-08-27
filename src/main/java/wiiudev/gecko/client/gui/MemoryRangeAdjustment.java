package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.AddressRange;
import wiiudev.gecko.client.tcpgecko.main.utilities.memory.MemoryRange;
import wiiudev.gecko.client.titles.Title;
import wiiudev.gecko.client.titles.TitleDatabaseManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MemoryRangeAdjustment
{
	private Path gameSpecificBoundsPath;
	private Path boundsFolderPath;

	public MemoryRangeAdjustment(TitleDatabaseManager titleDatabaseManager) throws IOException
	{
		Title gameTitle = titleDatabaseManager.readTitle();
		String gameId = gameTitle.getGameId();
		String boundsFolderName = "bounds";
		boundsFolderPath = Paths.get(boundsFolderName);
		gameSpecificBoundsPath = boundsFolderPath.resolve(gameId + ".txt");
	}

	public void setAdjustedMemoryRanges() throws IOException
	{
		if (!Files.exists(boundsFolderPath))
		{
			Files.createDirectories(boundsFolderPath);
		}

		if (!Files.exists(gameSpecificBoundsPath))
		{
			store();
		} else
		{
			restore();
		}
	}

	private void store() throws IOException
	{
		// First, find the limits
		AddressRange.setAppExecutableLibrariesStart();
		AddressRange.setMEM2RegionEnd();

		// Then store them
		String memoryRanges = AddressRange.appExecutableLibraries +
				System.lineSeparator() +
				AddressRange.mem2Region;
		byte[] memoryRangeBytes = memoryRanges.getBytes(StandardCharsets.UTF_8);
		Files.write(gameSpecificBoundsPath, memoryRangeBytes);
	}

	private void restore() throws IOException
	{
		// Read the lines
		List<String> lines = Files.readAllLines(gameSpecificBoundsPath);

		// Set the retrieved data
		AddressRange.appExecutableLibraries = getMemoryRange(lines.get(0));
		AddressRange.mem2Region = getMemoryRange(lines.get(1));
	}

	private MemoryRange getMemoryRange(String line)
	{
		String[] addresses = line.split(MemoryRange.MEMORY_RANGE_SEPARATOR);
		return new MemoryRange(Conversions.toDecimal(addresses[0]),
				Conversions.toDecimal(addresses[1]));
	}
}