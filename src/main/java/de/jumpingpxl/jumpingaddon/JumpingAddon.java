package de.jumpingpxl.jumpingaddon;

import com.google.gson.JsonObject;
import de.jumpingpxl.jumpingaddon.util.ChatComponent;
import de.jumpingpxl.jumpingaddon.util.Configuration;
import de.jumpingpxl.jumpingaddon.util.Connection;
import de.jumpingpxl.jumpingaddon.util.Settings;
import de.jumpingpxl.jumpingaddon.util.StringUtil;
import de.jumpingpxl.jumpingaddon.util.command.CommandHandler;
import de.jumpingpxl.jumpingaddon.util.event.EventHandler;
import de.jumpingpxl.jumpingaddon.util.languaging.LanguageHandler;
import de.jumpingpxl.jumpingaddon.util.mods.ModuleHandler;
import de.jumpingpxl.jumpingaddon.util.modules.IngameModuleHandler;
import de.jumpingpxl.jumpingaddon.util.serversupport.ServerSupportHandler;
import de.jumpingpxl.jumpingaddon.util.transformer.TransformHandler;
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

public class JumpingAddon extends LabyModAddon {

	private final double version = 1.4;
	private final File addonFolder = new File("LabyMod/", "JumpingAddon");
	private final String minecraftVersion = "1.8.9";
	private boolean debug;
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
	private ServerSupportHandler serverSupportHandler;
	private LanguageHandler languageHandler;

	@Override
	public void onEnable() {
		configManager = new Configuration.ConfigManager(new File(addonFolder, "configuration.json"));
		config = configManager.getJsonObject();
		configuration = new Configuration(this, "settings");
		supportConfiguration = new Configuration(this, "serversupport");
		commandConfiguration = new Configuration(this, "command");
		settings = new Settings(this).loadSettings();
		connection = new Connection(this);
		languageHandler = new LanguageHandler(this).loadLanguages(true);
		serverSupportHandler = new ServerSupportHandler(this).loadGameTypes(true).loadCheckMessages(
				true);
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
		if (configManager != null) {
			configManager.save();
		}
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
			public void renderTileEntityAt(TileEntitySign tileEntitySign, double x, double y, double z,
			                               float partialTicks, int destroyStage) {
				eventHandler.getRenderSignListener().onRenderSign(tileEntitySign, x, y, z, partialTicks,
						destroyStage);
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
		if (!getEventHandler().getMessageSendListener().onSend(string)) {
			LabyModCore.getMinecraft().getPlayer().sendChatMessage(string);
		}
	}

	public void displayChatComponent(ChatComponent chatComponent) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(chatComponent.create());
	}

	public void displayMessage(String string) {
		LabyMod.getInstance().displayMessageInChat(string);
	}

	public void displayPrefixMessage(String string) {
		displayMessage(StringUtil.PREFIX + string);
	}

	public void displayMessage(String message, String... replace) {
		displayMessage(languageHandler.getMessage(message, replace));
	}

	public double getVersion() {
		return this.version;
	}

	public boolean isDebug() {
		return this.debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public File getAddonFolder() {
		return this.addonFolder;
	}

	public String getMinecraftVersion() {
		return this.minecraftVersion;
	}

	public Configuration.ConfigManager getConfigManager() {
		return this.configManager;
	}

	public Configuration getConfiguration() {
		return this.configuration;
	}

	public Configuration getSupportConfiguration() {
		return this.supportConfiguration;
	}

	public Configuration getCommandConfiguration() {
		return this.commandConfiguration;
	}

	public Connection getConnection() {
		return this.connection;
	}

	public Settings getSettings() {
		return this.settings;
	}

	public ModuleHandler getModuleHandler() {
		return this.moduleHandler;
	}

	public EventHandler getEventHandler() {
		return this.eventHandler;
	}

	public IngameModuleHandler getIngameModuleHandler() {
		return this.ingameModuleHandler;
	}

	public CommandHandler getCommandHandler() {
		return this.commandHandler;
	}

	public ServerSupportHandler getServerSupportHandler() {
		return this.serverSupportHandler;
	}

	public LanguageHandler getLanguageHandler() {
		return this.languageHandler;
	}
}
