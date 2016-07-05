package wiiudev.gecko.client;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class XMLHelper
{
	public static final Charset ENCODING = StandardCharsets.UTF_8;

	public static void writeFile(StringWriter writer, String targetFilePath) throws Exception
	{
		String text = writer.getBuffer().toString();
		text = XMLHelper.format(text);
		writeFile(text, targetFilePath);
	}

	public static NodeList getNodesList(String filePath, String tagName) throws ParserConfigurationException, SAXException, IOException
	{
		File xmlFile = new File(filePath);
		Document document = getDocument(xmlFile);

		return document.getElementsByTagName(tagName);
	}

	public static void writeFile(String text, String targetFilePath) throws IOException
	{
		File titleDatabaseFile = new File(targetFilePath);
		FileUtils.writeStringToFile(titleDatabaseFile, text, ENCODING);
	}

	public static Document getDocument(File xmlFile) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream inputStream = new FileInputStream(xmlFile);
		Reader reader = new InputStreamReader(inputStream, ENCODING);
		InputSource inputSource = new InputSource(reader);
		inputSource.setEncoding(ENCODING.displayName());
		return documentBuilder.parse(inputSource);
	}

	public static String getText(Element element, String tagName)
	{
		return element.getElementsByTagName(tagName).item(0).getTextContent();
	}

	public static String format(String xml) throws Exception
	{
		InputSource src = new InputSource(new StringReader(xml));
		Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
		boolean keepDeclaration = xml.startsWith("<?xml");
		DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
		DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
		LSSerializer writer = impl.createLSSerializer();
		writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
		writer.getDomConfig().setParameter("xml-declaration", keepDeclaration);

		return writer.writeToString(document);
	}
}
