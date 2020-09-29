package de.jumpingpxl.jumpingaddon.util.languaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.Configuration;
import de.jumpingpxl.jumpingaddon.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 17.03.2019
 */

public class LanguageHandler {

	private final JumpingAddon jumpingAddon;
	private final Configuration languageConfiguration;
	private final List<String> languages = new ArrayList<>();
	private final List<Message> messages = new ArrayList<>();

	public LanguageHandler(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
		languageConfiguration = new Configuration(jumpingAddon,
				new Configuration.ConfigManager(new File(jumpingAddon.getAddonFolder(), "language.json"),
						jumpingAddon.getSettings().getJsonObjectFromResource("language.json")));
	}

	public LanguageHandler loadLanguages(boolean thread) {
		Runnable runnable = () -> {
			jumpingAddon.log("[JumpingAddon] Loading the Languages...");
			try {
				JsonObject json = jumpingAddon.getSettings().getJsonObjectFromUrl("language.json",
						languageConfiguration.getConfig());
				languageConfiguration.getConfigManager().setConfiguration(json);
				languageConfiguration.save();
				json.getAsJsonArray("language").forEach(element -> {
					JsonObject object = element.getAsJsonObject();
					String language = object.get("memberName").getAsString();
					jumpingAddon.log("[JumpingAddon] Loading the language " + language + "...");
					JsonArray jsonArray = object.getAsJsonArray("messages");
					jsonArray.forEach(jsonElement -> {
						JsonObject jsonObject = jsonElement.getAsJsonObject();
						messages.add(
								new Message(language.toLowerCase(), jsonObject.get("memberName").getAsString(),
										jsonObject.get("message").getAsString()));
					});
					languages.add(language);
					jumpingAddon.log("[JumpingAddon] Successfully loaded the language " + language);
				});
				jumpingAddon.log(
						"[JumpingAddon] Successfully loaded " + languages.size() + " languages and "
								+ messages.size() + " strings");
			} catch (Exception e) {
				jumpingAddon.log("[JumpingAddon] An error occurred while loading the Languages");
			}
		};
		if (thread) {
			new Thread(runnable).start();
		} else {
			runnable.run();
		}
		return this;
	}

	public String getMessage(String message, String... replace) {
		if (messages.isEmpty()) {
			return "";
		}
		String language = jumpingAddon.getSettings().getLanguage().getAsString();
		if (!languages.contains(language)) {
			jumpingAddon.getSettings().getLanguage().setValue("Deutsch");
			return getMessage(message, replace);
		}
		String string = "";
		for (Message messages : messages) {
			if (messages.getMemberName().equals(message)) {
				if (messages.getLanguage().equalsIgnoreCase(language)) {
					string = messages.getMessage();
					break;
				}
			}
		}
		string = string.replace("%prefix",
				StringUtil.PREFIX.substring(0, StringUtil.PREFIX.length() - 3));
		int i = 0;
		for (String strings : replace) {
			string = string.replace("{" + i + "}", strings);
			i++;
		}
		return string;
	}

	public JumpingAddon getJumpingAddon() {
		return this.jumpingAddon;
	}

	public Configuration getLanguageConfiguration() {
		return this.languageConfiguration;
	}

	public List<String> getLanguages() {
		return this.languages;
	}

	public List<Message> getMessages() {
		return this.messages;
	}

	public class Message {

		private final String language;
		private final String memberName;
		private final String message;

		Message(String language, String memberName, String message) {
			this.language = language;
			this.memberName = memberName;
			this.message = message;
		}

		public String getLanguage() {
			return this.language;
		}

		public String getMemberName() {
			return this.memberName;
		}

		public String getMessage() {
			return this.message;
		}
	}
}
