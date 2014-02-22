package net.aetherteam.aether.launcher.gui.elements;

import net.aetherteam.aether.launcher.gui.forms.GuiForm;
import net.aetherteam.aether.launcher.gui.utils.Sprite;

import org.lwjgl.opengl.GL11;

public class GuiButtonSprite extends GuiElement {

	private Sprite sprite;

	public GuiButtonSprite(GuiForm form, int x, int y, Sprite sprite) {
		super(form, x, y, sprite.getWidth(), sprite.getHeight());

		this.sprite = sprite;
	}

	@Override
	public void render() {
		super.render();

		if (this.isMouseHovering()) {
			this.hoveringColor.bind();
		} else {
			GL11.glColor4f(1, 1, 1, 1);
		}

		this.sprite.render(this.getFadingX(), this.y);

		GL11.glColor4f(1, 1, 1, 1);
	}

}
