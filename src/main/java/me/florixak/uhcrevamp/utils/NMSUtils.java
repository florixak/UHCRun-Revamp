package me.florixak.uhcrevamp.utils;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NMSUtils {

	public static void sendPacket(final Object playerConnection, final Class<?> packetClass, final Object packet) throws Exception {
		final Method sendPacket = playerConnection.getClass().getMethod("sendPacket", packetClass);
		sendPacket.invoke(playerConnection, packet);
	}

	public static Class<?> getNMSClass(final String name) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server.v1_8_R3." + name);
	}

	public static void sendHotBarMessageViaNMS(final Player player, final String message) {
		if (!player.isOnline()) {
			return; // Player may have logged out
		}

		// Call the event, if cancelled don't send Action Bar
		try {
			final Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + UHCRevamp.nmsVer + ".entity.CraftPlayer");
			final Object craftPlayer = craftPlayerClass.cast(player);
			Object packet;
			final Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".PacketPlayOutChat");
			final Class<?> packetClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".Packet");
			if (UHCRevamp.useOldMethods) {
				final Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".ChatSerializer");
				final Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".IChatBaseComponent");
				final Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
				final Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + TextUtils.color(message) + "\"}"));
				packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(cbc, (byte) 2);
			} else {
				final Class<?> chatComponentTextClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".ChatComponentText");
				final Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".IChatBaseComponent");
				try {
					final Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".ChatMessageType");
					final Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
					Object chatMessageType = null;
					for (final Object obj : chatMessageTypes) {
						if (obj.toString().equals("GAME_INFO")) {
							chatMessageType = obj;
						}
					}
					final Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(TextUtils.color(message));
					packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, chatMessageTypeClass}).newInstance(chatCompontentText, chatMessageType);
				} catch (final ClassNotFoundException cnfe) {
					final Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(TextUtils.color(message));
					packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(chatCompontentText, (byte) 2);
				}
			}
			final Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
			final Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer);
			final Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
			final Object playerConnection = playerConnectionField.get(craftPlayerHandle);
			final Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
			sendPacketMethod.invoke(playerConnection, packet);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
