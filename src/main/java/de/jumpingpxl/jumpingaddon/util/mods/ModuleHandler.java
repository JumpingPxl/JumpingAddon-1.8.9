package de.jumpingpxl.jumpingaddon.util.mods;

import de.jumpingpxl.jumpingaddon.JumpingAddon;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */

public class ModuleHandler {

	private final JumpingAddon jumpingAddon;
	private PingModule pingModule;
	private ActionModule actionModule;
	private DiscordRPCModule discordRPCModule;
	private HitBoxModule hitBoxModule;
	private SignSearchModule signSearchModule;

	public ModuleHandler(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public ModuleHandler registerModules() {
		pingModule = new PingModule(jumpingAddon);
		actionModule = new ActionModule(jumpingAddon);
		discordRPCModule = new DiscordRPCModule(jumpingAddon);
		hitBoxModule = new HitBoxModule(jumpingAddon);
		//signSearchModule = new SignSearchModule(jumpingAddon);
		return this;
	}

	public JumpingAddon getJumpingAddon() {
		return this.jumpingAddon;
	}

	public PingModule getPingModule() {
		return this.pingModule;
	}

	public ActionModule getActionModule() {
		return this.actionModule;
	}

	public DiscordRPCModule getDiscordRPCModule() {
		return this.discordRPCModule;
	}

	public HitBoxModule getHitBoxModule() {
		return this.hitBoxModule;
	}

	public SignSearchModule getSignSearchModule() {
		return this.signSearchModule;
	}
}
