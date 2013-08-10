package net.aetherteam.aether.launcher.gui.elements;

import net.aetherteam.aether.launcher.gui.forms.GuiForm;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

public abstract class GuiElement {

	protected GuiForm form;

	protected int x;

	protected int y;

	protected int width;

	protected int height;

	protected Color color;

	protected Color hoveringColor;

	protected boolean shouldRender;

	public GuiElement(GuiForm form, int x, int y, int width, int height) {
		this.form = form;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.shouldRender = true;
	}

	public GuiElement(GuiForm form, Color color) {
		this.form = form;
		this.color = color;
		this.hoveringColor = color;
	}

	public void render() {
	}

	public void render(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static void renderColoredRect(int x, int y, int width, int height, Color color) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(color.r, color.g, color.b, color.a);
		GL11.glVertex3f(x, y, 0);
		GL11.glVertex3f(x + width, y, 0);
		GL11.glVertex3f(x + width, y + height, 0);
		GL11.glVertex3f(x, y + height, 0);
		GL11.glEnd();

		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public boolean containsPoint(int x, int y) {
		return (x >= this.x) && (x <= (this.x + this.width)) && (y >= this.y) && (y <= (this.y + this.height));
	}

	public boolean isMouseHovering() {
		return this.containsPoint(Mouse.getX(), Display.getHeight() - Mouse.getY()) && this.form.isFocused();
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getFadingX() {
		return this.form.getFadeX() + this.x;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void onMouseClick() {
	}

	public void onKey(char character) {
	}

	public Color getColor() {
		if (this.form.getFade() != 1.f) {
			return new Color(this.color.r, this.color.g, this.color.b, this.color.a * this.form.getFade());
		}

		return this.color;
	}

	public void setColor(Color color, Color hoveringColor) {
		this.color = color;
		this.hoveringColor = hoveringColor;
	}

	public Color getHoveringColor() {
		if (this.form.getFade() != 1.f) {
			return new Color(this.hoveringColor.r, this.hoveringColor.g, this.hoveringColor.b, this.hoveringColor.a * this.form.getFade());
		}

		return this.hoveringColor;
	}

	public boolean shouldRender() {
		return this.shouldRender;
	}

}
