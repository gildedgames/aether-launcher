package net.aetherteam.aether.launcher.gui.forms;

import java.awt.Font;

import net.aetherteam.aether.launcher.Launcher;
import net.aetherteam.aether.launcher.gui.GuiPanel;
import net.aetherteam.aether.launcher.gui.elements.GuiButton;
import net.aetherteam.aether.launcher.gui.elements.GuiCheckbox;
import net.aetherteam.aether.launcher.gui.elements.GuiElement;
import net.aetherteam.aether.launcher.gui.elements.GuiText;
import net.aetherteam.aether.launcher.gui.elements.GuiTextfield;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

public class LoginForm extends GuiForm {

	private GuiText usernameLabel;

	private GuiText usernameInput;

	private GuiTextfield usernameField;

	private GuiText passwordLabel;

	private GuiText passwordInput;

	private GuiTextfield passwordField;

	private GuiText rememberLabel;

	private GuiCheckbox rememberCheckbox;

	private GuiButton loginButton;

	private GuiText loggedInLabel;

	private GuiText loggedInAsLabel;

	private GuiButton playButton;

	private GuiButton logoutButton;

	private boolean isLoggedIn;

	public LoginForm(GuiPanel panel, GuiForm parentForm) {
		super(panel, parentForm);

		MainForm mainForm = (MainForm) this.parentForm;
		this.isLoggedIn = Launcher.getInstance().getProfileManager().getAuthenticationService().isLoggedIn();

		Font bfont = new Font("Athelas", Font.BOLD, 30);
		Font font = new Font("Athelas", Font.BOLD, 24);
		Font sfont = new Font("Athelas", Font.BOLD, 20);
		Font ssfont = new Font("Athelas", Font.BOLD, 18);

		if (this.isLoggedIn) {
			this.loggedInLabel = new GuiText(this, sfont, "Logged in as:");
			this.loggedInAsLabel = new GuiText(this, bfont, "cafaxo");

			GuiText play = new GuiText(this, font, "Play");
			this.playButton = new GuiButton(this, (Display.getWidth() - 100) / 2, 357 - 50, 100, 35, play);
			this.playButton.setColor(mainForm.backgroundColor, mainForm.textFieldColor);
			this.add(this.playButton);

			GuiText logoutText = new GuiText(this, font, "Logout");
			this.logoutButton = new GuiButton(this, (Display.getWidth() - 100) / 2, 357, 100, 35, logoutText);
			this.logoutButton.setColor(mainForm.backgroundColor, mainForm.textFieldColor);
			this.add(this.logoutButton);
		} else {
			this.usernameLabel = new GuiText(this, font, "Username");
			this.usernameInput = new GuiText(this, sfont, "");
			this.usernameField = new GuiTextfield(this, (Display.getWidth() - 210) / 2, mainForm.background.getY() + 50, 210, 30, this.usernameInput, false);
			this.usernameField.setColor(mainForm.textFieldColor, mainForm.textFieldHoveredColor);
			this.add(this.usernameField);

			this.passwordLabel = new GuiText(this, font, "Password");
			this.passwordInput = new GuiText(this, sfont, "");
			this.passwordField = new GuiTextfield(this, (Display.getWidth() - 210) / 2, this.usernameField.getY() + 70, 210, 30, this.passwordInput, true);
			this.passwordField.setColor(mainForm.textFieldColor, mainForm.textFieldHoveredColor);
			this.add(this.passwordField);

			this.rememberLabel = new GuiText(this, ssfont, "Remember");
			this.rememberCheckbox = new GuiCheckbox(this, this.passwordField.getX() + 45, this.passwordField.getY() + 40, 20, 20);
			this.rememberCheckbox.setColor(mainForm.textFieldColor, mainForm.textFieldHoveredColor, new Color(0, 0, 0, 0.6f));
			this.add(this.rememberCheckbox);

			GuiText loginText = new GuiText(this, font, "Login");
			this.loginButton = new GuiButton(this, (Display.getWidth() - 100) / 2, this.rememberCheckbox.getY() + 40, 100, 35, loginText);
			this.loginButton.setColor(mainForm.backgroundColor, mainForm.textFieldColor);
			this.add(this.loginButton);
		}
	}

	@Override
	public void render() {
		super.render();

		if (this.isLoggedIn) {
			this.loggedInLabel.render((Display.getWidth() - this.loggedInLabel.getWidth()) / 2, ((MainForm) this.parentForm).background.getY() + 35);
			this.loggedInAsLabel.render((Display.getWidth() - this.loggedInAsLabel.getWidth()) / 2, this.loggedInLabel.getY() + 30);
		} else {
			this.usernameLabel.render((Display.getWidth() - this.usernameLabel.getWidth()) / 2, this.usernameField.getY() - 32);
			this.passwordLabel.render((Display.getWidth() - this.passwordLabel.getWidth()) / 2, this.passwordField.getY() - 32);
			this.rememberLabel.render(this.rememberCheckbox.getX() + 30, this.rememberCheckbox.getY());
		}
	}

	@Override
	public void onElementClick(GuiElement element) {
		if (element == this.loginButton) {
			Launcher.getInstance().login(this.usernameField.getText(), this.passwordField.getText());

			LoadingForm loadingForm = new LoadingForm(this.panel, this);
			this.panel.setOnlyFocus(loadingForm);
		} else if (element == this.logoutButton) {
			Launcher.getInstance().getProfileManager().getAuthenticationService().logOut();

			this.panel.remove(this);
			((MainForm) this.parentForm).activeForm = new LoginForm(this.panel, this.parentForm);
		} else if (element == this.playButton) {
			LoadingForm loadingForm = new LoadingForm(this.panel, this);
			this.panel.setOnlyFocus(loadingForm);

			Launcher.getInstance().getGameLauncher().playGame();
		}
	}

}
