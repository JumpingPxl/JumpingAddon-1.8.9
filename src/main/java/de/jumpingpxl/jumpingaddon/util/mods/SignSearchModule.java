package de.jumpingpxl.jumpingaddon.util.mods;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.Configuration;
import de.jumpingpxl.jumpingaddon.util.SettingValue;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;
import lombok.Getter;
import lombok.Setter;
import net.labymod.core.BlockPosition;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 12.06.2019
 */

@Getter
public class SignSearchModule {

	private JumpingAddon jumpingAddon;
	private SettingValue signSearchString;
	private SettingValue signSearchEnabled;
	private SettingValue signSearchBlacklist;
	private SettingValue signSearchFullServer;
	private SettingValue signSearchEmptyServer;
	private SettingValue signSearchPartySize;
	private SettingValue signSearchAutoJoin;
	private Map<BlockPosition, SignData> signDataMap = new HashMap<>();

	public SignSearchModule(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
		loadSettings(jumpingAddon.getConnection().getGommeHD().getConfig());
	}

	public void loadSettings(Configuration configuration) {
		signSearchString = new SettingValue(configuration, "signSearchString", "");
		signSearchEnabled = new SettingValue(configuration, "signSearchEnabled", false);
		signSearchBlacklist = new SettingValue(configuration, "signSearchBlacklist", "");
		signSearchFullServer = new SettingValue(configuration, "signSearchFullServer", true);
		signSearchEmptyServer = new SettingValue(configuration, "signSearchEmptyServer", false);
		signSearchPartySize = new SettingValue(configuration, "signSearchPartySize", "0");
		signSearchAutoJoin = new SettingValue(configuration, "signSearchAutoJoin", false);
	}

	public void renderTileEntitySign(TileEntitySign tileEntitySign) {
		if (LabyMod.getSettings().signSearch && signSearchEnabled.getAsBoolean()) {
			if (jumpingAddon.getConnection().isOnServer(Server.GOMMEHD_NET) && jumpingAddon.getConnection().getGameType().getPrefix() == null) {
				BlockPosition blockPosition = LabyModCore.getMinecraft().getPosition(tileEntitySign.getPos());
				SignData signData = signDataMap.get(blockPosition);
				if (signData == null || signData.lastSignUpdated + 500L < System.currentTimeMillis())
					signDataMap.put(blockPosition, signData = new SignData(this, tileEntitySign));
				signData.getSignColor().applyColor();
			}
		} else {
			if (!signDataMap.isEmpty())
				signDataMap.clear();
		}
	}

	@Setter
	@Getter
	public static class SignData {
		private SignSearchModule signSearchModule;
		private TileEntitySign tileEntitySign;
		private SignColor signColor;
		private long lastSignUpdated;
		Integer currentUserCount;
		Integer maxUserCount;

		public SignData(SignSearchModule signSearchModule, TileEntitySign sign) {
			this.signSearchModule = signSearchModule;
			this.signColor = SignColor.NONE;
			this.tileEntitySign = sign;
			this.lastSignUpdated = System.currentTimeMillis();
			this.parseSignData();
		}

