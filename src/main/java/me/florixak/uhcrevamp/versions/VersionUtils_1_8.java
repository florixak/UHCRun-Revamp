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
	public net.minecraft.server.v1_8_R3.ItemStack giveLapis(final Player player, final int amount) {
		final EntityPlayer p = ((CraftPlayer) player).getHandle();
		final net.minecraft.server.v1_8_R3.ItemStack lapisItem = new net.minecraft.server.v1_8_R3.ItemStack(Items.DYE, amount, (short) 4);
		p.inventory.pickup(lapisItem);
		return lapisItem;
	}

	@Override
	public ShapedRecipe createRecipe(final ItemStack item, final String key) {
		return new ShapedRecipe(item);
	}

	@Override
	public void sendTitle(final Player player, final String title, final String subTitle, final int fadeIn, final int stay, final int fadeOut) {
		try {
			final Object handle = player.getClass().getMethod("getHandle").invoke(player);
			final Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

			final Class<?> packetPlayOutTitleClass = NMSUtils.getNMSClass("PacketPlayOutTitle");
			final Class<?> packetClass = NMSUtils.getNMSClass("Packet");
			final Class<?> chatComponentTextClass = NMSUtils.getNMSClass("ChatComponentText");
			final Class<?> iChatBaseComponentClass = NMSUtils.getNMSClass("IChatBaseComponent");
			final Class<?> enumTitleActionClass = packetPlayOutTitleClass.getDeclaredClasses()[0];

			final Constructor<?> chatComponentTextConstructor = chatComponentTextClass.getConstructor(String.class);
			final Object titleComponent = chatComponentTextConstructor.newInstance(title);
			final Object subTitleComponent = chatComponentTextConstructor.newInstance(subTitle);

			final Method valueOf = enumTitleActionClass.getMethod("valueOf", String.class);
			final Object titleEnum = valueOf.invoke(null, "TITLE");
			final Object subTitleEnum = valueOf.invoke(null, "SUBTITLE");

			final Constructor<?> packetPlayOutTitleConstructor = packetPlayOutTitleClass.getConstructor(enumTitleActionClass, iChatBaseComponentClass, int.class, int.class, int.class);
			final Object titlePacket = packetPlayOutTitleConstructor.newInstance(titleEnum, titleComponent, fadeIn, stay, fadeOut);
			final Object subTitlePacket = packetPlayOutTitleConstructor.newInstance(subTitleEnum, subTitleComponent, fadeIn, stay, fadeOut);

			NMSUtils.sendPacket(playerConnection, packetClass, titlePacket);
			NMSUtils.sendPacket(playerConnection, packetClass, subTitlePacket);
		} catch (final Exception e) {
			Bukkit.getLogger().info("Failed to send title.");
		}
	}

	@Override
	public void openAnvil(final Player player) {
		final EntityPlayer p = ((CraftPlayer) player).getHandle();
		final AnvilContainer container = new AnvilContainer(p);
		final int c = p.nextContainerCounter();
		p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing", new Object[]{}), 0));
		p.activeContainer = container;
		p.activeContainer.windowId = c;
		p.activeContainer.addSlotListener(p);
	}

	public static class AnvilContainer extends ContainerAnvil {
		public AnvilContainer(final EntityHuman entity) {
			super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
		}

		@Override
		public boolean a(final EntityHuman entityhuman) {
			return true;
		}
	}
}
