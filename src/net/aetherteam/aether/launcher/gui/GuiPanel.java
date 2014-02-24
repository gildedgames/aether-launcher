package net.aetherteam.aether.launcher.gui;

import java.util.ArrayList;

import net.aetherteam.aether.launcher.gui.elements.GuiElement;
import net.aetherteam.aether.launcher.gui.elements.GuiTextfield;
import net.aetherteam.aether.launcher.gui.forms.GuiForm;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GuiPanel {

	private GuiSettings settings;

	private ArrayList<GuiForm> forms = new ArrayList<GuiForm>();

	private ArrayList<GuiForm> addForms = new ArrayList<GuiForm>();

	private ArrayList<GuiForm> removeForms = new ArrayList<GuiForm>();

	public GuiPanel(GuiSettings settings) {
		this.settings = settings;
	}

	public void render() {
		this.forms.addAll(this.addForms);
		this.addForms.clear();

		this.forms.removeAll(this.removeForms);
		this.removeForms.clear();

		this.processKeyEvents();
		this.processMouseEvents();

		for (GuiForm form : this.forms) {
			form.render();
		}
	}

	private void processMouseEvents() {
		while (Mouse.next()) {
			if (Mouse.getEventButtonState() && (Mouse.getEventButton() == 0)) {
				GuiTextfield.activeTextfield = null;

				int x = Mouse.getEventX();
				int y = Display.getHeight() - Mouse.getEventY();

				for (GuiForm form : this.forms) {
					for (GuiElement element : form.getElements()) {
						if (element.containsPoint(x, y)) {
							form.onElementClick(element);
						}
					}
				}
			}
		}
	}

	private void processKeyEvents() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				int key = Keyboard.getEventKey();
				char character = Keyboard.getEventCharacter();
				boolean repeated = Keyboard.isRepeatEvent();

				for (GuiForm form : this.forms) {
					for (GuiElement element : form.getElements()) {
						element.onKey(key, character, repeated);
					}
					form.onKey(key, character);
				}
			}
		}
	}

	public void add(GuiForm form) {
		this.addForms.add(form);
	}

	public void remove(GuiForm form) {
		this.removeForms.add(form);
	}

	public GuiSettings getSettings() {
		return this.settings;
	}

}