		private void parseSignData() {
			StringBuilder fullString = new StringBuilder();
			String[] lines = new String[4];
			int lineCount = -1;
			IChatComponent[] signText = this.tileEntitySign.signText;
			for (Object chatComponentObj : signText) {
				++lineCount;
				if (chatComponentObj != null) {
					String line = LabyModCore.getMinecraft().getChatComponent(chatComponentObj).getUnformattedText();
					if (line != null) {
						fullString.append(line.toLowerCase());
						lines[lineCount] = line;
					}
				}
			}
			if (signText[3].getUnformattedText().contains("/")) {
				currentUserCount = getUserCount(signText[3].getUnformattedText(), true);
				maxUserCount = getUserCount(signText[3].getUnformattedText(), false);
				boolean searchFound = false;
				if (!signSearchModule.signSearchString.getAsString().isEmpty())
					for (String search : signSearchModule.signSearchString.getAsString().replace(" ", "").split(","))
						if (fullString.toString().replace(" ", "").contains(search.toLowerCase())) {
							searchFound = true;
							break;
						}
				if (!searchFound && !signSearchModule.signSearchString.getAsString().isEmpty()) {
					this.signColor = SignColor.RED;
					return;
				}
				boolean blacklistFound = false;
				if (!signSearchModule.signSearchBlacklist.getAsString().isEmpty())
					for (String blacklist : signSearchModule.signSearchBlacklist.getAsString().replace(" ", "").split(","))
						if (fullString.toString().replace(" ", "").contains(blacklist.toLowerCase())) {
							blacklistFound = true;
							break;
						}
				if (blacklistFound) {
					this.signColor = SignColor.RED;
					return;
				}
				if (!signSearchModule.signSearchEmptyServer.getAsBoolean() && !signSearchModule.signSearchFullServer.getAsBoolean()) {
					this.signColor = SignColor.GREEN;
					return;
				}
				if(!signSearchModule.signSearchPartySize.getAsString().isEmpty() && !signSearchModule.signSearchPartySize.getAsString().equals("0")){
					int partySize = Integer.parseInt(signSearchModule.signSearchPartySize.getAsString());
					boolean isParty = (maxUserCount != null && currentUserCount != null) && (maxUserCount - currentUserCount) >= partySize;
					if(isParty){
						this.signColor = SignColor.PURPLE;
						return;
					}
				}
				boolean isEmpty = (currentUserCount != null && currentUserCount == 0) && signSearchModule.signSearchEmptyServer.getAsBoolean();
				boolean isFull = (maxUserCount != null && currentUserCount != null) && (currentUserCount >= maxUserCount && signSearchModule.signSearchFullServer.getAsBoolean());
				if(isEmpty){
					this.signColor = SignColor.GRAY;
				} else if (isFull)
					this.signColor = SignColor.ORANGE;
				else
					this.signColor = SignColor.GREEN;
			}
		}

		private Integer getUserCount(String line, boolean pre) {
			if (line != null && line.contains("/")) {
				String[] parts = line.split("/");
				if (parts.length > (pre ? 0 : 1)) {
					String result = parts[pre ? 0 : 1].replaceAll(" ", "");
					return result.matches("^-?\\d+$") ? Integer.parseInt(result) : null;
				}
			}
			return null;
		}
	}

	@Getter
	public enum SignColor {

		NONE(1.0F, 1.0F, 1.0F, 1.0F),
		GREEN(0.6F, 23.6F, 0.6F, 0.6F),
		RED(23.6F, 0.6F, 0.6F, 0.6F),
		ORANGE(10.0F, 10.0F, 0.6F, 0.6F),
		PURPLE(200, 0, 200, 0.6F),
		GRAY(0.6F, 0.6F, 0.6F, 0.6F);

		private float red;
		private float green;
		private float blue;
		private float alpha;

		public void applyColor() {
			GlStateManager.color(this.red, this.green, this.blue, this.alpha);
		}

