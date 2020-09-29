package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.gui.ModGuiSignSearch;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;
import net.labymod.core.LabyModCore;
import net.labymod.core_implementation.mc18.gui.ModGuiMainMenu;
import net.labymod.gui.GuiSignSearch;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.Map;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public class GuiOpenListener {

	private final JumpingAddon jumpingAddon;
	private boolean ownSignRenderer;

	public GuiOpenListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onGuiOpen(GuiOpenEvent event) {
		GuiScreen gui = LabyModCore.getForge().getGuiOpenEventGui(event);
		jumpingAddon.getSettings().setPreviousGui(jumpingAddon.getSettings().getCurrentGui());
		if (gui instanceof GuiSignSearch && jumpingAddon.getConnection().isOnServer(
				Server.GOMMEHD_NET)) {
			gui = new ModGuiSignSearch(jumpingAddon, null);
		}
		if ((gui instanceof ModGuiMainMenu || gui instanceof GuiMainMenu) && !ownSignRenderer) {
			Map mapSpecialRenderers = null;
			try {
				mapSpecialRenderers = (Map) ReflectionHelper.findField(TileEntityRendererDispatcher.class,
						LabyModCore.getMappingAdapter().getMapSpecialRenderersMappings()).get(
						TileEntityRendererDispatcher.instance);
			} catch (IllegalAccessException var7) {
				var7.printStackTrace();
			}
			TileEntitySignRenderer tileEntitySignRendererCustom = jumpingAddon.getCustomSignRenderer();
			mapSpecialRenderers.put(TileEntitySign.class, tileEntitySignRendererCustom);
			tileEntitySignRendererCustom.setRendererDispatcher(TileEntityRendererDispatcher.instance);
			ownSignRenderer = true;
		}
		LabyModCore.getForge().setGuiOpenEventGui(event, gui);
		jumpingAddon.getSettings().setCurrentGui(gui);
		if (jumpingAddon.isDebug()) {
			System.out.println("GUI " + gui);
		}
	}
}
