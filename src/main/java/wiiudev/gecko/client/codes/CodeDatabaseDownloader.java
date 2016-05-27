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
	private String codesDatabaseURL;
	private String gameId;
	private int availableCodesCount;
	private static String lineBreak = "\n";

	public CodeDatabaseDownloader(String gameId) throws Exception
	{
		this.gameId = gameId;
		String codeDatabaseBaseURL = "http://wiiucodes.tk/codes.php?id=";
		String titleId = Title.getTitleId(gameId);
		codesDatabaseURL = codeDatabaseBaseURL + titleId;
		webDocument = Jsoup.connect(codesDatabaseURL).get();
		setAvailableCodesCount();
	}

	public String getCodeDatabaseURL()
	{
		return codesDatabaseURL;
	}

	public boolean codesExist() throws Exception
	{
		return availableCodesCount > 0;
	}

	public List<GeckoCode> downloadCodes() throws IOException
	{
		List<GeckoCode> codes = new ArrayList<>();
		Elements elements = webDocument.select("body");
		String bodyHTML = elements.toString();
		bodyHTML = stripHeaderAndFooter(bodyHTML);
		String plainText = parsePlainTextFromHTML(bodyHTML);
		String[] parsedCodes = plainText.split(lineBreak + lineBreak);

		for(String parsedCode : parsedCodes)
		{
			GeckoCode code = parseCode(parsedCode);
			codes.add(code);
		}

		return codes;
	}

	private GeckoCode parseCode(String parsedCode)
	{
		String[] codeLines = parsedCode.split(lineBreak);
		GeckoCode code = new GeckoCode();
		StringBuilder codeBuilder = new StringBuilder();
		StringBuilder commentBuilder = new StringBuilder();

		for(int codeLineIndex = 0; codeLineIndex < codeLines.length; codeLineIndex++)
		{
			String codeLine = codeLines[codeLineIndex];

			if(codeLineIndex == 0)
			{
				// The first line is always the title
				code.setTitle(codeLine);
			}
			else
			{
				try
				{
					// Check if the cheat code is valid
					new CheatCode(codeLine);
					codeBuilder.append(codeLine);
					codeBuilder.append(lineBreak);
				}
				catch(InvalidCheatCodeException invalidCheatCodeException)
				{
					// Otherwise attachTo it as a comment
					commentBuilder.append(codeLine);
					commentBuilder.append(lineBreak);
				}
			}
		}

		String builtCode = codeBuilder.toString().trim();

		if(builtCode.equals(""))
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
			public void handleText(final char[] data, final int pos)
			{
				String string = new String(data);
				stringBuilder.append(string.trim());
			}

			@Override
			public void handleStartTag(final HTML.Tag t, final MutableAttributeSet a, final int pos)
			{
				if (t == HTML.Tag.DIV || t == HTML.Tag.BR || t == HTML.Tag.P)
				{
					stringBuilder.append("\n");
				}
			}

			@Override
			public void handleSimpleTag(final HTML.Tag t, final MutableAttributeSet a, final int pos)
			{
				handleStartTag(t, a, pos);
			}
		};

		new ParserDelegator().parse(new StringReader(html), parserCallback, false);

		return stringBuilder.toString().trim();
	}

	private void setAvailableCodesCount()
	{
		Elements codeListStatistics = webDocument.select("body > p");
		String codeStatistics = codeListStatistics.toString();
		codeStatistics = codeStatistics.replace("<p>Amount: ", "");
		int index = codeStatistics.indexOf(" ");
		codeStatistics = codeStatistics.substring(0, index);
		availableCodesCount = Integer.parseInt(codeStatistics);
	}

	public int getAvailableCodesCount()
	{
		return availableCodesCount;
	}

	public String getGameId()
	{
		return gameId;
	}
}