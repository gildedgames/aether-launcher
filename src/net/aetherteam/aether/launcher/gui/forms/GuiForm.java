package net.aetherteam.aether.launcher.gui.forms;

import java.util.ArrayList;

import net.aetherteam.aether.launcher.gui.GuiPanel;
import net.aetherteam.aether.launcher.gui.elements.GuiElement;

import org.lwjgl.opengl.Display;

public abstract class GuiForm {

	private ArrayList<GuiElement> elements = new ArrayList<GuiElement>();

	private ArrayList<GuiElement> addElements = new ArrayList<GuiElement>();

	private ArrayList<GuiElement> removeElements = new ArrayList<GuiElement>();

	protected GuiPanel panel;

	protected GuiForm parentForm;

	protected boolean isFocused;

	private int fadingState;

	private float fade;

	private int fadeX;

	public GuiForm(GuiPanel panel, GuiForm parentForm) {
		this.panel = panel;
		this.parentForm = parentForm;
		this.isFocused = true;
		this.fade = 1;
		this.fadeX = 0;

		this.panel.add(this);
	}

	public void render() {
		this.elements.addAll(this.addElements);
		this.addElements.clear();

		this.elements.removeAll(this.removeElements);
		this.removeElements.clear();

		if (this.fadingState != 0) {
			this.fadeX -= Math.abs(this.fadingState);
			this.fade += (float) this.fadingState / Display.getWidth();

			if (this.fade > 1) {
				this.fade = 1;
				this.fadeX = 0;
				this.fadingState = 0;
			}

			if (this.fade < 0) {
				this.fade = 0;
				this.fadeX = Display.getWidth();
				this.fadingState = 0;
			}
		}

		for (GuiElement element : this.elements) {
			if (element.shouldRender()) {
				element.render();
			}
		}
	}

	public abstract void onElementClick(GuiElement element);

	public void add(GuiElement element) {
		this.addElements.add(element);
	}

	public void remove(GuiElement element) {
		this.removeElements.add(element);
	}

	public ArrayList<GuiElement> getElements() {
		return this.elements;
	}

	public boolean isFocused() {
		return this.isFocused;
	}

	public void setFocused(boolean isFocused) {
		this.isFocused = isFocused;
	}

	public int getFadeX() {
		return this.fadeX;
	}

	public float getFade() {
		return this.fade;
	}

	public void fadeIn(int speed) {
		this.fadeX = Display.getWidth();
		this.fade = 0;
		this.fadingState = speed;
	}

	public void fadeOut(int speed) {
		this.fadeX = 0;
		this.fadingState = -speed;
	}

	public GuiPanel getPanel() {
		return this.panel;
	}

}
