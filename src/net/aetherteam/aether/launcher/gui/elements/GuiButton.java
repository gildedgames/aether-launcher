package net.aetherteam.aether.launcher.gui.elements;

import net.aetherteam.aether.launcher.gui.forms.GuiForm;

public class GuiButton extends GuiRectangle {

	protected GuiText text;

	public GuiButton(GuiForm form, int x, int y, int width, int height, GuiText text) {
		super(form, x, y, width, height);
		
		this.text = text;
	}

	@Override
	public void render() {
		super.render();

		this.text.render(this.x + ((this.width - this.text.getWidth()) / 2), this.getY() + ((this.height - this.text.getHeight()) / 2));
	}

	public GuiText getText() {
		return this.text;
	}

}
