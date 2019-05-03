package de.jumpingpxl.jumpingaddon.util;

import com.google.gson.*;
import de.jumpingpxl.jumpingaddon.JumpingAddon;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 06.03.2019
 */

@Getter
public class Configuration {

	@Getter
	private static List<Configuration> configurationList = new ArrayList<>();
	private ConfigManager configManager;
	private JsonObject config;
	private JumpingAddon jumpingAddon;
	private Configuration superConfiguration;
	private String memberName;
	private JsonObject jsonObject;

	public Configuration(JumpingAddon jumpingAddon, ConfigManager configManager) {
		this.jumpingAddon = jumpingAddon;
		this.configManager = configManager;
		this.config = configManager.getJsonObject();
		configurationList.add(this);
	}

	public Configuration(JumpingAddon jumpingAddon, String fileName, Configuration superConfiguration, String memberName) {
		this.jumpingAddon = jumpingAddon;
		this.configManager = fileName == null ? jumpingAddon.getConfigManager() : new ConfigManager(new File(jumpingAddon.getAddonFolder(), fileName));
		this.config = configManager.getJsonObject();
		this.superConfiguration = superConfiguration;
		this.memberName = memberName;
		configurationList.add(this);
	}

	public Configuration(JumpingAddon jumpingAddon, String memberName) {
		this(jumpingAddon, null, null, memberName);
	}

	public Configuration(JumpingAddon jumpingAddon, String fileName, String memberName) {
		this(jumpingAddon, fileName, null, memberName);
	}

	public Configuration(JumpingAddon jumpingAddon, Configuration superConfiguration, String memberName) {
		this(jumpingAddon, null, superConfiguration, memberName);
	}

	public void load() {
		if (superConfiguration == null) {
			if (memberName != null) {
				if (!config.has(memberName)) {
					config.add(memberName, new JsonObject());
					configManager.save();
				}
				this.jsonObject = config.getAsJsonObject(memberName);
			} else
				this.jsonObject = config;
		} else {
			if (memberName != null) {
				if (!superConfiguration.has(memberName)) {
					superConfiguration.set(memberName, new JsonObject());
					configManager.save();
				}
				this.jsonObject = superConfiguration.get(memberName).getAsJsonObject();
			} else
				this.jsonObject = config;
		}
	}

	public void addToConfig() {
		if (superConfiguration == null) {
			if (memberName != null)
				config.add(memberName, jsonObject);
		} else {
			superConfiguration.set(memberName, jsonObject);
			superConfiguration.addToConfig();
		}
	}

	public void save() {
		addToConfig();
		configManager.save();
	}

	public void set(String property, String value) {
		getRawJsonObject().addProperty(property, value);
	}

	public void set(String property, int value) {
		getRawJsonObject().addProperty(property, value);
	}

	public void set(String property, boolean value) {
		getRawJsonObject().addProperty(property, value);
	}

	public void set(String property, JsonElement value) {
		getRawJsonObject().add(property, value);
	}

	public String getAsString(String memberName) {
		return get(memberName).getAsString();
	}

	public int getAsInt(String memberName) {
		return get(memberName).getAsInt();
	}

	public boolean getAsBoolean(String memberName) {
		return get(memberName).getAsBoolean();
	}

	public JsonElement get(String memberName) {
		return getRawJsonObject().get(memberName);
	}

	public boolean has(String memberName) {
		if (jsonObject == null)
			System.out.println(memberName);
		return getRawJsonObject().has(memberName);
	}

	public boolean exists() {
		return configManager.file.exists();
	}

	private JsonObject getRawJsonObject() {
		if (jsonObject == null)
			load();
		return jsonObject;
	}

	public static class ConfigManager {
		private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
		private File file;
		@Setter
		private JsonObject configuration;

		public ConfigManager(File file, JsonObject defaultValue) {
			this.file = file;
			this.loadConfig(defaultValue);
		}

		public ConfigManager(File file) {
			this(file, new JsonObject());
		}

		private void loadConfig(JsonObject defaultValue) {
			if (!file.getParentFile().exists())
				file.getParentFile().mkdir();
			boolean createdNewFile = !file.exists();
			if (createdNewFile) {
				try {
					file.createNewFile();
					Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
					writer.write(gson.toJson(defaultValue));
					writer.flush();
					writer.close();
					configuration = defaultValue;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),
							StandardCharsets.UTF_8);
					configuration = (JsonObject) new JsonParser().parse(inputStreamReader);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void save() {
			try {
				Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
				writer.write(gson.toJson(configuration));
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public JsonObject getJsonObject() {
			return configuration;
		}

		public File getFile() {
			return file;
		}
	}
}
