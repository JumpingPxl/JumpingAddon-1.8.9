package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 09.04.2019
 */

public class RenderWorldLastListener {

	private final JumpingAddon jumpingAddon;

	public RenderWorldLastListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@SubscribeEvent
	public void onWorldRender(RenderWorldLastEvent event) {
	}
}
