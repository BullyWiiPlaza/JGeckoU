package wiiudev.gecko.client.titles;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import wiiudev.gecko.client.XMLHelper;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

		if(new File(titleDatabaseFilePath).exists())
		{
			restore();
		}
		else
		{
			update();
		}
	}

	/**
	 * @return The {@link Title} object from the title database
	 */
	public Title getTitle() throws IOException
	{
		String dashedTitleId = TitleIdentifierUtilities.readDashedTitleId();

		for(Title currentTitle : titleDatabase)
		{
			if(currentTitle.getTitleId().equals(dashedTitleId))
			{
				return currentTitle;
			}
		}

		throw new TitleNotFoundException("Title " + dashedTitleId + " not found!");
	}

	public Title getTitle(String gameId)
	{
		for (Title title : titleDatabase)
		{
			String productCode = title.getProductCode();

			if(productCode.length() <= 4)
			{
				continue;
			}

			String productCodePart = productCode.substring(productCode.length() - 4);

			if (productCodePart.equals(gameId.substring(0, gameId.length() - 2)))
			{
				return title;
			}
		}

		throw new TitleNotFoundException("Title not found for game id " + gameId + "!");
	}

	public void restore() throws IOException, SAXException, ParserConfigurationException
	{
		NodeList nodesList = getNodesList();

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
		TitleDatabaseUpdater.update(titleDatabase);
		store();
	}

	private NodeList getNodesList() throws ParserConfigurationException, SAXException, IOException
	{
		File xmlFile = new File(titleDatabaseFilePath);
		Document document = XMLHelper.getDocument(xmlFile);

		return document.getElementsByTagName(titleTagName);
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
}