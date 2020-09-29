package de.jumpingpxl.jumpingaddon.util.libraries.discordrpc;

import com.sun.jna.Structure;
import de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.callbacks.DisconnectedCallback;
import de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.callbacks.ErroredCallback;
import de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.callbacks.JoinGameCallback;
import de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.callbacks.JoinRequestCallback;
import de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.callbacks.ReadyCallback;
import de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.callbacks.SpectateGameCallback;

import java.util.Arrays;
import java.util.List;

public class DiscordEventHandlers extends Structure {

	public ReadyCallback ready;
	public DisconnectedCallback disconnected;
	public ErroredCallback errored;
	public JoinGameCallback joinGame;
	public SpectateGameCallback spectateGame;
	public JoinRequestCallback joinRequest;

	@Override
	public List<String> getFieldOrder() {
		return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame",
				"joinRequest");
	}
}
