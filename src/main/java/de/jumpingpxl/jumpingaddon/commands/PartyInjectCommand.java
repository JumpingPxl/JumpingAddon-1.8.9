package de.jumpingpxl.jumpingaddon.commands;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.Configuration;
import de.jumpingpxl.jumpingaddon.util.SettingValue;
import de.jumpingpxl.jumpingaddon.util.command.CommandExecutor;
import de.jumpingpxl.jumpingaddon.util.command.CommandHandler;
import de.jumpingpxl.jumpingaddon.util.command.CommandSettings;
import de.jumpingpxl.jumpingaddon.util.elements.BetterBooleanElement;
import de.jumpingpxl.jumpingaddon.util.serversupport.GommeHDSupport;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Material;

import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 24.03.2019
 */

public class PartyInjectCommand implements CommandExecutor, CommandSettings {

	private final JumpingAddon jumpingAddon;
	private SettingValue accept;
	private SettingValue invite;
	private SettingValue makeLeader;

	public PartyInjectCommand(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public boolean execute(CommandHandler.Command command, String label, String[] args) {
		if (jumpingAddon.getConnection().getServer() != Server.GOMMEHD_NET) {
			return false;
		}
		GommeHDSupport gommeHDSupport = (GommeHDSupport) jumpingAddon.getConnection().getSupport();
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("accept")) {
				if (!accept.getAsBoolean()) {
					return false;
				}
				if (gommeHDSupport.getLastPartyInvite() == null) {
					send(jumpingAddon, "partyNoParty");
				} else {
					jumpingAddon.sendMessage("/party accept " + gommeHDSupport.getLastPartyInvite());
				}
				return true;
			} else if (args[0].equalsIgnoreCase("invite")) {
				if (!invite.getAsBoolean()) {
					return false;
				}
				if (gommeHDSupport.getLastOnlineFriend() == null) {
					send(jumpingAddon, "partyNoFriend");
				} else {
					jumpingAddon.sendMessage("/party invite " + gommeHDSupport.getLastOnlineFriend());
				}
				return true;
			} else if (args[0].equalsIgnoreCase("makeleader")) {
				if (!makeLeader.getAsBoolean()) {
					return false;
				}
				send(jumpingAddon, "commandUsage", label + " makeleader <player>");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("makeleader")) {
			if (!makeLeader.getAsBoolean()) {
				return false;
			}
			jumpingAddon.sendMessage("/party promote " + args[1]);
			jumpingAddon.sendMessage("/party promote " + args[1]);
			return true;
		}
		return false;
	}

	@Override
	public Server getServer() {
		return Server.GOMMEHD_NET;
	}

	@Override
	public Object[] getSettings(CommandHandler.Command command) {
		return new Object[]{new BetterBooleanElement("QuickInvite\n §8» §e/party invite",
				new ControlElement.IconData(Material.COMMAND), invite), new BetterBooleanElement(
				"QuickAccept\n §8» §e/party accept", new ControlElement.IconData(Material.COMMAND),
				accept),
				new BetterBooleanElement("MakeLeader\n §8» §e/party makeleader",
						new ControlElement.IconData(Material.COMMAND), makeLeader)};
	}

	@Override
	public void setSettings(CommandHandler.Command command, List<SettingValue> list,
	                        Configuration configuration) {
		invite = new SettingValue(jumpingAddon, list, configuration, "enabledInvite", true);
		accept = new SettingValue(jumpingAddon, list, configuration, "enabledAccept", true);
		makeLeader = new SettingValue(jumpingAddon, list, configuration, "enabledMakeLeader", true);
	}
}