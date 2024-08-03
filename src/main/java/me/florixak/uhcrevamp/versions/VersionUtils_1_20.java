package me.florixak.uhcrevamp.versions;

import me.florixak.uhcrevamp.UHCRevamp;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.EnumSet;
import java.util.Set;

public class VersionUtils_1_20 implements VersionUtils {

	@Override
	public Set<Material> getWoodPlankValues() {
		return EnumSet.of(Material.OAK_PLANKS,
				Material.SPRUCE_PLANKS,
				Material.BIRCH_PLANKS,
				Material.JUNGLE_PLANKS,
				Material.ACACIA_PLANKS,
				Material.DARK_OAK_PLANKS,
				Material.CRIMSON_PLANKS,
				Material.WARPED_PLANKS);
	}

	@Override
	public net.minecraft.server.v1_8_R3.ItemStack giveLapis(final Player player, final int amount) {
		return null;
	}

	@Override
	public ShapedRecipe createRecipe(final ItemStack item, final String key) {
		return new ShapedRecipe(new NamespacedKey(UHCRevamp.getInstance(), key), item);
	}

	@Override
	public void sendTitle(final Player player, final String title, final String subTitle, final int fadeIn, final int stay, final int fadeOut) {
		player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
	}

	@Override
	public void openAnvil(final Player player) {
		player.sendMessage("Anvil is not supported in this version. Coming soon!");
//		Inventory anvil = Bukkit.createInventory(player, InventoryType.ANVIL);
//		player.openInventory(anvil);
	}
}
