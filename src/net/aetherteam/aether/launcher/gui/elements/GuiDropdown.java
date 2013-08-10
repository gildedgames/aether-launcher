package net.aetherteam.aether.launcher.gui.elements;

import java.awt.Font;

import net.aetherteam.aether.launcher.gui.forms.GuiForm;

import org.newdawn.slick.Color;

public class GuiDropdown extends GuiElement {

	private GuiButton[] elements;

	private GuiButton activeElement;

	public GuiDropdown(GuiForm form, int x, int y, int width, int height, Font font, String[] elements) {
		super(form, x, y, width, height);

		this.elements = new GuiButton[elements.length];

		for (int i = 0; i < elements.length; ++i) {
			GuiText text = new GuiText(this.form, font, elements[i]);
			GuiButton button = new GuiButton(this.form, x, y + (this.height * i), width, height, text);
			this.form.add(button);
			this.elements[i] = button;
		}

		this.activeElement = this.elements[0];
	}

	@Override
	public void setColor(Color color, Color hoveringColor) {
		for (GuiButton element : this.elements) {
			element.setColor(color, hoveringColor);
		}
	}

	@Override
	public void render() {
		boolean isActive = false;

		this.activeElement.y = this.y;
		this.activeElement.render();

		for (GuiButton element : this.elements) {
			if (element.isMouseHovering()) {
				isActive = true;
				break;
			}
		}

		int y = this.y + this.height;

		for (GuiButton element2 : this.elements) {
			GuiButton element = element2;

			if (element != this.activeElement) {
				element.y = y;
				element.shouldRender = isActive;

				y += this.height;
			}
		}
	}

	public void onElementClick(GuiElement clickedElement) {
		for (GuiButton element : this.elements) {
			if (element == clickedElement) {
				this.activeElement = (GuiButton) clickedElement;
			}
		}
	}

	public String getSelectedElement() {
		return this.activeElement.getText().getString();
	}
}
