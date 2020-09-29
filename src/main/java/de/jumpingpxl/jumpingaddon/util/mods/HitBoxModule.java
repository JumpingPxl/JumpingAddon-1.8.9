package de.jumpingpxl.jumpingaddon.util.mods;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.SettingValue;
import de.jumpingpxl.jumpingaddon.util.elements.BetterBooleanElement;
import de.jumpingpxl.jumpingaddon.util.elements.BetterColorPickerElement;
import de.jumpingpxl.jumpingaddon.util.elements.BetterListContainerElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.utils.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 18.03.2019
 */

public class HitBoxModule {

	private final JumpingAddon jumpingAddon;
	private SettingValue hitbox;
	private SettingValue hitboxSelf;
	private SettingValue hitboxPlayer;
	private SettingValue hitboxAnimals;
	private SettingValue hitboxMobs;
	private SettingValue hitboxDrops;
	private SettingValue hitboxThrowables;
	private SettingValue color;
	private SettingValue colorPlayer;
	private SettingValue colorAnimals;
	private SettingValue colorMobs;
	private SettingValue colorDrops;
	private SettingValue colorThrowables;

	private SettingValue ownColorPlayer;
	private SettingValue ownColorAnimals;
	private SettingValue ownColorMobs;
	private SettingValue ownColorDrops;
	private SettingValue ownColorThrowables;

