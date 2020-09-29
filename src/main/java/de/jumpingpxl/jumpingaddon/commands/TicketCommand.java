package de.jumpingpxl.jumpingaddon.commands;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.command.CommandExecutor;
import de.jumpingpxl.jumpingaddon.util.command.CommandHandler;
import de.jumpingpxl.jumpingaddon.util.serversupport.GommeHDSupport;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 24.03.2019
 */

public class TicketCommand implements CommandExecutor {

	private final JumpingAddon jumpingAddon;

	public TicketCommand(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public boolean execute(CommandHandler.Command command, String label, String[] args) {
		if (jumpingAddon.getConnection().getServer() != Server.GOMMEHD_NET) {
			return false;
		}
		GommeHDSupport gommeHDSupport = (GommeHDSupport) jumpingAddon.getConnection().getSupport();
		if (jumpingAddon.getConnection().getGameType() == null || !jumpingAddon.getConnection()
				.getGameType()
				.getName()
				.equals("TTT")) {
			send(jumpingAddon, "ticketNotInTTT", label);
		} else if (gommeHDSupport.isIngame()) {
			send(jumpingAddon, "ticketAlreadyIngame");
		} else if (label.contains("detectiveticket") || label.contains("dticket")) {
			gommeHDSupport.setTicket("d");
			send(jumpingAddon, "ticketDetective");
		} else {
			gommeHDSupport.setTicket("t");
			send(jumpingAddon, "ticketTraitor");
		}
		return true;
	}

	@Override
	public Server getServer() {
		return Server.GOMMEHD_NET;
	}
}