package wiiudev.gecko.client.tcpgecko.main.enumerations;

/**
 * An enumeration of all the supported commands
 *
 * @author gudenau
 */
public enum Commands
{
	MEMORY_POKE_8(0x01),
	MEMORY_POKE_16(0x02),
	MEMORY_POKE_32(0x03),
	MEMORY_UPLOAD(0x41),
	// MEMORY_KERNEL_UPLOAD(0x42),
	MEMORY_READ(0x04),
	MEMORY_DISASSEMBLE(0x05),
	MEMORY_KERNEL_WRITE(0x0B),
	MEMORY_KERNEL_READ(0x0C),
	MEMORY_WRITE(0x41),
	GET_STATUS(0x50),
	RPC(0x70),
	GET_SYMBOL(0x71),
	MEMORY_SEARCH_32(0x72),
	RPC_BIG(0x80),
	GET_VERSION(0x99),
	GET_OS_VERSION(0x9A),
	GC_FAIL(0xCC);

	public final byte value;

	Commands(int value)
	{
		this.value = (byte) value;
	}
}