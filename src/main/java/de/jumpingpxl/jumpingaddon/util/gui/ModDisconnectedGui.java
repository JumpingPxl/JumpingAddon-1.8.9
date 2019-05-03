package de.jumpingpxl.jumpingaddon.util.gui;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.Task;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 09.04.2019
 */

public class ModDisconnectedGui implements GuiEditor {

	private String serverAddress = "";
	private int delay;
	List<String> multilineMessage;
	private GuiButton guiButton;
	private Task task;

	@Override
	public Class<? extends GuiScreen> getGuiScreen() {
		return GuiDisconnected.class;
	}

	@Override
	public void drawScreen(GuiScreen guiScreen, int mouseX, int mouseY, float partialTicks) {
		DrawUtils drawUtils = LabyMod.getInstance().getDrawUtils();
		if (!serverAddress.equals(JumpingAddon.getInstance().getConnection().getServerDomain() + ":" + JumpingAddon.getInstance().getConnection().getServerPort())) {
			serverAddress = JumpingAddon.getInstance().getConnection().getServerDomain() + ":" + JumpingAddon.getInstance().getConnection().getServerPort();
			multilineMessage = drawUtils.getFontRenderer().listFormattedStringToWidth(JumpingAddon.getInstance().getConnection().getLastKickMessage().getFormattedText(), guiScreen.width - 50);
			guiButton = new GuiButton(0, 0, 0, 100, 20, "");
			delay = 5;
			task = new Task(() -> {
				if (delay <= 0) {
					task.cancel();
					System.out.println("RLEOG " + serverAddress);
					LabyMod.getInstance().connectToServer(serverAddress);
					serverAddress = "";
					return;
				}
				delay--;
			}).repeat(1, TimeUnit.SECONDS);
		}
		guiButton.xPosition = guiScreen.width / 2 - 50;
		guiButton.yPosition = guiScreen.height / 2 + (multilineMessage.size() * drawUtils.getFontRenderer().FONT_HEIGHT) / 2 + drawUtils.getFontRenderer().FONT_HEIGHT + 25;
		guiButton.displayString = "Reconnect in " + delay + "s";
		guiButton.drawButton(guiScreen.mc, mouseX, mouseY);
	}

	@Override
	public void mouseClicked(GuiScreen guiScreen, int mouseX, int mouseY) {
		System.out.println("DISCONNECTEDCLICK " + mouseX + ";" + mouseY);
		if (guiButton != null && guiButton.isMouseOver()) {
			task.cancel();
			LabyMod.getInstance().connectToServer(serverAddress);
			serverAddress = "";
		}
	}
}
