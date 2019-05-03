package de.jumpingpxl.jumpingaddon.commands;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.command.CommandExecutor;
import de.jumpingpxl.jumpingaddon.util.command.CommandHandler;
import de.jumpingpxl.jumpingaddon.util.mods.ActionModule;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 10.03.2019
 */

public class ActionCommand implements CommandExecutor {

	private JumpingAddon jumpingAddon;
	private List<ActionModule.Action> tempActions = new ArrayList<>();

	public ActionCommand(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public boolean execute(CommandHandler.Command command, String label, String[] args) {
		if (args.length == 0) {
			actionHelp(label);
			return true;
		}
		switch (args[0].toLowerCase()) {
			case "help":
				actionHelp(label);
				break;
			case "list":
				actionList();
				break;
			case "get":
				actionGet(label, args);
				break;
			case "add":
				actionAdd(label, args);
				break;
			case "set":
				actionSet(label, args);
				break;
			case "remove":
				actionRemove(label, args);
				break;
			case "toggle":
				actionToggle(label, args);
				break;
			default:
				actionHelp(label);
				break;
		}
		return true;
	}

	@Override
	public Server getServer() {
		return null;
	}

	private void actionHelp(String label) {
		send("actionHelp", label);
	}

	private void actionList() {

		StringBuilder stringBuilder = new StringBuilder();
		jumpingAddon.getModuleHandler().getActionModule().getActionList().forEach(action -> stringBuilder.append("§7, ").append(action.isEnabled() ? "§a" : "§c").append(action.getName()));
		if (stringBuilder.toString().length() > 4)
			send("actionListActions", stringBuilder.toString().substring(4));
		else
			send("actionListNoActions");
	}

	private void actionGet(String label, String[] args) {
		if (args.length == 2) {
			ActionModule.Action action = jumpingAddon.getModuleHandler().getActionModule().getAction(args[1]);
			if (action == null)
				send("actionGetNoAction", args[1]);
			else
				send("actionGetInfos", action.getName(), (action.isEnabled() ? "§aJa" : "§cNein"), action.getCheckType().toString(), action.getCheckMessage(), action.getResponse(), action.getDelay() + " Sekunde" + (action.getDelay() == 1 ? "" : "n"));
		} else
			send("commandUsage", label + " get <actionName>");
	}

	private void actionAdd(String label, String[] args) {
		if (args.length == 2) {
			ActionModule.Action action = jumpingAddon.getModuleHandler().getActionModule().getAction(args[1]);
			if (action == null) {
				action = new ActionModule.Action(jumpingAddon, args[1], null, null, null, 0, true);
				tempActions.add(action);
				send("actionAddAdded", action.getName(), label);
			} else
				send("actionAddAlreadyExists", action.getName());
		} else
			send("commandUsage", label + " add <actionName>");
	}

	private void actionSet(String label, String[] args) {
		if (args.length <= 2) {
			send("commandUsage", label + " set <actionName> <checkMessage | checkType | response | config | [delay]>");
			return;
		}
		ActionModule.Action savedAction = jumpingAddon.getModuleHandler().getActionModule().getAction(args[1]);
		ActionModule.Action tempAction = jumpingAddon.getModuleHandler().getActionModule().getAction(args[1], tempActions);
		if ((savedAction == null && tempAction == null) && args.length > 3) {
			send("actionGetNoAction", args[1]);
			return;
		}
		String actionName = tempAction == null ? (savedAction == null ? "null" : savedAction.getName()) : tempAction.getName();
		if (args[2].equalsIgnoreCase("checkMessage")) {
			if (args.length >= 4) {
				StringBuilder stringBuilder = new StringBuilder();
				for (int i = 3; i < args.length; i++)
					stringBuilder.append(" ").append(args[i]);
				if (tempAction == null) {
					ActionModule.Action action = createCopy(savedAction);
					action.setCheckMessage(stringBuilder.toString().substring(1));
					tempActions.add(action);
				} else
					tempAction.setCheckMessage(stringBuilder.toString().substring(1));
				send("actionSetCheckMessageSuccess", actionName, stringBuilder.toString().substring(1), label);
			} else
				send("commandUsage", label + " set <actionName> checkMessage <message>");
			return;
		}
		if (args[2].equalsIgnoreCase("checkType")) {
			if (args.length == 4) {
				ActionModule.ActionCheckType actionCheckType;
				try {
					actionCheckType = ActionModule.ActionCheckType.valueOf(args[3].toUpperCase());
				} catch (IllegalArgumentException e) {
					send("actionSetCheckTypeNotExists", args[3].toUpperCase());
					return;
				}
				if (tempAction == null) {
					ActionModule.Action action = createCopy(savedAction);
					action.setCheckType(actionCheckType);
					tempActions.add(action);
				} else
					tempAction.setCheckType(actionCheckType);
				send("actionSetCheckTypeSuccess", actionName, actionCheckType.toString(), label);
			} else
				send("commandUsage", label + " set <actionName> checkType <startsWith | endsWith | startsEndsWith | contains | regex>");
			return;
		}
		if (args[2].equalsIgnoreCase("response")) {
			if (args.length >= 4) {
				StringBuilder stringBuilder = new StringBuilder();
				for (int i = 3; i < args.length; i++)
					stringBuilder.append(" ").append(args[i]);
				if (tempAction == null) {
					ActionModule.Action action = createCopy(savedAction);
					action.setResponse(stringBuilder.toString().substring(1));
					tempActions.add(action);
				} else
					tempAction.setResponse(stringBuilder.toString().substring(1));
				send("actionSetResponseSuccess", actionName, stringBuilder.toString().substring(1), label);
			} else
				send("commandUsage", label + " set <actionName> response <message>");
			return;
		}
		if (args[2].equalsIgnoreCase("delay")) {
			if (args.length == 4) {
				int delay;
				try {
					int tempDelay = Integer.valueOf(args[3]);
					if (tempDelay < 0 || tempDelay > 30) {
						send("actionSetDelayNotInRange");
						return;
					} else
						delay = tempDelay;
				} catch (NumberFormatException e) {
					send("actionSetDelayNoNumber", args[3]);
					return;
				}
				if (tempAction == null) {
					ActionModule.Action action = createCopy(savedAction);
					action.setDelay(delay);
					tempActions.add(action);
				} else
					tempAction.setDelay(delay);
				send("actionSetDelaySuccess", actionName, String.valueOf(delay), label);
			} else
				send("commandUsage", label + " set <actionName> delay <timeInSeconds>");
			return;
		}
		if (args[2].equalsIgnoreCase("config")) {
			if (tempAction == null) {
				send("actionSetConfigNoAction", args[1]);
				return;
			}
			boolean checkMessage = tempAction.getCheckMessage() != null;
			boolean checkType = tempAction.getCheckType() != null;
			boolean response = tempAction.getResponse() != null;
			if (!checkMessage || (!checkType || !response)) {
				StringBuilder stringBuilder = new StringBuilder();
				if (!checkMessage)
					stringBuilder.append("§7, §eCheckMessage");
				if (!checkType)
					stringBuilder.append("§7, §eCheckType");
				if (!response)
					stringBuilder.append("§7, §eResponse");
				send("actionSetConfigMustConfigure", actionName, stringBuilder.toString().substring(4), label);
				return;
			}
			if (savedAction != null)
				jumpingAddon.getModuleHandler().getActionModule().getActionList().remove(savedAction);
			jumpingAddon.getModuleHandler().getActionModule().addAction(tempAction);
			send("actionSetConfigSuccess", actionName);
			return;
		}
		actionSet(label, new String[]{"set"});
	}

	private void actionRemove(String label, String[] args) {
		if (args.length != 2 && args.length != 3) {
			send("commandUsage", label + " remove <actionName>");
			return;
		}
		ActionModule.Action savedAction = jumpingAddon.getModuleHandler().getActionModule().getAction(args[1]);
		ActionModule.Action tempAction = jumpingAddon.getModuleHandler().getActionModule().getAction(args[1], tempActions);
		if (savedAction == null && tempAction == null) {
			send("actionGetNoAction", args[1]);
			return;
		}
		if (args.length == 2) {
			String actionName = tempAction == null ? savedAction.getName() : tempAction.getName();
			send("actionRemoveConfirm", actionName, label);
			return;
		}
		if (args[2].equalsIgnoreCase("confirm")) {
			String actionName = tempAction == null ? savedAction.getName() : tempAction.getName();
			if (tempAction == null) {
				jumpingAddon.getModuleHandler().getActionModule().removeAction(savedAction);
				send("actionRemoveSuccess1", actionName);
			} else {
				tempActions.remove(tempAction);
				send("actionRemoveSuccess2", actionName);
			}
		}
	}

	private void actionToggle(String label, String[] args) {
		if (args.length != 2) {
			send("commandUsage", label + " toggle <actionName>");
			return;
		}
		ActionModule.Action savedAction = jumpingAddon.getModuleHandler().getActionModule().getAction(args[1]);
		if (savedAction == null) {
			send("actionGetNoAction", args[1]);
			return;
		}
		if (savedAction.isEnabled()) {
			savedAction.setEnabled(false);
			send("actionToggleDeactivated", args[1]);
		} else {
			savedAction.setEnabled(true);
			send("actionToggleActivated", args[1]);
		}
		jumpingAddon.getModuleHandler().getActionModule().save();
	}

	private ActionModule.Action createCopy(ActionModule.Action action) {
		return new ActionModule.Action(jumpingAddon, action.getName(), action.getCheckMessage(), action.getCheckType(), action.getResponse(), action.getDelay(), action.isEnabled());
	}
}
