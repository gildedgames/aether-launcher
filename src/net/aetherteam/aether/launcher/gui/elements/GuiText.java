package net.aetherteam.aether.launcher.gui.elements;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import net.aetherteam.aether.launcher.gui.forms.GuiForm;
import net.aetherteam.aether.launcher.gui.utils.BufferTexture;
import net.aetherteam.aether.launcher.gui.utils.GaussianFilter;
import net.aetherteam.aether.launcher.gui.utils.Sprite;

import org.newdawn.slick.Color;

public class GuiText extends GuiElement {

	private Font font;

	private FontMetrics fontMetrics;

	private boolean applyShadow;

	private String text;

	private Color shadowColor;

	private Sprite cachedText;

	private Sprite cachedTextShadow;

	private boolean isDirty;

	public GuiText(GuiForm form, Font font, String text, Color color, Color shadowColor, boolean applyShadow) {
		super(form, color);
		this.font = font;
		this.text = text;
		this.shadowColor = shadowColor;
		this.applyShadow = applyShadow;

		this.fontMetrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics().getFontMetrics(this.font);
		this.isDirty = true;
	}

	public GuiText(GuiForm form, Font font, String text) {
		this(form, font, text, Color.white, Color.black, true);
	}

	@Override
	public void render(int x, int y) {
		super.render(x, y);

		if (this.text.isEmpty()) {
			return;
		}

		if (this.isDirty) {
			BufferedImage fontImage = new BufferedImage(this.getWidth() + 4, this.getHeight() + 4, BufferedImage.TYPE_INT_ARGB);
			Graphics2D gt = (Graphics2D) fontImage.getGraphics();

			gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			gt.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			gt.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			gt.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			gt.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

			gt.setFont(this.font);
			gt.setColor(java.awt.Color.WHITE);
			gt.drawString(this.text, 0, this.fontMetrics.getAscent());

			if (this.applyShadow) {
				BufferedImage blurredImage = new BufferedImage(this.getWidth() + 4, this.getHeight() + 4, BufferedImage.TYPE_INT_ARGB);

				GaussianFilter gaussianFilter = new GaussianFilter(7);
				gaussianFilter.filter(fontImage, blurredImage);

				blurredImage.getGraphics().drawImage(fontImage, 0, 0, null);
				this.cachedTextShadow = new Sprite(new BufferTexture(blurredImage));
			}

			this.cachedText = new Sprite(new BufferTexture(fontImage));

			this.isDirty = false;
		}

		if (this.applyShadow) {
			Color col = new Color(this.shadowColor.r, this.shadowColor.g, this.shadowColor.b, this.shadowColor.a * this.form.getFade());
			col.bind();
			this.cachedTextShadow.render(this.getFadingX(), y);
		}

		this.getColor().bind();
		this.cachedText.render(this.getFadingX(), y);
	}

	public String getString() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
		this.isDirty = true;
	}

	@Override
	public int getWidth() {
		return this.fontMetrics.stringWidth(this.text);
	}

	@Override
	public int getHeight() {
		return this.fontMetrics.getHeight();
	}

	public Font getFont() {
		return this.font;
	}

}
