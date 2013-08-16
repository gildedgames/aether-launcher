package net.aetherteam.aether.launcher.gui.forms;

import java.awt.Font;

import net.aetherteam.aether.launcher.Launcher;
import net.aetherteam.aether.launcher.gui.GuiPanel;
import net.aetherteam.aether.launcher.gui.elements.GuiButton;
import net.aetherteam.aether.launcher.gui.elements.GuiDropdown;
import net.aetherteam.aether.launcher.gui.elements.GuiElement;
import net.aetherteam.aether.launcher.gui.elements.GuiRectangle;
import net.aetherteam.aether.launcher.gui.elements.GuiText;

import org.lwjgl.opengl.Display;

public class PlayForm extends GuiForm {

	private GuiRectangle background;

	private GuiText selectProfileLabel;

	private GuiDropdown profiles;

	private GuiText selectVersionLabel;

	private GuiDropdown versions;

	private GuiButton playButton;

	private GuiButton logoutButton;

	public PlayForm(GuiPanel panel, GuiForm parentForm) {
		super(panel, parentForm);

		Font font = new Font("Athelas", Font.BOLD, 24);
		Font ssfont = new Font("Athelas", Font.BOLD, 18);

		this.background = new GuiRectangle(this, (Display.getWidth() - 264) / 2, 157, 264, 284);
		this.background.setColor(this.panel.getSettings().backgroundColor, this.panel.getSettings().backgroundColor);
		this.add(this.background);

		this.selectProfileLabel = new GuiText(this, ssfont, "Select profile:");

		int profilesW = 120;
		this.profiles = new GuiDropdown(this, (Display.getWidth() - profilesW) / 2, this.background.getY() + 25 + 20, profilesW, 35, font, Launcher.getInstance().getProfileManager().getAuthenticationService().getAvailableProfileNames());
		this.profiles.setColor(this.panel.getSettings().backgroundColor, this.panel.getSettings().textFieldColor);
		this.add(this.profiles);

		this.selectVersionLabel = new GuiText(this, ssfont, "Select version:");

		int versionsW = 120;
		this.versions = new GuiDropdown(this, (Display.getWidth() - versionsW) / 2, this.background.getY() + 75 + 25 + 15, versionsW, 35, font, Launcher.getInstance().getVersionManager().getVersions());
		this.versions.setColor(this.panel.getSettings().backgroundColor, this.panel.getSettings().textFieldColor);
		this.add(this.versions);

		GuiText play = new GuiText(this, font, "Play");
		this.playButton = new GuiButton(this, (Display.getWidth() - 100) / 2, 387 - 50, 100, 35, play);
		this.playButton.setColor(this.panel.getSettings().backgroundColor, this.panel.getSettings().textFieldColor);
		this.add(this.playButton);

		GuiText logoutText = new GuiText(this, font, "Logout");
		this.logoutButton = new GuiButton(this, (Display.getWidth() - 100) / 2, 387, 100, 35, logoutText);
		this.logoutButton.setColor(this.panel.getSettings().backgroundColor, this.panel.getSettings().textFieldColor);
		this.add(this.logoutButton);
	}

	@Override
	public void render() {
		super.render();

		this.selectProfileLabel.render((Display.getWidth() - this.selectProfileLabel.getWidth()) / 2, this.background.getY() + 15);
		this.selectVersionLabel.render((Display.getWidth() - this.selectVersionLabel.getWidth()) / 2, this.background.getY() + 85);
	}

	@Override
	public void onElementClick(GuiElement element) {
		super.onElementClick(element);

		this.versions.onElementClick(element);

		if (element == this.versions) {
			Launcher.getInstance().getProfileManager().getAuthenticationService().setSelectedVersion(this.versions.getSelectedElement());
		} else if (element == this.logoutButton) {
			Launcher.getInstance().getProfileManager().getAuthenticationService().logOut();

			LoginForm loginForm = new LoginForm(this.panel, null);

			loginForm.setFadeX(-Display.getWidth());
			loginForm.fadeRight();

			this.fadeRight();
			this.kill = true;
		} else if (element == this.playButton) {
			LoadingForm loadingForm = new LoadingForm(this.panel, null);
			loadingForm.setFadeX(Display.getWidth());
			loadingForm.fadeLeft();

			Launcher.getInstance().getGameLauncher().playGame();

			this.fadeLeft();
			this.kill = true;
		}
	}

}
