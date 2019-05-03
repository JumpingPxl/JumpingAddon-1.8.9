package de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.callbacks;

import com.sun.jna.Callback;

public interface SpectateGameCallback extends Callback {
	void apply(String spectateSecret);
}
