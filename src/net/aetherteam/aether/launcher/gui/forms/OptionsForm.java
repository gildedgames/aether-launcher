package net.aetherteam.aether.launcher.gui.forms;

import java.awt.Font;

import net.aetherteam.aether.launcher.Launcher;
import net.aetherteam.aether.launcher.gui.GuiPanel;
import net.aetherteam.aether.launcher.gui.elements.GuiCheckbox;
import net.aetherteam.aether.launcher.gui.elements.GuiDropdown;
import net.aetherteam.aether.launcher.gui.elements.GuiElement;
import net.aetherteam.aether.launcher.gui.elements.GuiText;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

public class OptionsForm extends GuiForm {

	private GuiText versionsLabel;

	private GuiDropdown versions;

	//private GuiText modSubVersionsLabel;

	private GuiText makeBackupLabel;

	private GuiCheckbox makeBackup;

	public static OptionsForm instance;

	public OptionsForm(GuiPanel panel, GuiForm parentForm) {
		super(panel, parentForm);

		MainForm mainForm = (MainForm) this.parentForm;

		Font font = new Font("Athelas", Font.BOLD, 24);
		Font sfont = new Font("Athelas", Font.BOLD, 22);
		Font ssfont = new Font("Athelas", Font.BOLD, 18);

		this.versionsLabel = new GuiText(this, sfont, "Version selection");
		int versionsW = 75;
		this.versions = new GuiDropdown(this, (Display.getWidth() - versionsW) / 2, mainForm.background.getY() + 75, versionsW, 35, font, Launcher.getInstance().getVersionManager().getVersions());
		this.versions.setColor(mainForm.backgroundColor, mainForm.textFieldColor);
		this.add(this.versions);

		this.makeBackupLabel = new GuiText(this, ssfont, "make backups");
		this.makeBackup = new GuiCheckbox(this, ((Display.getWidth() - 150) / 2), ((MainForm) this.parentForm).background.getY() + 135, 20, 20);
		this.makeBackup.setColor(mainForm.textFieldColor, mainForm.textFieldHoveredColor, new Color(0, 0, 0, 0.6f));
		this.add(this.makeBackup);

		OptionsForm.instance = this;
	}

	@Override
	public void render() {
		super.render();

		this.versionsLabel.render((Display.getWidth() - this.versionsLabel.getWidth()) / 2, ((MainForm) this.parentForm).background.getY() + 35);
		this.makeBackupLabel.render(this.makeBackup.getX() + 30, this.makeBackup.getY());
	}

	@Override
	public void onElementClick(GuiElement element) {
		this.versions.onElementClick(element);
	}

	public String getSelectedVersion() {
		return this.versions.getSelectedElement();
	}

}
