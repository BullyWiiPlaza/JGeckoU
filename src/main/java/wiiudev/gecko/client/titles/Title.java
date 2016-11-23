package wiiudev.gecko.client.titles;

public class Title
{
	private String titleId;
	private String gameName;
	private String productCode;
	private String companyCode;

	public static int TITLE_ID_DASHED_LENGTH = 17;
	public static int PRODUCT_CODE_ID_LENGTH = 4;
	public static int PRODUCT_CODE_FULL_LENGTH = 10;
	public static int COMPANY_CODE_LENGTH = 4;
	public static int COMPANY_CODE_ID_LENGTH = 2;

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
		int productCodeLength = productCode.length();
		String productCodePart = "";

		if (productCodeLength == PRODUCT_CODE_FULL_LENGTH)
		{
			productCodePart = productCode.substring(productCodeLength - PRODUCT_CODE_ID_LENGTH);
		}

		int companyCodeLength = companyCode.length();
		String companyCodePart = "";

		// Only do this for valid company codes
		if (companyCodeLength == COMPANY_CODE_LENGTH)
		{
			companyCodePart = companyCode.substring(companyCodeLength - COMPANY_CODE_ID_LENGTH);
		}

		if (productCodePart.equals("")
				&& companyCodePart.equals(""))
		{
			return titleId;
		}

		return productCodePart + companyCodePart;
	}

	public static String getTitleId(String gameId) throws Exception
	{
		TitleDatabaseManager titleDatabaseManager = new TitleDatabaseManager();
		Title title = titleDatabaseManager.getTitleFromGameId(gameId);

		return title.getTitleId().replace("-", "");
	}
}