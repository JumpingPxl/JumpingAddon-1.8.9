package de.jumpingpxl.jumpingaddon.util.gui;

import net.labymod.ingamechat.GuiChatCustom;
import net.labymod.main.LabyMod;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 09.04.2019
 */

public class ModChatGui implements GuiEditor {

	@Override
	public Class<? extends GuiScreen> getGuiScreen() {
		return GuiChatCustom.class;
	}

	@Override
	public void drawScreen(GuiScreen guiScreen, int mouseX, int mouseY, float partialTicks) {
		LabyMod.getInstance().getDrawUtils().drawCenteredString("§d<dddddddddddd", (double) (guiScreen.width / 2), (double) (guiScreen.height / 2 - 100));
	}

	@Override
	public void mouseClicked(GuiScreen guiScreen, int mouseX, int mouseY) {
		System.out.println("CHATCLICK " + mouseX + ";" + mouseY + ";" + guiScreen.width + ";" + guiScreen.height);
	}
}
