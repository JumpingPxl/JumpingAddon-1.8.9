package de.jumpingpxl.jumpingaddon.util.modules;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.serversupport.GommeHDSupport;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;
import net.labymod.core.LabyModCore;
import net.labymod.ingamegui.ModuleCategory;
import net.labymod.ingamegui.moduletypes.SimpleModule;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Material;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */

public class GommeTTTOnlineCoutInGameModule extends SimpleModule implements InGameModule {

	private JumpingAddon jumpingAddon;

	private GommeHDSupport gommeHDSupport;

	public GommeTTTOnlineCoutInGameModule(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
		gommeHDSupport = (GommeHDSupport) Server.GOMMEHD_NET.getServerSupport();
	}

	@Override
	public String getDisplayName() {
		return "Online";
	}

	@Override
	public String getDisplayValue() {
		int i = LabyModCore.getMinecraft().getConnection().getPlayerInfoMap().size();
		return "§a" + i + "§7/§c" + (gommeHDSupport.getTttOnlineCount() == -1 ? i : gommeHDSupport.getTttOnlineCount());
	}

	@Override
	public String getDefaultValue() {
		return "?";
	}

	@Override
	public boolean isShown() {
		return jumpingAddon.getConnection().isOnServer(Server.GOMMEHD_NET) && (jumpingAddon.getConnection().getGameType().getName().equalsIgnoreCase("TTT") && gommeHDSupport.isIngame());
	}

	@Override
	public ControlElement.IconData getIconData() {
		return new ControlElement.IconData(Material.STICK);
	}

	@Override
	public void loadSettings() {

	}

	@Override
	public String getSettingName() {
		return "jumping_gtttonlinecount";
	}

	@Override
	public String getControlName() {
		return "GommeTTTOnline";
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
