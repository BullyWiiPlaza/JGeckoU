package wiiudev.gecko.client.titles;

public class Title
{
	private String titleId;
	private String gameName;
	private String productCode;
	private String companyCode;

	public Title(String titleId, String gameName, String productCode, String companyCode)
	{
		this.titleId = titleId;
		this.gameName = gameName;
		this.productCode = productCode;
		this.companyCode = companyCode;
	}

	public String getCompanyCode()
	{
		return companyCode;
	}

	public String getProductCode()
	{
		return productCode;
	}

	public String getGameName()
	{
		return gameName;
	}

	public String getTitleId()
	{
		return titleId;
	}

	public String getGameId()
	{
		int lastProductCodeIndex = productCode.length();
		String productCodePart = productCode.substring(lastProductCodeIndex - 4, lastProductCodeIndex);

		int lastCompanyCodeIndex = companyCode.length();
		String companyCodePart = companyCode.substring(lastCompanyCodeIndex - 2, lastCompanyCodeIndex);

		return productCodePart + companyCodePart;
	}

	public static String getTitleId(String gameId) throws Exception
	{
		TitleDatabaseManager titleDatabaseManager = new TitleDatabaseManager();
		Title title = titleDatabaseManager.getTitle(gameId);
		return title.getTitleId().replace("-", "");
	}
}