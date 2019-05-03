package de.jumpingpxl.jumpingaddon.util.command;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.commands.*;
import de.jumpingpxl.jumpingaddon.util.Configuration;
import de.jumpingpxl.jumpingaddon.util.SettingValue;
import de.jumpingpxl.jumpingaddon.util.Settings;
import de.jumpingpxl.jumpingaddon.util.elements.BetterBooleanElement;
import de.jumpingpxl.jumpingaddon.util.elements.BetterListContainerElement;
import lombok.Getter;
import lombok.Setter;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.utils.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 25.02.2019
 */

@Getter
public class CommandHandler {

	private JumpingAddon jumpingAddon;
	private List<Command> commands = new ArrayList<>();

	public CommandHandler(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public CommandHandler registerCommands() {
		register(new JumpingAddonCommand(jumpingAddon), "/jumpingaddon", ".jumpingaddon");
		register(new ActionCommand(jumpingAddon), ".action");
		register(new ChatReportCommand(jumpingAddon), ".chatreport", ".cr", "/chatreport");
		register(new RandomkillReportCommand(jumpingAddon), ".randomkillreport", ".rk", "/randomkillreport");
		register(new TicketCommand(jumpingAddon), ".ticket", ".dticket", "/ticket", "/traitorticket", "/dticket", "/detectiveticket");
		register(new PartyInjectCommand(jumpingAddon), "%party Injection", "/party");
		register(new QuickShopCommand(jumpingAddon), ".quickshop", ".qs");
		return this;
	}

	public boolean handleCommand(String[] args) {
		CommandHandler.Command command = getCommand(args[0]);
		if (command != null && (command.getExecutor().isStatic() || (!command.getExecutor().isStatic() && command.isEnabled())))
			return command.getExecutor().execute(command, args[0], Arrays.copyOfRange(args, 1, args.length));
		return false;
	}

	private void register(Object command, String label, String... aliases) {
		if (command instanceof CommandExecutor)
			commands.add(new Command(command, label, aliases));
	}

	public BetterListContainerElement getCommandSettings() {
		BetterListContainerElement commandsElement = new BetterListContainerElement("Client Commands", new ControlElement.IconData(Material.COMMAND)).addSettings(new HeaderElement("§ePrefix for client-side commands is \"§c.§e\""));
		commands.stream().filter(command -> !command.getExecutor().isStatic()).forEach(command -> {
			String capitalized = jumpingAddon.getStringUtils().capitalizeFirstLetter(command.getLabel().substring(1));
			BetterBooleanElement enabledElement = new BetterBooleanElement(command.getSettings() == null ? capitalized + (command.getExecutor().getServer() == null ? "" : "\n §8» " + Settings.SettingsCategory.SERVERSUPPORT.getColor() + command.getExecutor().getServer().getName()) : "Enabled", new ControlElement.IconData(command.getSettings() == null ? Material.COMMAND : Material.LEVER), command.isEnabled(), enabled -> {
				command.setEnabled(enabled);
				command.getConfiguration().set("enabled", enabled);
				command.getConfiguration().save();
			});
			if (command.getSettings() == null) {
				commandsElement.addSettings(enabledElement);
			} else {
				BetterListContainerElement containerElement = new BetterListContainerElement(capitalized + (command.getExecutor().getServer() == null ? "" : "\n §8» " + Settings.SettingsCategory.SERVERSUPPORT.getColor() + command.getExecutor().getServer().getName()), new ControlElement.IconData(Material.COMMAND)).addSettings("Client Commands",
						enabledElement,
						new HeaderElement("")
				);
				containerElement.addSettings(command.getSettings().getSettings(command));
				commandsElement.addSettings(containerElement);
			}
		});
		return commandsElement;
	}

	public void setCommandSettings(List<SettingValue> list) {
		commands.forEach(command -> {
			if (command.getSettings() != null)
				command.getSettings().setSettings(command, list, command.getConfiguration());
			command.setEnabled(command.getConfiguration().has("enabled") ? command.getConfiguration().getAsBoolean("enabled") : command.getExecutor().defaultEnabled());
		});
	}

	public Command getCommand(String label) {
		Command command = null;
		for (Command commands : commands)
			if (commands.getLabel().equalsIgnoreCase(label)) {
				command = commands;
				break;
			} else {
				for (String aliases : commands.getAliases())
					if (aliases.equalsIgnoreCase(label)) {
						command = commands;
						break;
					}
			}
		return command;
	}

	@Getter
	public class Command {
		private String label;
		private Configuration configuration;
		private String[] aliases;
		@Setter
		private boolean enabled = false;
		private CommandExecutor executor;
		private CommandSettings settings;

		Command(Object executor, String label, String... aliases) {
			this.executor = (CommandExecutor) executor;
			this.label = label;
			this.aliases = aliases;
			if (executor instanceof CommandSettings)
				this.settings = (CommandSettings) executor;
			configuration = new Configuration(jumpingAddon, jumpingAddon.getCommandConfiguration(), label.toLowerCase().substring(1));
		}
	}
}
