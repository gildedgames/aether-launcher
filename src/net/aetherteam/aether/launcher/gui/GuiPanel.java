package net.aetherteam.aether.launcher.gui;

import java.util.ArrayList;

import net.aetherteam.aether.launcher.gui.elements.GuiElement;
import net.aetherteam.aether.launcher.gui.elements.GuiTextfield;
import net.aetherteam.aether.launcher.gui.forms.GuiForm;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GuiPanel {

	private ArrayList<GuiForm> forms = new ArrayList<GuiForm>();

	private ArrayList<GuiForm> addForms = new ArrayList<GuiForm>();

	private ArrayList<GuiForm> removeForms = new ArrayList<GuiForm>();

	public GuiPanel() {
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
					if (form.isFocused()) {
						for (GuiElement element : form.getElements()) {
							if (element.containsPoint(x, y)) {
								element.onMouseClick();
								form.onElementClick(element);
							}
						}
					}
				}
			}
		}
	}

	private void processKeyEvents() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				char character = Keyboard.getEventCharacter();

				for (GuiForm form : this.forms) {
					if (form.isFocused()) {
						for (GuiElement element : form.getElements()) {
							element.onKey(character);
						}
					}
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

	public void setOnlyFocus(GuiForm focusForm) {
		for (GuiForm form : this.forms) {
			if (form != focusForm) {
				form.fadeOut(40);
				form.setFocused(false);
			}
		}
	}

	public void setAllFocus(boolean focus) {
		for (GuiForm form : this.forms) {
			form.setFocused(focus);
		}
	}

}
