package de.jumpingpxl.jumpingaddon.util.gui;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 09.04.2019
 */

public class GuiEditorHandler {

	private JumpingAddon jumpingAddon;
	private List<GuiEditor> editors = new ArrayList<>();

	public GuiEditorHandler(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public GuiEditorHandler load() {
		if (!Mouse.isCreated())
			try {
				Mouse.create();
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		jumpingAddon.getApi().registerForgeListener(this);
		addEditor(new ModDisconnectedGui());
		addEditor(new ModChatGui());
		return this;
	}

	public void addEditor(GuiEditor guiEditor) {
		editors.add(guiEditor);
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (Minecraft.getMinecraft().currentScreen == null)
			return;
		if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1))
			this.editors.stream().filter(guiEditor -> guiEditor.getGuiScreen() == Minecraft.getMinecraft().currentScreen.getClass()).forEach(guiEditor -> guiEditor.mouseClicked(Minecraft.getMinecraft().currentScreen, Mouse.getX(), Mouse.getY()));
	}

	@SubscribeEvent
	public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
		this.editors.stream().filter(guiEditor -> guiEditor.getGuiScreen() == event.gui.getClass()).forEach(guiEditor -> guiEditor.drawScreen(event.gui, event.mouseX, event.mouseY, event.renderPartialTicks));
	}
}
