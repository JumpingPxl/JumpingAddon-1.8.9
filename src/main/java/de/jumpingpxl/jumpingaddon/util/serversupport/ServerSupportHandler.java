package de.jumpingpxl.jumpingaddon.util.serversupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.Configuration;
import de.jumpingpxl.jumpingaddon.util.SettingValue;
import net.labymod.utils.Consumer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 15.03.2019
 */

public class ServerSupportHandler {

	private final JumpingAddon jumpingAddon;
	private final Configuration gameTypeConfiguration;
	private final Configuration checkMessagesConfiguration;

	public ServerSupportHandler(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
		gameTypeConfiguration = new Configuration(jumpingAddon,
				new Configuration.ConfigManager(new File(jumpingAddon.getAddonFolder(), "gametypes.json"),
						jumpingAddon.getSettings().getJsonObjectFromResource("gametypes.json")));
		checkMessagesConfiguration = new Configuration(jumpingAddon, new Configuration.ConfigManager(
				new File(jumpingAddon.getAddonFolder(), "checkmessages.json"),
				jumpingAddon.getSettings().getJsonObjectFromResource("checkmessages.json")));
	}

	public ServerSupportHandler loadGameTypes(boolean thread) {
		Runnable runnable = () -> {
			jumpingAddon.log("[JumpingAddon] Loading GameTypes...");
			try {
				JsonObject json = jumpingAddon.getSettings().getJsonObjectFromUrl("gametypes.json",
						gameTypeConfiguration.getConfig());
				gameTypeConfiguration.getConfigManager().setConfiguration(json);
				gameTypeConfiguration.save();
				JsonObject finalJson = json;
				Arrays.stream(Server.values()).filter(server -> server.getServerSupport() != null).filter(
						server -> finalJson.has(server.getName().toLowerCase())).forEach(server -> {
					jumpingAddon.log("[JumpingAddon] Loading the GameTypes for " + server.getName() + "...");
					JsonArray jsonArray = finalJson.getAsJsonArray(server.getName().toLowerCase());
					List<GameType> list = new ArrayList<>();
					jsonArray.forEach(jsonElement -> {
						JsonObject jsonObject = jsonElement.getAsJsonObject();
						list.add(new GameType(jsonObject.get("definition").getAsString(),
								jsonObject.get("name").getAsString(), jsonObject.get("prefix").isJsonNull() ? null
								: jsonObject.get("prefix").getAsString(),
								jsonObject.get("minigame").getAsBoolean()));
					});
					server.getServerSupport().applyGameTypes(list);
					jumpingAddon.log(
							"[JumpingAddon] Loaded " + list.size() + " GameTypes for " + server.getName());
				});
				jumpingAddon.log("[JumpingAddon] Successfully loaded the GameTypes");
			} catch (Exception e) {
				jumpingAddon.log("[JumpingAddon] An error occurred while loading the GameTypes");
			}
		};
		if (thread) {
			new Thread(runnable).start();
		} else {
			runnable.run();
		}
		return this;
	}

	public ServerSupportHandler loadCheckMessages(boolean thread) {
		Runnable runnable = () -> {
			jumpingAddon.log("[JumpingAddon] Loading CheckMessages...");
			try {
				JsonObject json = jumpingAddon.getSettings().getJsonObjectFromUrl("checkmessages.json",
						checkMessagesConfiguration.getConfig());
				checkMessagesConfiguration.getConfigManager().setConfiguration(json);
				checkMessagesConfiguration.save();
				JsonObject finalJson = json;
				Arrays.stream(Server.values()).filter(server -> server.getServerSupport() != null).filter(
						server -> finalJson.has(server.getName().toLowerCase())).forEach(server -> {
					jumpingAddon.log(
							"[JumpingAddon] Loading the CheckMessages for " + server.getName() + "...");
					JsonArray jsonArray = finalJson.getAsJsonArray(server.getName().toLowerCase());
					jsonArray.forEach(jsonElement -> {
						JsonObject jsonObject = jsonElement.getAsJsonObject();
						List<Pattern> list = new ArrayList<>();
						jsonObject.get("messages").getAsJsonArray().forEach(
								checkMessage -> list.add(Pattern.compile(checkMessage.getAsString())));
						server.getServerSupport().applyCheckMessage(jsonObject.get("definition").getAsString(),
								list);
					});
				});
				jumpingAddon.log("[JumpingAddon] Successfully loaded the CheckMessages");
			} catch (Exception e) {
				jumpingAddon.log("[JumpingAddon] An error occurred while loading the CheckMessages");
			}
		};
		if (thread) {
			new Thread(runnable).start();
		} else {
			runnable.run();
		}
		return this;
	}

	public GameType getByDefinition(List<GameType> list, String definition) {
		GameType gameType = null;
		for (GameType gameTypes : list) {
			if (gameTypes.getDefinition().equals(definition.toLowerCase())) {
				gameType = gameTypes;
			}
		}
		return gameType;
	}

	public static class GameType {

		private final String definition;
		private final String name;
		private final String prefix;
		private final boolean miniGame;

		public GameType(String definition, String name, String prefix, boolean miniGame) {
			this.definition = definition;
			this.name = name;
			this.prefix = prefix;
			this.miniGame = miniGame;
		}

		public String getDefinition() {
			return this.definition;
		}

		public String getName() {
			return this.name;
		}

		public String getPrefix() {
			return this.prefix;
		}

		public boolean isMiniGame() {
			return this.miniGame;
		}
	}

	public static class CheckMessage {

		private String definition;
		private List<Pattern> messages = new ArrayList<>();

		public CheckMessage(List<CheckMessage> list, String definition) {
			this.definition = definition;
			list.add(this);
		}

		public boolean matches(String message, Consumer<String> consumer) {
			boolean found = false;
			for (Pattern pattern : messages) {
				Matcher matcher = pattern.matcher(message.replace("§r", ""));
				while (matcher.find()) {
					found = true;
					if (consumer != null) {
						consumer.accept(matcher.group());
					}
				}
			}
			return found;
		}

		public boolean matches(SettingValue settingValue, String message, Consumer<String> consumer) {
			return settingValue.getAsBoolean() && matches(message, consumer);
		}

		public String getDefinition() {
			return this.definition;
		}

		public List<Pattern> getMessages() {
			return this.messages;
		}

		public void setDefinition(String definition) {
			this.definition = definition;
		}

		public void setMessages(List<Pattern> messages) {
			this.messages = messages;
		}

/*		public boolean matches(String message) {
			boolean isMessage = false;
			for (String strings : messages)
				if (strings.contains(";+;")) {
					String[] split = strings.split(";\\+;");
					if (split.length == 1) {
						isMessage = message.startsWith(strings);
						if (isMessage) break;
					} else {
						isMessage = (message.startsWith(split[0]) && message.endsWith(split[1]));
						if (isMessage) break;
					}
				} else {
					isMessage = message.startsWith(strings);
					if (isMessage) break;
				}
			return isMessage;
		} */
	}
}
