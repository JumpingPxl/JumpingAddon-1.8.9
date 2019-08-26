package de.jumpingpxl.jumpingaddon.util.mods;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.user.User;
import net.labymod.user.group.EnumGroupDisplayType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */

public class PingModule {

	private JumpingAddon jumpingAddon;
	private RenderManager renderManager;
	private DamageIndicatorsWrapper damageIndicatorsWrapper;

	public PingModule(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
		renderManager = Minecraft.getMinecraft().getRenderManager();
		damageIndicatorsWrapper = new DamageIndicatorsWrapper();
	}

	public void render(Entity entity, double x, double y, double z) {
		if (!jumpingAddon.getSettings().getPing().getAsBoolean())
			return;
		if (!(entity instanceof EntityPlayer))
			return;
		EntityPlayer entityplayer = (EntityPlayer) entity;
		if (entityplayer.isSneaking() || entityplayer.isInvisible())
			return;
		if (LabyModCore.getMinecraft().getPlayer().equals(entityplayer))
			return;
		double distance = getDistanceSq(entity, LabyModCore.getMinecraft().getPlayer());
		User user = LabyMod.getInstance().getUserManager().getUsers().get(entity.getUniqueID());
		float maxNameTagHeight = user == null || !LabyMod.getSettings().cosmetics ? 0 : user.getMaxNameTagHeight();
		String displayRank = user == null || user.getGroup().getDisplayType() != EnumGroupDisplayType.ABOVE_HEAD ? null : user.getGroup().getDisplayName();
		y += (double) (LabyMod.getInstance().getDrawUtils().getFontRenderer().FONT_HEIGHT * 1.15F * 0.02666667F);
		y += maxNameTagHeight;
		if (distance < 100.0D) {
			Scoreboard scoreboard = LabyModCore.getMinecraft().getWorld().getScoreboard();
			ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
			if (scoreobjective != null)
				y += (double) (LabyMod.getInstance().getDrawUtils().getFontRenderer().FONT_HEIGHT * 1.15F * 0.02666667F);
			if (displayRank != null)
				y += 0.13f;
		}
		if (damageIndicatorsWrapper.isDamageIndicatorEnabled() && distance <= damageIndicatorsWrapper.getMaxDistance() && !Minecraft.getMinecraft().gameSettings.hideGUI)
			y += (double) (LabyMod.getInstance().getDrawUtils().getFontRenderer().FONT_HEIGHT * 1.15F * 0.02666667F);
		NetworkPlayerInfo playerInfo = LabyModCore.getMinecraft().getConnection().getPlayerInfo(entityplayer.getUniqueID());
		int ping = playerInfo != null ? playerInfo.getResponseTime() : -1;
		if (ping > 0) {
			String pingColor;
			if (ping < 80) {
				pingColor = "§a";
			} else if (ping < 120) {
				pingColor = "§e";
			} else if (ping < 250) {
				pingColor = "§c";
			} else {
				pingColor = "§4";
			}
			renderLivingLabel(entity, jumpingAddon.getSettings().getPingFormatting().getAsString('&').replace("%color%", pingColor).replace("%ping%", String.valueOf(ping)), x, y, z);
		}
	}

	private void renderLivingLabel(Entity entity, String label, double x, double y, double z) {
		FontRenderer fontrenderer = renderManager.getFontRenderer();
		float f = 1.6F;
		float f1 = 0.016666668F * f;
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.0F, (float) y + entity.height + 0.5F, (float) z);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-f1, -f1, f1);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		int i = 0;
		int j = fontrenderer.getStringWidth(label) / 2;
		GlStateManager.disableTexture2D();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos((double) (-j - 1), (double) (-1 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		worldrenderer.pos((double) (-j - 1), (double) (8 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		worldrenderer.pos((double) (j + 1), (double) (8 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		worldrenderer.pos((double) (j + 1), (double) (-1 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		fontrenderer.drawString(label, -fontrenderer.getStringWidth(label) / 2, i, 553648127);
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		fontrenderer.drawString(label, -fontrenderer.getStringWidth(label) / 2, i, -1);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	public double getDistanceSq(Entity entity, Entity entity2) {
		return distanceSq(entity, entity2.posX, entity2.posY, entity2.posZ);
	}

	private double distanceSq(Entity entityPos, double toX, double toY, double toZ) {
		double d0 = entityPos.posX - toX;
		double d1 = entityPos.posY - toY;
		double d2 = entityPos.posZ - toZ;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public class DamageIndicatorsWrapper {

		private boolean damageIndicatorEnabled;
		private int maxDistance;
		private long lastDamageIndicatorUpdate;
		private boolean notFoundDamageIndicator;
		private int damageIndicatorTries = 0;
		private Class<?> damageIndicatorClass;
		private Method getInstanceMethod;
		private Method isVisibleMethod;
		private Method getDistanceMethod;
		private Field allowedField;

		public DamageIndicatorsWrapper() {

		}

		public boolean isDamageIndicatorEnabled() {
			return damageIndicatorEnabled;
		}

		public int getMaxDistance() {
			return maxDistance;
		}

		@SubscribeEvent
		public void onTick(TickEvent.ClientTickEvent event) {
			long currentTimestamp = System.currentTimeMillis();
			if ((currentTimestamp - lastDamageIndicatorUpdate) >= 1000L) {
				updateDamageIndicatorsState();
				this.lastDamageIndicatorUpdate = currentTimestamp;
			}
		}

		public void updateDamageIndicatorsState() {
			if (this.notFoundDamageIndicator)
				return;
			if (damageIndicatorClass == null)
				try {
					this.damageIndicatorClass = Class.forName("net.labymod.addons.damageindicator.DamageIndicator");
				} catch (ClassNotFoundException ex) {
					if (++damageIndicatorTries >= 10)
						this.notFoundDamageIndicator = true;
					return;
				}
			if (this.getInstanceMethod == null)
				try {
					this.getInstanceMethod = damageIndicatorClass.getDeclaredMethod("getInstance");
					getInstanceMethod.setAccessible(true);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			if (this.isVisibleMethod == null)
				try {
					this.isVisibleMethod = damageIndicatorClass.getDeclaredMethod("isVisible");
					isVisibleMethod.setAccessible(true);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			if (this.getDistanceMethod == null)
				try {
					this.getDistanceMethod = damageIndicatorClass.getDeclaredMethod("getDistance");
					getDistanceMethod.setAccessible(true);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			if (this.allowedField == null)
				try {
					this.allowedField = damageIndicatorClass.getDeclaredField("allowed");
					allowedField.setAccessible(true);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
			if (getInstanceMethod == null || isVisibleMethod == null || getDistanceMethod == null || allowedField == null)
				return;
			try {
				Object instance = getInstanceMethod.invoke(null);
				if (instance == null)
					return;
				this.damageIndicatorEnabled = (Boolean) isVisibleMethod.invoke(instance) && (Boolean) allowedField.get(instance);
				int distance = (Integer) getDistanceMethod.invoke(instance);
				this.maxDistance = distance * distance;
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
