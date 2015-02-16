package net.aetherteam.aether.launcher.gui.forms;

import java.net.URI;
import java.net.URISyntaxException;

import net.aetherteam.aether.launcher.OperatingSystem;
import net.aetherteam.aether.launcher.gui.GuiPanel;
import net.aetherteam.aether.launcher.gui.LauncherDisplay;
import net.aetherteam.aether.launcher.gui.elements.GuiButtonSprite;
import net.aetherteam.aether.launcher.gui.elements.GuiElement;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

public class AdForm extends GuiForm {

	GuiButtonSprite craftHosting;

	GuiButtonSprite facebook;

	GuiButtonSprite twitter;

	GuiButtonSprite audioMute;

	public AdForm(GuiPanel panel, GuiForm parentForm, boolean isMusicMuted) {
		super(panel, parentForm);

		this.facebook = new GuiButtonSprite(this, Display.getWidth() - 60, Display.getHeight() - 60, LauncherDisplay.instance.facebook);
		this.facebook.setColor(new Color(0, 0, 0, 0), new Color(1, 1, 1, 0.8F));
		this.add(this.facebook);

		this.twitter = new GuiButtonSprite(this, Display.getWidth() - 120, Display.getHeight() - 60, LauncherDisplay.instance.twitter);
		this.twitter.setColor(new Color(0, 0, 0, 0), new Color(1, 1, 1, 0.8F));
		this.add(this.twitter);
		
		if (isMusicMuted) {
			this.audioMute = new GuiButtonSprite(this, 8, Display.getHeight() - 64, LauncherDisplay.instance.audioMute);
			this.audioMute.setColor(new Color(0, 0, 0, 0), new Color(1, 1, 1, 0.8F));
			this.add(this.audioMute);
		} else {
			this.audioMute = new GuiButtonSprite(this, 8, Display.getHeight() - 64, LauncherDisplay.instance.audioPlay);
			this.audioMute.setColor(new Color(0, 0, 0, 0), new Color(1, 1, 1, 0.8F));
			this.add(this.audioMute);
		}
	}

	@Override
	public void onElementClick(GuiElement element) {
		super.onElementClick(element);

		if (element == this.facebook) {
			try {
				OperatingSystem.openLink(new URI("http://facebook.com/aethermod"));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else if (element == this.twitter) {
			try {
				OperatingSystem.openLink(new URI("http://twitter.com/DevAether"));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else if (element == this.audioMute) {
			if (LauncherDisplay.instance.isMusicPlaying()) {
				LauncherDisplay.instance.stopMusic();
				this.remove(this.audioMute);
				this.audioMute = new GuiButtonSprite(this, 8, Display.getHeight() - 64, LauncherDisplay.instance.audioMute);
				this.audioMute.setColor(new Color(0, 0, 0, 0), new Color(1, 1, 1, 0.8F));
				this.add(this.audioMute);
			} else {
				LauncherDisplay.instance.startMusic();
				this.remove(this.audioMute);
				this.audioMute = new GuiButtonSprite(this, 8, Display.getHeight() - 64, LauncherDisplay.instance.audioPlay);
				this.audioMute.setColor(new Color(0, 0, 0, 0), new Color(1, 1, 1, 0.8F));
				this.add(this.audioMute);
			}
		}
	}

}
