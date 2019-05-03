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

public class GommeNickNameInGameModule extends SimpleModule implements InGameModule {

	private JumpingAddon jumpingAddon;
	private GommeHDSupport gommeHDSupport;

	public GommeNickNameInGameModule(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
		gommeHDSupport = (GommeHDSupport) Server.GOMMEHD_NET.getServerSupport();
	}

	@Override
	public String getDisplayName() {
		return "Nick";
	}

	@Override
	public String getDisplayValue() {
		return gommeHDSupport.getNickName();
	}

	@Override
	public String getDefaultValue() {
		return "?";
	}

	@Override
	public boolean isShown() {
		return jumpingAddon.getConnection().isOnServer(Server.GOMMEHD_NET) && gommeHDSupport.getNickName() != null;
	}

	@Override
	public ControlElement.IconData getIconData() {
		return new ControlElement.IconData(Material.NAME_TAG);
	}

	@Override
	public void loadSettings() {

	}

	@Override
	public String getSettingName() {
		return "jumping_gnickname";
	}

	@Override
	public String getControlName() {
		return "GommeNickName";
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
