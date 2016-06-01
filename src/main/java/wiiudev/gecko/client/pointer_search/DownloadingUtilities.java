package wiiudev.gecko.client.pointer_search;

import org.apache.commons.io.FilenameUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class DownloadingUtilities
{
	public static void downloadAndExecute(String downloadURL) throws IOException
	{
		String fileName = getFileName(downloadURL);
		download(downloadURL);
		executeApplication(fileName);
	}

	/**
	 * Allows all SSL certificates for downloading files.
	 * @see <a href="http://stackoverflow.com/a/2893932/3764804" StackOverflow></a>
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static void trustAllCertificates() throws NoSuchAlgorithmException, KeyManagementException
	{
		TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager()
		{
			public X509Certificate[] getAcceptedIssuers()
			{
				return new X509Certificate[0];
			}

			public void checkClientTrusted(
					X509Certificate[] certs, String authType)
			{
			}

			public void checkServerTrusted(
					X509Certificate[] certs, String authType)
			{
			}
		}};

		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, trustManagers, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	}

	public static boolean canDownload(String filePath)
	{
		try
		{
			// Cause an exception if the program is already running
			if(new File(filePath).exists())
			{
				Files.delete(Paths.get(filePath));
			}

			return true;
		}
		catch(IOException ignored)
		{
			return false;
		}
	}

	public static void download(String downloadURL) throws IOException
	{
		URL website = new URL(downloadURL);
		String fileName = getFileName(downloadURL);

		try (InputStream inputStream = website.openStream())
		{
			Files.copy(inputStream, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public static String getFileName(String downloadURL)
	{
		String baseName = FilenameUtils.getBaseName(downloadURL);
		String extension = FilenameUtils.getExtension(downloadURL);
		String fileName = baseName + "." + extension;

		int questionMarkIndex = fileName.indexOf("?");
		if (questionMarkIndex != -1)
		{
			fileName = fileName.substring(0, questionMarkIndex);
		}

		fileName = fileName.replaceAll("-", "");

		return fileName;
	}

	public static void executeApplication(String filePath) throws IOException
	{
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("java", "-jar", filePath);
		processBuilder.start();
	}
}