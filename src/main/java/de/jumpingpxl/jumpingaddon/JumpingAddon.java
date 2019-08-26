package de.jumpingpxl.jumpingaddon;

import com.google.gson.JsonObject;
import de.jumpingpxl.jumpingaddon.util.*;
import de.jumpingpxl.jumpingaddon.util.command.CommandHandler;
import de.jumpingpxl.jumpingaddon.util.event.EventHandler;
import de.jumpingpxl.jumpingaddon.util.languaging.LanguageHandler;
import de.jumpingpxl.jumpingaddon.util.mods.ModuleHandler;
import de.jumpingpxl.jumpingaddon.util.modules.IngameModuleHandler;
import de.jumpingpxl.jumpingaddon.util.serversupport.ServerSupportHandler;
import de.jumpingpxl.jumpingaddon.util.transformer.TransformHandler;
import lombok.Getter;
import lombok.Setter;
import net.labymod.api.LabyModAddon;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.tileentity.TileEntitySign;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 25.02.2019
 */

@Getter
public class JumpingAddon extends LabyModAddon {

	private double version = 1.3;
	@Setter
	private boolean debug;
	@Setter
	private boolean createdConfig;

	@Getter
	private static JumpingAddon instance;
	private File addonFolder = new File("LabyMod/", "JumpingAddon");
	private String minecraftVersion = "1.8.9";
	private Configuration.ConfigManager configManager;
	private JsonObject config;
	private Configuration configuration;
	private Configuration supportConfiguration;
	private Configuration commandConfiguration;

	private Connection connection;
	private Settings settings;
	private ModuleHandler moduleHandler;
	private EventHandler eventHandler;
	private IngameModuleHandler ingameModuleHandler;
	private CommandHandler commandHandler;
	private StringUtils stringUtils;
	private ServerSupportHandler serverSupportHandler;
	private LanguageHandler languageHandler;

	@Override
	public void onEnable() {
		instance = this;
		configManager = new Configuration.ConfigManager(new File(addonFolder, "configuration.json"));
		config = configManager.getJsonObject();
		configuration = new Configuration(this, "settings");
		supportConfiguration = new Configuration(this, "serversupport");
		commandConfiguration = new Configuration(this, "command");
		stringUtils = new StringUtils(this);
		settings = new Settings(this).loadSettings();
		connection = new Connection(this);
		languageHandler = new LanguageHandler(this).loadLanguages(true);
		serverSupportHandler = new ServerSupportHandler(this).loadGameTypes(true).loadCheckMessages(true);
		moduleHandler = new ModuleHandler(this).registerModules();
		eventHandler = new EventHandler(this).registerListener();
		commandHandler = new CommandHandler(this).registerCommands();
		ingameModuleHandler = new IngameModuleHandler(this).registerIngameModules();
		TransformHandler.setJumpingAddon(this);
		loadConfiguration();
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void loadConfig() {
	}

	@Override
	protected void fillSettings(List<SettingsElement> list) {
		settings.fillSettings(list);
	}

	@Override
	public void saveConfig() {
		if (configManager != null)
			configManager.save();
	}

	@Override
	public JsonObject getConfig() {
		return getConfiguration().getJsonObject();
	}

	public void loadConfiguration() {
		Configuration.getConfigurationList().forEach(Configuration::load);
		settings.loadConfig();
	}

	public TileEntitySignRenderer getCustomSignRenderer() {
		return new TileEntitySignRenderer() {
			public void renderTileEntityAt(TileEntitySign tileEntitySign, double x, double y, double z, float partialTicks, int destroyStage) {
				eventHandler.getRenderSignListener().onRenderSign(tileEntitySign, x, y, z, partialTicks, destroyStage);
				super.renderTileEntityAt(tileEntitySign, x, y, z, partialTicks, destroyStage);
			}
		};
	}

	public JsonObject getLabyModConfig() {
		return config;
	}

	public void log(String string) {
		LogManager.getLogger().log(Level.INFO, string);
	}

	public String getMessage(String message, String... replace) {
		return languageHandler.getMessage(message, replace);
	}

	public void sendMessage(String string) {
		if (!getEventHandler().getMessageSendListener().onSend(string))
			LabyModCore.getMinecraft().getPlayer().sendChatMessage(string);
	}

	public void displayChatComponent(ChatComponent chatComponent) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(chatComponent.create());
	}

	public void displayMessage(String string) {
		LabyMod.getInstance().displayMessageInChat(string);
	}

	public void displayPrefixMessage(String string) {
		displayMessage(getSettings().getPrefix() + string);
	}

	public void displayMessage(String message, String... replace) {
		displayMessage(languageHandler.getMessage(message, replace));
	}
}
