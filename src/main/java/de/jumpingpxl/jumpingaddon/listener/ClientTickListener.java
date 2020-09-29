package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.labymod.core.LabyModCore;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public class ClientTickListener {

	//MOUSE -> 0 = Links, 1 = Rechts, 2 = Mid, 3 = G4, 4 = G5

	private final JumpingAddon jumpingAddon;

	public ClientTickListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (jumpingAddon.getSettings().getNobob().getAsBoolean()
				&& LabyModCore.getMinecraft().getWorld() != null) {
			LabyModCore.getMinecraft().getPlayer().distanceWalkedModified = 0.0F;
		}
	}
}
