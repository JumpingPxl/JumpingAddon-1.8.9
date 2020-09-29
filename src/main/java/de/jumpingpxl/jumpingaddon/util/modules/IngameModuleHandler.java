package de.jumpingpxl.jumpingaddon.util.modules;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.labymod.ingamegui.Module;
import net.labymod.ingamegui.ModuleCategory;
import net.labymod.ingamegui.ModuleCategoryRegistry;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Material;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 25.02.2019
 */

public class IngameModuleHandler {

	private final JumpingAddon jumpingAddon;
	private ModuleCategory category;

	private ServerSupportInGameModule serverSupportModule;
	private AfkTimerInGameModule afkTimerModule;
	private GameTypeInGameModule gameTypeModule;
	private GommeNickNameInGameModule gommeNickNameModule;
	private GommeReportsInGameModule gommeReportsModule;
	private GommeTTTOnlineCoutInGameModule gommeTTTOnlineCoutModule;
	private GommeTTTRoleInGameModule gommeTTTRoleModule;

	public IngameModuleHandler(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public IngameModuleHandler registerIngameModules() {
		category = new ModuleCategory("JumpingAddon", true, new ControlElement.IconData(
				Material.ENCHANTED_BOOK));//new ResourceLocation("jumpingaddon/textures/icon.png")));
		ModuleCategoryRegistry.loadCategory(category);
		serverSupportModule = (ServerSupportInGameModule) registerModule(
				new ServerSupportInGameModule(jumpingAddon));
		afkTimerModule = (AfkTimerInGameModule) registerModule(new AfkTimerInGameModule(jumpingAddon));
		gameTypeModule = (GameTypeInGameModule) registerModule(new GameTypeInGameModule(jumpingAddon));
		gommeNickNameModule = (GommeNickNameInGameModule) registerModule(
				new GommeNickNameInGameModule(jumpingAddon));
		gommeReportsModule = (GommeReportsInGameModule) registerModule(
				new GommeReportsInGameModule(jumpingAddon));
		gommeTTTOnlineCoutModule = (GommeTTTOnlineCoutInGameModule) registerModule(
				new GommeTTTOnlineCoutInGameModule(jumpingAddon));
		gommeTTTRoleModule = (GommeTTTRoleInGameModule) registerModule(
				new GommeTTTRoleInGameModule(jumpingAddon));
		return this;
	}

	private InGameModule registerModule(Object module) {
		if (module instanceof Module) {
			jumpingAddon.getApi().registerModule((Module) module);
		}
		return (InGameModule) module;
	}

	public JumpingAddon getJumpingAddon() {
		return this.jumpingAddon;
	}

	public ModuleCategory getCategory() {
		return this.category;
	}

	public ServerSupportInGameModule getServerSupportModule() {
		return this.serverSupportModule;
	}

	public AfkTimerInGameModule getAfkTimerModule() {
		return this.afkTimerModule;
	}

	public GameTypeInGameModule getGameTypeModule() {
		return this.gameTypeModule;
	}

	public GommeNickNameInGameModule getGommeNickNameModule() {
		return this.gommeNickNameModule;
	}

	public GommeReportsInGameModule getGommeReportsModule() {
		return this.gommeReportsModule;
	}

	public GommeTTTOnlineCoutInGameModule getGommeTTTOnlineCoutModule() {
		return this.gommeTTTOnlineCoutModule;
	}

	public GommeTTTRoleInGameModule getGommeTTTRoleModule() {
		return this.gommeTTTRoleModule;
	}
}
