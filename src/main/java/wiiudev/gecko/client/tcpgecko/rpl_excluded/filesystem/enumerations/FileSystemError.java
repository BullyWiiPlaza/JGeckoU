package wiiudev.gecko.client.connector.rpl.filesystem.enumerations;

public enum FileSystemError
{
	NONE(0x0000),
	MAX(0x0001),
	ALREADY_OPEN(0x0002),
	EXISTS(0x0004),
	NOT_FOUND(0x0008),
	NOT_FILE(0x0010),
	NOT_DIR(0x0020),
	ACCESS_ERROR(0x0040),
	PERMISSION_ERROR(0x0080),
	FILE_TOO_BIG(0x0100),
	STORAGE_FULL(0x0200),
	UNSUPPORTED_CMD(0x0400),
	JOURNAL_FULL(0x0800),
	ALL(-1);

	public final int value;

	FileSystemError(int value)
	{
		this.value = value;
	}
}