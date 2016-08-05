package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

import java.util.ArrayList;
import java.util.List;

public class FileStructure
{
	private String PName;
	private int PTag;
	private FileStructure PParent;
	List<FileStructure> subFolders;
	List<SubFile> subFiles;


	public String Path()
	{
		return parent() == null ? "" : parent().Path() + "/" + PName;
	}

	public int tag()
	{
		return PTag;
	}

	public FileStructure parent()
	{
		return PParent;
	}

	public List<FileStructure> GetFolders()
	{
		return subFolders;
	}

	public List<SubFile> GetFiles()
	{
		return subFiles;
	}

	private FileStructure(String name, int tag, FileStructure parent)
	{
		PName = name;
		PTag = tag;
		PParent = parent;
		subFiles = new ArrayList<>();
		subFolders = new ArrayList<>();
	}

	public FileStructure(String name, int tag)
	{
		this(name, tag, null);
	}

	public FileStructure addSubFolder(String name, int tag)
	{
		FileStructure nFS = new FileStructure(name, tag, this);
		subFolders.add(nFS);

		return nFS;
	}

	public void addFile(String name, int tag, long length)
	{
		SubFile nSF = new SubFile(name, tag, length, this);
		subFiles.add(nSF);
	}

	public int CompareTo(FileStructure other)
	{
		return PName.compareTo(other.PName);
	}

	public String getPName()
	{
		return PName;
	}

	public void setPName(String PName)
	{
		this.PName = PName;
	}
}