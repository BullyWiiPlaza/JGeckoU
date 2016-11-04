package wiiudev.gecko.client.tcpgecko.rpl.filesystem.enumerations;

public enum FileSystemReturnFlag
{
	NONE(0x0),
	MAX(0x1),
	ALREADY_OPEN(0x2),
	EXISTS(0x4),
	NOT_FOUND(0x8),
	NOT_FILE(0x10),
	NOT_DIR(0x20),
	ACCESS_ERROR(0x40),
	PERMISSION_ERROR(0x80),
	FILE_TOO_BIG(0x100),
	STORAGE_FULL(0x200),
	UNSUPPORTED_CMD(0x400),
	JOURNAL_FULL(0x800),
	ALL(0xFFFFFFFF);

	private int value;

	FileSystemReturnFlag(int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}
}