package de.jumpingpxl.jumpingaddon.util.libraries.discordrpc;

import com.sun.jna.Library;
import com.sun.jna.Native;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class DiscordRPC {

	static {
		loadDLL();
	}

	private static final String DLL_VERSION = "3.0.0";

	public static void discordInitialize(String applicationId, DiscordEventHandlers handlers, boolean autoRegister) {
		DLL.INSTANCE.Discord_Initialize(applicationId, handlers, autoRegister ? 1 : 0, null);
	}

	public static void discordRegister(String applicationId, String command) {
		DLL.INSTANCE.Discord_Register(applicationId, command);
	}

	public static void discordInitialize(String applicationId, DiscordEventHandlers handlers, boolean autoRegister, String steamId) {
		DLL.INSTANCE.Discord_Initialize(applicationId, handlers, autoRegister ? 1 : 0, steamId);
	}

	public static void discordRegisterSteam(String applicationId, String steamId) {
		DLL.INSTANCE.Discord_RegisterSteamGame(applicationId, steamId);
	}

	public static void discordShutdown() {
		DLL.INSTANCE.Discord_Shutdown();
	}

	public static void discordRunCallbacks() {
		DLL.INSTANCE.Discord_RunCallbacks();
	}

	public static void discordUpdatePresence(DiscordRichPresence presence) {
		DLL.INSTANCE.Discord_UpdatePresence(presence);
	}

	public static void discordClearPresence() {
		DLL.INSTANCE.Discord_ClearPresence();
	}

	public static void discordRespond(String userId, DiscordReply reply) {
		DLL.INSTANCE.Discord_Respond(userId, reply.reply);
	}

	private static void loadDLL() {
		String name = System.mapLibraryName("discord-rpc");
		String finalPath = "";
		String tempPath = "";
		if (SystemUtils.IS_OS_WINDOWS) {
			boolean is64bit = System.getProperty("sun.arch.data.model").equals("64");
			finalPath = is64bit ? "/win-x64/discord-rpc.dll" : "/lib/win-x32/discord-rpc.dll";
			tempPath = System.getenv("TEMP") + "/discord-rpc.jar/discord-rpc.dll";
		} else if (SystemUtils.IS_OS_LINUX) {
			finalPath = "/linux/discord-rpc.so";
			tempPath = System.getenv("TMPDIR") + "/discord-rpc.jar/discord-rpc.so";
		} else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
			finalPath = "/osx/discord-rpc.dylib";
			tempPath = System.getenv("TMPDIR") + "/discord-rpc/discord-rpc.dylib";
		}

		File f = new File(tempPath);

		try (InputStream in = DiscordRPC.class.getResourceAsStream(finalPath); OutputStream out = FileUtils.openOutputStream(f)) {
			IOUtils.copy(in, out);
			FileUtils.forceDeleteOnExit(f);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.load(f.getAbsolutePath());
	}

	private interface DLL extends Library {
		DLL INSTANCE = (DLL) Native.loadLibrary("discord-rpc", DLL.class);

		void Discord_Initialize(String applicationId, DiscordEventHandlers handlers, int autoRegister, String optionalSteamId);

		void Discord_Register(String applicationId, String command);

		void Discord_RegisterSteamGame(String applicationId, String steamId);

		void Discord_Shutdown();

		void Discord_RunCallbacks();

		void Discord_UpdatePresence(DiscordRichPresence presence);

		void Discord_ClearPresence();

		void Discord_Respond(String userId, int reply);
	}
}
