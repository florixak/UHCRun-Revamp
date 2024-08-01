package me.florixak.uhcrevamp.versions;

import me.florixak.uhcrevamp.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class VersionUtils_1_8 implements VersionUtils {

	@Override
	public Set<Material> getWoodPlankValues() {
		return EnumSet.of(Material.valueOf("WOOD"));
	}

	@Override
	public ItemStack getLapis(int amount) {
		return new ItemStack(Material.valueOf("INK_SACK"), amount, (short) 4);
	}

	@Override
	public ShapedRecipe createRecipe(ItemStack item, String key) {
		return new ShapedRecipe(item);
	}

	@Override
	public void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

			Class<?> packetPlayOutTitleClass = NMSUtils.getNMSClass("PacketPlayOutTitle");
			Class<?> packetClass = NMSUtils.getNMSClass("Packet");
			Class<?> chatComponentTextClass = NMSUtils.getNMSClass("ChatComponentText");
			Class<?> iChatBaseComponentClass = NMSUtils.getNMSClass("IChatBaseComponent");
			Class<?> enumTitleActionClass = packetPlayOutTitleClass.getDeclaredClasses()[0];

			Constructor<?> chatComponentTextConstructor = chatComponentTextClass.getConstructor(String.class);
			Object titleComponent = chatComponentTextConstructor.newInstance(title);
			Object subTitleComponent = chatComponentTextConstructor.newInstance(subTitle);

			Method valueOf = enumTitleActionClass.getMethod("valueOf", String.class);
			Object titleEnum = valueOf.invoke(null, "TITLE");
			Object subTitleEnum = valueOf.invoke(null, "SUBTITLE");

			Constructor<?> packetPlayOutTitleConstructor = packetPlayOutTitleClass.getConstructor(enumTitleActionClass, iChatBaseComponentClass, int.class, int.class, int.class);
			Object titlePacket = packetPlayOutTitleConstructor.newInstance(titleEnum, titleComponent, fadeIn, stay, fadeOut);
			Object subTitlePacket = packetPlayOutTitleConstructor.newInstance(subTitleEnum, subTitleComponent, fadeIn, stay, fadeOut);

			NMSUtils.sendPacket(playerConnection, packetClass, titlePacket);
			NMSUtils.sendPacket(playerConnection, packetClass, subTitlePacket);
		} catch (Exception e) {
			Bukkit.getLogger().info("Failed to send title.");
		}
	}
}
