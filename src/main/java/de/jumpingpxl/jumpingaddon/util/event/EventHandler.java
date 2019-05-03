package de.jumpingpxl.jumpingaddon.util.event;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.listener.*;
import lombok.Getter;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 25.02.2019
 */

@Getter
public class EventHandler {

	private JumpingAddon jumpingAddon;
	private MessageSendListener messageSendListener;
	private GameTypeUpdateListener gameTypeUpdateListener;

	public EventHandler(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public EventHandler registerListener() {
		jumpingAddon.getApi().registerForgeListener(new ClientTickListener(jumpingAddon));
		jumpingAddon.getApi().registerForgeListener(new GuiOpenListener(jumpingAddon));
		jumpingAddon.getApi().getEventManager().registerOnJoin(new JoinServerListener(jumpingAddon).onJoinServer());
		jumpingAddon.getApi().getEventManager().register(new MessageModifyChatListener(jumpingAddon));
		jumpingAddon.getApi().getEventManager().register(new MessageReceiveListener(jumpingAddon));
		messageSendListener = new MessageSendListener(jumpingAddon);
		jumpingAddon.getApi().getEventManager().register(messageSendListener);
		jumpingAddon.getApi().getEventManager().registerOnIncomingPacket(new PacketReceiveListener(jumpingAddon).onPacketReceive());
		jumpingAddon.getApi().getEventManager().registerOnQuit(new QuitServerListener(jumpingAddon).onQuitServer());
		jumpingAddon.getApi().getEventManager().register(new RenderEntityListener(jumpingAddon));
		jumpingAddon.getApi().getEventManager().register(new TabListUpdateListener(jumpingAddon));
		gameTypeUpdateListener = new GameTypeUpdateListener(jumpingAddon);
		return this;
	}
}
