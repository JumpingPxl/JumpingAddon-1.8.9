package de.jumpingpxl.jumpingaddon.util.serversupport;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

@Getter
public enum Server {

	GOMMEHD_NET("GommeHD", "gommehd.net", "gommehd.de", "gommehd.cloud", "gommehd.vip", "gommehd.fun", "gommehd.com", "gommehd.tk"),
	TIMOLIA_DE("Timolia", "timolia.de"),
	REWINSIDE_TV("rewinside", "rewinside.tv");

	@Getter
	private static Map<Server, ServerSupport> supportMap = new HashMap<>();

	private String name;
	private String[] domains;

	Server(String name, String... domains) {
		this.name = name;
		this.domains = domains;
	}

	public ServerSupport getServerSupport() {
		return supportMap.get(this);
	}
}
