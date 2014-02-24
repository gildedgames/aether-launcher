package net.aetherteam.aether.launcher.gui.forms;

import java.awt.Font;
import java.util.ArrayList;

import net.aetherteam.aether.launcher.Launcher;
import net.aetherteam.aether.launcher.gui.GuiPanel;
import net.aetherteam.aether.launcher.gui.elements.GuiRectangle;
import net.aetherteam.aether.launcher.gui.elements.GuiText;
import net.aetherteam.aether.launcher.version.Version;
import net.aetherteam.aether.launcher.version.VersionSyncInfo;

import org.lwjgl.opengl.Display;

public class PatchNotesForm extends GuiForm {

	private GuiRectangle background;

	private String currentVersionId = "";

	private ArrayList<GuiText> changelog = new ArrayList<GuiText>();

	private Font font;

	private GuiText changelogLabel;

	public PatchNotesForm(GuiPanel panel, GuiForm parentForm) {
		super(panel, parentForm);

		this.font = new Font("Athelas", Font.BOLD, 16);
		Font sfont = new Font("Athelas", Font.BOLD, 20);
		Font ssfont = new Font("Athelas", Font.BOLD, 18);

		this.background = new GuiRectangle(this, ((Display.getWidth() - 224) / 2) + 264, 157, 224, 254);
		this.background.setColor(this.panel.getSettings().backgroundColor, this.panel.getSettings().backgroundColor);
		this.add(this.background);

		this.changelogLabel = new GuiText(this, ssfont, "Changelog:");
	}

	public void setVersion(String versionId) {
		if (!this.currentVersionId.equals(versionId)) {
			VersionSyncInfo versionSyncInfo = Launcher.getInstance().getVersionManager().getVersionSyncInfo(versionId);
			Version version = versionSyncInfo.getLatestVersion();

			this.changelog.clear();

			if (version.getChangelog() != null) {
				for (String changelog : version.getChangelog()) {
					this.changelog.add(new GuiText(this, this.font, changelog));
				}
			}
		}

		this.currentVersionId = versionId;
	}

	@Override
	public void render() {
		super.render();

		this.changelogLabel.render(this.background.getFadingX() + ((this.background.getWidth() - this.changelogLabel.getWidth()) / 2), this.background.getY() + 15);

		int offset = 50;

		for (GuiText text : this.changelog) {
			text.render(this.background.getFadingX() + 20, this.background.getY() + offset);
			offset += 25;
		}
	}

}
