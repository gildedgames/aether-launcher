package net.aetherteam.aether.launcher.gui;

import java.io.IOException;

import net.aetherteam.aether.launcher.Launcher;
import net.aetherteam.aether.launcher.gui.forms.LoginForm;
import net.aetherteam.aether.launcher.gui.forms.PlayForm;
import net.aetherteam.aether.launcher.gui.utils.Sprite;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class LauncherDisplay {

	private Panorama panorama;

	private Sprite logo;

	private Audio music;

	private GuiPanel panel;

	public void init() {
		try {
			Display.setTitle("The Aether II");
			Display.setDisplayMode(new DisplayMode(854, 480));
			Display.create();
			Display.setVSyncEnabled(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		this.panorama = new Panorama(new String[] { "assets/bg/panorama0.png", "assets/bg/panorama1.png", "assets/bg/panorama2.png", "assets/bg/panorama3.png", "assets/bg/panorama4.png", "assets/bg/panorama5.png", });

		try {
			this.music = AudioLoader.getStreamingAudio("OGG", ResourceLoader.getResource("assets/music.ogg"));
			this.logo = new Sprite(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("assets/aether_logo.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		GuiSettings settings = new GuiSettings();

		settings.backgroundColor = new Color(0, 0, 0, 0.2F);
		settings.textFieldColor = new Color(0, 0, 0, 0.3F);
		settings.textFieldHoveredColor = new Color(0, 0, 0, 0.5F);
		settings.fadeSpeed = 40;

		this.panel = new GuiPanel(settings);

		boolean isLoggedIn = Launcher.getInstance().getProfileManager().getAuthenticationService().isLoggedIn();

		if (isLoggedIn) {
			new PlayForm(this.panel, null);
		} else {
			new LoginForm(this.panel, null);
		}

		//this.music.playAsMusic(1.0f, 0.5f, true);
	}

	public void start() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearDepth(1.0D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 854, 480, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());

		while (true) {
			this.render();

			Display.update();
			Display.sync(60);

			if (Display.isCloseRequested()) {
				Display.destroy();
				AL.destroy();
				System.exit(0);
			}
		}
	}

	public void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

		this.panorama.render();

		GL11.glDisable(GL11.GL_CULL_FACE);
		this.logo.render((Display.getWidth() - this.logo.getWidth()) / 2, 20);

		this.panel.render();

		SoundStore.get().poll(0);
	}

	public static void main(String[] argv) {
		new Launcher();

		LauncherDisplay launcherDisplay = new LauncherDisplay();
		launcherDisplay.init();
		launcherDisplay.start();
	}

}