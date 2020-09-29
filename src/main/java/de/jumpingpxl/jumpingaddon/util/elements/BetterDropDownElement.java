package de.jumpingpxl.jumpingaddon.util.elements;

import de.jumpingpxl.jumpingaddon.util.SettingValue;
import de.jumpingpxl.jumpingaddon.util.Settings;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.settings.elements.DropDownElement;
import net.labymod.utils.Consumer;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 17.03.2019
 */

public class BetterDropDownElement implements BetterElement {

	private final DropDownElement<String> dropDownElement;

	public BetterDropDownElement(String displayName, String[] values, String defaultValue,
	                             Consumer<String> consumer) {
		DropDownMenu<String> dropDownMenu = new DropDownMenu<String>(
				" §7» " + Settings.SettingsCategory.getLastCategory().getColor() + displayName, 0, 0, 0,
				0).fill(values);
		dropDownMenu.setSelected(defaultValue);
		dropDownElement = new DropDownElement<>(
				" §7» " + Settings.SettingsCategory.getLastCategory().getColor() + displayName,
				dropDownMenu);
		dropDownElement.setChangeListener(consumer);
	}

	public BetterDropDownElement(String displayName, String[] values, SettingValue settingValue) {
		this(displayName, values, settingValue.getAsString(), string -> {
			settingValue.setValue(string);
			settingValue.getConfiguration().set(settingValue.getConfigPath(), string);
			settingValue.getConfiguration().save();
		});
	}

	@Override
	public DropDownElement<String> getElement() {
		return dropDownElement;
	}

	@Override
	public BetterDropDownElement setDescription(String description) {
		getElement().bindDescription(description);
		return this;
	}
}
