package de.jumpingpxl.jumpingaddon.util.mods;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.StringUtil;
import de.jumpingpxl.jumpingaddon.util.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 03.03.2019
 */

public class ActionModule {

	private final JumpingAddon jumpingAddon;
	private final List<Action> actionList = new ArrayList<>();

	public ActionModule(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public ActionModule loadActions() {
		if (jumpingAddon.getConfiguration().has("actions")) {
			jumpingAddon.getConfiguration().get("actions").getAsJsonArray().forEach(jsonElement -> {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				actionList.add(new Action(jumpingAddon, jsonObject.get("name").getAsString(),
						jsonObject.get("checkMessage").getAsString(),
						ActionCheckType.valueOf(jsonObject.get("checkType").getAsString()),
						jsonObject.get("response").getAsString(), jsonObject.get("delay").getAsInt(),
						jsonObject.get("enabled").getAsBoolean()));
			});
		}
		return this;
	}

	public void checkMessage(String message) {
		actionList.stream().filter(Action::isEnabled).forEach(action -> action.checkMessage(message));
	}

	public void addAction(Action action) {
		actionList.add(action);
		save();
	}

	public void removeAction(Action action) {
		if (actionList.remove(action)) {
			save();
		}
	}

	public void save() {
		JsonArray jsonArray = new JsonArray();
		actionList.forEach(action -> {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("name", action.getName());
			jsonObject.addProperty("checkMessage", action.getCheckMessage());
			jsonObject.addProperty("checkType", action.getCheckType().toString());
			jsonObject.addProperty("response", action.getResponse());
			jsonObject.addProperty("delay", action.getDelay());
			jsonObject.addProperty("enabled", action.isEnabled());
			jsonArray.add(jsonObject);
		});
		jumpingAddon.getConfiguration().set("actions", jsonArray);
		jumpingAddon.getConfiguration().save();
	}

	public Action getAction(String name) {
		return getAction(name, actionList);
	}

	public Action getAction(String name, List<Action> actionList) {
		Action action = null;
		for (Action actions : actionList) {
			if (actions.getName().equalsIgnoreCase(name)) {
				action = actions;
				break;
			}
		}
		return action;
	}

	public JumpingAddon getJumpingAddon() {
		return this.jumpingAddon;
	}

	public List<Action> getActionList() {
		return this.actionList;
	}

	public enum ActionCheckType {
		STARTSWITH,
		ENDSWITH,
		SPLIT,
		CONTAINS,
		REGEX
	}

	public static class Action {

		private JumpingAddon jumpingAddon;
		private String name;
		private String checkMessage;
		private ActionCheckType checkType;
		private String response;
		private int delay;
		private boolean enabled;

		public Action(JumpingAddon jumpingAddon, String name, String checkMessage,
		              ActionCheckType checkType, String response, int delay, boolean enabled) {
			this.jumpingAddon = jumpingAddon;
			this.name = name;
			this.checkMessage = checkMessage;
			this.checkType = checkType;
			this.response = response;
			this.enabled = enabled;
			this.delay = delay;
		}

		private void respond(String message) {
			if (delay == 0) {
				jumpingAddon.sendMessage(checkArguments(response, message));
			} else {
				new Task(() -> jumpingAddon.sendMessage(checkArguments(response, message))).delay(delay,
						TimeUnit.SECONDS);
			}
		}

		private String checkArguments(String response, String checkMessage) {
			if (!response.contains("%[args")) {
				return response;
			}
			String message = response;
			String[] args = checkMessage.split(" ");
			for (String strings : response.split("%\\[args")) {
				int i = -1;
				try {
					i = Integer.valueOf(strings.split("]")[0]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				if (i != -1) {
					try {
						message = message.replace("%[args" + i + "]", args[i]);
					} catch (ArrayIndexOutOfBoundsException ignored) {
					}
				}
			}
			return message;
		}

		private void checkMessage(String message) {
			String toCheck = StringUtil.stripColor(message);
			switch (checkType) {
				case STARTSWITH:
					if (toCheck.startsWith(checkMessage)) {
						respond(toCheck);
					}
					break;
				case ENDSWITH:
					if (toCheck.endsWith(checkMessage)) {
						respond(toCheck);
					}
					break;
				case SPLIT:
					if (checkMessage.contains("%&&")) {
						String[] split = checkMessage.split("%&&");
						if (split.length == 2 && (toCheck.startsWith(split[0]) && toCheck.endsWith(split[1]))) {
							respond(toCheck);
						}
					}
					break;
				case CONTAINS:
					if (checkMessage.contains("%&&")) {
						String[] split = checkMessage.split("%&&");
						boolean contains = false;
						for (String s : split) {
							if (toCheck.contains(s)) {
								contains = true;
							} else {
								contains = false;
								break;
							}
						}
						if (contains) {
							respond(toCheck);
						}
					} else {
						if (toCheck.contains(checkMessage)) {
							respond(toCheck);
						}
					}
					break;
				case REGEX:
					if (toCheck.matches(checkMessage)) {
						respond(toCheck);
					}
					break;
			}
		}

		public JumpingAddon getJumpingAddon() {
			return this.jumpingAddon;
		}

		public void setJumpingAddon(JumpingAddon jumpingAddon) {
			this.jumpingAddon = jumpingAddon;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCheckMessage() {
			return this.checkMessage;
		}

		public void setCheckMessage(String checkMessage) {
			this.checkMessage = checkMessage;
		}

		public ActionCheckType getCheckType() {
			return this.checkType;
		}

		public void setCheckType(ActionCheckType checkType) {
			this.checkType = checkType;
		}

		public String getResponse() {
			return this.response;
		}

		public void setResponse(String response) {
			this.response = response;
		}

		public int getDelay() {
			return this.delay;
		}

		public void setDelay(int delay) {
			this.delay = delay;
		}

		public boolean isEnabled() {
			return this.enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}
}
