package wiiudev.gecko.client.tcpgecko.rpl.filesystem.structures;

public class SubFile
{
	private String PName;
	private int PTag;
	private FileStructure PParent;
	private long length;

	public FileStructure parent()
	{
		return PParent;
	}

	public String Path()
	{
		return parent().getPath() + "/" + PName;
	}

	public SubFile(String name, int tag, long length, FileStructure parent)
	{
		PName = name;
		PTag = tag;
		PParent = parent;
		this.length = length;
	}

	public int CompareTo(SubFile other)
	{
		return PName.compareTo(other.PName);
	}

	public long getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}
}