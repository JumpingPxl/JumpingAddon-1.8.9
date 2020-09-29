package de.jumpingpxl.jumpingaddon.util.elements;

import de.jumpingpxl.jumpingaddon.util.SettingValue;
import de.jumpingpxl.jumpingaddon.util.Settings;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.settings.elements.StringElement;
import net.labymod.utils.Consumer;

import java.util.Arrays;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 12.01.2019
 */

public class BetterStringElement implements BetterElement {

	private final StringElement stringElement;

	public BetterStringElement(String displayName, ControlElement.IconData iconData,
	                           String currentValue, Consumer<String> updateValue) {
		stringElement = new StringElement(
				Settings.SettingsCategory.getLastCategory().getColor() + displayName, iconData,
				currentValue, updateValue);
	}

	public BetterStringElement(String displayName, ControlElement.IconData iconData,
	                           SettingValue settingValue) {
		this(displayName, iconData, settingValue.getAsString(), string -> {
			settingValue.setValue(string);
			settingValue.getConfiguration().set(settingValue.getConfigPath(), string);
			settingValue.getConfiguration().save();
		});
	}

	@Override
	public StringElement getElement() {
		return stringElement;
	}

	@Override
	public BetterStringElement setDescription(String description) {
		return null;
	}

	public BetterStringElement addSettings(Object... objects) {
		if (objects.length == 0) {
			return this;
		}
		Arrays.stream(objects).forEach(object -> {
			if (object instanceof BetterElement) {
				stringElement.getSubSettings().add(((BetterElement) object).getElement());
			} else if (object instanceof SettingsElement) {
				stringElement.getSubSettings().add((SettingsElement) object);
			}
		});
		return this;
	}
}