package wiiudev.gecko.client.memory_search;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import wiiudev.gecko.client.conversions.Conversions;
import wiiudev.gecko.client.gui.utilities.XMLHelper;
import wiiudev.gecko.client.memory_search.enumerations.ValueSize;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ResultsStorage
{
	private String filePath;

	public ResultsStorage(String filePath) throws Exception
	{
		this.filePath = filePath;
	}

	public SearchBackup readResults() throws Exception
	{
		File xmlFile = new File(filePath);
		Document document = XMLHelper.getDocument(xmlFile);

		String valueSizeText = getElementText(document, "value_size");
		ValueSize valueSize = ValueSize.parse(valueSizeText);

		int startingAddress = Conversions.toDecimal(getElementText(document, "start_address"));
		int length = Conversions.toDecimal(getElementText(document, "end_address")) - startingAddress;
		SearchBounds searchBounds = new SearchBounds(startingAddress, length);

		List<SearchResult> searchResults = new ArrayList<>();
		NodeList results = getNodesList("result");

		for (int nodeIndex = 0; nodeIndex < results.getLength(); nodeIndex++)
		{
			Node node = results.item(nodeIndex);

			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;

				int address = Conversions.toDecimal(element.getAttribute("address"));
				BigInteger previous = new BigInteger(XMLHelper.getText(element, "previous_value"), 16);
				BigInteger current = new BigInteger(XMLHelper.getText(element, "current_value"), 16);

				SearchResult searchResult = new SearchResult(address, previous, current, valueSize);
				searchResults.add(searchResult);
			}
		}

		return new SearchBackup(searchBounds, valueSize, searchResults);
	}

	private String getElementText(Document document, String tagName)
	{
		Element element = (Element) document.getElementsByTagName(tagName).item(0);

		return element.getTextContent();
	}

	private NodeList getNodesList(String tagName) throws ParserConfigurationException, SAXException, IOException
	{
		File xmlFile = new File(filePath);
		Document document = XMLHelper.getDocument(xmlFile);

		return document.getElementsByTagName(tagName);
	}

	public void writeResults(List<SearchResult> searchResults, SearchBounds searchBounds) throws Exception
	{
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

		xMLStreamWriter.writeStartDocument();

		String resultsElementTagName = "memory_search";
		xMLStreamWriter.writeStartElement(resultsElementTagName);

		xMLStreamWriter.writeStartElement("settings");
		xMLStreamWriter.writeStartElement("start_address");
		xMLStreamWriter.writeCharacters(Conversions.toHexadecimal(searchBounds.getAddress()));
		xMLStreamWriter.writeEndElement();
		xMLStreamWriter.writeStartElement("end_address");
		xMLStreamWriter.writeCharacters(Conversions.toHexadecimal(searchBounds.getAddress() + searchBounds.getLength()));
		xMLStreamWriter.writeEndElement();
		xMLStreamWriter.writeStartElement("value_size");
		xMLStreamWriter.writeCharacters(searchResults.get(0).getValueSize().toString());
		xMLStreamWriter.writeEndElement();
		xMLStreamWriter.writeEndElement();

		xMLStreamWriter.writeStartElement("results");

		for (SearchResult searchResult : searchResults)
		{
			xMLStreamWriter.writeStartElement("result");
			xMLStreamWriter.writeAttribute("address", Conversions.toHexadecimal(searchResult.getAddress()));

			xMLStreamWriter.writeStartElement("previous_value");
			xMLStreamWriter.writeCharacters(Conversions.toHexadecimal(searchResult.getPreviousValue(), searchResult.getValueSize()));
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeStartElement("current_value");
			xMLStreamWriter.writeCharacters(Conversions.toHexadecimal(searchResult.getCurrentValue(), searchResult.getValueSize()));
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeEndElement();
		}

		xMLStreamWriter.writeEndElement();

		xMLStreamWriter.writeEndDocument();
		filePath = forceExtension(filePath, "xml");
		XMLHelper.writeFile(stringWriter, filePath);
	}

	private String forceExtension(String filePath, String extension)
	{
		if (!filePath.toLowerCase().endsWith("." + extension))
		{
			filePath += "." + extension;
		}

		return filePath;
	}

	public String getFilePath()
	{
		return filePath;
	}
}
