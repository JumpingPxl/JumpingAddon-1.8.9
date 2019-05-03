package de.jumpingpxl.jumpingaddon.commands;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.command.CommandExecutor;
import de.jumpingpxl.jumpingaddon.util.command.CommandHandler;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 23.03.2019
 */

public class RandomkillReportCommand implements CommandExecutor {

	private JumpingAddon jumpingAddon;

	public RandomkillReportCommand(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public boolean execute(CommandHandler.Command command, String label, String[] args) {
		if (jumpingAddon.getConnection().getServer() != Server.GOMMEHD_NET)
			return false;
		if (jumpingAddon.getConnection().getGameType() == null || !(jumpingAddon.getConnection().getGameType().getName().equals("TTT") || jumpingAddon.getConnection().getGameType().getName().equals("Replay")))
			send("randomkillReportNotInTTT", label);
		else if (args.length == 0)
			send("commandUsage", label + " <player>");
		else
			jumpingAddon.sendMessage("/report " + args[0] + " randomkilling confirm");
		return true;
	}

	@Override
	public Server getServer() {
		return Server.GOMMEHD_NET;
	}
}
