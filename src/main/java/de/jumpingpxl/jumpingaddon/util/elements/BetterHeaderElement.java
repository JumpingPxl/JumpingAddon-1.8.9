package de.jumpingpxl.jumpingaddon.util.elements;

import de.jumpingpxl.jumpingaddon.util.Settings;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.SettingsElement;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */

public class BetterHeaderElement implements BetterElement {

	private HeaderElement headerElement;

	public BetterHeaderElement(String string) {
		headerElement = new HeaderElement(string);
	}

	public BetterHeaderElement(Settings.SettingsCategory settingsCategory) {
		headerElement = new HeaderElement(settingsCategory.getCompleteName());
		Settings.SettingsCategory.setLastCategory(settingsCategory);
	}

	@Override
	public SettingsElement getElement() {
		return headerElement;
	}
}
