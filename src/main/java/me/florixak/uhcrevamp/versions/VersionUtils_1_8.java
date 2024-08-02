package me.florixak.uhcrevamp.versions;

import me.florixak.uhcrevamp.utils.NMSUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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
	public net.minecraft.server.v1_8_R3.ItemStack giveLapis(Player player, int amount) {
		EntityPlayer p = ((CraftPlayer) player).getHandle();
		net.minecraft.server.v1_8_R3.ItemStack lapisItem = new net.minecraft.server.v1_8_R3.ItemStack(Items.DYE, amount, (short) 4);
		p.inventory.pickup(lapisItem);
		return lapisItem;
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

	@Override
	public void openAnvil(Player player) {
		EntityPlayer p = ((CraftPlayer) player).getHandle();
		AnvilContainer container = new AnvilContainer(p);
		int c = p.nextContainerCounter();
		p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing", new Object[]{}), 0));
		p.activeContainer = container;
		p.activeContainer.windowId = c;
		p.activeContainer.addSlotListener(p);
	}

	public static class AnvilContainer extends ContainerAnvil {
		public AnvilContainer(EntityHuman entity) {
			super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
		}

		@Override
		public boolean a(EntityHuman entityhuman) {
			return true;
		}
	}
}
