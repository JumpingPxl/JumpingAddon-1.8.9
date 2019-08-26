package de.jumpingpxl.jumpingaddon.util.modules;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.labymod.core.LabyModCore;
import net.labymod.gui.ModGuiMultiplayer;
import net.labymod.ingamegui.ModuleCategory;
import net.labymod.ingamegui.moduletypes.SimpleModule;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.NumberElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;
import net.labymod.utils.ModColor;
import net.labymod.utils.ModUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */

public class AfkTimerInGameModule extends SimpleModule implements InGameModule {

	private JumpingAddon jumpingAddon;
	private String timer = "";
	private int currentCount = 0;
	private int currentTick = 0;
	private GuiScreen currentGui;
	private long lastMove;
	private long lastTimeAfk;
	private int lastMouseX;
	private int lastMouseY;
	private int idleTimeSeconds = 10;

	public AfkTimerInGameModule(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public String getDisplayName() {
		return "AFK";
	}

	@Override
	public String getDisplayValue() {
		return timer;
	}

	@Override
	public String getDefaultValue() {
		return "00:00";
	}

	@Override
	public boolean isShown() {
		return !timer.isEmpty() && jumpingAddon.getConnection().isSupported();
	}

	@Override
	public ControlElement.IconData getIconData() {
		return new ControlElement.IconData(Material.WATCH);
	}

	@Override
	public void fillSubSettings(List<SettingsElement> settingsElements) {
		super.fillSubSettings(settingsElements);
		settingsElements.add((new NumberElement(this, new ControlElement.IconData(Material.WATCH), "Show idled Time", "idledTimeSeconds")).setRange(3, 1800));
	}

	@Override
	public void loadSettings() {
		this.idleTimeSeconds = Integer.parseInt(this.getAttribute("idledTimeSeconds", "10"));
		if (jumpingAddon.getSettings().getIdleSeconds().getAsInteger() < 10)
			jumpingAddon.getSettings().getIdleSeconds().setValue(10);
		if (this.idleTimeSeconds < 5)
			this.idleTimeSeconds = 5;
	}

	@Override
	public String getSettingName() {
		return "jumping_afk";
	}

	@Override
	public String getControlName() {
		return "AFKTimer";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public ModuleCategory getCategory() {
		return jumpingAddon.getIngameModuleHandler().getCategory();
	}

	@Override
	public int getSortingId() {
		return 0;
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			if (jumpingAddon.getConnection().getServerDomain() != null && this.getEnabled().size() != 0) {
				int currentMouseX = Mouse.getX();
				int currentMouseY = Mouse.getY();
				long currentMillis = System.currentTimeMillis();
				if (!(!(this.lastMouseX == currentMouseX && this.lastMouseY == currentMouseY) && (currentGui == null || ((currentGui instanceof GuiChat || currentGui instanceof GuiChest) || ((currentGui instanceof GuiInventory || currentGui instanceof ModGuiMultiplayer))))) && LabyModCore.getMinecraft().getPlayer().moveForward == 0.0F && LabyModCore.getMinecraft().getPlayer().fallDistance == 0.0F) {
					if (jumpingAddon.getConnection().isAfk()) {
						if (++this.currentTick >= 20) {
							this.currentTick = 0;
							++this.currentCount;
							this.timer = ModUtils.parseTimer(this.currentCount + jumpingAddon.getSettings().getIdleSeconds().getAsInteger());
						}
					} else if (currentMillis - this.lastMove >= (long) (1000 * jumpingAddon.getSettings().getIdleSeconds().getAsInteger())) {
						jumpingAddon.getConnection().setAfk(true);
						this.currentCount = 0;
						this.timer = ModUtils.parseTimer(jumpingAddon.getSettings().getIdleSeconds().getAsInteger());
					}
				} else {
					if (jumpingAddon.getConnection().isAfk()) {
						jumpingAddon.getConnection().setAfk(false);
						this.timer = ModColor.cl("c") + this.timer;
						this.lastTimeAfk = currentMillis;
					}
					this.lastMove = currentMillis;
				}
				if (!jumpingAddon.getConnection().isAfk() && currentMillis - this.lastTimeAfk >= (long) (1000 * this.idleTimeSeconds)) {
					this.timer = "";
					this.currentCount = 0;
				}
				this.lastMouseX = currentMouseX;
				this.lastMouseY = currentMouseY;
			} else {
				this.currentCount = 0;
				this.timer = "";
			}
		}
	}

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		currentGui = LabyModCore.getForge().getGuiOpenEventGui(event);
	}
}
