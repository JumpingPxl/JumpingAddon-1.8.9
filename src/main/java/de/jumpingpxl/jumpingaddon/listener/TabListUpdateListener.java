package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.serversupport.Server;
import net.labymod.api.events.TabListEvent;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public class TabListUpdateListener implements TabListEvent {

	private JumpingAddon jumpingAddon;

	public TabListUpdateListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public void onUpdate(Type type, String formatted, String unformatted) {
		if (type == Type.HEADER) {
			jumpingAddon.getSettings().setTabHeader(formatted.replace("\n", "\\n"));
			if (jumpingAddon.getConnection().getServer() == Server.GOMMEHD_NET)
				jumpingAddon.getConnection().getSupport().handleGameType(unformatted);
		} else
			jumpingAddon.getSettings().setTabFooter(formatted.replace("\n", "\\n"));
	}
}
