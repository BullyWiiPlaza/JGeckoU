package wiiudev.gecko.client.gui.watch_list;

import org.w3c.dom.Element;
import wiiudev.gecko.client.XMLHelper;
import wiiudev.gecko.client.gui.AbstractXMLStorage;
import wiiudev.gecko.client.gui.MemoryPointerExpression;
import wiiudev.gecko.client.gui.code_list.code_wizard.selections.ValueSize;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.List;

public class WatchListStorage extends AbstractXMLStorage
{
	private static String nameTagName = "name";
	private static String addressExpressionTagName = "address";
	private static String valueSizeTagName = "size";

	public WatchListStorage(String baseFileName) throws IOException
	{
		super(baseFileName, "watches", "watch");
	}

	@Override
	protected void write(XMLStreamWriter xmlStreamWriter, WatchListElement watchListElement) throws XMLStreamException
	{
		xmlStreamWriter.writeStartElement(elementTagName);
		xmlStreamWriter.writeAttribute(nameTagName, watchListElement.getName());

		xmlStreamWriter.writeStartElement(addressExpressionTagName);
		xmlStreamWriter.writeCharacters(watchListElement.getAddressExpression().toString());
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement(valueSizeTagName);
		String valueSize = watchListElement.getValueSize().toString();
		xmlStreamWriter.writeCharacters(valueSize);
		xmlStreamWriter.writeEndElement();
	}

	@Override
	protected WatchListElement get(Element element)
	{
		String entryName = element.getAttribute(nameTagName);
		String addressExpression = XMLHelper.getText(element, addressExpressionTagName);
		String valueSize = XMLHelper.getText(element, valueSizeTagName);

		return new WatchListElement(entryName, new MemoryPointerExpression(addressExpression), ValueSize.get(valueSize));
	}

	@Override
	protected StringBuilder getStringBuilder(List<WatchListElement> watchListElements)
	{
		StringBuilder exportedWatchListBuilder = new StringBuilder();

		for (WatchListElement watchListElement : watchListElements)
		{
			String name = watchListElement.getName();
			exportedWatchListBuilder.append(name);
			exportedWatchListBuilder.append(System.lineSeparator());

			String addressExpression = watchListElement.getAddressExpression().toString();
			exportedWatchListBuilder.append(addressExpression);
			exportedWatchListBuilder.append(System.lineSeparator());
			exportedWatchListBuilder.append(System.lineSeparator());
		}

		return exportedWatchListBuilder;
	}
}