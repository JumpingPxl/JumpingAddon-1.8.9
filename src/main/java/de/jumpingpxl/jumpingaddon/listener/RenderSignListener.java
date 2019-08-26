package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.minecraft.tileentity.TileEntitySign;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 12.06.2019
 */

public class RenderSignListener {

	private JumpingAddon jumpingAddon;

	public RenderSignListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public void onRenderSign(TileEntitySign tileEntitySign, double x, double y, double z, float partialTicks, int destroyStage) {
		jumpingAddon.getModuleHandler().getSignSearchModule().renderTileEntitySign(tileEntitySign);
	}
}
