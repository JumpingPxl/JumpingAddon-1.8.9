package de.jumpingpxl.jumpingaddon.util.gui;

import net.minecraft.client.gui.GuiScreen;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 09.04.2019
 */

public interface GuiEditor {
	Class<? extends GuiScreen> getGuiScreen();

	void drawScreen(GuiScreen guiScreen, int mouseX, int mouseY, float partialTicks);

	void mouseClicked(GuiScreen guiScreen, int mouseX, int mouseY);
}
