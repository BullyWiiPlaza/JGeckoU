package wiiudev.gecko.client.tcpgecko.main.enumerations;

/**
 * An enumeration of all known status codes
 *
 * @author gudenau
 */
public enum Status
{
	RUNNING(0x01),
	OK(0xBD),
	OK_EMPTY(0xB0),
	GC_ACK(0xAA);

	public final byte value;

	Status(int value)
	{
		this.value = (byte) value;
	}

	/**
	 * Returns the status corresponding to the provided byte
	 *
	 * @return The status, null if unknown
	 */
	public static Status getStatus(byte statusByte)
	{
		for (Status status : values())
		{
			if (status.value == statusByte)
			{
				return status;
			}
		}

		throw new IllegalStateException("Illegal status received: " + statusByte);
	}
}