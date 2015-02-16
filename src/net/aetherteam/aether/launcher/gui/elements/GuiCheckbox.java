package net.aetherteam.aether.launcher.gui.elements;

import net.aetherteam.aether.launcher.gui.LauncherDisplay;
import net.aetherteam.aether.launcher.gui.forms.GuiForm;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class GuiCheckbox extends GuiRectangle {

	private Color checkColor;

	private boolean isChecked;

	public GuiCheckbox(GuiForm form, int x, int y, int width, int height, boolean startChecked) {
		super(form, x, y, width, height);
		this.isChecked = startChecked;
	}

	public void setColor(Color color, Color hoveringColor, Color checkColor) {
		super.setColor(color, hoveringColor);
		this.checkColor = checkColor;
	}

	@Override
	public void render() {
		super.render();

		if (this.isChecked) {
			LauncherDisplay.instance.checkboxCheck.render(this.getFadingX(), this.getY());
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
