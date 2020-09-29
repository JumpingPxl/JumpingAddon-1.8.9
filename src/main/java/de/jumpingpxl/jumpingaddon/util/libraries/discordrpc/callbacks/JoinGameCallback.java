package de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.callbacks;

import com.sun.jna.Callback;

public interface JoinGameCallback extends Callback {

	void apply(String joinSecret);
}
