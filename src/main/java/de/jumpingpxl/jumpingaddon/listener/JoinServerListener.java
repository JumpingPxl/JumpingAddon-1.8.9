package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.labymod.utils.Consumer;
import net.labymod.utils.ServerData;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public class JoinServerListener {

	private JumpingAddon jumpingAddon;

	public JoinServerListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public Consumer<ServerData> onJoinServer() {
		return serverData -> {
			if (jumpingAddon.getSettings().getStartupNotification() != null) {
				jumpingAddon.displayChatComponent(jumpingAddon.getSettings().getStartupNotification());
				jumpingAddon.getSettings().setStartupNotification(null);
			}
			jumpingAddon.getConnection().connect(serverData);
		};
	}
}
