package wiiudev.gecko.client.codes;

public class GeckoCode
{
	private String title;
	private String code;
	private String comment;
	private boolean enabled;

	public boolean isEnabled()
	{
		return enabled;
	}

	public String getComment()
	{
		return comment;
	}

	public String getCode()
	{
		return code;
	}

	public String getTitle()
	{
		return title;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
}