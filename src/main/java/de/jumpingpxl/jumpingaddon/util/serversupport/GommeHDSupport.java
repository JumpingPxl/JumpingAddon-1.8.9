package de.jumpingpxl.jumpingaddon.util.serversupport;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.ChatComponent;
import de.jumpingpxl.jumpingaddon.util.Configuration;
import de.jumpingpxl.jumpingaddon.util.SettingValue;
import de.jumpingpxl.jumpingaddon.util.StringUtil;
import de.jumpingpxl.jumpingaddon.util.elements.BetterBooleanElement;
import de.jumpingpxl.jumpingaddon.util.elements.BetterListContainerElement;
import de.jumpingpxl.jumpingaddon.util.elements.BetterStringElement;
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
		config = new Configuration(jumpingAddon, jumpingAddon.getSupportConfiguration(),
				getServer().getName().toLowerCase());
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
		messageSenderCheck = new ServerSupportHandler.CheckMessage(checkMessages,
				"checkMessageSender");
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

	public void setConfig(Configuration config) {
		this.config = config;
	}

	@Override
	public ChatComponent handleIncomingMessage(ChatComponent chatComponent, String formatted,
	                                           String unformatted) {
		friendJoinCheck.matches(formatted, string -> {
			String player = StringUtil.stripColor(string);
			lastOnlineFriend = player;
			if (settings.friendQuickInvite.getAsBoolean()) {
				ChatComponent sibling = chatComponent.getFormattedSibling(string, false, 0);
				if (sibling != null) {
					sibling.setHoverEvent(HoverEvent.Action.SHOW_TEXT,
							jumpingAddon.getMessage("moduleFriendQuickInvite", string)).setClickEvent(
							ClickEvent.Action.RUN_COMMAND, StringUtil.stripColor("/party invite " + player));
				}
			}
		});
		partyInviteCheck.matches(formatted, string -> {
			if (settings.partyAutoAcceptAfk.getAsBoolean() || !jumpingAddon.getConnection().isAfk()) {
				String player = StringUtil.stripColor(string);
				lastPartyInvite = player;
				if (settings.partyAutoAccept.getAsBoolean() && (
						settings.partyAutoAcceptPlayers.getAsString().isEmpty() || Arrays.asList(
								settings.partyAutoAcceptPlayers.getAsString().split(";")).contains(player))) {
					jumpingAddon.sendMessage("/party accept " + player);
				}
			}
		});
		messageSenderCheck.matches(settings.playerQuickAnswer, formatted, string -> {
			String[] args = string.split(" ");
			String player = (args.length == 1 ? args[0] : args[1]);
			if (jumpingAddon.getConnection().playerExists(player)) {
				ChatComponent sibling = chatComponent.getFormattedSibling(player, false);
				if (sibling != null) {
					sibling.setHoverEvent(HoverEvent.Action.SHOW_TEXT,
							jumpingAddon.getMessage("modulePlayerQuickAnswer", player)).setClickEvent(
							ClickEvent.Action.SUGGEST_COMMAND, StringUtil.stripColor(player + ", "));
				}
			}
		});
		reportCheck.matches(unformatted, string -> {
			settings.reports.setValue(settings.reports.getAsInteger() + 1);
			settings.reports.getConfiguration().set(settings.reports.getConfigPath(),
					settings.reports.getAsInteger());
			settings.reports.getConfiguration().save();
		});
		nickedCheck.matches(unformatted, string -> nickName = string);
		unnickedCheck.matches(unformatted, string -> nickName = null);
		coloredGgCheck.matches(settings.hideColoredGg, formatted, string -> {
			try {
				String[] coloredGG = string.split(" ");
				if (coloredGG.length == 3) {
					String[] strippedColoredGG = StringUtil.stripColor(string).split(" ");
					int i = 0;
					for (ChatComponent sibling : chatComponent.getSiblings()) {
						if (sibling.getFormattedText().replace("§r", "").equals(
								coloredGG[0].substring(0, coloredGG[0].length() - 2))) {
							break;
						}
						i++;
					}
					if (chatComponent.getSibling(i + 1).getUnformattedText().replace(" ", "").equals(
							strippedColoredGG[1])) {
						chatComponent.getSibling(i).setText("");
						chatComponent.getSibling(i + 1).setText(strippedColoredGG[1]);
						chatComponent.getSibling(i + 2).setText("");
					}
				} else if (coloredGG.length == 1) {
					ChatComponent sibling = chatComponent.getFormattedSibling(coloredGG[0], false);
					if (sibling != null) {
						sibling.setText(StringUtil.stripColor(coloredGG[0]));
					}
				}
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		});
		if (jumpingAddon.getConnection().getGameType() != null && jumpingAddon.getConnection()
				.getGameType()
				.getName()
				.equalsIgnoreCase("TTT")) {
			tttLobbyEndCheck.matches(unformatted, string -> {
				if (ticket != null) {
					jumpingAddon.sendMessage(ticket.equals("t") ? "/traitor" : "/detective");
					ticket = null;
				}
			});
			tttKilledCheck.matches(settings.tttQuickReport, formatted, string -> {
				if (string.startsWith("§a") || string.startsWith("§9")) {
					chatComponent.getSiblings().forEach(
							chatComponents -> chatComponents.setHoverEvent(HoverEvent.Action.SHOW_TEXT,
									jumpingAddon.getMessage("checkTTTQuickReportHover", string))
									.setClickEvent(ClickEvent.Action.RUN_COMMAND,
											"/report " + StringUtil.stripColor(string) + " randomkilling confirm"));
				}
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
				if (settings.tttRoleTitle.getAsBoolean()) {
					jumpingAddon.getConnection().sendTitle(null, string);
				}
			});
		}
		return chatComponent;
	}

	@Override
	public boolean handleReceivingMessage(String formatted, String unformatted) {
		return settings.hideBlankMessages.getAsBoolean() && unformatted.replace(" ", "").length() == 0;
	}

	@Override
	public ServerSupportHandler.GameType handleGameType(String unformatted) {
		ServerSupportHandler.GameType[] gameType = {null};
		try {
			String gameTypeString;
			if (!gameTypeCheck.matches(unformatted, string -> {
				for (ServerSupportHandler.GameType gameTypes : gameTypes) {
					if (gameTypes.getDefinition().equalsIgnoreCase(string.replace(" ", ""))) {
						gameType[0] = gameTypes;
						break;
					}
				}
				if (gameType[0] == null) {
					gameType[0] = new ServerSupportHandler.GameType("", string, "", false);
				}
			})) {
				gameType[0] = new ServerSupportHandler.GameType("", "Unknown", "", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			gameType[0] = new ServerSupportHandler.GameType("", "Unknown", "", false);
		}
		jumpingAddon.getEventHandler().getGameTypeUpdateListener().onGameTypeUpdate(
				jumpingAddon.getConnection().getServer(), gameType[0],
				jumpingAddon.getConnection().getGameType());
		return gameType[0];
	}

	@Override
	public BetterListContainerElement getSettingsElement() {
		return new BetterListContainerElement(getServer().getName(),
				new ControlElement.IconData("jumpingaddon/textures/gommehd.png")).addSettings(
				new BetterBooleanElement("Hide blank messages",
						new ControlElement.IconData(Material.LEVER),
						settings.hideBlankMessages),
				new BetterBooleanElement("Hide colored GG", new ControlElement.IconData(Material.LEVER),
						settings.hideColoredGg),
				new BetterBooleanElement("Quick answer", new ControlElement.IconData(Material.LEVER),
						settings.playerQuickAnswer),
				new BetterBooleanElement("Quick party invite", new ControlElement.IconData(Material.LEVER),
						settings.friendQuickInvite), new BetterListContainerElement("Auto party accept",
						new ControlElement.IconData(Material.CHEST)).addSettings(getServer().getName(),
						new BetterBooleanElement("Enabled", new ControlElement.IconData(Material.LEVER),
								settings.partyAutoAccept), new HeaderElement(""),
						new BetterBooleanElement("While AFK", new ControlElement.IconData(Material.LEVER),
								settings.partyAutoAcceptAfk),
						new BetterStringElement("Players", new ControlElement.IconData(Material.PAPER),
								settings.partyAutoAcceptPlayers)), new HeaderElement(""),
				new BetterListContainerElement("§4TTT",
						new ControlElement.IconData(Material.STICK)).addSettings(getServer().getName(),
						new BetterBooleanElement("Quick report", new ControlElement.IconData(Material.LEVER),
								settings.tttQuickReport), new BetterBooleanElement("Display role title",
								new ControlElement.IconData(Material.LEVER), settings.tttRoleTitle)));
	}

	@Override
	public List<ServerSupportHandler.GameType> getGameTypes() {
		return gameTypes;
	}

	public void setGameTypes(List<ServerSupportHandler.GameType> gameTypes) {
		this.gameTypes = gameTypes;
	}

	@Override
	public void applyGameTypes(List<ServerSupportHandler.GameType> gameTypes) {
		this.gameTypes = gameTypes;
	}

	@Override
	public void applyCheckMessage(String definition, List<Pattern> list) {
		checkMessages.stream()
				.filter(checkMessage -> checkMessage.getDefinition().equals(definition))
				.forEach(checkMessage -> checkMessage.setMessages(list));
	}

	public JumpingAddon getJumpingAddon() {
		return this.jumpingAddon;
	}

	public void setJumpingAddon(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public SupportSettings getSettings() {
		return this.settings;
	}

	public void setSettings(SupportSettings settings) {
		this.settings = settings;
	}

	public List<ServerSupportHandler.CheckMessage> getCheckMessages() {
		return this.checkMessages;
	}

	public void setCheckMessages(List<ServerSupportHandler.CheckMessage> checkMessages) {
		this.checkMessages = checkMessages;
	}

	public String getNickName() {
		return this.nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getTicket() {
		return this.ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getLastPartyInvite() {
		return this.lastPartyInvite;
	}

	public void setLastPartyInvite(String lastPartyInvite) {
		this.lastPartyInvite = lastPartyInvite;
	}

	public String getLastOnlineFriend() {
		return this.lastOnlineFriend;
	}

	public void setLastOnlineFriend(String lastOnlineFriend) {
		this.lastOnlineFriend = lastOnlineFriend;
	}

	public String getTttRole() {
		return this.tttRole;
	}

	public void setTttRole(String tttRole) {
		this.tttRole = tttRole;
	}

	public String getTttRoleShort() {
		return this.tttRoleShort;
	}

	public void setTttRoleShort(String tttRoleShort) {
		this.tttRoleShort = tttRoleShort;
	}

	public int getTttOnlineCount() {
		return this.tttOnlineCount;
	}

	public void setTttOnlineCount(int tttOnlineCount) {
		this.tttOnlineCount = tttOnlineCount;
	}

	public boolean isIngame() {
		return this.ingame;
	}

	public void setIngame(boolean ingame) {
		this.ingame = ingame;
	}

	public ServerSupportHandler.CheckMessage getGameTypeCheck() {
		return this.gameTypeCheck;
	}

	public void setGameTypeCheck(ServerSupportHandler.CheckMessage gameTypeCheck) {
		this.gameTypeCheck = gameTypeCheck;
	}

	public ServerSupportHandler.CheckMessage getFriendJoinCheck() {
		return this.friendJoinCheck;
	}

	public void setFriendJoinCheck(ServerSupportHandler.CheckMessage friendJoinCheck) {
		this.friendJoinCheck = friendJoinCheck;
	}

	public ServerSupportHandler.CheckMessage getMessageSenderCheck() {
		return this.messageSenderCheck;
	}

	public void setMessageSenderCheck(ServerSupportHandler.CheckMessage messageSenderCheck) {
		this.messageSenderCheck = messageSenderCheck;
	}

	public ServerSupportHandler.CheckMessage getPartyInviteCheck() {
		return this.partyInviteCheck;
	}

	public void setPartyInviteCheck(ServerSupportHandler.CheckMessage partyInviteCheck) {
		this.partyInviteCheck = partyInviteCheck;
	}

	public ServerSupportHandler.CheckMessage getReportCheck() {
		return this.reportCheck;
	}

	public void setReportCheck(ServerSupportHandler.CheckMessage reportCheck) {
		this.reportCheck = reportCheck;
	}

	public ServerSupportHandler.CheckMessage getNickedCheck() {
		return this.nickedCheck;
	}

	public void setNickedCheck(ServerSupportHandler.CheckMessage nickedCheck) {
		this.nickedCheck = nickedCheck;
	}

	public ServerSupportHandler.CheckMessage getUnnickedCheck() {
		return this.unnickedCheck;
	}

	public void setUnnickedCheck(ServerSupportHandler.CheckMessage unnickedCheck) {
		this.unnickedCheck = unnickedCheck;
	}

	public ServerSupportHandler.CheckMessage getColoredGgCheck() {
		return this.coloredGgCheck;
	}

	public void setColoredGgCheck(ServerSupportHandler.CheckMessage coloredGgCheck) {
		this.coloredGgCheck = coloredGgCheck;
	}

	public ServerSupportHandler.CheckMessage getTttLobbyEndCheck() {
		return this.tttLobbyEndCheck;
	}

	public void setTttLobbyEndCheck(ServerSupportHandler.CheckMessage tttLobbyEndCheck) {
		this.tttLobbyEndCheck = tttLobbyEndCheck;
	}

	public ServerSupportHandler.CheckMessage getTttKilledCheck() {
		return this.tttKilledCheck;
	}

	public void setTttKilledCheck(ServerSupportHandler.CheckMessage tttKilledCheck) {
		this.tttKilledCheck = tttKilledCheck;
	}

	public ServerSupportHandler.CheckMessage getTttEndCheck() {
		return this.tttEndCheck;
	}

	public void setTttEndCheck(ServerSupportHandler.CheckMessage tttEndCheck) {
		this.tttEndCheck = tttEndCheck;
	}

	public ServerSupportHandler.CheckMessage getTttRoleCheck() {
		return this.tttRoleCheck;
	}

	public void setTttRoleCheck(ServerSupportHandler.CheckMessage tttRoleCheck) {
		this.tttRoleCheck = tttRoleCheck;
	}

	public class SupportSettings implements ServerSupport.SupportSettings {

		private SettingValue hideBlankMessages;
		private SettingValue hideColoredGg;
		private SettingValue friendQuickInvite;
		private SettingValue playerQuickAnswer;
		private SettingValue partyAutoAccept;
		private SettingValue partyAutoAcceptAfk;
		private SettingValue partyAutoAcceptPlayers;
		private SettingValue tttRoleTitle;
		private SettingValue tttQuickReport;

		private SettingValue reports;

		private SupportSettings() {

		}

		@Override
		public void loadSettings(Configuration configuration) {
			hideBlankMessages = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
					configuration, "enabledHideBlankMessages", true);
			hideColoredGg = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
					configuration,
					"enabledHideColoredGG", false);
			friendQuickInvite = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
					configuration, "enabledFriendQuickInvite", true);
			playerQuickAnswer = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
					configuration, "enabledPlayerQuickAnswer", true);
			partyAutoAccept = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
					configuration, "enabledPartyAutoAccept", false);
			partyAutoAcceptAfk = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
					configuration, "enabledPartyAutoAcceptAfk", false);
			partyAutoAcceptPlayers = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
					configuration, "partyAutoAcceptPlayers", "");
			tttRoleTitle = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(), configuration,
					"enabledTTTRoleTitle", true);
			tttQuickReport = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
					configuration, "enabledTTTQuickReport", true);
			reports = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(), configuration,
					"reports", 0);
		}

		public SettingValue getHideBlankMessages() {
			return this.hideBlankMessages;
		}

		public SettingValue getHideColoredGg() {
			return this.hideColoredGg;
		}

		public SettingValue getFriendQuickInvite() {
			return this.friendQuickInvite;
		}

		public SettingValue getPlayerQuickAnswer() {
			return this.playerQuickAnswer;
		}

		public SettingValue getPartyAutoAccept() {
			return this.partyAutoAccept;
		}

		public SettingValue getPartyAutoAcceptAfk() {
			return this.partyAutoAcceptAfk;
		}

		public SettingValue getPartyAutoAcceptPlayers() {
			return this.partyAutoAcceptPlayers;
		}

		public SettingValue getTttRoleTitle() {
			return this.tttRoleTitle;
		}

		public SettingValue getTttQuickReport() {
			return this.tttQuickReport;
		}

		public SettingValue getReports() {
			return this.reports;
		}
	}
}
