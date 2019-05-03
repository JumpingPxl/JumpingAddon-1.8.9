package de.jumpingpxl.jumpingaddon.util.serversupport;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.ChatComponent;
import de.jumpingpxl.jumpingaddon.util.Configuration;
import de.jumpingpxl.jumpingaddon.util.SettingValue;
import de.jumpingpxl.jumpingaddon.util.elements.BetterBooleanElement;
import de.jumpingpxl.jumpingaddon.util.elements.BetterListContainerElement;
import de.jumpingpxl.jumpingaddon.util.elements.BetterStringElement;
import lombok.Getter;
import lombok.Setter;
import net.labymod.core.LabyModCore;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.utils.Material;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

@Getter
@Setter
public class GommeHDSupport implements ServerSupport {

	private JumpingAddon jumpingAddon;
	private Configuration config;
	private SupportSettings settings;
	private List<ServerSupportHandler.GameType> gameTypes;
	private List<ServerSupportHandler.CheckMessage> checkMessages = new ArrayList<>();

	private String nickName;
	private String ticket;
	private String lastPartyInvite;
	private String lastOnlineFriend;
	private String tttRole;
	private String tttRoleShort;

	private int tttOnlineCount;

	private boolean ingame;

	private ServerSupportHandler.CheckMessage gameTypeCheck;
	private ServerSupportHandler.CheckMessage friendJoinCheck;
	private ServerSupportHandler.CheckMessage messageSenderCheck;
	private ServerSupportHandler.CheckMessage partyInviteCheck;
	private ServerSupportHandler.CheckMessage reportCheck;
	private ServerSupportHandler.CheckMessage nickedCheck;
	private ServerSupportHandler.CheckMessage unnickedCheck;
	private ServerSupportHandler.CheckMessage coloredGgCheck;
	private ServerSupportHandler.CheckMessage tttLobbyEndCheck;
	private ServerSupportHandler.CheckMessage tttKilledCheck;
	private ServerSupportHandler.CheckMessage tttEndCheck;
	private ServerSupportHandler.CheckMessage tttRoleCheck;

	public GommeHDSupport(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
		config = new Configuration(jumpingAddon, jumpingAddon.getSupportConfiguration(), getServer().getName().toLowerCase());
		settings = new SupportSettings();
		settings.loadSettings(config);
		loadChecks();
		Server.getSupportMap().put(getServer(), this);
	}

	@Override
	public Server getServer() {
		return Server.GOMMEHD_NET;
	}

	@Override
	public void loadChecks() {
		gameTypeCheck = new ServerSupportHandler.CheckMessage(checkMessages, "checkGameType");
		friendJoinCheck = new ServerSupportHandler.CheckMessage(checkMessages, "checkFriendJoin");
		messageSenderCheck = new ServerSupportHandler.CheckMessage(checkMessages, "checkMessageSender");
		partyInviteCheck = new ServerSupportHandler.CheckMessage(checkMessages, "checkPartyInvite");
		reportCheck = new ServerSupportHandler.CheckMessage(checkMessages, "checkReport");
		nickedCheck = new ServerSupportHandler.CheckMessage(checkMessages, "checkNicked");
		unnickedCheck = new ServerSupportHandler.CheckMessage(checkMessages, "checkUnnicked");
		coloredGgCheck = new ServerSupportHandler.CheckMessage(checkMessages, "checkColoredGG");
		tttLobbyEndCheck = new ServerSupportHandler.CheckMessage(checkMessages, "checkTTTLobbyEnd");
		tttKilledCheck = new ServerSupportHandler.CheckMessage(checkMessages, "checkTTTKilled");
		tttEndCheck = new ServerSupportHandler.CheckMessage(checkMessages, "checkTTTEnd");
		tttRoleCheck = new ServerSupportHandler.CheckMessage(checkMessages, "checkTTTRole");
	}

	@Override
	public Configuration getConfig() {
		return config;
	}

