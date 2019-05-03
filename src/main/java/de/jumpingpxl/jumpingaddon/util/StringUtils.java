package de.jumpingpxl.jumpingaddon.util;

import de.jumpingpxl.jumpingaddon.JumpingAddon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */

public class StringUtils {

	private JumpingAddon jumpingAddon;

	public StringUtils(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public boolean messageMatches(String message, String string) {
		boolean isMessage = false;
		for (String strings : string.split(";;;"))
			if (strings.contains(";+;")) {
				String[] splitted = strings.split(";\\+;");
				if (splitted.length == 1) {
					isMessage = message.startsWith(strings);
					if (isMessage) break;
				} else {
					isMessage = (message.startsWith(splitted[0]) && message.endsWith(splitted[1]));
					if (isMessage) break;
				}
			} else {
				isMessage = message.startsWith(strings);
				if (isMessage) break;
			}
		return isMessage;
	}

	public String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++)
			if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
				b[i] = '\u00A7';
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		return new String(b);
	}

	public String stripColor(String input) {
		if (input == null)
			return null;
		return Pattern.compile("(?i)" + '\u00A7' + "[0-9A-FK-OR]").matcher(input).replaceAll("");
	}

	public String formatDate(String format, long date) {
		return new SimpleDateFormat(format).format(new Date(date));
	}

	public String capitalizeFirstLetter(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	public String repeatLastColor(String input) {
		char lastColor = ' ';
		StringBuilder result = new StringBuilder();
		String[] split = input.split(" ");
		for (int i = 0; i < split.length; i++) {
			boolean append = (split.length - 1) != i;
			String word = split[i];
			result.append(word).append(append ? " " : "");
			char[] chars = word.toCharArray();
			for (int a = 0; a < chars.length; a++) {
				char character = chars[a];
				if (character == '&' || character == '§') {
					if (chars.length < (a + 1)) continue;
					char color;
					try{
						color = chars[a + 1];
					} catch (ArrayIndexOutOfBoundsException e){
						color = chars[a];
					}
					if ("0123456789abcdefklmnor".indexOf(color) == -1) continue;
					lastColor = color;
				}
			}
			if (lastColor != ' ' && append)
				result.append("§").append(lastColor);
		}
		return result.toString();
	}
}
