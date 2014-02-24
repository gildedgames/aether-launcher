package net.aetherteam.aether.launcher.gui.forms;

import java.awt.Font;

import net.aetherteam.aether.launcher.Launcher;
import net.aetherteam.aether.launcher.gui.GuiPanel;
import net.aetherteam.aether.launcher.gui.elements.GuiButton;
import net.aetherteam.aether.launcher.gui.elements.GuiCheckbox;
import net.aetherteam.aether.launcher.gui.elements.GuiElement;
import net.aetherteam.aether.launcher.gui.elements.GuiRectangle;
import net.aetherteam.aether.launcher.gui.elements.GuiText;
import net.aetherteam.aether.launcher.gui.elements.GuiTextfield;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

public class LoginForm extends GuiForm {

	private GuiRectangle background;

	private GuiText usernameLabel;

	private GuiText usernameInput;

	private GuiTextfield usernameField;

	private GuiText passwordLabel;

	private GuiText passwordInput;

	private GuiTextfield passwordField;

	private GuiText rememberLabel;

	private GuiCheckbox rememberCheckbox;

	private GuiButton loginButton;
	
	private ErrorForm errorForm;

	public LoginForm(GuiPanel panel, GuiForm parentForm) {
		super(panel, parentForm);

		Font font = new Font("Athelas", Font.BOLD, 24);
		Font sfont = new Font("Athelas", Font.BOLD, 20);
		Font ssfont = new Font("Athelas", Font.BOLD, 18);

		this.background = new GuiRectangle(this, (Display.getWidth() - 264) / 2, 157, 264, 254);
		this.background.setColor(this.panel.getSettings().backgroundColor, this.panel.getSettings().backgroundColor);
		this.add(this.background);

		this.usernameLabel = new GuiText(this, font, "Username");
		this.usernameInput = new GuiText(this, sfont, "");
		this.usernameField = new GuiTextfield(this, (Display.getWidth() - 210) / 2, this.background.getY() + 50, 210, 30, this.usernameInput, false);
		this.usernameField.setColor(this.panel.getSettings().textFieldColor, this.panel.getSettings().textFieldHoveredColor);
		this.add(this.usernameField);

		this.passwordLabel = new GuiText(this, font, "Password");
		this.passwordInput = new GuiText(this, sfont, "");
		this.passwordField = new GuiTextfield(this, (Display.getWidth() - 210) / 2, this.usernameField.getY() + 70, 210, 30, this.passwordInput, true);
		this.passwordField.setColor(this.panel.getSettings().textFieldColor, this.panel.getSettings().textFieldHoveredColor);
		this.add(this.passwordField);

		this.rememberLabel = new GuiText(this, ssfont, "Remember");
		this.rememberCheckbox = new GuiCheckbox(this, this.passwordField.getX() + 45, this.passwordField.getY() + 40, 20, 20);
		this.rememberCheckbox.setColor(this.panel.getSettings().textFieldColor, this.panel.getSettings().textFieldHoveredColor, new Color(0, 0, 0, 0.6f));
		this.add(this.rememberCheckbox);

		GuiText loginText = new GuiText(this, font, "Login");
		this.loginButton = new GuiButton(this, (Display.getWidth() - 100) / 2, this.rememberCheckbox.getY() + 40, 100, 35, loginText);
		this.loginButton.setColor(this.panel.getSettings().backgroundColor, this.panel.getSettings().textFieldColor);
		this.add(this.loginButton);
	}

	@Override
	public void render() {
		super.render();

		this.usernameLabel.render((Display.getWidth() - this.usernameLabel.getWidth()) / 2, this.usernameField.getY() - 32);
		this.passwordLabel.render((Display.getWidth() - this.passwordLabel.getWidth()) / 2, this.passwordField.getY() - 32);
		this.rememberLabel.render(this.rememberCheckbox.getX() + 30, this.rememberCheckbox.getY());
	}

	@Override
	public void onElementClick(GuiElement element) {
		super.onElementClick(element);

		if (element == this.loginButton) {
			Launcher.getInstance().getProfileManager().getAuthenticationService().setRememberMe(this.rememberCheckbox.isChecked());
			Launcher.getInstance().login(this.usernameField.getText(), this.passwordField.getText());

			if (Launcher.getInstance().getProfileManager().getAuthenticationService().isLoggedIn()) {
				PlayForm playForm = new PlayForm(this.panel, this);
				playForm.setFadeX(Display.getWidth());
				playForm.fadeLeft();
				this.fadeLeft();
			} else {
				errorForm = new ErrorForm(this.panel, this, "Login failed.");
				errorForm.setFadeX(Display.getWidth());
				errorForm.fadeLeft();
				errorForm.setOnScreen(true);
				this.fadeLeft();
			}
		}
	}

	@Override
	public void onKey(int key, char character){
		if(key == Keyboard.KEY_RETURN){
			if(this.onScreen)
			{
				this.onElementClick(this.loginButton);
				this.onScreen = false;
			}
		}
		else if(key == Keyboard.KEY_TAB){
			if(GuiTextfield.activeTextfield == this.usernameField)
			{
				GuiTextfield.activeTextfield = this.passwordField;
			}
			else
			{
				GuiTextfield.activeTextfield = this.usernameField;
			}
		}
	}
	
}
