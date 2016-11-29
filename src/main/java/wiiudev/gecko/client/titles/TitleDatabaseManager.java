package wiiudev.gecko.client.titles;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import wiiudev.gecko.client.gui.utilities.InternetAvailabilityChecker;
import wiiudev.gecko.client.gui.utilities.ProgramDirectoryUtilities;
import wiiudev.gecko.client.gui.utilities.XMLHelper;
import wiiudev.gecko.client.tcpgecko.main.utilities.conversions.Hexadecimal;
import wiiudev.gecko.client.tcpgecko.rpl.CoreInit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

public class TitleDatabaseManager
{
	private static final String titleDatabaseFileName = "Titles.xml";

	private static String titleTagName = "title";
	private static String idTagName = "id";
	private static String descriptionTagName = "description";
	private static String productTagName = "product";
	private static String companyTagName = "company";

	private List<Title> titles;
	private String titleDatabaseFilePath;

	public TitleDatabaseManager() throws Exception
	{
		titles = new LinkedList<>();
		titleDatabaseFilePath = ProgramDirectoryUtilities.getProgramDirectory() + File.separator + titleDatabaseFileName;

		boolean titleDatabaseFileExists = new File(titleDatabaseFilePath).exists();
		boolean isInternetAvailable = InternetAvailabilityChecker.isInternetAvailable();

		if (titleDatabaseFileExists)
		{
			restore();
		} else if (isInternetAvailable)
		{
			update();
		}
	}

	/**
	 * @return The {@link Title} object from the title database
	 */
	public Title readTitle() throws IOException
	{
		String dashedTitleId = TitleIdentifierUtilities.readDashedTitleID();
		return getTitle(dashedTitleId);
	}

	public Title getTitle(String dashedTitleId)
	{
		for (Title currentTitle : titles)
		{
			if (currentTitle.getTitleId().equals(dashedTitleId))
			{
				return currentTitle;
			}
		}

		throw new TitleNotFoundException(dashedTitleId);
	}

	public Title getTitleFromGameId(String gameId)
	{
		int gameIdLength = gameId.length();

		for (Title title : titles)
		{
			// Using title id as game id
			if (gameIdLength == Title.TITLE_ID_DASHED_LENGTH)
			{
				String titleId = title.getTitleId();

				if (titleId.endsWith(gameId))
				{
					return title;
				}
			}
			// Using product code as game id
			else if (gameIdLength == Title.PRODUCT_CODE_ID_LENGTH)
			{
				String productCode = title.getProductCode();

				if (productCode.endsWith(gameId))
				{
					return title;
				}
			}

			String productCode = title.getProductCode();

			if (productCode.length() != Title.PRODUCT_CODE_FULL_LENGTH)
			{
				continue;
			}

			String currentProductCodePart = productCode.substring(productCode.length() - Title.PRODUCT_CODE_ID_LENGTH);
			String gameIdProductCodePart = gameId.substring(0, gameId.length() - Title.COMPANY_CODE_ID_LENGTH);

			if (currentProductCodePart.equals(gameIdProductCodePart))
			{
				return title;
			}
		}

		throw new TitleNotFoundException(gameId);
	}

	private void restore() throws IOException, SAXException, ParserConfigurationException
	{
		NodeList nodesList = XMLHelper.getNodesList(titleDatabaseFilePath, titleTagName);

		for (int nodeIndex = 0; nodeIndex < nodesList.getLength(); nodeIndex++)
		{
			Node node = nodesList.item(nodeIndex);

			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;

				String entryName = element.getAttribute(idTagName);
				String codeName = XMLHelper.getText(element, descriptionTagName);
				String codeComment = XMLHelper.getText(element, productTagName);
				String company = XMLHelper.getText(element, companyTagName);

				Title title = new Title(entryName, codeName, codeComment, company);
				titles.add(title);
			}
		}
	}

	public void update() throws Exception
	{
		titles = TitlesDownloader.getTitles();
		store();
	}

	private void store() throws Exception
	{
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

		xMLStreamWriter.writeStartDocument();
		String rootElementName = "titles";
		xMLStreamWriter.writeStartElement(rootElementName);

		for (Title title : titles)
		{
			xMLStreamWriter.writeStartElement(titleTagName);
			xMLStreamWriter.writeAttribute(idTagName, title.getTitleId());

			xMLStreamWriter.writeStartElement(descriptionTagName);
			xMLStreamWriter.writeCharacters(title.getGameName());
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeStartElement(productTagName);
			xMLStreamWriter.writeCharacters(title.getProductCode());
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeStartElement(companyTagName);
			xMLStreamWriter.writeCharacters(title.getCompanyCode());
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeEndElement();
		}

		xMLStreamWriter.writeEndDocument();
		XMLHelper.writeFile(stringWriter, titleDatabaseFilePath);
	}

	public static class TitleNotFoundException extends IllegalArgumentException
	{
		TitleNotFoundException(String titleId)
		{
			super("The title id " + titleId + " has not been found in the " + titleDatabaseFileName + "!\nPlease make sure you're connected to the Internet\nso it can be downloaded automatically.");
		}
	}

	/**
	 * A class for reading the game's title id from the memory
	 */
	private static class TitleIdentifierUtilities
	{
		public static String readDashedTitleID() throws IOException
		{
			long titleID = CoreInit.getTitleID();
			String titleIDString = new Hexadecimal(titleID, 16).toString();
			titleIDString = titleIDString.toUpperCase();

			return getDashedTitleID(titleIDString);
		}

		/**
		 * This is needed for comparing with the title database entries
		 *
		 * @return The dashed title id
		 */
		private static String getDashedTitleID(String titleID)
		{
			int startingIndex = 0;
			int stepSize = 8;

			String firstPart = titleID.substring(startingIndex, stepSize);
			String secondPart = titleID.substring(stepSize, stepSize + stepSize);

			return firstPart + "-" + secondPart;
		}
	}
}