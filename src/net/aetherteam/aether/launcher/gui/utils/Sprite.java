package net.aetherteam.aether.launcher.gui.utils;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class Sprite {

	private Texture texture;

	public Sprite(Texture texture) {
		this.texture = texture;
	}

	public void render(int x, int y) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		this.texture.bind();

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(x + this.texture.getTextureWidth(), y);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(x + this.texture.getTextureWidth(), y + this.texture.getTextureHeight());
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(x, y + this.texture.getTextureHeight());
		GL11.glEnd();
	}

	public int getWidth() {
		return this.texture.getImageWidth();
	}

	public int getHeight() {
		return this.texture.getImageHeight();
	}
}
