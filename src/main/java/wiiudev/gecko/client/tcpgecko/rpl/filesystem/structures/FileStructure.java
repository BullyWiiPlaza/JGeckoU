package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import java.util.ArrayList;
import java.util.List;

public class FileStructure
{
	private String name;
	private int tag;
	private FileStructure parent;
	private List<FileStructure> subFolders;
	private List<SubFile> subFiles;

	private FileStructure(String name, int tag, FileStructure parent)
	{
		this.name = name;
		this.tag = tag;
		this.parent = parent;

		subFiles = new ArrayList<>();
		subFolders = new ArrayList<>();
	}

	public FileStructure(String name, int tag)
	{
		this(name, tag, null);
	}

	public String getPath()
	{
		FileStructure parent = getParent();
		String parentPath = parent == null ? "" : parent.getPath();

		return parentPath + "/" + name;
	}

	public int tag()
	{
		return tag;
	}

	public FileStructure getParent()
	{
		return parent;
	}

	public List<FileStructure> getFolders()
	{
		return subFolders;
	}

	public List<SubFile> getFiles()
	{
		return subFiles;
	}

	public FileStructure addSubFolder(String name, int tag)
	{
		FileStructure subFolder = new FileStructure(name, tag, this);
		subFolders.add(subFolder);

		return subFolder;
	}

	public void addFile(String name, int tag, long length)
	{
		SubFile file = new SubFile(name, tag, length, this);
		subFiles.add(file);
	}

	public int compareTo(Object other)
	{
		return name.compareTo(((FileStructure) other).name);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}