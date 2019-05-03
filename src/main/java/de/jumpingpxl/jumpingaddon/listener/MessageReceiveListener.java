package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.labymod.api.events.MessageReceiveEvent;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public class MessageReceiveListener implements MessageReceiveEvent {

	private JumpingAddon jumpingAddon;

	public MessageReceiveListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public boolean onReceive(String formatted, String unformatted) {
		if (!unformatted.startsWith(jumpingAddon.getStringUtils().stripColor(jumpingAddon.getSettings().getPrefix())))
			jumpingAddon.getModuleHandler().getActionModule().checkMessage(unformatted);
		if (jumpingAddon.getConnection().getSupport() != null)
			return jumpingAddon.getConnection().getSupport().handleReceivingMessage(formatted, unformatted);
		return false;
	}
}
