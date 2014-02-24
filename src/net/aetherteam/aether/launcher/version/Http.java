package net.aetherteam.aether.launcher.version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import net.aetherteam.aether.launcher.Launcher;
import net.aetherteam.aether.launcher.utils.Utils;

public class Http {

	public static String buildQuery(Map<String, Object> query) {
		StringBuilder builder = new StringBuilder();

		for (Map.Entry<String, Object> entry : query.entrySet()) {
			if (builder.length() > 0) {
				builder.append('&');
			}
			try {
				builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				Launcher.getInstance().println("Unexpected exception building query", e);
			}

			if (entry.getValue() != null) {
				builder.append('=');
				try {
					builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					Launcher.getInstance().println("Unexpected exception building query", e);
				}
			}
		}

		return builder.toString();
	}

	public static String performPost(URL url, Map<String, Object> query, Proxy proxy) throws IOException {
		return Utils.performPost(url, buildQuery(query), proxy, "application/x-www-form-urlencoded", false);
	}

	public static String performGet(URL url, Proxy proxy) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
		connection.setRequestMethod("GET");

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		StringBuilder response = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}

		reader.close();
		return response.toString();
	}

	public static URL concatenateURL(URL url, String args) throws MalformedURLException {
		if ((url.getQuery() != null) && (url.getQuery().length() > 0)) {
			return new URL(url.getProtocol(), url.getHost(), url.getFile() + "?" + args);
		}
		return new URL(url.getProtocol(), url.getHost(), url.getFile() + "&" + args);
	}
}
