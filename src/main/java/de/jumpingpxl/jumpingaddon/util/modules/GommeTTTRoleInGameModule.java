package de.jumpingpxl.jumpingaddon.util.modules;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.serversupport.GommeHDSupport;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;
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

public class GommeTTTRoleInGameModule extends SimpleModule implements InGameModule {

	private final JumpingAddon jumpingAddon;
	private final GommeHDSupport gommeHDSupport;
	private boolean shortName;

	public GommeTTTRoleInGameModule(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
		gommeHDSupport = (GommeHDSupport) Server.GOMMEHD_NET.getServerSupport();
	}

	@Override
	public String getDisplayName() {
		return "Role";
	}

	@Override
	public String getDisplayValue() {
		return shortName ? gommeHDSupport.getTttRoleShort() : gommeHDSupport.getTttRole();
	}

	@Override
	public String getDefaultValue() {
		return "?";
	}

	@Override
	public boolean isShown() {
		return jumpingAddon.getConnection().isOnServer(Server.GOMMEHD_NET) && (
				jumpingAddon.getConnection().getGameType().getName().equalsIgnoreCase("TTT")
						&& gommeHDSupport.isIngame());
	}

	@Override
	public ControlElement.IconData getIconData() {
		return new ControlElement.IconData(Material.STICK);
	}

	@Override
	public void fillSubSettings(List<SettingsElement> settingsElements) {
		super.fillSubSettings(settingsElements);
		settingsElements.add(
				(new BooleanElement(this, new ControlElement.IconData(Material.LEVER), "Short name",
						"tttShortRoleName")));
	}

	@Override
	public void loadSettings() {
		this.shortName = Boolean.parseBoolean(this.getAttribute("tttShortRoleName", "false"));
	}

	@Override
	public String getSettingName() {
		return "jumping_gtttrole";
	}

	@Override
	public String getControlName() {
		return "GommeTTTRole";
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