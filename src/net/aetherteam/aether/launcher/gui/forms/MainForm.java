package net.aetherteam.aether.launcher.gui.forms;

import java.awt.Font;

import net.aetherteam.aether.launcher.gui.GuiPanel;
import net.aetherteam.aether.launcher.gui.elements.GuiButton;
import net.aetherteam.aether.launcher.gui.elements.GuiElement;
import net.aetherteam.aether.launcher.gui.elements.GuiRectangle;
import net.aetherteam.aether.launcher.gui.elements.GuiText;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

public class MainForm extends GuiForm {

	protected Color backgroundColor;

	protected Color textFieldColor;

	protected Color textFieldHoveredColor;

	protected GuiRectangle background;

	protected GuiButton loginButton;

	protected GuiButton newsButton;

	protected GuiButton optionsButton;

	protected GuiForm activeForm;

	public MainForm(GuiPanel panel, GuiForm parentForm) {
		super(panel, parentForm);

		this.backgroundColor = new Color(0, 0, 0, 0.2F);
		this.textFieldColor = new Color(0, 0, 0, 0.3F);
		this.textFieldHoveredColor = new Color(0, 0, 0, 0.5F);

		this.background = new GuiRectangle(this, (Display.getWidth() - 264) / 2, 157, 264, 254);
		this.background.setColor(this.backgroundColor, this.backgroundColor);
		this.add(this.background);

		Font font = new Font("Athelas", Font.BOLD, 24);

		GuiText loginText = new GuiText(this, font, "Login");
		this.loginButton = new GuiButton(this, this.background.getX() - 100, 157, 100, 40, loginText);
		this.loginButton.setColor(this.backgroundColor, this.textFieldColor);
		this.add(this.loginButton);

		GuiText newsText = new GuiText(this, font, "News");
		this.newsButton = new GuiButton(this, this.background.getX() - 100, 157 + 44, 100, 40, newsText);
		this.newsButton.setColor(this.backgroundColor, this.textFieldColor);
		this.add(this.newsButton);

		GuiText optionsText = new GuiText(this, font, "Options");
		this.optionsButton = new GuiButton(this, this.background.getX() - 100, 157 + 88, 100, 40, optionsText);
		this.optionsButton.setColor(this.backgroundColor, this.textFieldColor);
		this.add(this.optionsButton);

		this.activeForm = new LoginForm(this.panel, this);
	}

	@Override
	public void onElementClick(GuiElement element) {
		if (element == this.loginButton) {
			this.panel.remove(this.activeForm);
			this.activeForm = new LoginForm(this.panel, this);
		} else if (element == this.newsButton) {
			this.panel.remove(this.activeForm);
			//this.activeForm = new LoginForm(this.panel, this);
		} else if (element == this.optionsButton) {
			this.panel.remove(this.activeForm);
			this.activeForm = new OptionsForm(this.panel, this);
		}
	}

}
