package net.aetherteam.aether.launcher.gui;

import java.io.IOException;
import java.nio.IntBuffer;

import net.aetherteam.aether.launcher.gui.utils.Tessellator;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Panorama {

	private Texture[] panoramaList;

	private int width;

	private int height;

	private int zLevel = 0;

	private float panoramaTimer;

	private int viewportTexture;

	public Panorama(String[] panoramaList) {
		this.width = 854;
		this.height = 480;

		this.panoramaList = new Texture[panoramaList.length];

		for (int i = 0; i < panoramaList.length; ++i) {
			try {
				this.panoramaList[i] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(panoramaList[i]));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.viewportTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.viewportTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 256, 256, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer) null);
	}

	public void render() {
		this.panoramaTimer += 0.6f;
		this.renderSkybox(0, 0, 0);
	}

	private void drawPanorama(int par1, int par2, float par3) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		this.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		byte b0 = 8;

		for (int k = 0; k < (b0 * b0); ++k) {
			GL11.glPushMatrix();
			float f1 = (((float) (k % b0) / (float) b0) - 0.5F) / 64.0F;
			float f2 = (((float) (k / b0) / (float) b0) - 0.5F) / 64.0F;
			float f3 = 0.0F;
			GL11.glTranslatef(f1, f2, f3);
			GL11.glRotatef((float) ((Math.sin((this.panoramaTimer + par3) / 400.0F) * 25.0F) + 20.0F), 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-(this.panoramaTimer + par3) * 0.1F, 0.0F, 1.0F, 0.0F);

			for (int l = 0; l < 6; ++l) {
				GL11.glPushMatrix();

				if (l == 1) {
					GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 2) {
					GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 3) {
					GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 4) {
					GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (l == 5) {
					GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				this.panoramaList[l].bind();
				tessellator.startDrawingQuads();
				tessellator.setColorRGBA_I(16777215, 255 / (k + 1));
				float f4 = 0.0F;
				tessellator.addVertexWithUV(-1.0D, -1.0D, 1.0D, 0.0F + f4, 0.0F + f4);
				tessellator.addVertexWithUV(1.0D, -1.0D, 1.0D, 1.0F - f4, 0.0F + f4);
				tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, 1.0F - f4, 1.0F - f4);
				tessellator.addVertexWithUV(-1.0D, 1.0D, 1.0D, 0.0F + f4, 1.0F - f4);
				tessellator.draw();
				GL11.glPopMatrix();
			}

			GL11.glPopMatrix();
			GL11.glColorMask(true, true, true, false);
		}

		tessellator.setTranslation(0.0D, 0.0D, 0.0D);
		GL11.glColorMask(true, true, true, true);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	private void rotateAndBlurSkybox(float par1) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.viewportTexture);
		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColorMask(true, true, true, false);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		byte b0 = 3;

		for (int i = 0; i < b0; ++i) {
			tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (i + 1));
			int j = this.width;
			int k = this.height;
			float f1 = (i - (b0 / 2)) / 256.0F;
			tessellator.addVertexWithUV(j, k, this.zLevel, 0.0F + f1, 0.0D);
			tessellator.addVertexWithUV(j, 0.0D, this.zLevel, 1.0F + f1, 0.0D);
			tessellator.addVertexWithUV(0.0D, 0.0D, this.zLevel, 1.0F + f1, 1.0D);
			tessellator.addVertexWithUV(0.0D, k, this.zLevel, 0.0F + f1, 1.0D);
		}

		tessellator.draw();
		GL11.glColorMask(true, true, true, true);
	}

	private void renderSkybox(int par1, int par2, float par3) {
		GL11.glViewport(0, 0, 256, 256);
		this.drawPanorama(par1, par2, par3);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		float f1 = this.width > this.height ? 120.0F / this.width : 120.0F / this.height;
		float f2 = (this.height * f1) / 256.0F;
		float f3 = (this.width * f1) / 256.0F;
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
		int k = this.width;
		int l = this.height;
		tessellator.addVertexWithUV(0.0D, l, this.zLevel, 0.5F - f2, 0.5F + f3);
		tessellator.addVertexWithUV(k, l, this.zLevel, 0.5F - f2, 0.5F - f3);
		tessellator.addVertexWithUV(k, 0.0D, this.zLevel, 0.5F + f2, 0.5F - f3);
		tessellator.addVertexWithUV(0.0D, 0.0D, this.zLevel, 0.5F + f2, 0.5F + f3);
		tessellator.draw();
	}

	void gluPerspective(double fovY, double aspect, double zNear, double zFar) {
		double pi = 3.1415926535897932384626433832795;
		double fW, fH;

		fH = Math.tan((fovY / 360) * pi) * zNear;
		fW = fH * aspect;

		GL11.glFrustum(-fW, fW, -fH, fH, zNear, zFar);
	}

}
