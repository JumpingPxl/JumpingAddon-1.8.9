package de.jumpingpxl.jumpingaddon.util.mods;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.Task;
import de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.DiscordRPC;
import de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.DiscordRichPresence;
import de.jumpingpxl.jumpingaddon.util.serversupport.ServerSupportHandler;
import net.labymod.main.Source;
import net.labymod.utils.ServerData;

import java.util.concurrent.TimeUnit;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 03.03.2019
 */

public class DiscordRPCModule {

	private final JumpingAddon jumpingAddon;
	private String state;
	private String details;
	private boolean initialized;
	private long lastAction = 0;

	public DiscordRPCModule(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public void load() {
		toggleStatus(jumpingAddon.getSettings().getDiscord().getAsBoolean());
		new Task(() -> {
			if (jumpingAddon.getSettings().getDiscord().getAsBoolean() && initialized) {
				DiscordRPC.discordRunCallbacks();
				update(getDefaultPresence());
			}
		}).repeat(2500, TimeUnit.MILLISECONDS);
	}

	private void initialize() {
		DiscordRPC.discordInitialize("502122710864756736", null, true);
		initialized = true;
	}

	public void toggleStatus(boolean value) {
		if (value) {
			enable();
		} else {
			disable();
		}
	}

	public void toggleSmallIconStatus(boolean value) {
		update(getDefaultPresence());
	}

	public void toggleTimeStampStatus(boolean value) {
		update(getDefaultPresence());
	}

	public void setState(DiscordRichPresence presence, String state) {
		this.state = state;
		if (presence != null) {
			presence.state = state;
		}
	}

	public void setDetails(DiscordRichPresence presence, String details) {
		this.details = details;
		if (presence != null) {
			presence.details = details;
		}
	}

	public void setGameType(ServerSupportHandler.GameType gameType) {
		DiscordRichPresence presence = getDefaultPresence();
		StringBuilder stringBuilder = new StringBuilder();
		if (jumpingAddon.getConnection().isAfk()) {
			stringBuilder.append(jumpingAddon.getMessage("discordIdle"));
		} else {
			stringBuilder.append(jumpingAddon.getMessage("discordPlaying"));
		}
		if (gameType == null) {
			stringBuilder.append(jumpingAddon.getMessage("discordPlayingUnknown"));
		} else {
			if (!gameType.isMiniGame()) {
				stringBuilder.append(jumpingAddon.getMessage("discordPlayingOn"));
				if (gameType.getPrefix() == null) {
					stringBuilder.append(jumpingAddon.getMessage("discordPlayingOnA"));
				}
			} else if (jumpingAddon.getConnection().isAfk()) {
				stringBuilder.append(jumpingAddon.getMessage("discordIdleOn"));
			}
			stringBuilder.append(gameType.getName());
		}
		setState(presence,
				jumpingAddon.getConnection().isSupported() ? stringBuilder.toString() : null);
		update(presence);
	}

	public void setIdle(boolean value) {
		if (jumpingAddon.getConnection().getGameType() != null) {
			setGameType(jumpingAddon.getConnection().getGameType());
		} else {
			DiscordRichPresence presence = getDefaultPresence();
			setState(presence, value ? jumpingAddon.getMessage("discordIdle")
					: jumpingAddon.getMessage("discordIngame"));
			update(presence);
		}
	}

	public void connectToServer(ServerData serverData) {
		lastAction = System.currentTimeMillis();
		DiscordRichPresence presence = getDefaultPresence();
		setDetails(presence, jumpingAddon.getMessage("discordPlayingOn") + serverData.getIp() + (
				serverData.getPort() == 25565 ? "" : ":" + serverData.getPort()));
		setState(presence, jumpingAddon.getMessage("discordIngame"));
		update(presence);
	}

	public void disconnectFromServer() {
		lastAction = 0L;
		DiscordRichPresence presence = getDefaultPresence();
		setDetails(presence, jumpingAddon.getMessage("discordMenu"));
		setState(presence, null);
		update(presence);
	}

	private void update(DiscordRichPresence presence) {
		if (jumpingAddon.getSettings().getDiscord().getAsBoolean()) {
			DiscordRPC.discordUpdatePresence(presence);
		}
	}

	private void enable() {
		if (!initialized) {
			initialize();
		}
		update(getDefaultPresence());
	}

	private void disable() {
		if (!initialized) {
			return;
		}
		DiscordRPC.discordShutdown();
		initialized = false;
	}

	private DiscordRichPresence getDefaultPresence() {
		DiscordRichPresence presence = new DiscordRichPresence();
		presence.largeImageKey = "icon";
		presence.largeImageText = jumpingAddon.getMessage("discordLargeImage",
				jumpingAddon.getMinecraftVersion());
		if (jumpingAddon.getSettings().getDiscordSmallIcon().getAsBoolean()) {
			presence.smallImageKey = "labymod";
			presence.smallImageText = jumpingAddon.getMessage("discordSmallImage", Source.ABOUT_VERSION,
					String.valueOf(jumpingAddon.getVersion()));
		} else {
			presence.largeImageText += jumpingAddon.getMessage("discordLargeAddition",
					Source.ABOUT_VERSION, String.valueOf(jumpingAddon.getVersion()));
		}
		if (this.details != null) {
			presence.details = this.details;
		} else {
			setDetails(presence, jumpingAddon.getMessage("discordMenu"));
		}
		presence.state = this.state;
		if (jumpingAddon.getSettings().getDiscordTimeStamp().getAsBoolean()) {
			presence.startTimestamp = lastAction;
		}
		return presence;
	}
}
