package net.aetherteam.aether.launcher.gui.utils;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class BufferTexture implements Texture {

	private int textureId;

	private int width;

	private int height;

	public BufferTexture(BufferedImage image) {
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[(y * image.getWidth()) + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}

		buffer.flip();

		this.width = image.getWidth();
		this.height = image.getHeight();

		this.textureId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	}

	@Override
	public void bind() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
	}

	@Override
	public float getHeight() {
		return this.width;
	}

	@Override
	public int getImageHeight() {
		return this.height;
	}

	@Override
	public int getImageWidth() {
		return this.width;
	}

	@Override
	public byte[] getTextureData() {
		return null;
	}

	@Override
	public int getTextureHeight() {
		return this.height;
	}

	@Override
	public int getTextureID() {
		return this.textureId;
	}

	@Override
	public String getTextureRef() {
		return null;
	}

	@Override
	public int getTextureWidth() {
		return this.width;
	}

	@Override
	public float getWidth() {
		return this.width;
	}

	@Override
	public boolean hasAlpha() {
		return false;
	}

	@Override
	public void release() {
		GL11.glDeleteTextures(this.textureId);
	}

	@Override
	public void setTextureFilter(int arg0) {
	}

}
