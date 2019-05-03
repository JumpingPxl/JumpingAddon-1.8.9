package de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.callbacks;

import com.sun.jna.Callback;
import de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.DiscordJoinRequest;

public interface JoinRequestCallback extends Callback {
	void apply(DiscordJoinRequest request);
}
