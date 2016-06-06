package wiiudev.gecko.client.codes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import wiiudev.gecko.client.gui.code_list.code_wizard.CodeWizardDialog;
import wiiudev.gecko.client.titles.Title;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CodeDatabaseDownloader
{
	private Document webDocument;
	private static String lineBreak = "\n";
	private List<GeckoCode> codes;

	public CodeDatabaseDownloader(String gameId) throws Exception
	{
		String codeDatabaseBaseURL = "http://wiiucodes.tk/codes.php?id=";
		String titleId = Title.getTitleId(gameId);
		String codesDatabaseURL = codeDatabaseBaseURL + titleId;
		webDocument = Jsoup.connect(codesDatabaseURL).get();
		codes = new ArrayList<>();
		parseCodes();
	}

	public void parseCodes() throws IOException
	{
		Elements elements = webDocument.select("body");
		String bodyHTML = elements.toString();
		bodyHTML = stripHeaderAndFooter(bodyHTML);
		String plainText = parsePlainTextFromHTML(bodyHTML);
		String[] parsedCodes = plainText.split(lineBreak + lineBreak);

		for (String parsedCode : parsedCodes)
		{
			// No codes found?
			if(parsedCode.contains("No codes found."))
			{
				return;
			}

			GeckoCode code = parseCode(parsedCode);
			codes.add(code);
		}
	}

	public List<GeckoCode> getCodes()
	{
		return codes;
	}

	private GeckoCode parseCode(String parsedCode)
	{
		String[] codeLines = parsedCode.split(lineBreak);
		GeckoCode code = new GeckoCode();
		StringBuilder codeBuilder = new StringBuilder();
		StringBuilder commentBuilder = new StringBuilder();

		for (int codeLineIndex = 0; codeLineIndex < codeLines.length; codeLineIndex++)
		{
			String codeLine = codeLines[codeLineIndex];

			if (codeLineIndex == 0)
			{
				// The first line is always the title
				code.setTitle(codeLine);
			} else
			{
				try
				{
					// Check if the cheat code is valid
					new CheatCode(codeLine);
					codeBuilder.append(codeLine);
					codeBuilder.append(lineBreak);
				} catch (InvalidCheatCodeException invalidCheatCodeException)
				{
					// Otherwise add it as a comment
					commentBuilder.append(codeLine);
					commentBuilder.append(lineBreak);
				}
			}
		}

		String builtCode = codeBuilder.toString().trim();

		if (builtCode.equals(""))
		{
			// No valid code given, let's use no operation
			builtCode = CodeWizardDialog.NOP_CODE;
		}

		code.setCode(builtCode);

		String builtComment = commentBuilder.toString().trim();
		code.setComment(builtComment);

		return code;
	}

	private String stripHeaderAndFooter(String bodyHTML)
	{
		int separatorLineIndex = bodyHTML.indexOf("<hr>");
		bodyHTML = bodyHTML.substring(separatorLineIndex);
		separatorLineIndex = bodyHTML.lastIndexOf("<hr>");
		bodyHTML = bodyHTML.substring(0, separatorLineIndex);
		return bodyHTML;
	}

	private String parsePlainTextFromHTML(String html) throws IOException
	{
		StringBuilder stringBuilder = new StringBuilder();

		HTMLEditorKit.ParserCallback parserCallback = new HTMLEditorKit.ParserCallback()
		{
			@Override
			public void handleText(char[] data, int pos)
			{
				String string = new String(data);
				stringBuilder.append(string.trim());
			}

			@Override
			public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos)
			{
				if (t == HTML.Tag.DIV || t == HTML.Tag.BR || t == HTML.Tag.P)
				{
					stringBuilder.append("\n");
				}
			}

			@Override
			public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos)
			{
				handleStartTag(t, a, pos);
			}
		};

		new ParserDelegator().parse(new StringReader(html), parserCallback, false);

		return stringBuilder.toString().trim();
	}
}