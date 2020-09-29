package de.jumpingpxl.jumpingaddon.util;

import de.jumpingpxl.jumpingaddon.JumpingAddon;

import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */
public class SettingValue {

	private final Configuration configuration;
	private final String configPath;
	private final Object defaultVar;
	private Object value;
	private String stringValue = "";
	private int intValue = 0;
	private boolean booleanValue = false;

	public SettingValue(JumpingAddon jumpingAddon, List<SettingValue> list, String configPath,
	                    Object defaultValue) {
		this.configuration = jumpingAddon.getConfiguration();
		this.configPath = configPath;
		this.defaultVar = defaultValue;
		list.add(this);
	}

	public SettingValue(JumpingAddon jumpingAddon, List<SettingValue> list,
	                    Configuration configuration, String configPath, Object defaultValue) {
		this.configuration = configuration;
		this.configPath = configPath;
		this.defaultVar = defaultValue;
		list.add(this);
	}

	public SettingValue(JumpingAddon jumpingAddon, Configuration configuration, String configPath,
	                    Object defaultValue) {
		this.configuration = configuration;
		this.configPath = configPath;
		this.defaultVar = defaultValue;
		jumpingAddon.getSettings().getSettingValues().add(this);
	}

	public boolean getAsBoolean() {
		return booleanValue;
	}

	public String getAsString() {
		return stringValue;
	}

	public String getAsString(char altColorChar) {
		return StringUtil.translateAlternateColorCodes(altColorChar, stringValue);
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

	public Configuration getConfiguration() {
		return this.configuration;
	}

	public String getConfigPath() {
		return this.configPath;
	}

	public Object getDefaultVar() {
		return this.defaultVar;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
		if (defaultVar instanceof String) {
			stringValue = String.valueOf(value);
		} else if (defaultVar instanceof Boolean) {
			booleanValue = Boolean.parseBoolean(String.valueOf(value));
		} else if (defaultVar instanceof Integer) {
			intValue = Integer.valueOf(String.valueOf(value));
		}
	}

	public String getStringValue() {
		return this.stringValue;
	}

	public int getIntValue() {
		return this.intValue;
	}

	public boolean isBooleanValue() {
		return this.booleanValue;
	}
}
