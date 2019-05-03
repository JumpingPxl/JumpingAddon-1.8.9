package de.jumpingpxl.jumpingaddon.util.serversupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.Configuration;
import de.jumpingpxl.jumpingaddon.util.SettingValue;
import lombok.Getter;
import lombok.Setter;
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

	private JumpingAddon jumpingAddon;
	private Configuration gameTypeConfiguration;
	private Configuration checkMessagesConfiguration;

	public ServerSupportHandler(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
		gameTypeConfiguration = new Configuration(jumpingAddon, new Configuration.ConfigManager(new File(jumpingAddon.getAddonFolder(), "gametypes.json"), jumpingAddon.getSettings().getJsonObjectFromResource("gametypes.json")));
		checkMessagesConfiguration = new Configuration(jumpingAddon, new Configuration.ConfigManager(new File(jumpingAddon.getAddonFolder(), "checkmessages.json"), jumpingAddon.getSettings().getJsonObjectFromResource("checkmessages.json")));
	}

	public ServerSupportHandler loadGameTypes(boolean thread) {
		Runnable runnable = () -> {
			jumpingAddon.log("[JumpingAddon] Loading GameTypes...");
			try {
				JsonObject json = jumpingAddon.getSettings().getJsonObjectFromUrl("gametypes.json", gameTypeConfiguration.getConfig());
				gameTypeConfiguration.getConfigManager().setConfiguration(json);
				gameTypeConfiguration.save();
				JsonObject finalJson = json;
				Arrays.stream(Server.values()).filter(server -> server.getServerSupport() != null).filter(server -> finalJson.has(server.getName().toLowerCase())).forEach(server -> {
					jumpingAddon.log("[JumpingAddon] Loading the GameTypes for " + server.getName() + "...");
					JsonArray jsonArray = finalJson.getAsJsonArray(server.getName().toLowerCase());
					List<GameType> list = new ArrayList<>();
					jsonArray.forEach(jsonElement -> {
						JsonObject jsonObject = jsonElement.getAsJsonObject();
						list.add(new GameType(jsonObject.get("definition").getAsString(), jsonObject.get("name").getAsString(), jsonObject.get("prefix").isJsonNull() ? null : jsonObject.get("prefix").getAsString(), jsonObject.get("minigame").getAsBoolean()));
					});
					server.getServerSupport().applyGameTypes(list);
					jumpingAddon.log("[JumpingAddon] Loaded " + list.size() + " GameTypes for " + server.getName());
				});
				jumpingAddon.log("[JumpingAddon] Successfully loaded the GameTypes");
			} catch (Exception e) {
				jumpingAddon.log("[JumpingAddon] An error occurred while loading the GameTypes");
			}
		};
		if (thread)
			new Thread(runnable).start();
		else
			runnable.run();
		return this;
	}

	public ServerSupportHandler loadCheckMessages(boolean thread) {
		Runnable runnable = () -> {
			jumpingAddon.log("[JumpingAddon] Loading CheckMessages...");
			try {
				JsonObject json = jumpingAddon.getSettings().getJsonObjectFromUrl("checkmessages.json", checkMessagesConfiguration.getConfig());
				checkMessagesConfiguration.getConfigManager().setConfiguration(json);
				checkMessagesConfiguration.save();
				JsonObject finalJson = json;
				Arrays.stream(Server.values()).filter(server -> server.getServerSupport() != null).filter(server -> finalJson.has(server.getName().toLowerCase())).forEach(server -> {
					jumpingAddon.log("[JumpingAddon] Loading the CheckMessages for " + server.getName() + "...");
					JsonArray jsonArray = finalJson.getAsJsonArray(server.getName().toLowerCase());
					jsonArray.forEach(jsonElement -> {
						JsonObject jsonObject = jsonElement.getAsJsonObject();
						List<Pattern> list = new ArrayList<>();
						jsonObject.get("messages").getAsJsonArray().forEach(checkMessage -> list.add(Pattern.compile(checkMessage.getAsString())));
						server.getServerSupport().applyCheckMessage(jsonObject.get("definition").getAsString(), list);
					});
				});
				jumpingAddon.log("[JumpingAddon] Successfully loaded the CheckMessages");
			} catch (Exception e) {
				jumpingAddon.log("[JumpingAddon] An error occurred while loading the CheckMessages");
			}
		};
		if (thread)
			new Thread(runnable).start();
		else
			runnable.run();
		return this;
	}

	public GameType getByDefinition(List<GameType> list, String definition) {
		GameType gameType = null;
		for (GameType gameTypes : list)
			if (gameTypes.getDefinition().equals(definition.toLowerCase()))
				gameType = gameTypes;
		return gameType;
	}

	@Getter
	public static class GameType {

		private String definition;
		private String name;
		private String prefix;
		private boolean miniGame;

		public GameType(String definition, String name, String prefix, boolean miniGame) {
			this.definition = definition;
			this.name = name;
			this.prefix = prefix;
			this.miniGame = miniGame;
		}
	}

	@Getter
	@Setter
	public static class CheckMessage {

		private String definition;
		private List<Pattern> messages = new ArrayList<>();

		public CheckMessage(List<CheckMessage> list, String definition) {
			this.definition = definition;
			list.add(this);
		}

		public boolean matches(String message, Consumer<String> consumer) {
			String string = null;
			for (Pattern pattern : messages) {
				Matcher matcher = pattern.matcher(message.replace("§r", ""));
				if (matcher.find()) {
					string = matcher.group();
					break;
				}
			}
			if (string != null && consumer != null)
				consumer.accept(string);
			return string != null;
		}

		public boolean matches(SettingValue settingValue, String message, Consumer<String> consumer) {
			return settingValue.getAsBoolean() && matches(message, consumer);
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
