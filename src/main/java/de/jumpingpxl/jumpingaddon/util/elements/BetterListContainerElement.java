package de.jumpingpxl.jumpingaddon.util.elements;

import de.jumpingpxl.jumpingaddon.util.Settings;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.ListContainerElement;
import net.labymod.settings.elements.SettingsElement;

import java.util.Arrays;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 08.12.2018
 */

public class BetterListContainerElement implements BetterElement {

	private ListContainerElement listContainerElement;

	public BetterListContainerElement(String displayName, ControlElement.IconData iconData) {
		listContainerElement = new ListContainerElement(Settings.SettingsCategory.getLastCategory().getColor() + displayName, iconData);
	}

	@Override
	public ListContainerElement getElement() {
		return listContainerElement;
	}

	@Override
	public BetterListContainerElement setDescription(String description) {
		getElement().bindDescription(description);
		return this;
	}

	public BetterListContainerElement addSettings(Object... objects) {
		if (objects.length == 0)
			return this;
		if (listContainerElement.getSubSettings().getElements().isEmpty()) {
			StringBuilder stringBuilder = new StringBuilder();
			Arrays.stream(objects).filter(object -> object instanceof String).forEach(object -> stringBuilder.append(" §7» ").append(Settings.SettingsCategory.getLastCategory().getColor()).append(object));
			stringBuilder.append(" §7» ").append(Settings.SettingsCategory.getLastCategory().getColor()).append(listContainerElement.getDisplayName().split("\n")[0]);
			listContainerElement.getSubSettings().add(new HeaderElement(Settings.SettingsCategory.getLastCategory().getCompleteName() + stringBuilder.toString()));
		}
		Arrays.stream(objects).forEach(object -> {
			if (object instanceof BetterElement) {
				listContainerElement.getSubSettings().add(((BetterElement) object).getElement());
			} else if (object instanceof SettingsElement) {
				listContainerElement.getSubSettings().add((SettingsElement) object);
			}
		});
		return this;
	}
}
