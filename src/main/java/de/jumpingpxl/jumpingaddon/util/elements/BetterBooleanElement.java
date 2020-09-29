package de.jumpingpxl.jumpingaddon.util.elements;

import de.jumpingpxl.jumpingaddon.util.SettingValue;
import de.jumpingpxl.jumpingaddon.util.Settings;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;

import java.util.Arrays;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 30.11.2018
 */

public class BetterBooleanElement implements BetterElement {

	private final BooleanElement booleanElement;

	public BetterBooleanElement(String displayName, ControlElement.IconData iconData,
	                            boolean currentValue, Consumer<Boolean> updateValue) {
		booleanElement = new BooleanElement(
				Settings.SettingsCategory.getLastCategory().getColor() + displayName, iconData,
				updateValue,
				currentValue);
	}

	public BetterBooleanElement(String displayName, ControlElement.IconData iconData,
	                            SettingValue settingValue) {
		this(displayName, iconData, settingValue.getAsBoolean(), enabled -> {
			settingValue.setValue(enabled);
			settingValue.getConfiguration().set(settingValue.getConfigPath(), enabled);
			settingValue.getConfiguration().save();
		});
	}

	public BetterBooleanElement(String displayName, ControlElement.IconData iconData,
	                            SettingValue settingValue, Consumer<Boolean> updateValue) {
		this(displayName, iconData, settingValue.getAsBoolean(), enabled -> {
			settingValue.setValue(enabled);
			settingValue.getConfiguration().set(settingValue.getConfigPath(), enabled);
			settingValue.getConfiguration().save();
			updateValue.accept(enabled);
		});
	}

	@Override
	public BooleanElement getElement() {
		return booleanElement;
	}

	@Override
	public BetterBooleanElement setDescription(String description) {
		getElement().bindDescription(description);
		return this;
	}

	public BetterBooleanElement addSettings(Object... objects) {
		if (objects.length == 0) {
			return this;
		}
		if (booleanElement.getSubSettings().getElements().isEmpty()) {
			StringBuilder stringBuilder = new StringBuilder();
			Arrays.stream(objects).filter(object -> object instanceof String).forEach(
					object -> stringBuilder.append(" §7» ")
							.append(Settings.SettingsCategory.getLastCategory().getColor())
							.append(object));
			stringBuilder.append(" §7» ")
					.append(Settings.SettingsCategory.getLastCategory().getColor())
					.append(booleanElement.getDisplayName());
			booleanElement.getSubSettings().add(new HeaderElement(
					Settings.SettingsCategory.getLastCategory().getCompleteName()
							+ stringBuilder.toString()));
		}
		Arrays.stream(objects).forEach(object -> {
			if (object instanceof BetterElement) {
				booleanElement.getSubSettings().add(((BetterElement) object).getElement());
			} else if (object instanceof SettingsElement) {
				booleanElement.getSubSettings().add((SettingsElement) object);
			}
		});
		return this;
	}
}
