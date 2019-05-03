package de.jumpingpxl.jumpingaddon.util.elements;

import de.jumpingpxl.jumpingaddon.util.SettingValue;
import de.jumpingpxl.jumpingaddon.util.Settings;
import lombok.Getter;
import lombok.Setter;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.ColorPicker;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.settings.LabyModModuleEditorGui;
import net.labymod.settings.PreviewRenderer;
import net.labymod.settings.elements.ColorPickerCheckBoxBulkElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.io.IOException;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 11.03.2019
 */

public class BetterColorPickerElement implements BetterElement {

	private ColorPickerCheckBoxBulkElement colorPickerCheckBoxBulkElement;
	private ColorPickerElement colorPickerElement;

	public BetterColorPickerElement(String displayName, Color currentValue, Color defaultValue, Consumer<Color> updateValue) {
		colorPickerCheckBoxBulkElement = new ColorPickerCheckBoxBulkElement(displayName);
		ColorPicker colorPicker = new ColorPicker(displayName, currentValue, () -> defaultValue == null ? Color.WHITE : defaultValue, 0, 0, 0, 0);
		if (defaultValue != null)
			colorPicker.setHasDefault(true);
		colorPicker.setHasAdvanced(true);
		colorPicker.setUpdateListener(updateValue);
		colorPickerCheckBoxBulkElement.addColorPicker(colorPicker);
	}

	public BetterColorPickerElement(String displayName, Color currentValue, Consumer<Color> updateValue) {
		this(displayName, currentValue, null, updateValue);
	}

	public BetterColorPickerElement(String displayName, SettingValue settingValue) {
		this(displayName, new Color(settingValue.getAsInteger()), color -> {
			settingValue.setValue(color.getRGB());
			settingValue.getConfiguration().set(settingValue.getConfigPath(), color.getRGB());
			settingValue.getConfiguration().save();
		});
	}

	public BetterColorPickerElement(String displayName, ControlElement.IconData iconData, Color currentValue, Consumer<Color> updateValue) {
		colorPickerElement = new ColorPickerElement(Settings.SettingsCategory.getLastCategory().getColor() + displayName, iconData, currentValue, updateValue);
	}

	public BetterColorPickerElement(String displayName, ControlElement.IconData iconData, SettingValue settingValue) {
		this(displayName, iconData, new Color(settingValue.getAsInteger()), color -> {
			settingValue.setValue(color.getRGB());
			settingValue.getConfiguration().set(settingValue.getConfigPath(), color.getRGB());
			settingValue.getConfiguration().save();
		});
	}

	@Override
	public SettingsElement getElement() {
		return colorPickerCheckBoxBulkElement == null ? colorPickerElement : colorPickerCheckBoxBulkElement;
	}

	private static class ColorPickerElement extends ControlElement {

		private ColorPicker colorPicker;
		private Color currentColor;

		public ColorPickerElement(String displayName, IconData iconData, Color currentValue, Consumer<Color> updateValue) {
			super(displayName, null, iconData);
			currentColor = currentValue;
			ColorPicker colorPicker = new ColorPicker("", currentValue, () -> Color.WHITE, 0, 0, 0, 0);
			colorPicker.setUpdateListener(color -> {
				if (color.getRGB() != currentColor.getRGB()) {
					currentColor = color;
					updateValue.accept(color);
				}
			});
			this.colorPicker = colorPicker;
		}

		public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
			super.draw(x, y, maxX, maxY, mouseX, mouseY);
			int width = this.getObjectWidth();
			colorPicker.setX(maxX - width - 2);
			colorPicker.setY(y + 1);
			colorPicker.setWidth(width);
			colorPicker.setHeight(20);
			colorPicker.drawColorPicker(mouseX, mouseY);
			int buttonWidth = colorPicker.getWidth();
			this.mc.getTextureManager().bindTexture(buttonTextures);
		}

