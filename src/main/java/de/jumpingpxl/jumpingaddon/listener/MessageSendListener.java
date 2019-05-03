package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.labymod.api.events.MessageSendEvent;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public class MessageSendListener implements MessageSendEvent {

	private JumpingAddon jumpingAddon;

	public MessageSendListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public boolean onSend(String message) {
		return jumpingAddon.getCommandHandler().handleCommand(message.split(" "));
	}
}
