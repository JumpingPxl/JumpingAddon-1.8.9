package de.jumpingpxl.jumpingaddon.util.modules;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.serversupport.GommeHDSupport;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;
import net.labymod.ingamegui.ModuleCategory;
import net.labymod.ingamegui.moduletypes.SimpleModule;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Material;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */

public class GommeReportsInGameModule extends SimpleModule implements InGameModule {

	private final JumpingAddon jumpingAddon;
	private final GommeHDSupport gommeHDSupport;

	public GommeReportsInGameModule(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
		gommeHDSupport = (GommeHDSupport) Server.GOMMEHD_NET.getServerSupport();
	}

	@Override
	public String getDisplayName() {
		return "Reports";
	}

	@Override
	public String getDisplayValue() {
		return String.valueOf(gommeHDSupport.getSettings().getReports().getAsInteger());
	}

	@Override
	public String getDefaultValue() {
		return "?";
	}

	@Override
	public boolean isShown() {
		return jumpingAddon.getConnection().isOnServer(Server.GOMMEHD_NET);
	}

	@Override
	public ControlElement.IconData getIconData() {
		return new ControlElement.IconData(Material.COMMAND);
	}

	@Override
	public void loadSettings() {

	}

	@Override
	public String getSettingName() {
		return "jumping_greports";
	}

	@Override
	public String getControlName() {
		return "GommeReports";
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
