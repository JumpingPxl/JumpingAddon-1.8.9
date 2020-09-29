package de.jumpingpxl.jumpingaddon.util.modules;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.labymod.ingamegui.ModuleCategory;
import net.labymod.ingamegui.moduletypes.SimpleModule;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;

import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */

public class ServerSupportInGameModule extends SimpleModule implements InGameModule {

	private final JumpingAddon jumpingAddon;
	private boolean showWhenNotSupported;

	public ServerSupportInGameModule(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public String getDisplayName() {
		return "Support";
	}

	@Override
	public String getDisplayValue() {
		return String.valueOf(jumpingAddon.getConnection().isSupported());
	}

	@Override
	public String getDefaultValue() {
		return "false";
	}

	@Override
	public boolean isShown() {
		return showWhenNotSupported || jumpingAddon.getConnection().isSupported();
	}

	@Override
	public ControlElement.IconData getIconData() {
		return new ControlElement.IconData(Material.COMMAND);
	}

	@Override
	public void fillSubSettings(List<SettingsElement> settingsElements) {
		super.fillSubSettings(settingsElements);
		settingsElements.add(new BooleanElement(this, new ControlElement.IconData(Material.LEVER),
				"Show when not supported", "showSupportWhenNotSupported"));
	}

	@Override
	public void loadSettings() {
		showWhenNotSupported = Boolean.parseBoolean(
				this.getAttribute("showSupportWhenNotSupported", "false"));
	}

	@Override
	public String getSettingName() {
		return "jumping_serversupport";
	}

	@Override
	public String getControlName() {
		return "ServerSupport";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public ModuleCategory getCategory() {
		return jumpingAddon.getIngameModuleHandler().getCategory();
	}

	@Override
	public int getSortingId() {
		return 0;
	}
}
