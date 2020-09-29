package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.serversupport.GommeHDSupport;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;
import de.jumpingpxl.jumpingaddon.util.serversupport.ServerSupportHandler;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 24.03.2019
 */

public class GameTypeUpdateListener {

	private final JumpingAddon jumpingAddon;

	public GameTypeUpdateListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public void onGameTypeUpdate(Server server, ServerSupportHandler.GameType gameType,
	                             ServerSupportHandler.GameType oldGameType) {
		if (server == Server.GOMMEHD_NET) {
			GommeHDSupport gommeHDSupport = (GommeHDSupport) server.getServerSupport();
			gommeHDSupport.setIngame(false);
		}
		if (!jumpingAddon.getIngameModuleHandler().getGameTypeModule().isShown()) {
			jumpingAddon.getIngameModuleHandler().getGameTypeModule().setShown(true);
		}
		jumpingAddon.getConnection().setGameType(gameType);
		jumpingAddon.getModuleHandler().getDiscordRPCModule().setGameType(gameType);
	}
}
