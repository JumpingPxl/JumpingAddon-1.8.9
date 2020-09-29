package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.ChatComponent;
import net.labymod.api.events.MessageModifyChatEvent;
import net.minecraft.util.IChatComponent;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public class MessageModifyChatListener implements MessageModifyChatEvent {

	private final JumpingAddon jumpingAddon;

	public MessageModifyChatListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public Object onModifyChatMessage(Object object) {
		IChatComponent component = (IChatComponent) object;
		ChatComponent chatComponent = new ChatComponent((IChatComponent) object);
		if (jumpingAddon.getConnection().getSupport() != null) {
			chatComponent =
					jumpingAddon.getConnection().getSupport().handleIncomingMessage(chatComponent,
					chatComponent.getFormattedText(), chatComponent.getUnformattedText());
			component = chatComponent.create();
		}

		if (jumpingAddon.isDebug()) {
			StringBuilder iComponent = new StringBuilder();
			((IChatComponent) object).getSiblings().forEach(iChatComponent -> {
				iComponent.append(",\"");
				iComponent.append(iChatComponent.getFormattedText().replace("§r", ""));
				iComponent.append("\"");
				if (iChatComponent.getChatStyle().getChatClickEvent() != null
						|| iChatComponent.getChatStyle().getChatHoverEvent() != null) {
					iComponent.append("(");
					if (iChatComponent.getChatStyle().getChatClickEvent() != null) {
						iComponent.append("CHAT[Value=")
								.append(iChatComponent.getChatStyle().getChatClickEvent().getValue())
								.append(",Action=")
								.append(iChatComponent.getChatStyle().getChatClickEvent().getAction())
								.append("]")
								.append(iChatComponent.getChatStyle().getChatHoverEvent() == null ? "" : ",");
					}
					if (iChatComponent.getChatStyle().getChatHoverEvent() != null) {
						iComponent.append("HOVER[Value=")
								.append(iChatComponent.getChatStyle().getChatHoverEvent().getValue())
								.append(",Action=")
								.append(iChatComponent.getChatStyle().getChatHoverEvent().getAction())
								.append("]");
					}
					iComponent.append(")");
				}
			});
			StringBuilder cComponent = new StringBuilder();
			chatComponent.getSiblings().forEach(iChatComponent -> {
				cComponent.append(",\"");
				cComponent.append(iChatComponent.getFormattedText().replace("§r", ""));
				cComponent.append("\"");
				if (iChatComponent.isClickEvent() || iChatComponent.isHoverEvent()) {
					cComponent.append("(");
					if (iChatComponent.isClickEvent()) {
						cComponent.append("CHAT[Value=").append(iChatComponent.getClickEventValue()).append(
								",Action=").append(iChatComponent.getClickEventAction()).append("]").append(
								iChatComponent.isHoverEvent() ? "" : ",");
					}
					if (iChatComponent.isHoverEvent()) {
						cComponent.append("HOVER[Value=").append(iChatComponent.getHoverEventValue()).append(
								",Action=").append(iChatComponent.getHoverEventAction()).append("]");
					}
					cComponent.append(")");
				}
			});
			System.out.println(
					"IComponent1 " + ((IChatComponent) object).getFormattedText().replace("§r", ""));
			if (!iComponent.toString().isEmpty()) {
				System.out.println("IComponent2 " + iComponent.substring(1));
			}
			if (!cComponent.toString().isEmpty()) {
				System.out.println("IComponent3 " + cComponent.substring(1));
			}
		}
		return component;
	}
}