	public HitBoxModule(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public HitBoxModule loadSettings() {
		hitbox = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(), "enabledHitbox",
				true);
		hitboxSelf = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"enabledHitboxSelf", false);
		hitboxPlayer = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"enabledHitboxPlayer", true);
		hitboxAnimals = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"enabledHitboxAnimals", true);
		hitboxMobs = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"enabledHitboxMobs", true);
		hitboxDrops = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"enabledHitboxDrops", true);
		hitboxThrowables = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"enabledHitboxThrowables", true);
		ownColorPlayer = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"enabledOwnHitboxPlayer", false);
		ownColorAnimals = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"enabledOwnHitboxAnimals", false);
		ownColorMobs = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"enabledOwnHitboxMobs", false);
		ownColorDrops = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"enabledOwnHitboxDrops", false);
		ownColorThrowables = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"enabledOwnHitboxThrowables", false);
		int white = Color.WHITE.getRGB();
		color = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(), "hitboxColor", white);
		colorPlayer = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"hitboxColorPlayer", white);
		colorAnimals = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"hitboxColorAnimals", white);
		colorMobs = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(), "hitboxColorMobs",
				white);
		colorDrops = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"hitboxColorDrops",
				white);
		colorThrowables = new SettingValue(jumpingAddon, jumpingAddon.getSettings().getSettingValues(),
				"hitboxColorThrowables", white);
		return this;
	}

	public void renderHitBox(Entity entity, double x, double y, double z, float partialTicks) {
		if (!hitbox.getAsBoolean()) {
			return;
		}
		if (entity == Minecraft.getMinecraft().thePlayer && !hitboxSelf.getAsBoolean()) {
			return;
		}
		if (entity.isInvisible()) {
			return;
		}
		int color = this.color.getAsInteger();
		if (entity instanceof EntityPlayer) {
			if (!hitboxPlayer.getAsBoolean()) {
				return;
			}
			if (ownColorPlayer.getAsBoolean()) {
				color = colorPlayer.getAsInteger();
			}
		} else if (entity instanceof IMob) {
			if (!hitboxMobs.getAsBoolean()) {
				return;
			}
			if (ownColorMobs.getAsBoolean()) {
				color = colorMobs.getAsInteger();
			}
		} else if (entity instanceof IAnimals) {
			if (!hitboxAnimals.getAsBoolean()) {
				return;
			}
			if (ownColorAnimals.getAsBoolean()) {
				color = colorAnimals.getAsInteger();
			}
		} else if (entity instanceof EntityItem) {
			if (!hitboxDrops.getAsBoolean()) {
				return;
			}
			if (ownColorDrops.getAsBoolean()) {
				color = colorDrops.getAsInteger();
			}
		} else if (entity instanceof EntityFireball || entity instanceof IProjectile
				|| entity instanceof EntityFishHook) {
			if (!hitboxThrowables.getAsBoolean()) {
				return;
			}
			if (ownColorThrowables.getAsBoolean()) {
				color = colorThrowables.getAsInteger();
			}
		} else {
			return;
		}
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox();
		AxisAlignedBB axisAlignedBB = new AxisAlignedBB(entityBoundingBox.minX - entity.posX + x,
				entityBoundingBox.minY - entity.posY + y, entityBoundingBox.minZ - entity.posZ + z,
				entityBoundingBox.maxX - entity.posX + x, entityBoundingBox.maxY - entity.posY + y,
				entityBoundingBox.maxZ - entity.posZ + z);
		Color hitBoxColor = new Color(color);
		RenderGlobal.drawOutlinedBoundingBox(axisAlignedBB, hitBoxColor.getRed(),
				hitBoxColor.getGreen(), hitBoxColor.getBlue(), hitBoxColor.getAlpha());
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

	public BetterListContainerElement getElement() {
		return new BetterListContainerElement("Hitbox",
				new ControlElement.IconData(Material.BOWL)).addSettings(
				new BetterBooleanElement("Enabled", new ControlElement.IconData(Material.LEVER), hitbox),
				new HeaderElement(""),
				new BetterColorPickerElement("Color", new ControlElement.IconData(Material.INK_SACK),
						color), new BetterListContainerElement("Player",
						new ControlElement.IconData(Material.MINECART)).addSettings("Hitbox",
						new BetterBooleanElement("Enabled Player", new ControlElement.IconData(Material.LEVER),
								hitboxPlayer), new HeaderElement(""),
						new BetterBooleanElement("Self", new ControlElement.IconData(Material.LEVER),
								hitboxSelf),
						new BetterBooleanElement("Own Color", new ControlElement.IconData(Material.LEVER),
								ownColorPlayer), new BetterColorPickerElement("Player Color",
								new ControlElement.IconData(Material.INK_SACK), colorPlayer)),
				new BetterListContainerElement("Animals",
						new ControlElement.IconData(Material.MONSTER_EGG))
						.addSettings("Hitbox", new BetterBooleanElement("Enabled Animals",
										new ControlElement.IconData(Material.LEVER), hitboxAnimals),
								new HeaderElement(""),
								new BetterBooleanElement("Own Color", new ControlElement.IconData(Material.LEVER),
										ownColorAnimals), new BetterColorPickerElement("Animal Color",
										new ControlElement.IconData(Material.INK_SACK), colorAnimals)),
				new BetterListContainerElement("Mobs",
						new ControlElement.IconData(Material.DRAGON_EGG)).addSettings("Hitbox",
						new BetterBooleanElement("Enabled Mobs", new ControlElement.IconData(Material.LEVER),
								hitboxMobs), new HeaderElement(""),
						new BetterBooleanElement("Own Color", new ControlElement.IconData(Material.LEVER),
								ownColorMobs), new BetterColorPickerElement("Mob Color",
								new ControlElement.IconData(Material.INK_SACK), colorMobs)),
				new BetterListContainerElement("Drops",
						new ControlElement.IconData(Material.APPLE)).addSettings("Hitbox",
						new BetterBooleanElement("Enabled Drops", new ControlElement.IconData(Material.LEVER),
								hitboxDrops), new HeaderElement(""),
						new BetterBooleanElement("Own Color", new ControlElement.IconData(Material.LEVER),
								ownColorDrops), new BetterColorPickerElement("Drop Color",
								new ControlElement.IconData(Material.INK_SACK), colorDrops)),
				new BetterListContainerElement("Throwables",
						new ControlElement.IconData(Material.ARROW)).addSettings("Hitbox",
						new BetterBooleanElement("Enabled Throwables",
								new ControlElement.IconData(Material.LEVER), hitboxThrowables),
						new HeaderElement(""),
						new BetterBooleanElement("Own Color", new ControlElement.IconData(Material.LEVER),
								ownColorThrowables), new BetterColorPickerElement("Throwable Color",
								new ControlElement.IconData(Material.INK_SACK), colorThrowables)));
	}
}
