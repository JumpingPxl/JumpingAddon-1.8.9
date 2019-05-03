package de.jumpingpxl.jumpingaddon.util.gui;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import lombok.Getter;
import net.labymod.gui.elements.Tabs;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 05.04.2019
 */

public class SettingsGui extends GuiScreen {

	private List<GuiButton> tabs = new ArrayList<>();
	private HashMap<ControlElement, SettingElement> settingsElements = new HashMap<>();
	private List<SettingsElement> elementList;
	private SettingElement currentElement;
	private GuiButton backButton;

	@Override
	public void initGui() {
		super.initGui();
		elementList = JumpingAddon.getInstance().getSubSettings();
		loadSettings(elementList);
		backButton = new GuiButton(1, 20, 50, (this.width / 2 - this.width / 5) - 40, 20, "");
		buttonList.add(backButton);
		Tabs.initGuiScreen(tabs, this);
		buttonList.addAll(tabs);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		DrawUtils draw = LabyMod.getInstance().getDrawUtils();
		draw.drawAutoDimmedBackground(0.0D);
		draw.drawOverlayBackground(0, 41);
		draw.drawOverlayBackground(this.height - 32, this.height);
		draw.drawOverlayBackground(this.height - 32, this.height);
		draw.drawGradientShadowTop(41.0D, 0.0D, (double) this.width);
		draw.drawGradientShadowBottom((double) (this.height - 32), 0.0D, (double) this.width);
		draw.drawOverlayBackground(0, 0, this.width / 2 - this.width / 5, this.height);
		draw.drawGradientShadowLeft(((double) this.width / 2 - this.width / 5), 41.0D, (double) (this.height - 32));
		tabs.forEach(tab -> tab.drawButton(mc, mouseX, mouseY));
		if (backButton.isMouseOver())
			backButton.displayString = "§e<-- Previous <--";
		else
			backButton.displayString = "<-- Previous <--";
		backButton.drawButton(mc, mouseX, mouseY);
		int y = 75;
		if (currentElement == null)
			for (SettingsElement settingsElement : elementList) {
				settingsElement.draw(20, y, (this.width / 2 - this.width / 5) - 20, y + settingsElement.getEntryHeight(), mouseX, mouseY);
				y += settingsElement.getEntryHeight() + 2;
			}
		else
			for (SettingElement settingsElement : currentElement.getSubSettings()) {
				settingsElement.getSettingsElement().draw(20, y, (this.width / 2 - this.width / 5) - 20, y + settingsElement.getSettingsElement().getEntryHeight(), mouseX, mouseY);
				y += settingsElement.getSettingsElement().getEntryHeight() + 2;
			}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		Tabs.actionPerformedButton(button);
		if (button == backButton) {
			if (currentElement == null)
				return;
			if (currentElement.getParent() == null)
				this.currentElement = null;
			else
				this.currentElement = currentElement.getParent();
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (backButton.isMouseOver())
			return;
		(currentElement == null ? elementList : currentElement.getControlElement().getSubSettings().getElements()).forEach(settingsElement -> {
			if (settingsElement instanceof ControlElement) {
				ControlElement controlElement = (ControlElement) settingsElement;
				controlElement.mouseClicked(mouseX, mouseY, mouseButton);
				if (controlElement.getSubSettings().getElements() != null && controlElement.getButtonAdvanced().isMouseOver())
					currentElement = settingsElements.get(controlElement);

			}
		});
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
	}

	public void loadSettings(List<SettingsElement> settingsElements) {
		for (SettingsElement settingsElement : settingsElements)
			if (settingsElement instanceof ControlElement)
				new SettingElement(this.settingsElements, settingsElement);
	}

	@Getter
	private static class SettingElement {

		private List<SettingElement> subSettings = new ArrayList<>();
		private SettingsElement settingsElement;
		private ControlElement controlElement;
		private SettingElement parent;

		private SettingElement(HashMap<ControlElement, SettingElement> settingElements, SettingsElement settingsElement) {
			this.settingsElement = settingsElement;
			if (settingsElement instanceof ControlElement) {
				controlElement = (ControlElement) settingsElement;
				settingElements.put(controlElement, this);
				controlElement.getSubSettings().getElements().forEach(settingsElements -> {
					SettingElement settingElement = new SettingElement(settingElements, settingsElements);
					settingElement.parent = this;
					if (settingsElements instanceof ControlElement)
						settingElements.put((ControlElement) settingsElements, settingElement);
					subSettings.add(settingElement);
				});
			}
		}
	}
}