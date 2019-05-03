package de.jumpingpxl.jumpingaddon.util.libraries.discordrpc;

import com.sun.jna.Structure;
import de.jumpingpxl.jumpingaddon.util.libraries.discordrpc.callbacks.*;

import java.util.Arrays;
import java.util.List;

public class DiscordEventHandlers extends Structure {

    @Override
    public List<String> getFieldOrder() {
        return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
    }

    public ReadyCallback ready;
    public DisconnectedCallback disconnected;
    public ErroredCallback errored;
    public JoinGameCallback joinGame;
    public SpectateGameCallback spectateGame;
    public JoinRequestCallback joinRequest;
}
