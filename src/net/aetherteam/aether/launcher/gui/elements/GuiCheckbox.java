package net.aetherteam.aether.launcher.gui.elements;

import net.aetherteam.aether.launcher.gui.forms.GuiForm;

import org.newdawn.slick.Color;

public class GuiCheckbox extends GuiRectangle {

	private Color checkColor;

	private boolean isChecked;

	public GuiCheckbox(GuiForm form, int x, int y, int width, int height) {
		super(form, x, y, width, height);
	}

	public void setColor(Color color, Color hoveringColor, Color checkColor) {
		super.setColor(color, hoveringColor);
		this.checkColor = checkColor;
	}

	@Override
	public void render() {
		super.render();

		if (this.isChecked) {
			GuiElement.renderColoredRect(this.getFadingX() + 4, this.getY() + 4, this.width - 8, this.height - 8, this.checkColor);
		}
	}

	@Override
	public void onMouseClick() {
		this.isChecked = !this.isChecked;
	}

	public boolean isChecked() {
		return this.isChecked;
	}

}
