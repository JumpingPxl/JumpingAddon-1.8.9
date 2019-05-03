package de.jumpingpxl.jumpingaddon.util.command;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;
import net.labymod.main.LabyMod;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 25.02.2019
 */

public interface CommandExecutor {
	boolean execute(CommandHandler.Command command, String label, String[] args);

	Server getServer();

	default boolean isStatic() {
		return false;
	}

	default boolean defaultEnabled() {
		return true;
	}

	default void sendString(String string) {
		LabyMod.getInstance().displayMessageInChat(JumpingAddon.getInstance().getSettings().getPrefix() + string);
	}

	default void send(String message, String... replace) {
		JumpingAddon.getInstance().displayMessage(message, replace);
	}

	static CommandHandler.Command getCommand(JumpingAddon jumpingAddon, String label) {
		return jumpingAddon.getCommandHandler().getCommand(label);
	}
}
