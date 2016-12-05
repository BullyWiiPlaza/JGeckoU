package wiiudev.gecko.client.tcpgecko.main.enumerations;

/**
 * An enumeration of all the supported commands
 *
 * @author gudenau
 */
public enum Command
{
	MEMORY_POKE_8(0x01),
	MEMORY_POKE_16(0x02),
	MEMORY_POKE_32(0x03),
	MEMORY_READ(0x04),
	VALIDATE_ADDRESS(0x05),
	VALIDATE_ADDRESS_RANGE(0x06),
	// MEMORY_KERNEL_UPLOAD(0x42),
	// MEMORY_DISASSEMBLE(0x05),
	MEMORY_KERNEL_WRITE(0x0B),
	MEMORY_KERNEL_READ(0x0C),
	MEMORY_UPLOAD(0x41),
	GET_STATUS(0x50),
	DATA_BUFFER_SIZE(0x51),
	CODE_HANDLER_INSTALLATION_ADDRESS(0x55),
	RPC(0x70),
	GET_SYMBOL(0x71),
	MEMORY_SEARCH_32(0x72),
	MEMORY_SEARCH(0x73),
	RPC_BIG(0x80),
	GET_VERSION(0x99),
	GET_OS_VERSION(0x9A),
	GC_FAIL(0xCC),
	DIRECTORY(0x53),
	READ_FILE(0x52);

	public final byte value;

	Command(int value)
	{
		this.value = (byte) value;
	}
}