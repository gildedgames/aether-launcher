package net.aetherteam.aether.launcher.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

public class UnpackUtil {
	public static void unpackLibrary(File output, byte[] data)
			throws IOException {
		if (output.exists()) {
			output.delete();
		}

		byte[] decompressed = readFully(new XZCompressorInputStream(
				new ByteArrayInputStream(data)));

		String end = new String(decompressed, decompressed.length - 4, 4);
		if (!end.equals("SIGN")) {
			System.out.println("Unpacking failed, signature missing " + end);
			return;
		}

		int x = decompressed.length;
		int len = decompressed[(x - 8)] & 0xFF
				| (decompressed[(x - 7)] & 0xFF) << 8
				| (decompressed[(x - 6)] & 0xFF) << 16
				| (decompressed[(x - 5)] & 0xFF) << 24;

		byte[] checksums = Arrays.copyOfRange(decompressed, decompressed.length
				- len - 8, decompressed.length - 8);

		FileOutputStream jarBytes = new FileOutputStream(output);
		JarOutputStream jos = new JarOutputStream(jarBytes);

		Pack200.newUnpacker().unpack(new ByteArrayInputStream(decompressed),
				jos);

		jos.putNextEntry(new JarEntry("checksums.sha1"));
		jos.write(checksums);
		jos.closeEntry();

		jos.close();
		jarBytes.close();
	}

	public static byte[] readFully(InputStream stream) throws IOException {
		byte[] data = new byte[4096];
		ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();
		int len;
		do {
			len = stream.read(data);
			if (len > 0) {
				entryBuffer.write(data, 0, len);
			}
		} while (len != -1);

		return entryBuffer.toByteArray();
	}
}
