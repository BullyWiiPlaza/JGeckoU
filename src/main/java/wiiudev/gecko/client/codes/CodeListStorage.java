package wiiudev.gecko.client.codes;

import org.w3c.dom.Document;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CodeListStorage
{
	private static String codeListEntryTagName = "entry";
	private static String entryIdentifierAttribute = "name";
	private static String codeAttributeTagName = "code";
	private static String codeAttributeCommentTagName = "comment";
	private static String codeAttributeEnabledTagName = "enabled";
	public static String codesDirectory = "codes";
	public static String fileExtension = "xml";

	private String codeListFilePath;

	public CodeListStorage(String codeListFilePath) throws IOException
	{
		String desiredExtension = "." + fileExtension;

		if (!codeListFilePath.endsWith(desiredExtension))
		{
			codeListFilePath = codeListFilePath.concat(desiredExtension);
		}

		createCodesDirectory();
		codeListFilePath = codesDirectory + File.separator + codeListFilePath;

		this.codeListFilePath = codeListFilePath;
		createCodeListFile();
	}

	private void createCodesDirectory() throws IOException
	{
		Path path = Paths.get(codesDirectory);
		Files.createDirectories(path);
	}

	private void createCodeListFile() throws IOException
	{
		Path path = Paths.get(codeListFilePath);

		if (!Files.exists(path))
		{
			Files.createFile(path);
		}
	}

	public List<GeckoCode> getCodeList() throws IOException, SAXException, ParserConfigurationException
	{
		List<GeckoCode> codesList = new ArrayList<>();

		boolean isEmpty = new File(codeListFilePath).length() == 0;

		// Do not try to parse a new file
		if (!isEmpty)
		{
			NodeList nodesList = getNodesList();

			for (int nodeIndex = 0; nodeIndex < nodesList.getLength(); nodeIndex++)
			{
				GeckoCode geckoCode = new GeckoCode();
				Node node = nodesList.item(nodeIndex);

				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) node;

					String entryName = element.getAttribute(entryIdentifierAttribute);
					String codeName = XMLHelper.getText(element, codeAttributeTagName);
					String codeComment = XMLHelper.getText(element, codeAttributeCommentTagName);
					boolean codeEnabled = XMLHelper.getText(element, codeAttributeEnabledTagName).equals("true");

					geckoCode.setTitle(entryName);
					geckoCode.setCode(codeName);
					geckoCode.setComment(codeComment);
					geckoCode.setEnabled(codeEnabled);
				}

				codesList.add(geckoCode);
			}
		}

		return codesList;
	}

	private NodeList getNodesList() throws ParserConfigurationException, SAXException, IOException
	{
		File xmlFile = new File(codeListFilePath);
		Document document = XMLHelper.getDocument(xmlFile);
		return document.getElementsByTagName(codeListEntryTagName);
	}

	public String writeCodeList(List<GeckoCode> codeList) throws Exception
	{
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

		xMLStreamWriter.writeStartDocument();
		String rootElementName = "codes";
		xMLStreamWriter.writeStartElement(rootElementName);

		for (GeckoCode geckoCode : codeList)
		{
			xMLStreamWriter.writeStartElement(codeListEntryTagName);
			xMLStreamWriter.writeAttribute(entryIdentifierAttribute, geckoCode.getTitle());

			xMLStreamWriter.writeStartElement(codeAttributeTagName);
			xMLStreamWriter.writeCharacters(geckoCode.getCode());
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeStartElement(codeAttributeCommentTagName);
			xMLStreamWriter.writeCharacters(geckoCode.getComment());
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeStartElement(codeAttributeEnabledTagName);
			String enabled = geckoCode.isEnabled() ? "true" : "false";
			xMLStreamWriter.writeCharacters(enabled);
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeEndElement();
		}

		xMLStreamWriter.writeEndDocument();
		XMLHelper.writeFile(stringWriter, codeListFilePath);

		return codeListFilePath;
	}

	public String exportCodeList(List<GeckoCode> codeList, String fileName) throws IOException
	{
		String targetFilePath = codesDirectory + File.separator + fileName + ".txt";
		String exportedCodeList = getExportedCodeList(codeList);
		writeToFile(exportedCodeList, targetFilePath);

		return targetFilePath;
	}

	private String getExportedCodeList(List<GeckoCode> codeList)
	{
		StringBuilder exportedCodeListBuilder = new StringBuilder();

		for (GeckoCode code : codeList)
		{
			exportedCodeListBuilder.append(code.toString());
			exportedCodeListBuilder.append(System.lineSeparator());
			exportedCodeListBuilder.append(System.lineSeparator());
		}

		return exportedCodeListBuilder.toString().trim();
	}

	private static void writeToFile(String text, String targetFilePath) throws IOException
	{
		Path targetPath = Paths.get(targetFilePath);
		byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
		Files.write(targetPath, bytes);
	}
}