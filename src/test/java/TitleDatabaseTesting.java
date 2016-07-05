import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import wiiudev.gecko.client.titles.Title;
import wiiudev.gecko.client.titles.TitleDatabaseManager;

public class TitleDatabaseTesting
{
	private static TitleDatabaseManager titleDatabaseManager;

	@BeforeClass
	public static void setup() throws Exception
	{
		titleDatabaseManager = new TitleDatabaseManager();
	}

	@Test
	public void correctGameId() throws Exception
	{
		// Both valid product code and company code
		testTitleId("00050000-10102000", "ALCE01");

		// No valid product code or company
		testTitleId("00050000-10102500", "00050000-10102500");

		// Just valid product code but no company code
		testTitleId("00050000-101ED700", "ASEP");
	}

	private void testTitleId(String titleId, String targetGameId)
	{
		Title title = titleDatabaseManager.getTitle(titleId);
		String gameId = title.getGameId();

		// Game id matches the expected
		Assert.assertTrue(gameId.equals(targetGameId));
		Title newTitle = titleDatabaseManager.getTitleFromGameId(gameId);

		// The reverse works too
		Assert.assertTrue(title.getTitleId().equals(newTitle.getTitleId()));
	}
}