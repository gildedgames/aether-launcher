package net.aetherteam.aether.launcher.gui.forms;

import java.awt.Font;

import net.aetherteam.aether.launcher.gui.GuiPanel;
import net.aetherteam.aether.launcher.gui.elements.GuiButton;
import net.aetherteam.aether.launcher.gui.elements.GuiElement;
import net.aetherteam.aether.launcher.gui.elements.GuiRectangle;
import net.aetherteam.aether.launcher.gui.elements.GuiText;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

public class ErrorForm extends GuiForm {

	public GuiRectangle rect;

	GuiText errorMessage;

	GuiButton okButton;

	public ErrorForm(GuiPanel panel, GuiForm parentForm, String errorMessage) {
		super(panel, parentForm);

		this.rect = new GuiRectangle(this, (Display.getWidth() - 300) / 2, (Display.getHeight() - 150) / 2, 300, 150);
		this.rect.setColor(new Color(0, 0, 0, 0.2f), new Color(0, 0, 0, 0.2f));
		this.add(this.rect);

		Font font = new Font("Athelas", Font.BOLD, 24);

		this.errorMessage = new GuiText(this, font, errorMessage);

		GuiText buttonText = new GuiText(this, font, "OK");
		this.okButton = new GuiButton(this, (Display.getWidth() - 200) / 2, ((Display.getHeight() - 30) / 2) + 20, 200, 30, buttonText);
		this.okButton.setColor(this.panel.getSettings().backgroundColor, this.panel.getSettings().textFieldColor);
		this.add(this.okButton);
	}

	@Override
	public void onElementClick(GuiElement element) {
		super.onElementClick(element);

		if (element == this.okButton) {
			this.parentForm.fadeRight();
			this.fadeRight();
			this.kill = true;
		}
	}

	@Override
	public void render() {
		super.render();

		this.errorMessage.render((Display.getWidth() - this.errorMessage.getWidth()) / 2, ((Display.getHeight() - this.errorMessage.getHeight()) / 2) - 30);
	}
	
	@Override
	public void onKey(int key, char character){
		if(key == Keyboard.KEY_RETURN){
				this.onElementClick(this.okButton);
				if(this.parentForm instanceof LoginForm){
					this.setOnScreen(false);
					this.parentForm.setOnScreen(true);
				}
		}
	}

}
