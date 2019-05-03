package de.jumpingpxl.jumpingaddon.util.elements;

import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 29.11.2018
 */

public class TextElement extends SettingsElement {

	int height;
	int spaceTop;
	boolean centered;

	public TextElement(String text, int spaceTop, int height) {
		super(text, null);
		this.spaceTop = spaceTop;
		this.height = height;
	}

	public TextElement(String text) {
		super(text, null);
		this.spaceTop = 5;
		this.height = 10;
	}

	public TextElement(String text, boolean centered) {
		super(text, null);
		this.spaceTop = 5;
		this.height = 10;
		this.centered = centered;
	}

	public void init() {
	}

	public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
		super.draw(x, y, maxX, maxY, mouseX, mouseY);
		int absoluteY = y + spaceTop;
		if (centered)
			LabyMod.getInstance().getDrawUtils().drawCenteredString(super.getDisplayName(), x, absoluteY);
		else
			LabyMod.getInstance().getDrawUtils().drawString(super.getDisplayName(), x, absoluteY);
	}

	public int getEntryHeight() {
		return height;
	}

	public int getEntryWidth() {
		return 85;
	}

	public void drawDescription(int x, int y, int screenWidth) {
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
	}

	public void keyTyped(char typedChar, int keyCode) {
	}

	public void mouseRelease(int mouseX, int mouseY, int mouseButton) {
	}

	public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {
	}

	public void unfocus(int mouseX, int mouseY, int mouseButton) {
	}
}

