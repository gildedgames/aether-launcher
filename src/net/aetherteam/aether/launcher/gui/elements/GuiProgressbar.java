package net.aetherteam.aether.launcher.gui.elements;

import java.awt.Font;

import net.aetherteam.aether.launcher.gui.forms.GuiForm;

public class GuiProgressbar extends GuiElement {

	private float progress;

	private int border;
	
	private Font font = new Font("Athelas", Font.BOLD, 18);

	public GuiProgressbar(GuiForm form, int x, int y, int width, int height) {
		super(form, x, y, width, height);
	}

	@Override
	public void render() {
		GuiElement.renderColoredRect(this.getFadingX(), this.y, this.width, this.height, this.getColor());
		GuiElement.renderColoredRect(this.getFadingX() + this.border, this.y + this.border, (int) (this.progress * (this.width - (this.border * 2))), this.height - (this.border * 2), this.getHoveringColor());
		GuiText progressLabel = new GuiText(this.form, font, Math.round(progress * 100) + "%");
		progressLabel.render((this.x + this.width / 2) - progressLabel.getWidth() / 2, (this.y + this.height / 2) - progressLabel.getHeight() / 2);
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	public void reset() {
		this.progress = 0.f;
	}

}
