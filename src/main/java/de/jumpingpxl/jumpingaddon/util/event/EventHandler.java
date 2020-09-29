package de.jumpingpxl.jumpingaddon.util.event;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.listener.ClientTickListener;
import de.jumpingpxl.jumpingaddon.listener.GameTypeUpdateListener;
import de.jumpingpxl.jumpingaddon.listener.GuiOpenListener;
import de.jumpingpxl.jumpingaddon.listener.JoinServerListener;
import de.jumpingpxl.jumpingaddon.listener.MessageModifyChatListener;
import de.jumpingpxl.jumpingaddon.listener.MessageReceiveListener;
import de.jumpingpxl.jumpingaddon.listener.MessageSendListener;
import de.jumpingpxl.jumpingaddon.listener.PacketReceiveListener;
import de.jumpingpxl.jumpingaddon.listener.QuitServerListener;
import de.jumpingpxl.jumpingaddon.listener.RenderEntityListener;
import de.jumpingpxl.jumpingaddon.listener.RenderSignListener;
import de.jumpingpxl.jumpingaddon.listener.RenderWorldLastListener;
import de.jumpingpxl.jumpingaddon.listener.TabListUpdateListener;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 25.02.2019
 */

public class EventHandler {

	private final JumpingAddon jumpingAddon;
	private MessageSendListener messageSendListener;
	private GameTypeUpdateListener gameTypeUpdateListener;
	private RenderSignListener renderSignListener;

	public EventHandler(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public EventHandler registerListener() {
		jumpingAddon.getApi().registerForgeListener(new ClientTickListener(jumpingAddon));
		jumpingAddon.getApi().registerForgeListener(new GuiOpenListener(jumpingAddon));
		jumpingAddon.getApi().getEventManager().registerOnJoin(
				new JoinServerListener(jumpingAddon).onJoinServer());
		jumpingAddon.getApi().getEventManager().register(new MessageModifyChatListener(jumpingAddon));
		jumpingAddon.getApi().getEventManager().register(new MessageReceiveListener(jumpingAddon));
		messageSendListener = new MessageSendListener(jumpingAddon);
		jumpingAddon.getApi().getEventManager().register(messageSendListener);
		jumpingAddon.getApi().getEventManager().registerOnIncomingPacket(
				new PacketReceiveListener(jumpingAddon).onPacketReceive());
		jumpingAddon.getApi().getEventManager().registerOnQuit(
				new QuitServerListener(jumpingAddon).onQuitServer());
		jumpingAddon.getApi().getEventManager().register(new RenderEntityListener(jumpingAddon));
		jumpingAddon.getApi().registerForgeListener(new RenderWorldLastListener(jumpingAddon));
		jumpingAddon.getApi().getEventManager().register(new TabListUpdateListener(jumpingAddon));
		gameTypeUpdateListener = new GameTypeUpdateListener(jumpingAddon);
		renderSignListener = new RenderSignListener(jumpingAddon);
		return this;
	}

	public JumpingAddon getJumpingAddon() {
		return this.jumpingAddon;
	}

	public MessageSendListener getMessageSendListener() {
		return this.messageSendListener;
	}

	public GameTypeUpdateListener getGameTypeUpdateListener() {
		return this.gameTypeUpdateListener;
	}

	public RenderSignListener getRenderSignListener() {
		return this.renderSignListener;
	}
}
