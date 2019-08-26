package de.jumpingpxl.jumpingaddon.listener;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.labymod.utils.Consumer;
import net.labymod.utils.ServerData;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public class QuitServerListener {

	private JumpingAddon jumpingAddon;

	public QuitServerListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public Consumer<ServerData> onQuitServer() {
		return serverData -> jumpingAddon.getConnection().disconnect();
	}
}
