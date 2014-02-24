package net.aetherteam.aether.launcher.gui.elements;

import java.awt.Font;

import net.aetherteam.aether.launcher.gui.forms.GuiForm;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;

public class GuiDropdown extends GuiButton {

	private String[] elements;

	private int selectedElement;

	public GuiDropdown(GuiForm form, int x, int y, int width, int height, Font font, String[] elements) {
		super(form, x, y, width, height, new GuiText(form, font, elements[0]));

		this.elements = elements;
	}

	@Override
	public void render() {
		super.render();

		Color arrowColor = new Color(0, 0, 0, 0.3F);
		Color hoveringArrowColor = new Color(0, 0, 0, 0.6F);

		Color leftArrowColor = null;
		Color rightArrowColor = null;

		if (this.isMouseHovering()) {
			if (Mouse.getX() < (this.getFadingX() + (this.width / 2))) {
				leftArrowColor = hoveringArrowColor;
				rightArrowColor = arrowColor;
			} else {
				leftArrowColor = arrowColor;
				rightArrowColor = hoveringArrowColor;
			}
		} else {
			leftArrowColor = arrowColor;
			rightArrowColor = arrowColor;
		}

		GuiElement.renderArrow(this.getFadingX() + 5, this.y + 5, this.height - 20, this.height - 10, leftArrowColor, true);
		GuiElement.renderArrow(((this.getFadingX() + this.width) - this.height) + 15, this.y + 5, this.height - 20, this.height - 10, rightArrowColor, false);
	}

	@Override
	public void onMouseClick() {
		if (Mouse.getX() < (this.getFadingX() + (this.width / 2))) {
			if (this.selectedElement == 0) {
				return;
			}

			this.selectedElement--;

			this.text.setText(this.elements[this.selectedElement]);
		} else {
			if (this.selectedElement == (this.elements.length - 1)) {
				return;
			}

			this.selectedElement++;
			this.text.setText(this.elements[this.selectedElement]);
		}
	}

	public void setSelected(String selected) {
		for (int i = 0; i < this.elements.length; ++i) {
			if (selected.equals(this.elements[i])) {
				this.selectedElement = i;
				this.text.setText(this.elements[this.selectedElement]);
			}
		}
	}
}
