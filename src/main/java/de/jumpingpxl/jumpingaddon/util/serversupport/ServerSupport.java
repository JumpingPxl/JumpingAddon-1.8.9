package de.jumpingpxl.jumpingaddon.util.serversupport;

import de.jumpingpxl.jumpingaddon.util.ChatComponent;
import de.jumpingpxl.jumpingaddon.util.Configuration;
import de.jumpingpxl.jumpingaddon.util.elements.BetterElement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 27.02.2019
 */

public interface ServerSupport {

	Server getServer();

	void loadChecks();

	Configuration getConfig();

	ServerSupportHandler.GameType handleGameType(String handle);

	ChatComponent handleIncomingMessage(ChatComponent chatComponent, String formatted, String unformatted);

	boolean handleReceivingMessage(String formatted, String unformatted);

	default BetterElement getSettingsElement() {
		return null;
	}

	default List<ServerSupportHandler.GameType> getGameTypes() {
		return new ArrayList<>();
	}

	default void applyGameTypes(List<ServerSupportHandler.GameType> gameTypes) {
	}

	default void applyCheckMessage(String definition, List<Pattern> list) {
	}

	interface SupportSettings {
		void loadSettings(Configuration configuration);

		default BetterElement getSettingsElement() {
			return null;
		}
	}

}
