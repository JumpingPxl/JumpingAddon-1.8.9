package de.jumpingpxl.jumpingaddon.commands;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.Configuration;
import de.jumpingpxl.jumpingaddon.util.SettingValue;
import de.jumpingpxl.jumpingaddon.util.command.CommandExecutor;
import de.jumpingpxl.jumpingaddon.util.command.CommandHandler;
import de.jumpingpxl.jumpingaddon.util.command.CommandSettings;
import de.jumpingpxl.jumpingaddon.util.elements.BetterListContainerElement;
import de.jumpingpxl.jumpingaddon.util.elements.BetterNumberElement;
import de.jumpingpxl.jumpingaddon.util.serversupport.GommeHDSupport;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.utils.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 24.03.2019
 */

public class QuickShopCommand implements CommandExecutor, CommandSettings {

	private JumpingAddon jumpingAddon;
	private HashMap<Integer, Integer> traitorShop = new HashMap<>();
	private HashMap<Integer, Integer> detectiveShop = new HashMap<>();

	public QuickShopCommand(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public boolean execute(CommandHandler.Command command, String label, String[] args) {
		if (jumpingAddon.getConnection().getServer() != Server.GOMMEHD_NET)
			return false;
		if (jumpingAddon.getConnection().getGameType() == null || !jumpingAddon.getConnection().getGameType().getName().equals("TTT"))
			send("quickShopNotInTTT", label);
		else if (args.length == 0)
			send("commandUsage", label + " <shortcut>");
		else {
			GommeHDSupport gommeHDSupport = (GommeHDSupport) jumpingAddon.getConnection().getSupport();
			int i;
			try {
				i = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				i = 0;
			}
			i -= 1;
			if (i < 0 || i > 9)
				send("quickShopNotInRange");
			else if (gommeHDSupport.getTttRole().contains("§4"))
				jumpingAddon.sendMessage("/s " + traitorShop.get(i));
			else if (gommeHDSupport.getTttRole().contains("§9"))
				jumpingAddon.sendMessage("/s " + detectiveShop.get(i));
			else if (gommeHDSupport.getTttRole().contains("§a"))
				send("quickShopInnocent");
			else
				send("quickShopNotIngame");
		}
		return true;
	}

	@Override
	public Server getServer() {
		return Server.GOMMEHD_NET;
	}

	@Override
	public Object[] getSettings(CommandHandler.Command command) {
		List<Object> traitor = new ArrayList<>();
		List<Object> detective = new ArrayList<>();
		traitor.add(new HeaderElement("§bMiscellaneous §7» §bClient-Commands §7» §bQuickShop §7» §4Traitor-Shop"));
		detective.add(new HeaderElement("§bMiscellaneous §7» §bClient-Commands §7» §bQuickShop §7» §9Detective-Shop"));
		traitor.add(new HeaderElement("§cValue §e0 §c= Shortcut deaktiviert!"));
		detective.add(new HeaderElement("§cValue §e0 §c= Shortcut deaktiviert!"));
		for (int i = 1; i <= 10; i++) {
			int integer = i;
			traitor.add(new BetterNumberElement("Shortcut " + i + "\n §8» §4Traitor-Shop", new ControlElement.IconData(Material.IRON_SWORD), traitorShop.get(i), 0, 50, value -> {
				traitorShop.put(integer, value);
				command.getConfiguration().set("traitor" + integer, value);
				command.getConfiguration().save();
			}).getElement());
			detective.add(new BetterNumberElement("Shortcut " + i + "\n §8» §9Detective-Shop", new ControlElement.IconData(Material.BEACON), detectiveShop.get(i), 0, 50, value -> {
				traitorShop.put(integer, value);
				command.getConfiguration().set("detective" + integer, value);
				command.getConfiguration().save();
			}).getElement());
		}
		return new Object[]{
				new BetterListContainerElement("§4Traitor-Shop", new ControlElement.IconData(Material.IRON_SWORD)).addSettings(traitor.toArray(new Object[0])),
				new BetterListContainerElement("§9Detective-Shop", new ControlElement.IconData(Material.BEACON)).addSettings(detective.toArray(new Object[0]))
		};
	}

	@Override
	public void setSettings(CommandHandler.Command command, List<SettingValue> list, Configuration configuration) {
		for (int i = 1; i <= 10; i++) {
			traitorShop.put(i, configuration.has("traitor" + i) ? configuration.getAsInt("traitor" + i) : 0);
			detectiveShop.put(i, configuration.has("detective" + i) ? configuration.getAsInt("detective" + i) : 0);
		}
	}
}