package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public class GuiOpenListener {

	private JumpingAddon jumpingAddon;

	public GuiOpenListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		jumpingAddon.getSettings().setPreviousGui(jumpingAddon.getSettings().getCurrentGui());
		jumpingAddon.getSettings().setCurrentGui(event.gui);
	}
}
