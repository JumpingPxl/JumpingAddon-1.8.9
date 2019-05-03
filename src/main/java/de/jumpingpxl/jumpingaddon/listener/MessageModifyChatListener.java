package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.ChatComponent;
import net.labymod.api.events.MessageModifyChatEvent;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public class MessageModifyChatListener implements MessageModifyChatEvent {

	private JumpingAddon jumpingAddon;

	public MessageModifyChatListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public Object onModifyChatMessage(Object object) {
		System.out.println(((IChatComponent) object).getFormattedText().replace("§r", ""));
		ChatComponent chatComponent = new ChatComponent((IChatComponent) object);
		if (jumpingAddon.getConnection().getSupport() != null)
			chatComponent = jumpingAddon.getConnection().getSupport().handleIncomingMessage(chatComponent,
					chatComponent.getFormattedText(), chatComponent.getUnformattedText());
		if (jumpingAddon.getSettings().getChatTime().getAsBoolean())
			chatComponent = new ChatComponent(jumpingAddon.getSettings().getChatTimePrefix().getAsString('&').replace(
					"%time%", jumpingAddon.getStringUtils().formatDate(jumpingAddon.getSettings().getChatTimeFormat()
							.getAsString(), System.currentTimeMillis()))).append(chatComponent);
		return chatComponent.create();
	}
}