	@Override
	public ChatComponent handleIncomingMessage(ChatComponent chatComponent, String formatted, String unformatted) {
		friendJoinCheck.matches(formatted, string -> {
			String player = jumpingAddon.getStringUtils().stripColor(string);
			lastOnlineFriend = player;
			if (settings.friendQuickInvite.getAsBoolean()) {
				ChatComponent sibling = chatComponent.getFormattedSibling(string, false, 0);
				if (sibling != null)
					sibling.setHoverEvent(HoverEvent.Action.SHOW_TEXT, jumpingAddon.getMessage("moduleFriendQuickInvite", string)).setClickEvent(ClickEvent.Action.RUN_COMMAND, jumpingAddon.getStringUtils().stripColor("/party invite " + player));
			}
		});
		partyInviteCheck.matches(formatted, string -> {
			String player = jumpingAddon.getStringUtils().stripColor(string);
			lastPartyInvite = player;
			if (settings.partyAutoAccept.getAsBoolean() && (settings.partyAutoAcceptPlayers.getAsString().isEmpty() || Arrays.asList(settings.partyAutoAcceptPlayers.getAsString().split(";")).contains(player)))
				jumpingAddon.sendMessage("/party accept " + player);
		});
		messageSenderCheck.matches(settings.playerQuickAnswer, formatted, string -> {
			String[] args = string.split(" ");
			String player = (args.length == 1 ? args[0] : args[1]);
			if (jumpingAddon.getConnection().playerExists(player)) {
				ChatComponent sibling = chatComponent.getFormattedSibling(player, false);
				if (sibling != null)
					sibling.setHoverEvent(HoverEvent.Action.SHOW_TEXT, jumpingAddon.getMessage("modulePlayerQuickAnswer", player)).setClickEvent(ClickEvent.Action.SUGGEST_COMMAND, jumpingAddon.getStringUtils().stripColor(player + ", "));
			}
		});
		reportCheck.matches(unformatted, string -> {
			settings.reports.setValue(settings.reports.getAsInteger() + 1);
			settings.reports.getConfiguration().set(settings.reports.getConfigPath(), settings.reports.getAsInteger());
			settings.	reports.getConfiguration().save();
		});
		nickedCheck.matches(unformatted, string -> nickName = string);
		unnickedCheck.matches(unformatted, string -> nickName = null);
		coloredGgCheck.matches(settings.hideColoredGg, formatted, string -> {
			try {
				String[] coloredGG = string.split(" ");
				if (coloredGG.length == 3) {
					String[] strippedColoredGG = jumpingAddon.getStringUtils().stripColor(string).split(" ");
					int i = 0;
					for (ChatComponent sibling : chatComponent.getSiblings()) {
						if (sibling.getFormattedText().replace("§r", "").equals(coloredGG[0].substring(0, coloredGG[0].length() - 2)))
							break;
						i++;
					}
					if (chatComponent.getSibling(i + 1).getUnformattedText().replace(" ", "").equals(strippedColoredGG[1])) {
						chatComponent.getSibling(i).setText("");
						chatComponent.getSibling(i + 1).setText(strippedColoredGG[1]);
						chatComponent.getSibling(i + 2).setText("");
					}
				} else if (coloredGG.length == 1) {
					ChatComponent sibling = chatComponent.getFormattedSibling(coloredGG[0], false);
					if (sibling != null)
						sibling.setText(jumpingAddon.getStringUtils().stripColor(coloredGG[0]));
				}
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		});
		if (jumpingAddon.getConnection().getGameType() != null && jumpingAddon.getConnection().getGameType().getName().equalsIgnoreCase("TTT")) {
			tttLobbyEndCheck.matches(unformatted, string -> {
				if (ticket != null) {
					jumpingAddon.sendMessage(ticket.equals("t") ? "/traitor" : "/detective");
					ticket = null;
				}
			});
			tttKilledCheck.matches(settings.tttQuickReport, formatted, string -> {
				if (string.startsWith("§a") || string.startsWith("§9"))
					chatComponent.getSiblings().forEach(chatComponents -> chatComponents.setHoverEvent(HoverEvent.Action.SHOW_TEXT, jumpingAddon.getMessage("checkTTTQuickReportHover", string)).setClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + jumpingAddon.getStringUtils().stripColor(string) + " randomkilling confirm"));
			});
			tttEndCheck.matches(unformatted, string -> {
				ingame = false;
				tttOnlineCount = -1;
				tttRole = null;
				tttRoleShort = null;
			});
			tttRoleCheck.matches(formatted, string -> {
				ingame = true;
				tttOnlineCount = LabyModCore.getMinecraft().getConnection().getPlayerInfoMap().size();
				tttRole = string;
				tttRoleShort = string.substring(0, 3);
				if (settings.tttRoleTitle.getAsBoolean())
					jumpingAddon.getConnection().sendTitle(null, string);
			});
		}
		return chatComponent;
	}

	@Override
	public boolean handleReceivingMessage(String formatted, String unformatted) {
		if (settings.hideBlankMessages.getAsBoolean() && unformatted.replace(" ", "").length() == 0)
			return true;
		return false;
	}

