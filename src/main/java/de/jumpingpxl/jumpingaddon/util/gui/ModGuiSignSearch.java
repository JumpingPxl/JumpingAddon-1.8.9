package de.jumpingpxl.jumpingaddon.util.gui;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.mods.SignSearchModule;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.CheckBox;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 21.06.2019
 */

public class ModGuiSignSearch extends GuiScreen {
	
	private SignSearchModule signSearchModule;
	private GuiScreen lastScreen;
	private SignSearchModule.ModTextField fieldSearch;
	private SignSearchModule.ModTextField fieldBlacklist;
	private SignSearchModule.ModTextField fieldPartySize;
	private CheckBox checkBoxEnabled;
	private CheckBox checkBoxFilterFullServer;
	private CheckBox checkBoxNightmode;
	private CheckBox checkBoxAutoJoin;

	public ModGuiSignSearch(GuiScreen lastScreen) {
		this.lastScreen = lastScreen;
		signSearchModule = JumpingAddon.getInstance().getModuleHandler().getSignSearchModule();
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 110, "Done"));
		this.fieldSearch = new SignSearchModule.ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), this.width / 2 - 100, this.height / 4 + 20, 200, 20, false, -1);
		this.fieldSearch.setBlackBox(false);
		this.fieldSearch.setText(signSearchModule.getSignSearchString().getAsString());
		this.fieldSearch.setPlaceHolder(LanguageManager.translate("search_on_signs") + "..");
		this.fieldSearch.setFocused(false);
		this.fieldSearch.setDescription("Search for specific maps and gamemodes\nAffected signs are marked green ; not matching signs red\n \nSeparate multiple entries with \",\"");
		this.fieldPartySize = new SignSearchModule.ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), this.width / 2 + 10, this.height / 4 + 80, 90, 20, true, 2);
		this.fieldPartySize.setBlackBox(false);
		this.fieldPartySize.setText(signSearchModule.getSignSearchPartySize().getAsString());
		this.fieldPartySize.setPlaceHolder("Party size..");
		this.fieldPartySize.setDescription("Only search for servers which has " + (fieldPartySize.getText().isEmpty() || fieldPartySize.getText().equals("0") ? "x" : fieldPartySize.getText()) + " free slots");
		this.fieldBlacklist = new SignSearchModule.ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), this.width / 2 - 100, this.height / 4 + 50, 200, 20, false, -1);
		this.fieldBlacklist.setBlackBox(false);
		this.fieldBlacklist.setText(signSearchModule.getSignSearchBlacklist().getAsString());
		this.fieldBlacklist.setPlaceHolder(LanguageManager.translate("blacklist") + "..");
		this.fieldBlacklist.setDescription("Blacklist specific maps and gamemodes\nAffected signs are marked red\n \nSeparate multiple entries with \",\"");
		this.checkBoxEnabled = new CheckBox("Enabled", signSearchModule.getSignSearchEnabled().getAsBoolean() ? CheckBox.EnumCheckBoxValue.ENABLED : CheckBox.EnumCheckBoxValue.DISABLED, null, this.width / 2 + 100 + 5, this.height / 4 + 20, 20, 20);
		this.checkBoxEnabled.setUpdateListener(accepted -> {
			signSearchModule.getSignSearchEnabled().setValue(accepted == CheckBox.EnumCheckBoxValue.ENABLED);
			signSearchModule.getSignSearchEnabled().getConfiguration().save();
			initGui();
		});
		this.checkBoxFilterFullServer = new CheckBox(LanguageManager.translate("filter_full_servers"), signSearchModule.getSignSearchFullServer().getAsBoolean() ? CheckBox.EnumCheckBoxValue.ENABLED : CheckBox.EnumCheckBoxValue.DISABLED, null, this.width / 2 - 100 + 5, this.height / 4 + 80, 20, 20);
		this.checkBoxFilterFullServer.setUpdateListener(accepted -> {
			signSearchModule.getSignSearchFullServer().setValue(accepted == CheckBox.EnumCheckBoxValue.ENABLED);
			signSearchModule.getSignSearchFullServer().getConfiguration().save();
			initGui();
		});
		this.checkBoxFilterFullServer.setDescription("If enabled the signs of full servers are marked orange");
		this.checkBoxNightmode = new CheckBox(LanguageManager.translate("filter_empty_servers"), signSearchModule.getSignSearchEmptyServer().getAsBoolean() ? CheckBox.EnumCheckBoxValue.ENABLED : CheckBox.EnumCheckBoxValue.DISABLED, null, this.width / 2 - 100 + 35 + 5, this.height / 4 + 80, 20, 20);
		this.checkBoxNightmode.setUpdateListener(accepted -> {
			signSearchModule.getSignSearchEmptyServer().setValue(accepted == CheckBox.EnumCheckBoxValue.ENABLED);
			signSearchModule.getSignSearchEmptyServer().getConfiguration().save();
			initGui();
		});
		this.checkBoxNightmode.setDescription("If enabled, the signs of empty servers are excluded from the search");
		this.checkBoxAutoJoin = new CheckBox("Auto Join", signSearchModule.getSignSearchAutoJoin().getAsBoolean() ? CheckBox.EnumCheckBoxValue.ENABLED : CheckBox.EnumCheckBoxValue.DISABLED, null, this.width / 2 - 100 + 70 + 5, this.height / 4 + 80, 20, 20);
		this.checkBoxAutoJoin.setUpdateListener(accepted -> {
			signSearchModule.getSignSearchAutoJoin().setValue(accepted == CheckBox.EnumCheckBoxValue.ENABLED);
			signSearchModule.getSignSearchAutoJoin().getConfiguration().save();
			initGui();
		});
		this.checkBoxAutoJoin.setDescription("Automatic connects to an server based on the search results");
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		DrawUtils draw = LabyMod.getInstance().getDrawUtils();
		this.fieldSearch.drawTextBox();
		this.fieldBlacklist.drawTextBox();
		this.fieldPartySize.drawTextBox();
		this.fieldSearch.renderDescription(mouseX, mouseY);
		this.fieldBlacklist.renderDescription(mouseX, mouseY);
		this.fieldPartySize.renderDescription(mouseX, mouseY);
		this.checkBoxEnabled.drawCheckbox(mouseX, mouseY);
		this.checkBoxFilterFullServer.drawCheckbox(mouseX, mouseY);
		this.checkBoxNightmode.drawCheckbox(mouseX, mouseY);
		this.checkBoxAutoJoin.drawCheckbox(mouseX, mouseY);
		draw.drawCenteredString(LanguageManager.translate("title_sign_search"), (double) (this.width / 2), (double) (this.height / 4));
		Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_SIGNSEARCH);
		LabyMod.getInstance().getDrawUtils().drawTexture((double) (this.fieldSearch.xPosition - 22), (double) this.fieldSearch.yPosition, 255.0D, 255.0D, 20.0D, 20.0D);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button.id == 0)
			Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		if (!this.fieldSearch.isFocused() && (!this.fieldBlacklist.isFocused() && !this.fieldPartySize.isFocused()))
			this.fieldSearch.setFocused(true);
		if (this.fieldSearch.textboxKeyTyped(typedChar, keyCode)) {
			signSearchModule.getSignSearchString().setValue(this.fieldSearch.getText());
			signSearchModule.getSignSearchString().getConfiguration().save();
		}
		if (this.fieldPartySize.textboxKeyTyped(typedChar, keyCode)) {
			signSearchModule.getSignSearchPartySize().setValue(this.fieldPartySize.getText());
			signSearchModule.getSignSearchPartySize().getConfiguration().save();
		}
		if (this.fieldBlacklist.textboxKeyTyped(typedChar, keyCode)) {
			signSearchModule.getSignSearchBlacklist().setValue(this.fieldBlacklist.getText());
			signSearchModule.getSignSearchBlacklist().getConfiguration().save();
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.fieldSearch.mouseClicked(mouseX, mouseY, mouseButton);
		this.fieldBlacklist.mouseClicked(mouseX, mouseY, mouseButton);
		this.fieldPartySize.mouseClicked(mouseX, mouseY, mouseButton);
		this.checkBoxEnabled.mouseClicked(mouseX, mouseY, mouseButton);
		this.checkBoxFilterFullServer.mouseClicked(mouseX, mouseY, mouseButton);
		this.checkBoxNightmode.mouseClicked(mouseX, mouseY, mouseButton);
		this.checkBoxAutoJoin.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.fieldSearch.updateCursorCounter();
		this.fieldBlacklist.updateCursorCounter();
		this.fieldPartySize.updateCursorCounter();
	}
}
