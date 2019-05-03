package de.jumpingpxl.jumpingaddon.util.libraries.discordrpc;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class DiscordJoinRequest extends Structure {

	@Override
	public List<String> getFieldOrder() {
		return Arrays.asList("userId", "username", "discriminator", "avatar");
	}

	public String userId;

	public String username;

	public int discriminator;

	public String avatar;
}
