package de.jumpingpxl.jumpingaddon.commands;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.command.CommandExecutor;
import de.jumpingpxl.jumpingaddon.util.command.CommandHandler;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */

public class JumpingAddonCommand implements CommandExecutor {

	private final JumpingAddon jumpingAddon;

	public JumpingAddonCommand(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public boolean execute(CommandHandler.Command command, String label, String[] args) {
		if (args.length == 0) {
			send(jumpingAddon, "commandUsage", label + " <debug|reload>");
		} else if (args[0].equalsIgnoreCase("reload")) {
			if (args.length == 1) {
				send(jumpingAddon, "commandUsage",
						label + " reload <language[l],checkmessages[cm],gametypes[gt]>");
			} else {
				switch (args[1].toLowerCase()) {
					case "language":
					case "l":
						jumpingAddon.displayPrefixMessage("§7Reloading languages and messages...");
						jumpingAddon.getLanguageHandler().getLanguages().clear();
						jumpingAddon.getLanguageHandler().getMessages().clear();
						jumpingAddon.getLanguageHandler().loadLanguages(false);
						jumpingAddon.displayPrefixMessage("§7Language reload complete.");
						break;
					case "checkmessages":
					case "cm":
						jumpingAddon.displayPrefixMessage("§7Reloading checkmessages...");
						jumpingAddon.getServerSupportHandler().loadCheckMessages(false);
						jumpingAddon.displayPrefixMessage("§7Checkmessage reload complete.");
						break;
					case "gametypes":
					case "gt":
						jumpingAddon.displayPrefixMessage("§7Reloading gametypes...");
						jumpingAddon.getServerSupportHandler().loadGameTypes(false);
						jumpingAddon.displayPrefixMessage("§7Gametype reload complete.");
						break;
				}
			}
		} else if (args[0].equalsIgnoreCase("debug")) {
			jumpingAddon.setDebug(!jumpingAddon.isDebug());
			jumpingAddon.displayPrefixMessage(
					"§7Debug-Mode " + (jumpingAddon.isDebug() ? "§a" : "§cde") + "activated");
		}
		return true;
	}

	@Override
	public Server getServer() {
		return Server.GOMMEHD_NET;
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public boolean defaultEnabled() {
		return false;
	}
}
