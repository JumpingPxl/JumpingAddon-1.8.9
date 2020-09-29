package de.jumpingpxl.jumpingaddon.commands;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.command.CommandExecutor;
import de.jumpingpxl.jumpingaddon.util.command.CommandHandler;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 23.03.2019
 */

public class ChatReportCommand implements CommandExecutor {

	private final JumpingAddon jumpingAddon;

	public ChatReportCommand(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public boolean execute(CommandHandler.Command command, String label, String[] args) {
		if (jumpingAddon.getConnection().getServer() != Server.GOMMEHD_NET) {
			return false;
		}
		if (args.length == 0) {
			send(jumpingAddon, "commandUsage", label + " <player>");
		} else {
			jumpingAddon.sendMessage("/report " + args[0] + " chat confirm");
		}
		return true;
	}

	@Override
	public Server getServer() {
		return Server.GOMMEHD_NET;
	}
}
