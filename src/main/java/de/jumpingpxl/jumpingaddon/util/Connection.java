package de.jumpingpxl.jumpingaddon.util;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.serversupport.GommeHDSupport;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;
import de.jumpingpxl.jumpingaddon.util.serversupport.ServerSupport;
import de.jumpingpxl.jumpingaddon.util.serversupport.ServerSupportHandler;
import net.labymod.core.LabyModCore;
import net.labymod.utils.ServerData;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.ChatComponentText;

import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public class Connection {

	private final JumpingAddon jumpingAddon;
	private final GommeHDSupport gommeHD;
	private Server server;
	private String serverDomain;
	private Integer serverPort;
	private boolean afk;
	private ServerSupportHandler.GameType gameType;
	private String lastActionMessage;

	public Connection(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
		gameType = new ServerSupportHandler.GameType("", "Unknown", "", false);
		gommeHD = new GommeHDSupport(jumpingAddon);
	}

	public boolean isOnServer(Server server) {
		return isSupported() && this.server == server;
	}

	public boolean isSupported() {
		return server != null;
	}

	public ServerSupport getSupport() {
		return server == null ? null : Server.getSupportMap().get(server);
	}

	public void connect(ServerData serverData) {
		serverDomain = serverData.getIp();
		serverPort = serverData.getPort();
		for (Server server : Server.values()) {
			for (String string : server.getDomains()) {
				if (serverDomain.toLowerCase().endsWith(string.toLowerCase())) {
					this.server = server;
					break;
				}
			}
		}

		jumpingAddon.getModuleHandler().getDiscordRPCModule().connectToServer(serverData);
	}

	public void disconnect() {
		server = null;
		serverDomain = null;
		serverPort = null;
		afk = false;
		jumpingAddon.getModuleHandler().getDiscordRPCModule().disconnectFromServer();
		if (jumpingAddon.getIngameModuleHandler().getGameTypeModule().isShown()) {
			jumpingAddon.getIngameModuleHandler().getGameTypeModule().setShown(false);
		}
	}

	public ServerSupportHandler.GameType handleGameType(
			List<ServerSupportHandler.GameType> gameTypeList,
			ServerSupportHandler.CheckMessage gameTypeCheck, String unformatted) {
		ServerSupportHandler.GameType[] gameType = {null};
		String gameTypeString;
		if (!gameTypeCheck.matches(unformatted, string -> {
			for (ServerSupportHandler.GameType gameTypes : gameTypeList) {
				if (gameTypes.getDefinition().equalsIgnoreCase(string.replace(" ", ""))) {
					gameType[0] = gameTypes;
					break;
				}
			}
		})) {
			gameType[0] = new ServerSupportHandler.GameType("", "Unknown", "", false);
		}
		jumpingAddon.getEventHandler().getGameTypeUpdateListener().onGameTypeUpdate(
				jumpingAddon.getConnection().getServer(), gameType[0],
				jumpingAddon.getConnection().getGameType());
		return gameType[0];
	}

	public ServerSupportHandler.GameType getGameType() {
		if (gameType == null) {
			return new ServerSupportHandler.GameType("", "Unknown", "", false);
		}
		return gameType;
	}

	public void setGameType(ServerSupportHandler.GameType gameType) {
		this.gameType = gameType;
	}

	public boolean playerExists(String name) {
		boolean exists = false;
		for (NetworkPlayerInfo playerInfo : LabyModCore.getMinecraft()
				.getConnection()
				.getPlayerInfoMap()) {
			if (playerInfo.getGameProfile().getName().equalsIgnoreCase(StringUtil.stripColor(name))) {
				exists = true;
				break;
			}
		}
		return exists;
	}

	public void sendTitle(String title, String subtitle) {
		LabyModCore.getMinecraft().getConnection().handleTitle(
				new S45PacketTitle(S45PacketTitle.Type.TITLE,
						new ChatComponentText(title == null ? "" : title), 0, 1, 0));
		LabyModCore.getMinecraft().getConnection().handleTitle(
				new S45PacketTitle(S45PacketTitle.Type.SUBTITLE,
						new ChatComponentText(subtitle == null ? "" : subtitle), 0, 1, 0));
	}

	public JumpingAddon getJumpingAddon() {
		return this.jumpingAddon;
	}

	public Server getServer() {
		return this.server;
	}

	public String getServerDomain() {
		return this.serverDomain;
	}

	public Integer getServerPort() {
		return this.serverPort;
	}

	public boolean isAfk() {
		return this.afk;
	}

	public void setAfk(boolean value) {
		this.afk = value;
		jumpingAddon.getModuleHandler().getDiscordRPCModule().setIdle(value);
	}

	public GommeHDSupport getGommeHD() {
		return this.gommeHD;
	}

	public String getLastActionMessage() {
		return this.lastActionMessage;
	}

	public void setLastActionMessage(String lastActionMessage) {
		this.lastActionMessage = lastActionMessage;
	}
}
