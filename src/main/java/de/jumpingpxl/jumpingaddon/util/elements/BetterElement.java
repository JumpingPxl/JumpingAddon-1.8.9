package de.jumpingpxl.jumpingaddon.util.elements;

import net.labymod.settings.elements.SettingsElement;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 25.02.2019
 */

public interface BetterElement {

	SettingsElement getElement();

	default BetterElement setDescription(String description) {
		return null;
	}
}

