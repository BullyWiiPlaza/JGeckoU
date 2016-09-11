package wiiudev.gecko.client.gui;

import wiiudev.gecko.client.gui.tabs.pointer_search.DownloadingUtilities;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ApplicationLauncher
{
	private Path installedExecutablePath;
	private String downloadURL;
	private String name;
	private boolean unZip;

	public ApplicationLauncher(String installedExecutablePath,
	                           String downloadURL,
	                           String name,
	                           boolean unZip)
	{
		this.installedExecutablePath = Paths.get(installedExecutablePath);
		this.downloadURL = downloadURL;
		this.name = name;
		this.unZip = unZip;
	}

	public Path getInstalledExecutablePath()
	{
		return installedExecutablePath;
	}

	public String getDownloadURL()
	{
		return downloadURL;
	}

	public String getName()
	{
		return name;
	}

	public boolean shouldUnZip()
	{
		return unZip;
	}

	public String getSetupFileName()
	{
		return DownloadingUtilities.getFileName(downloadURL);
	}
}