package de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.callbacks;

import com.sun.jna.Callback;

public interface DisconnectedCallback extends Callback {

	void apply(int errorCode, String message);
}
