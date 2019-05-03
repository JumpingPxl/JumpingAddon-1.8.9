package de.jumpingpxl.jumpingaddon.util.mods;

import de.jumpingpxl.jumpingaddon.util.elements.BetterElement;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 21.03.2019
 */

public interface Module {
	void loadSettings();

	BetterElement getElement();
}
