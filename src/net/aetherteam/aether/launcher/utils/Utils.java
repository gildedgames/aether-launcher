package net.aetherteam.aether.launcher.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;

public class Utils {

	public static String performPost(URL url, String parameters, Proxy proxy, String contentType, boolean returnErrorPage) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
		byte[] paramAsBytes = parameters.getBytes(Charset.forName("UTF-8"));

		connection.setConnectTimeout(15000);
		connection.setReadTimeout(15000);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");

		connection.setRequestProperty("Content-Length", "" + paramAsBytes.length);
		connection.setRequestProperty("Content-Language", "en-US");

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
		writer.write(paramAsBytes);
		writer.flush();
		writer.close();

		BufferedReader reader;

		try {
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} catch (IOException e) {
			if (returnErrorPage) {
				InputStream stream = connection.getErrorStream();

				if (stream != null) {
					reader = new BufferedReader(new InputStreamReader(stream));
				} else {
					throw e;
				}
			} else {
				throw e;
			}
		}

		StringBuilder response = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}

		reader.close();
		return response.toString();
	}

	public static URL constantURL(String input) {
		try {
			return new URL(input);
		} catch (MalformedURLException e) {
			throw new Error(e);
		}
	}

}
