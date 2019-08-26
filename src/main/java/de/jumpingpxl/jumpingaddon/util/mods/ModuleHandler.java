package de.jumpingpxl.jumpingaddon.util.mods;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import lombok.Getter;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 02.03.2019
 */

@Getter
public class ModuleHandler {

	private JumpingAddon jumpingAddon;
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
		signSearchModule = new SignSearchModule(jumpingAddon);
		return this;
	}
}
