package de.jumpingpxl.jumpingaddon.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.command.CommandExecutor;
import de.jumpingpxl.jumpingaddon.util.command.CommandHandler;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.user.User;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;

import java.util.UUID;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */

public class JumpingAddonCommand implements CommandExecutor {

	private JumpingAddon jumpingAddon;

	public JumpingAddonCommand(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public boolean execute(CommandHandler.Command command, String label, String[] args) {
		if (args.length == 0)
			send("commandUsage", label + " <debug|reload>");
		else if (args[0].equalsIgnoreCase("reload"))
			if (args.length == 1)
				send("commandUsage", label + " reload <language[l],checkmessages[cm],gametypes[gt]>");
			else
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
		else if (args[0].equalsIgnoreCase("test")) {
			//Minecraft.getMinecraft().getNetHandler().handleChat(new S02PacketChat(new ChatComponentText("§4TEST"), (byte) 2));
			User user = LabyMod.getInstance().getUserManager().getUser(UUID.randomUUID());
			user.setSubTitle("SubTitle Text");
			user.setSubTitleSize(0.8D);
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
