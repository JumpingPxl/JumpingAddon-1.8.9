package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.labymod.api.events.RenderEntityEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public class RenderEntityListener implements RenderEntityEvent {

	private JumpingAddon jumpingAddon;

	public RenderEntityListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public void onRender(Entity entity, double x, double y, double z, float partialTicks) {
		jumpingAddon.getModuleHandler().getHitBoxModule().renderHitBox(entity, x, y, z, partialTicks);
		if (entity instanceof EntityPlayer)
			jumpingAddon.getModuleHandler().getPingModule().render(entity, x, y, z);
	}
}
