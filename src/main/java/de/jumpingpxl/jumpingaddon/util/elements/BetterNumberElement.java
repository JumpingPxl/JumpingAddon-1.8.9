package de.jumpingpxl.jumpingaddon.util.elements;

import de.jumpingpxl.jumpingaddon.util.SettingValue;
import de.jumpingpxl.jumpingaddon.util.Settings;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.NumberElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;

import java.util.Arrays;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 20.01.2019
 */

public class BetterNumberElement implements BetterElement {

	private NumberElement numberElement;

	public BetterNumberElement(String displayName, ControlElement.IconData iconData, int currentValue, int minValue, int maxValue, Consumer<Integer> updateValue) {
		numberElement = new NumberElement(Settings.SettingsCategory.getLastCategory().getColor() + displayName, iconData, currentValue);
		numberElement.addCallback(updateValue);
		numberElement.setRange(minValue, maxValue);
	}

	public BetterNumberElement(String displayName, ControlElement.IconData iconData, SettingValue settingValue, int minValue, int maxValue) {
		this(displayName, iconData, settingValue.getAsInteger(), minValue, maxValue, integer -> {
			settingValue.setValue(integer);
			settingValue.getConfiguration().set(settingValue.getConfigPath(), integer);
			settingValue.getConfiguration().save();
		});
	}

	@Override
	public NumberElement getElement() {
		return numberElement;
	}

	@Override
	public BetterNumberElement setDescription(String description) {
		return null;
	}

	public BetterNumberElement addSettings(Object... objects) {
		if (objects.length == 0)
			return this;
		Arrays.stream(objects).forEach(object -> {
			if (object instanceof BetterElement) {
				numberElement.getSubSettings().add(((BetterElement) object).getElement());
			} else if (object instanceof SettingsElement) {
				numberElement.getSubSettings().add((SettingsElement) object);
			}
		});
		return this;
	}
}