package wiiudev.gecko.client.titles;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import wiiudev.gecko.client.gui.utilities.XMLHelper;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TitleDatabaseManager
{
	private static String titleTagName = "title";
	private static String idTagName = "id";
	private static String descriptionTagName = "description";
	private static String productTagName = "product";
	private static String companyTagName = "company";

	private List<Title> titleDatabase;
	private String titleDatabaseFilePath;

	public TitleDatabaseManager() throws Exception
	{
		titleDatabase = Collections.synchronizedList(new LinkedList<>());
		titleDatabaseFilePath = "Titles.xml";

		boolean titleDatabaseFileExists = new File(titleDatabaseFilePath).exists();

		if (titleDatabaseFileExists)
		{
			restore();
		} else
		{
			update();
		}
	}

	/**
	 * @return The {@link Title} object from the title database
	 */
	public Title getTitle() throws IOException
	{
		String dashedTitleId = TitleIdentifierUtilities.readDashedTitleID();
		return getTitle(dashedTitleId);
	}

	public Title getTitle(String dashedTitleId)
	{
		for (Title currentTitle : titleDatabase)
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

		for (Title title : titleDatabase)
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

	public void restore() throws IOException, SAXException, ParserConfigurationException
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
				titleDatabase.add(title);
			}
		}
	}

	public void update() throws Exception
	{
		update(titleDatabase);
		store();
	}

	/**
	 * Parses and stores all Wii U game's title ids.
	 * This method may take a while and slow down your computer
	 *
	 * @throws Exception
	 */
	public static void update(List<Title> titlesDatabase) throws Exception
	{
		titlesDatabase.clear();
		int poolSize = Runtime.getRuntime().availableProcessors() * 2;
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
		List<Future<?>> tasks = new ArrayList<>();

		// Connect to the website and parse the table
		String titleDatabaseURL = "http://wiiubrew.org/wiki/Title_database";
		org.jsoup.nodes.Document titleDatabaseDocument = Jsoup.connect(titleDatabaseURL).get();
		Elements titlesTable = titleDatabaseDocument.select("#mw-content-text > table:nth-child(16) > tbody");
		Elements rows = titlesTable.select("tr");
		int rowsCount = rows.size();

		for (int rowsIndex = 1; rowsIndex < rowsCount; rowsIndex++)
		{
			org.jsoup.nodes.Element row = rows.get(rowsIndex);

			// Parse each row multi-threaded
			Future task = threadPool.submit(new Thread(() ->
			{
				int columnIndex = 0;
				String titleId = row.child(columnIndex++).text();
				String gameName = row.child(columnIndex++).text();
				String productCode = row.child(columnIndex++).text();
				String companyCode = row.child(columnIndex).text();

				Title title = new Title(titleId, gameName, productCode, companyCode);
				titlesDatabase.add(title);
			}));

			tasks.add(task);
		}

		// Wait for all tasks to finish
		for (Future<?> task : tasks)
		{
			task.get();
		}

		threadPool.shutdownNow();
	}

	private void store() throws Exception
	{
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

		xMLStreamWriter.writeStartDocument();
		String rootElementName = "titles";
		xMLStreamWriter.writeStartElement(rootElementName);

		for (Title title : titleDatabase)
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
			super("The title id " + titleId + " has not been found in the database");
		}
	}
}