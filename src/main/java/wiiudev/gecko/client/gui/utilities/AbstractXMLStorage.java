package wiiudev.gecko.client.gui.utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import wiiudev.gecko.client.XMLHelper;
import wiiudev.gecko.client.gui.tabs.watch_list.WatchListElement;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractXMLStorage
{
	private String fileName;
	private String rootElementName;
	protected String elementTagName;

	public AbstractXMLStorage(String baseFileName, String storageDirectory, String elementTagName) throws IOException
	{
		if (Files.exists(Paths.get(storageDirectory)))
		{
			Files.createDirectories(Paths.get(storageDirectory));
		}

		String forcedExtension = ".xml";
		baseFileName = baseFileName + forcedExtension;
		this.fileName = storageDirectory + File.separator + baseFileName;
		rootElementName = storageDirectory;
		this.elementTagName = elementTagName;
	}

	private NodeList getNodesList() throws ParserConfigurationException, SAXException, IOException
	{
		File xmlFile = new File(fileName);
		Document document = XMLHelper.getDocument(xmlFile);
		return document.getElementsByTagName(elementTagName);
	}

	public String store(List<WatchListElement> watchListElements) throws Exception
	{
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);

		xmlStreamWriter.writeStartDocument();
		xmlStreamWriter.writeStartElement(rootElementName);

		for (WatchListElement watchListElement : watchListElements)
		{
			write(xmlStreamWriter, watchListElement);

			xmlStreamWriter.writeEndElement();
		}

		xmlStreamWriter.writeEndDocument();
		XMLHelper.writeFile(stringWriter, fileName);

		return fileName;
	}

	protected abstract void write(XMLStreamWriter xmlStreamWriter, WatchListElement watchListElement) throws XMLStreamException;

	public List<WatchListElement> restore() throws Exception
	{
		boolean isEmpty = new File(fileName).length() == 0;

		// Do not try to parse a new file
		if (!isEmpty)
		{
			List<WatchListElement> watchListElements = new ArrayList<>();
			NodeList nodesList = getNodesList();

			for (int nodeIndex = 0; nodeIndex < nodesList.getLength(); nodeIndex++)
			{
				Node node = nodesList.item(nodeIndex);

				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) node;

					WatchListElement watchListElement = get(element);
					watchListElements.add(watchListElement);
				}
			}

			return watchListElements;
		}

		return null;
	}

	public String export(List<WatchListElement> watchListElements) throws IOException
	{
		String exportedWatchList = getStringBuilder(watchListElements).toString().trim();
		String exportedPath = fileName.substring(0, fileName.length() - 4) + ".txt";
		Files.write(Paths.get(exportedPath), exportedWatchList.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

		return exportedPath;
	}

	protected abstract StringBuilder getStringBuilder(List<WatchListElement> watchListElements);

	protected abstract WatchListElement get(Element element);
}