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

	public AdForm(GuiPanel panel, GuiForm parentForm) {
		super(panel, parentForm);

		this.craftHosting = new GuiButtonSprite(this, 10, Display.getHeight() - 50, LauncherDisplay.instance.craftHosting);
		this.craftHosting.setColor(new Color(0, 0, 0, 0), new Color(1, 1, 1, 0.8F));
		this.add(this.craftHosting);

		this.facebook = new GuiButtonSprite(this, Display.getWidth() - 60, Display.getHeight() - 55, LauncherDisplay.instance.facebook);
		this.facebook.setColor(new Color(0, 0, 0, 0), new Color(1, 1, 1, 0.8F));
		this.add(this.facebook);

		this.twitter = new GuiButtonSprite(this, Display.getWidth() - 110, Display.getHeight() - 50, LauncherDisplay.instance.twitter);
		this.twitter.setColor(new Color(0, 0, 0, 0), new Color(1, 1, 1, 0.8F));
		this.add(this.twitter);
	}

	@Override
	public void onElementClick(GuiElement element) {
		super.onElementClick(element);

		if (element == this.craftHosting) {
			try {
				OperatingSystem.openLink(new URI("http://craftnodehosting.com/"));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else if (element == this.facebook) {
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
		}
	}

}
