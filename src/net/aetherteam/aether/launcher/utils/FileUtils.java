package net.aetherteam.aether.launcher.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class FileUtils {

	public static void writeStringToFile(File file, String data) throws IOException {
		OutputStream out = null;

		try {
			if (file.exists()) {
				if (file.isDirectory()) {
					throw new IOException("File '" + file + "' exists but is a directory");
				}
				if (!file.canWrite()) {
					throw new IOException("File '" + file + "' cannot be written to");
				}
			} else {
				File parent = file.getParentFile();
				if ((parent != null) && (!parent.mkdirs()) && (!parent.isDirectory())) {
					throw new IOException("Directory '" + parent + "' could not be created");
				}
			}

			out = new FileOutputStream(file, false);

			if (data != null) {
				out.write(data.getBytes(Charset.defaultCharset()));
			}

			out.close();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException localIOException) {
			}
		}
	}

	public static String readFileToString(File file) throws IOException {
		InputStream inputStream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder result = new StringBuilder();
		String line;

		while ((line = reader.readLine()) != null) {
			if (result.length() > 0) {
				result.append("\n");
			}
			result.append(line);
		}

		reader.close();

		return result.toString();
	}
}
