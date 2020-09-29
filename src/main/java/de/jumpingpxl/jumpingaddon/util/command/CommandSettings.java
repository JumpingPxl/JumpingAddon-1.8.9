package de.jumpingpxl.jumpingaddon.util.command;

import de.jumpingpxl.jumpingaddon.util.Configuration;
import de.jumpingpxl.jumpingaddon.util.SettingValue;

import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 25.02.2019
 */

public interface CommandSettings {

	Object[] getSettings(CommandHandler.Command command);

	void setSettings(CommandHandler.Command command, List<SettingValue> list,
	                 Configuration configuration);
}
