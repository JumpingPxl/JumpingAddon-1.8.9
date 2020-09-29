package de.jumpingpxl.jumpingaddon.util.serversupport;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public enum Server {

	GOMMEHD_NET("GommeHD", "gommehd.net", "gommehd.de", "gommehd.cloud", "gommehd.vip", "gommehd"
			+ ".fun",
			"gommehd.com", "gommehd.tk", "horny.agency");

	private static final Map<Server, ServerSupport> supportMap = new HashMap<>();

	private final String name;
	private final String[] domains;

	Server(String name, String... domains) {
		this.name = name;
		this.domains = domains;
	}

	public static Map<Server, ServerSupport> getSupportMap() {
		return Server.supportMap;
	}

	public ServerSupport getServerSupport() {
		return supportMap.get(this);
	}

	public String getName() {
		return this.name;
	}

	public String[] getDomains() {
		return this.domains;
	}
}