		private SignColor(float red, float green, float blue, float alpha) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}
	}

	public static class ModTextField extends net.labymod.gui.elements.ModTextField {

		@Setter
		private BiConsumer<String, String> updateListener;
		@Setter
		private String description;
		private boolean numbersOnly;
		private int maxlength;

		public ModTextField(int componentId, FontRenderer fontrenderer, int x, int y, int par5Width, int par6Height, boolean numbersOnly, int maxlength) {
			super(componentId, fontrenderer, x, y, par5Width, par6Height);
			this.numbersOnly = numbersOnly;
			this.maxlength = maxlength;
		}

		@Override
		public boolean textboxKeyTyped(char typedChar, int typedKey) {
			boolean isFocused = false;
			boolean isEnabled = true;
			String text = "";
			try {
				isFocused = (Boolean) ReflectionHelper.findField(net.labymod.gui.elements.ModTextField.class, "isFocused").get(this);
				isEnabled = (Boolean) ReflectionHelper.findField(net.labymod.gui.elements.ModTextField.class, "isEnabled").get(this);
				text = (String) ReflectionHelper.findField(net.labymod.gui.elements.ModTextField.class, "text").get(this);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if (!isFocused) {
				return false;
			} else if (GuiScreen.isKeyComboCtrlA(typedKey)) {
				this.setCursorPositionEnd();
				this.setSelectionPos(0);
				return true;
			} else if (GuiScreen.isKeyComboCtrlC(typedKey)) {
				if (!this.isPasswordBox())
					GuiScreen.setClipboardString(this.getSelectedText());
				return true;
			} else if (GuiScreen.isKeyComboCtrlV(typedKey)) {
				if (isEnabled)
					this.writeText(GuiScreen.getClipboardString());
				return true;
			} else if (GuiScreen.isKeyComboCtrlX(typedKey)) {
				if (!this.isPasswordBox())
					GuiScreen.setClipboardString(this.getSelectedText());
				if (isEnabled)
					this.writeText("");
				return true;
			} else {
				switch (typedKey) {
					case 14:
						if (GuiScreen.isCtrlKeyDown()) {
							if (isEnabled)
								this.deleteWords(-1);
						} else if (isEnabled)
							this.deleteFromCursor(-1);
						return true;
					case 199:
						if (GuiScreen.isShiftKeyDown())
							this.setSelectionPos(0);
						else
							this.setCursorPositionZero();
						return true;
					case 203:
						if (GuiScreen.isShiftKeyDown())
							if (GuiScreen.isCtrlKeyDown())
								this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
							else
								this.setSelectionPos(this.getSelectionEnd() - 1);
						else if (GuiScreen.isCtrlKeyDown())
							this.setCursorPosition(this.getNthWordFromCursor(-1));
						else
							this.moveCursorBy(-1);
						return true;
					case 205:
						if (GuiScreen.isShiftKeyDown())
							if (GuiScreen.isCtrlKeyDown())
								this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
							else
								this.setSelectionPos(this.getSelectionEnd() + 1);
						else if (GuiScreen.isCtrlKeyDown())
							this.setCursorPosition(this.getNthWordFromCursor(1));
						else
							this.moveCursorBy(1);
						return true;
					case 207:
						if (GuiScreen.isShiftKeyDown())
							this.setSelectionPos(text.length());
						else
							this.setCursorPositionEnd();
						return true;
					case 211:
						if (GuiScreen.isCtrlKeyDown()) {
							if (isEnabled)
								this.deleteWords(1);
						} else if (isEnabled)
							this.deleteFromCursor(1);
						return true;
					default:
						if (maxlength == -1 || text.length() < maxlength)
							if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && isCharAllowed(typedChar)) {
								if (isEnabled) {
									System.out.println("KEY " + typedChar);
									this.writeText(Character.toString(typedChar));
									if (updateListener != null)
										updateListener.accept(Character.toString(typedChar), text);
								}
								return true;
							} else {
								return false;
							}
				}
			}
			return true;
		}

		public void renderDescription(int mouseX, int mouseY) {
			if (this.description != null && this.isMouseOver(mouseX, mouseY))
				TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 200L, this.description);
		}

		public boolean isMouseOver(int mouseX, int mouseY) {
			return mouseX > this.xPosition && mouseX < this.xPosition + this.width && mouseY > this.yPosition && mouseY < this.yPosition + this.height;
		}

		private boolean isCharAllowed(char typedChar) {
			if (!numbersOnly)
				return true;
			boolean allowed = false;
			for (char chars : new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'})
				if (typedChar == chars) {
					allowed = true;
					break;
				}
			return allowed;
		}
	}
}