		public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			super.mouseClicked(mouseX, mouseY, mouseButton);
			if (colorPicker.isMouseOver(mouseX, mouseY))
				Minecraft.getMinecraft().displayGuiScreen(new ColorPicker.AdvancedColorSelectorGui(colorPicker, Minecraft.getMinecraft().currentScreen, lastScreen -> Minecraft.getMinecraft().displayGuiScreen(lastScreen)));
		}

		@Getter
		@Setter
		static class ColorPicker extends Gui {
			private static final int[] ADVANCED_COLORS = new int[]{-4842468, -7795121, -11922292, -13624430, -15064194, -15841375, -16754788, -16687004, -16757697, -14918112, -13407970, -8292586, -753898, -37120, -1683200, -4246004, -12704222, -14606047, -14208456};
			private String title;
			private int x;
			private int y;
			private int width;
			private int height;
			private Color selectedColor;
			private Color colorForPreview;
			private boolean openedSelector;
			private boolean hoverSlider = false;
			private boolean hasAdvanced = false;
			private boolean hoverAdvancedButton = false;
			private boolean hoverDefaultButton;
			private boolean hasDefault = false;
			private boolean isDefault = true;
			private ColorPicker.DefaultColorCallback defaultColor;
			private Consumer<Color> updateListener;

			ColorPicker(String title, Color selectedColor, ColorPicker.DefaultColorCallback defaultColorCallback, int x, int y, int width, int height) {
				this.title = title;
				this.x = x;
				this.y = y;
				this.width = width;
				this.height = height;
				this.selectedColor = selectedColor;
				this.colorForPreview = selectedColor;
				this.defaultColor = defaultColorCallback;
			}

			public void onGuiClosed() {
				if (this.colorForPreview != this.selectedColor && this.updateListener != null)
					this.updateListener.accept(this.selectedColor);
			}

			void drawColorPicker(int mouseX, int mouseY) {
				LabyMod.getInstance().getDrawUtils().drawCenteredString(this.title, (double) (this.x + this.width / 2), (double) (this.y - 5), 0.5D);
				drawRect(this.x, this.y, this.x + this.width, this.y + this.height, this.openedSelector ? -1 : 2147483647);
				int bgColor = this.colorForPreview == null ? (this.defaultColor == null ? (this.openedSelector ? -2147483648 : 2147483647) : this.defaultColor.getDefaultColor().getRGB()) : this.colorForPreview.getRGB();
				drawRect(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, bgColor);
				if (this.hasDefault && this.selectedColor == null && this.isDefault) {
					Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_HOVER_DEFAULT);
					Color color = new Color(bgColor);
					int luma = (int) (0.2126F * (float) color.getRed() + 0.7152F * (float) color.getGreen() + 0.0722F * (float) color.getBlue());
					luma = 255 - luma;
					if (luma < 80) {
						GlStateManager.color((float) luma / 255.0F, (float) luma / 255.0F, (float) luma / 255.0F, 1.0F);
						LabyMod.getInstance().getDrawUtils().drawTexture((double) (this.x + 2), (double) (this.y + 2), 256.0D, 256.0D, (double) (this.width - 4), (double) (this.height - 4), 1.1F);
					} else
						LabyMod.getInstance().getDrawUtils().drawTexture((double) (this.x + 2), (double) (this.y + 2), 256.0D, 256.0D, (double) (this.width - 4), (double) (this.height - 4));
					if (this.isMouseOver(mouseX, mouseY))
						TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 200L, "Default");
				}
				if (this.openedSelector)
					this.drawColorsAndButtons(mouseX, mouseY);
			}

			private Color getContrastColor(int r, int g, int b) {
				double y = (double) ((299 * r + 587 * g + 114 * b) / 1000);
				return y >= 128.0D ? Color.black : Color.white;
			}

			private void drawColorsAndButtons(int mouseX, int mouseY) {
				int widthPerColor = 13;
				int sliderHeight = 13;
				int sliderWidth = 0;
				ModColor[] var6 = ModColor.values();
				int sliderY = var6.length;
				int minX;
				for (minX = 0; minX < sliderY; ++minX)
					if (var6[minX].getColor() != null)
						sliderWidth += widthPerColor;
				int sliderX = this.x - sliderWidth / 2 + this.width / 2;
				sliderY = this.y + this.height + 4;
				if (this.hasAdvanced)
					sliderX -= 20;
				minX = this.hasDefault && this.selectedColor != null ? 20 : 5;
				int maxX = LabyMod.getInstance().getDrawUtils().getWidth() - 5;
				int maxY = LabyMod.getInstance().getDrawUtils().getHeight() - 5;
				if (sliderX + sliderWidth > maxX)
					sliderX -= sliderX + sliderWidth - maxX;
				if (sliderX < minX)
					sliderX = minX;
				if (sliderY > maxY)
					sliderY = maxY - sliderHeight - this.height;
				else {
					drawRect(this.x + this.width / 2 - 1, sliderY - 3, this.x + this.width / 2 + 1, sliderY - 2, 2147483647);
					drawRect(this.x + this.width / 2 - 2, sliderY - 2, this.x + this.width / 2 + 2, sliderY - 1, 2147483647);
				}
				if (!(Minecraft.getMinecraft().currentScreen instanceof ColorPicker.AdvancedColorSelectorGui)) {
					this.drawSlider(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight, widthPerColor);
					this.drawButtons(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight, widthPerColor);
				}
				this.hoverAdvancedButton = mouseX > sliderX + sliderWidth + 3 - 1 && mouseX < sliderX + sliderWidth + 3 + widthPerColor + 1 && mouseY > sliderY - 1 && mouseY < sliderY + sliderHeight + 1;
				this.hoverSlider = mouseX > sliderX && mouseX < sliderX + sliderWidth + widthPerColor && mouseY > sliderY && mouseY < sliderY + sliderHeight;
				this.hoverDefaultButton = mouseX > sliderX - 3 - widthPerColor - 1 && mouseX < sliderX - 3 + 1 && mouseY > sliderY - 1 && mouseY < sliderY + sliderHeight + 1;
			}

			private void drawSlider(int mouseX, int mouseY, int sliderX, int sliderY, int sliderWidth, int sliderHeight, int widthPerColor) {
				drawRect(sliderX - 1, sliderY - 1, sliderX + sliderWidth + 1, sliderY + sliderHeight + 1, 2147483647);
				int pos = 0;
				int hoverPos = -1;
				int selectedPos = -1;
				ModColor hoverColorType = null;
				ModColor selectedColorType = null;
				ModColor[] var13 = ModColor.values();
				int var14 = var13.length;
				for (ModColor color : var13)
					if (color.getColor() != null) {
						drawRect(sliderX + pos, sliderY, sliderX + pos + widthPerColor, sliderY + sliderHeight, color.getColor().getRGB());
						boolean hoverColor = mouseX > sliderX + pos && mouseX < sliderX + pos + widthPerColor + 1 && mouseY > sliderY && mouseY < sliderY + sliderHeight;
						if (hoverPos == -1 && hoverColorType == null && hoverColor) {
							hoverPos = pos;
							hoverColorType = color;
						}
						if (color.getColor() == this.selectedColor) {
							selectedPos = pos;
							selectedColorType = color;
						}
						pos += widthPerColor;
					}
				if (hoverColorType != null) {
					drawRect(sliderX + hoverPos - 1, sliderY - 1, sliderX + hoverPos + widthPerColor + 1, sliderY + sliderHeight + 1, hoverColorType.getColor().getRGB());
					this.colorForPreview = hoverColorType.getColor();
					if (this.updateListener != null)
						this.updateListener.accept(this.colorForPreview);
				} else {
					this.colorForPreview = this.selectedColor;
					if (this.updateListener != null)
						this.updateListener.accept(this.selectedColor);
				}
				if (selectedColorType != null) {
					drawRect(sliderX + selectedPos - 1, sliderY - 1, sliderX + selectedPos + widthPerColor + 1, sliderY + sliderHeight + 1, -1);
					drawRect(sliderX + selectedPos, sliderY, sliderX + selectedPos + widthPerColor, sliderY + sliderHeight, selectedColorType.getColor().getRGB());
				}
			}

			private void drawAdvanced(int mouseX, int mouseY, int advancedX, int advancedY, int basicX, int basicY) {
				int alphaCount = 10;
				double widthPerColor = (double) 200 / (double) ADVANCED_COLORS.length;
				drawRect(advancedX - 1, advancedY - 1, (int) ((double) advancedX + widthPerColor * (double) ADVANCED_COLORS.length + 1.0D), (int) ((double) advancedY + widthPerColor * (double) alphaCount + 1.0D), 2147483647);
				drawRect(basicX - 1, basicY - 1, (int) ((double) basicX + widthPerColor * 16.0D + 1.0D), (int) ((double) basicY + widthPerColor + 1.0D), 2147483647);
				boolean basic = false;
				boolean basicSelected = false;
				double hoverPosX = -1.0D;
				double hoverPosY = -1.0D;
				Color hoveredColorType = null;
				double selectedPosX = -1.0D;
				double selectedPosY = -1.0D;
				Color selectedColorType = null;
				double posX = 0.0D;
				int[] var23 = ADVANCED_COLORS;
				int var24 = var23.length;
				for (int color : var23) {
					for (int posY = 0; posY < alphaCount; ++posY) {
						int rgb = ModColor.changeBrightness(new Color(color), 0.07F * (float) posY).getRGB();
						drawRect((int) ((double) advancedX + posX), (int) ((double) advancedY + (double) posY * widthPerColor), (int) ((double) advancedX + posX + widthPerColor), (int) ((double) advancedY + (double) posY * widthPerColor + widthPerColor), rgb);
						boolean hoverColor = (double) mouseX > (double) advancedX + posX && (double) mouseX < (double) advancedX + posX + widthPerColor + 1.0D && (double) mouseY > (double) advancedY + (double) posY * widthPerColor && (double) mouseY < (double) advancedY + (double) posY * widthPerColor + widthPerColor + 1.0D;
						if (hoverColor) {
							hoverPosX = posX;
							hoverPosY = (double) posY;
							hoveredColorType = new Color(rgb);
						}
						if (this.selectedColor != null && rgb == this.selectedColor.getRGB()) {
							selectedPosX = posX;
							selectedPosY = (double) posY;
							selectedColorType = this.selectedColor;
						}
					}
					posX += widthPerColor;
				}
				posX = 0.0D;
				for (ModColor color : ModColor.values()) {
					if (color.getColor() != null) {
						drawRect((int) ((double) basicX + posX), (int) ((double) basicY), (int) ((double) basicX + posX + widthPerColor), (int) ((double) basicY + widthPerColor), color.getColor().getRGB());
						boolean hoverColor = (double) mouseX > (double) basicX + posX && (double) mouseX < (double) basicX + posX + widthPerColor + 1.0D && (double) mouseY > (double) basicY && (double) mouseY < (double) basicY + widthPerColor + 1.0D;
						if (hoverColor) {
							hoverPosX = posX;
							hoverPosY = 0;
							hoveredColorType = color.getColor();
							basic = true;
						}
						if (selectedColor != null && color.getColor().getRGB() == selectedColor.getRGB()) {
							selectedPosX = posX;
							selectedPosY = 0;
							selectedColorType = selectedColor;
							basicSelected = true;
						}
						posX += widthPerColor;
					}
				}
				if (hoveredColorType != null) {
					if (!basic)
						drawRect((int) ((double) advancedX + hoverPosX - 1.0D), (int) ((double) advancedY + hoverPosY * widthPerColor - 1.0D), (int) ((double) advancedX + hoverPosX + widthPerColor + 1.0D), (int) ((double) advancedY + hoverPosY * widthPerColor + widthPerColor + 1.0D), hoveredColorType.getRGB());
					else
						drawRect((int) ((double) basicX + hoverPosX - 1.0D), (int) ((double) basicY + hoverPosY * widthPerColor - 1.0D), (int) ((double) basicX + hoverPosX + widthPerColor + 1.0D), (int) ((double) basicY + hoverPosY * widthPerColor + widthPerColor + 1.0D), hoveredColorType.getRGB());
					this.colorForPreview = hoveredColorType;
					if (this.updateListener != null)
						this.updateListener.accept(this.colorForPreview);
				} else {
					this.colorForPreview = this.selectedColor;
					if (this.updateListener != null)
						this.updateListener.accept(this.selectedColor);
				}
				if (selectedColorType != null) {
					if (!basicSelected) {
						drawRect((int) ((double) advancedX + selectedPosX - 1.0D), (int) ((double) advancedY + selectedPosY * widthPerColor - 1.0D), (int) ((double) advancedX + selectedPosX + widthPerColor + 1.0D), (int) ((double) advancedY + selectedPosY * widthPerColor + widthPerColor + 1.0D), -1);
						drawRect((int) ((double) advancedX + selectedPosX), (int) ((double) advancedY + selectedPosY * widthPerColor), (int) ((double) advancedX + selectedPosX + widthPerColor), (int) ((double) advancedY + selectedPosY * widthPerColor + widthPerColor), selectedColorType.getRGB());
					} else {
						drawRect((int) ((double) basicX + selectedPosX - 1.0D), (int) ((double) basicY + selectedPosY * widthPerColor - 1.0D), (int) ((double) basicX + selectedPosX + widthPerColor + 1.0D), (int) ((double) basicY + selectedPosY * widthPerColor + widthPerColor + 1.0D), -1);
						drawRect((int) ((double) basicX + selectedPosX), (int) ((double) basicY + selectedPosY * widthPerColor), (int) ((double) basicX + selectedPosX + widthPerColor), (int) ((double) basicY + selectedPosY * widthPerColor + widthPerColor), selectedColorType.getRGB());
					}
				}
			}

			private void drawButtons(int mouseX, int mouseY, int sliderX, int sliderY, int sliderWidth, int sliderHeight, int widthPerColor) {
				if (this.hasDefault && this.selectedColor != null) {
					drawRect(sliderX - 3 - widthPerColor - 1, sliderY - 1, sliderX - 3 + 1, sliderY + sliderHeight + 1, this.hoverDefaultButton ? -1 : 2147483647);
					drawRect(sliderX - 3 - widthPerColor, sliderY, sliderX - 3, sliderY + sliderHeight, -2147483648);
					LabyMod.getInstance().getDrawUtils().fontRenderer.drawString("D", (float) (sliderX - 3 - widthPerColor / 2 - 3), (float) (sliderY + sliderHeight / 2 - 3), -1, false);
					if (this.hoverDefaultButton) {
						this.updateListener.accept(null);
						this.colorForPreview = this.defaultColor.getDefaultColor();
					}
				}

				if (this.hasAdvanced) {
					drawRect(sliderX + sliderWidth + 3 - 1, sliderY - 1, sliderX + sliderWidth + 3 + widthPerColor + 1, sliderY + sliderHeight + 1, this.hoverAdvancedButton ? -1 : 2147483647);
					drawRect(sliderX + sliderWidth + 3, sliderY, sliderX + sliderWidth + 3 + widthPerColor, sliderY + sliderHeight, -1);
					int iconX = sliderX + sliderWidth + 3;
					double pxlX = (double) iconX;
					int[] var12 = ADVANCED_COLORS;
					int var13 = var12.length;
					for (int color : var12) {
						int pxlY = sliderY;
						for (int i = 0; i < 13; ++i) {
							Color theColor = new Color(color + i * 2000);
							int rgb = ModColor.toRGB(theColor.getRed(), theColor.getGreen(), theColor.getBlue(), 255 - i * 18);
							drawRect((int) pxlX, pxlY, (int) pxlX + 1, pxlY + 1, rgb);
							++pxlY;
						}
						pxlX += 0.7D;
					}
					if (this.hoverAdvancedButton)
						TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, "More colors");
				}
			}

			public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
				if (this.isMouseOver(mouseX, mouseY)) {
					this.openedSelector = !this.openedSelector;
					return true;
				} else {
					if (this.openedSelector) {
						if (this.hoverSlider) {
							this.selectedColor = this.colorForPreview;
							if (this.updateListener != null)
								this.updateListener.accept(this.selectedColor);
						}
						if (this.hasDefault && this.selectedColor != null && this.hoverDefaultButton) {
							this.selectedColor = null;
							this.colorForPreview = this.defaultColor.getDefaultColor();
							if (this.updateListener != null)
								this.updateListener.accept(this.selectedColor);
							return true;
						}
						if (this.hasAdvanced && this.hoverAdvancedButton) {
							Minecraft.getMinecraft().displayGuiScreen(new ColorPicker.AdvancedColorSelectorGui(this, Minecraft.getMinecraft().currentScreen, lastScreen -> Minecraft.getMinecraft().displayGuiScreen(lastScreen)));
							return true;
						}
					}
					boolean flag = this.openedSelector;
					this.openedSelector = false;
					return flag != this.openedSelector;
				}
			}

			public boolean mouseDragging(int mouseX, int mouseY, int mouseButton) {
				return false;
			}

			public boolean mouseReleased(int mouseX, int mouseY, int mouseButton) {
				return false;
			}

			public void setSelectedColor(Color selectedColor) {
				this.selectedColor = selectedColor;
				this.colorForPreview = selectedColor;
			}

			boolean isMouseOver(int mouseX, int mouseY) {
				return mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height;
			}

			interface DefaultColorCallback {
				Color getDefaultColor();
			}

			static class AdvancedColorSelectorGui extends GuiScreen {
				private GuiScreen backgroundScreen;
				private Consumer<GuiScreen> callback;
				private ColorPicker colorPicker;
				private GuiTextField fieldHexColor;
				private Color lastColor = null;
				private boolean validHex = true;

				AdvancedColorSelectorGui(ColorPicker colorPicker, GuiScreen backgroundScreen, Consumer<GuiScreen> callback) {
					this.backgroundScreen = backgroundScreen;
					this.callback = callback;
					this.colorPicker = colorPicker;
				}

				public void initGui() {
					super.initGui();
					this.backgroundScreen.width = this.width;
					this.backgroundScreen.height = this.height;
					if (this.backgroundScreen instanceof LabyModModuleEditorGui)
						PreviewRenderer.getInstance().init(ColorPicker.AdvancedColorSelectorGui.class);
					this.fieldHexColor = new GuiTextField(0, LabyModCore.getMinecraft().getFontRenderer(), this.width / 2 - 70, this.height / 4 + 115, 100, 16);
					this.fieldHexColor.setMaxStringLength(7);
					this.lastColor = null;
					this.buttonList.add(new GuiButton(1, this.width / 2 + 40, this.height / 4 + 113, 60, 20, "Done"));
				}

				public void onGuiClosed() {
					this.backgroundScreen.onGuiClosed();
					super.onGuiClosed();
				}

				public void drawScreen(int mouseX, int mouseY, float partialTicks) {
					this.backgroundScreen.drawScreen(mouseX, mouseY, partialTicks);
					drawRect(0, 0, this.width, this.height, -2147483648);
					this.colorPicker.drawColorPicker(this.width / 2 - 100, this.height / 4 - 40);
					drawRect(this.width / 2 - 105, this.height / 4 - 25, this.width / 2 + 105, this.height / 4 + 140, -2147483648);
					this.colorPicker.drawAdvanced(mouseX, mouseY, this.width / 2 - 100, this.height / 4 + 4, this.width / 2 - 85, this.height / 4 - 8);
					drawRect(LabyModCore.getMinecraft().getXPosition(this.fieldHexColor) - 2, LabyModCore.getMinecraft().getYPosition(this.fieldHexColor) - 2, LabyModCore.getMinecraft().getXPosition(this.fieldHexColor) + 100 + 2, LabyModCore.getMinecraft().getYPosition(this.fieldHexColor) + 16 + 2, this.validHex ? ModColor.toRGB(85, 255, 85, 100) : ModColor.toRGB(255, 85, 85, 100));
					this.fieldHexColor.drawTextBox();
					if (this.colorPicker.colorForPreview == null)
						this.colorPicker.colorForPreview = this.colorPicker.defaultColor.getDefaultColor();
					drawRect(this.width / 2 - 100, this.height / 4 + 113, this.width / 2 - 100 + 20, this.height / 4 + 113 + 20, 2147483647);
					drawRect(this.width / 2 - 100 + 1, this.height / 4 + 113 + 1, this.width / 2 - 100 + 20 - 1, this.height / 4 + 113 + 20 - 1, this.colorPicker.colorForPreview.getRGB());
					if (this.lastColor == null || !this.lastColor.equals(this.colorPicker.colorForPreview)) {
						this.lastColor = this.colorPicker.colorForPreview;
						String hex = String.format("#%02x%02x%02x", this.lastColor.getRed(), this.lastColor.getGreen(), this.lastColor.getBlue());
						this.fieldHexColor.setText(hex);
						this.validHex = true;
					}
					this.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), "Colors", this.width / 2, this.height / 4 - 20, -1);
					super.drawScreen(mouseX, mouseY, partialTicks);
				}

				protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
					super.mouseClicked(mouseX, mouseY, mouseButton);
					this.colorPicker.selectedColor = this.colorPicker.colorForPreview;
					if (this.colorPicker.updateListener != null)
						this.colorPicker.updateListener.accept(this.colorPicker.selectedColor);
					this.fieldHexColor.mouseClicked(mouseX, mouseY, mouseButton);
				}

				protected void keyTyped(char typedChar, int keyCode) {
					if (keyCode == 1)
						Minecraft.getMinecraft().displayGuiScreen(this.backgroundScreen);
					if (this.fieldHexColor.textboxKeyTyped(typedChar, keyCode)) {
						String hex = this.fieldHexColor.getText();
						if (hex.length() == 7)
							try {
								this.colorPicker.selectedColor = new Color(Integer.valueOf(hex.substring(1, 3), 16), Integer.valueOf(hex.substring(3, 5), 16), Integer.valueOf(hex.substring(5, 7), 16));
								this.colorPicker.colorForPreview = this.colorPicker.selectedColor;
								this.validHex = true;
							} catch (Exception var5) {
								this.validHex = false;
							}
						else
							this.validHex = false;
					}
				}

				public void updateScreen() {
					this.backgroundScreen.updateScreen();
					this.fieldHexColor.updateCursorCounter();
				}

				protected void actionPerformed(GuiButton button) throws IOException {
					super.actionPerformed(button);
					if (button.id == 1)
						Minecraft.getMinecraft().displayGuiScreen(this.backgroundScreen);
				}

				public GuiScreen getBackgroundScreen() {
					return this.backgroundScreen;
				}
			}
		}
	}
}
