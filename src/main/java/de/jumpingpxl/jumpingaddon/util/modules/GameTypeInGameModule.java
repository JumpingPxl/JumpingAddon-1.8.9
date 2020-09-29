package de.jumpingpxl.jumpingaddon.util.modules;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.labymod.ingamegui.ModuleCategory;
import net.labymod.ingamegui.moduletypes.SimpleModule;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Material;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */
public class GameTypeInGameModule extends SimpleModule implements InGameModule {

	private final JumpingAddon jumpingAddon;
	private boolean shown;

	public GameTypeInGameModule(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public String getDisplayName() {
		return "GameType";
	}

	@Override
	public String getDisplayValue() {
		return jumpingAddon.getConnection().getGameType().getName();
	}

	@Override
	public String getDefaultValue() {
		return "?";
	}

	@Override
	public boolean isShown() {
		return shown;
	}

	public void setShown(boolean shown) {
		this.shown = shown;
	}

	@Override
	public ControlElement.IconData getIconData() {
		return new ControlElement.IconData(Material.PAPER);
	}

	@Override
	public void loadSettings() {

	}

	@Override
	public String getSettingName() {
		return "jumping_gametype";
	}

	@Override
	public String getControlName() {
		return "GameType";
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
