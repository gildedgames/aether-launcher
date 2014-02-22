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

	private int fadeToDo;

	private float fade;

	private int fadeX;

	protected boolean kill;

	public GuiForm(GuiPanel panel, GuiForm parentForm) {
		this.panel = panel;
		this.parentForm = parentForm;
		this.fadeX = 0;
		this.fade = 1.F;

		this.panel.add(this);
	}

	public void render() {
		this.elements.addAll(this.addElements);
		this.addElements.clear();

		this.elements.removeAll(this.removeElements);
		this.removeElements.clear();

		if (this.fadeToDo < 0) {
			this.fadeX -= this.panel.getSettings().fadeSpeed;
			this.fadeToDo += this.panel.getSettings().fadeSpeed;

			if (this.fadeToDo >= 0) {
				this.fadeX += this.fadeToDo;
				this.fadeToDo = 0;

				if (this.kill && (this.fadeX < 0)) {
					this.panel.remove(this);
				}
			}

			this.fade = 1.0F - Math.abs(this.fadeX / (float) Display.getWidth());
		} else if (this.fadeToDo > 0) {
			this.fadeX += this.panel.getSettings().fadeSpeed;
			this.fadeToDo -= this.panel.getSettings().fadeSpeed;

			if (this.fadeToDo <= 0) {
				this.fadeX += this.fadeToDo;
				this.fadeToDo = 0;

				if (this.kill && (this.fadeX > 0)) {
					this.panel.remove(this);
				}
			}

			this.fade = 1.0F - Math.abs(this.fadeX / (float) Display.getWidth());
		}

		for (GuiElement element : this.elements) {
			if (element.shouldRender()) {
				element.render();
			}
		}
	}

	public void onElementClick(GuiElement element) {
		element.onMouseClick();
	}

	public void add(GuiElement element) {
		this.addElements.add(element);
	}

	public void remove(GuiElement element) {
		this.removeElements.add(element);
	}

	public ArrayList<GuiElement> getElements() {
		return this.elements;
	}

	public int getFadeX() {
		return this.fadeX;
	}

	public void setFadeX(int fadeX) {
		this.fadeX = fadeX;
	}

	public float getFade() {
		return this.fade;
	}

	public void setFade(float fade) {
		this.fade = fade;
	}

	public void fadeLeft() {
		this.fadeToDo -= Display.getWidth();
	}

	public void fadeRight() {
		this.fadeToDo += Display.getWidth();
	}

	public GuiPanel getPanel() {
		return this.panel;
	}

}
