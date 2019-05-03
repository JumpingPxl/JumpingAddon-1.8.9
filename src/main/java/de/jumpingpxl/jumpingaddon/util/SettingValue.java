package de.jumpingpxl.jumpingaddon.util;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */
@Getter
@Setter
public class SettingValue {

	private Configuration configuration;
	private String configPath;
	private Object defaultVar;
	@Getter(value = AccessLevel.NONE)
	private Object value;
	@Getter(value = AccessLevel.NONE)
	private String stringValue = "";
	@Getter(value = AccessLevel.NONE)
	private int intValue = 0;
	@Getter(value = AccessLevel.NONE)
	private boolean booleanValue = false;

	public SettingValue(List<SettingValue> list, String configPath, Object defaultValue) {
		this.configuration = JumpingAddon.getInstance().getConfiguration();
		this.configPath = configPath;
		this.defaultVar = defaultValue;
		list.add(this);
	}

	public SettingValue(List<SettingValue> list, Configuration configuration, String configPath, Object defaultValue) {
		this.configuration = configuration;
		this.configPath = configPath;
		this.defaultVar = defaultValue;
		list.add(this);
	}

	public boolean getAsBoolean() {
		return booleanValue;
	}

	public String getAsString() {
		return stringValue;
	}

	public String getAsString(char altColorChar) {
		return JumpingAddon.getInstance().getStringUtils().translateAlternateColorCodes(altColorChar, stringValue);
	}

	public int getAsInteger() {
		return intValue;
	}

	public Object getAsObject() {
		return value;
	}

	void load() {
		boolean has = configuration.has(configPath);
		if (defaultVar instanceof String) {
			if (!has) {
				configuration.set(configPath, String.valueOf(defaultVar));
				configuration.save();
			}
			stringValue = configuration.getAsString(configPath);
			value = stringValue;
		} else if (defaultVar instanceof Boolean) {
			if (!has) {
				configuration.set(configPath, Boolean.parseBoolean(String.valueOf(defaultVar)));
				configuration.save();
			}
			booleanValue = configuration.getAsBoolean(configPath);
			value = booleanValue;
		} else if (defaultVar instanceof Integer) {
			if (!has) {
				configuration.set(configPath, Integer.valueOf(String.valueOf(defaultVar)));
				configuration.save();
			}
			intValue = configuration.getAsInt(configPath);
			value = booleanValue;
		}
	}

	public void setValue(Object value) {
		this.value = value;
		if (defaultVar instanceof String)
			stringValue = String.valueOf(value);
		else if (defaultVar instanceof Boolean)
			booleanValue = Boolean.parseBoolean(String.valueOf(value));
		else if (defaultVar instanceof Integer)
			intValue = Integer.valueOf(String.valueOf(value));
	}
}