	@Override
	public ServerSupportHandler.GameType handleGameType(String unformatted) {
		ServerSupportHandler.GameType[] gameType = {null};
		String gameTypeString;
		if (!gameTypeCheck.matches(unformatted, string -> {
			for (ServerSupportHandler.GameType gameTypes : gameTypes)
				if (gameTypes.getDefinition().equalsIgnoreCase(string.replace(" ", ""))) {
					gameType[0] = gameTypes;
					break;
				}
			if (gameType[0] == null)
				gameType[0] = new ServerSupportHandler.GameType("", string.replace(" ", ""), "", true);
		}))
			gameType[0] = new ServerSupportHandler.GameType("", "Unknown", "", false);
		jumpingAddon.getEventHandler().getGameTypeUpdateListener().onGameTypeUpdate(jumpingAddon.getConnection().getServer(), gameType[0], jumpingAddon.getConnection().getGameType());
		return gameType[0];
	}

	@Override
	public BetterListContainerElement getSettingsElement() {
		return new BetterListContainerElement(getServer().getName(), new ControlElement.IconData("jumpingaddon/textures/gommehd.png")).addSettings(
				new BetterBooleanElement("Hide blank messages", new ControlElement.IconData(Material.LEVER), settings.hideBlankMessages),
				new BetterBooleanElement("Hide colored GG", new ControlElement.IconData(Material.LEVER), settings.hideColoredGg),
				new BetterBooleanElement("Quick answer", new ControlElement.IconData(Material.LEVER), settings.playerQuickAnswer),
				new BetterBooleanElement("Quick party invite", new ControlElement.IconData(Material.LEVER), settings.friendQuickInvite),
				new BetterListContainerElement("Auto party accept", new ControlElement.IconData(Material.CHEST)).addSettings(getServer().getName(),
						new BetterBooleanElement("Enabled", new ControlElement.IconData(Material.LEVER), settings.partyAutoAccept),
						new HeaderElement(""),
						new BetterStringElement("Players", new ControlElement.IconData(Material.PAPER), settings.partyAutoAcceptPlayers)
				),
				new HeaderElement(""),
				new BetterListContainerElement("§4TTT", new ControlElement.IconData(Material.STICK)).addSettings(getServer().getName(),
						new BetterBooleanElement("Quick report", new ControlElement.IconData(Material.LEVER), settings.tttQuickReport),
						new BetterBooleanElement("Display role title", new ControlElement.IconData(Material.LEVER), settings.tttRoleTitle)
				)
		);
	}

	@Override
	public List<ServerSupportHandler.GameType> getGameTypes() {
		return gameTypes;
	}

	@Override
	public void applyGameTypes(List<ServerSupportHandler.GameType> gameTypes) {
		this.gameTypes = gameTypes;
	}

	@Override
	public void applyCheckMessage(String definition, List<Pattern> list) {
		checkMessages.stream().filter(checkMessage -> checkMessage.getDefinition().equals(definition)).forEach(checkMessage -> checkMessage.setMessages(list));
	}

	@Getter
	public class SupportSettings implements ServerSupport.SupportSettings {

		private SettingValue hideBlankMessages;
		private SettingValue hideColoredGg;
		private SettingValue friendQuickInvite;
		private SettingValue playerQuickAnswer;
		private SettingValue partyAutoAccept;
		private SettingValue partyAutoAcceptPlayers;
		private SettingValue tttRoleTitle;
		private SettingValue tttQuickReport;

		private SettingValue reports;

		private SupportSettings() {

		}

		@Override
		public void loadSettings(Configuration configuration) {
			hideBlankMessages = new SettingValue(jumpingAddon.getSettings().getSettingValues(), configuration, "enabledHideBlankMessages", true);
			hideColoredGg = new SettingValue(jumpingAddon.getSettings().getSettingValues(), configuration, "enabledHideColoredGG", false);
			friendQuickInvite = new SettingValue(jumpingAddon.getSettings().getSettingValues(), configuration, "enabledFriendQuickInvite", true);
			playerQuickAnswer = new SettingValue(jumpingAddon.getSettings().getSettingValues(), configuration, "enabledPlayerQuickAnswer", true);
			partyAutoAccept = new SettingValue(jumpingAddon.getSettings().getSettingValues(), configuration, "enabledPartyAutoAccept", false);
			partyAutoAcceptPlayers = new SettingValue(jumpingAddon.getSettings().getSettingValues(), configuration, "partyAutoAcceptPlayers", "");
			tttRoleTitle = new SettingValue(jumpingAddon.getSettings().getSettingValues(), configuration, "enabledTTTRoleTitle", true);
			tttQuickReport = new SettingValue(jumpingAddon.getSettings().getSettingValues(), configuration, "enabledTTTQuickReport", true);

			reports = new SettingValue(jumpingAddon.getSettings().getSettingValues(), configuration, "reports", 0);
		}
	}
}
