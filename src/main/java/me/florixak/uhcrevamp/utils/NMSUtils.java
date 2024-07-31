package me.florixak.uhcrevamp.utils;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NMSUtils {

	public static void sendPacket(Object playerConnection, Class<?> packetClass, Object packet) throws Exception {
		Method sendPacket = playerConnection.getClass().getMethod("sendPacket", packetClass);
		sendPacket.invoke(playerConnection, packet);
	}

	public static Class<?> getNMSClass(String name) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server.v1_8_R3." + name);
	}

	public static void sendHotBarMessageViaNMS(Player player, String message) {
		if (!player.isOnline()) {
			return; // Player may have logged out
		}

		// Call the event, if cancelled don't send Action Bar
		try {
			Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + UHCRevamp.nmsVer + ".entity.CraftPlayer");
			Object craftPlayer = craftPlayerClass.cast(player);
			Object packet;
			Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".PacketPlayOutChat");
			Class<?> packetClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".Packet");
			if (UHCRevamp.useOldMethods) {
				Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".ChatSerializer");
				Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".IChatBaseComponent");
				Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
				Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + TextUtils.color(message) + "\"}"));
				packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(cbc, (byte) 2);
			} else {
				Class<?> chatComponentTextClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".ChatComponentText");
				Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".IChatBaseComponent");
				try {
					Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + UHCRevamp.nmsVer + ".ChatMessageType");
					Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
					Object chatMessageType = null;
					for (Object obj : chatMessageTypes) {
						if (obj.toString().equals("GAME_INFO")) {
							chatMessageType = obj;
						}
					}
					Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(TextUtils.color(message));
					packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, chatMessageTypeClass}).newInstance(chatCompontentText, chatMessageType);
				} catch (ClassNotFoundException cnfe) {
					Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(TextUtils.color(message));
					packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(chatCompontentText, (byte) 2);
				}
			}
			Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
			Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer);
			Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
			Object playerConnection = playerConnectionField.get(craftPlayerHandle);
			Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
			sendPacketMethod.invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
