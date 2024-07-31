package me.florixak.uhcrevamp.versions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Set;

public interface VersionUtils {

	Set<Material> getWoodPlankValues();

	ItemStack getLapis();

	ShapedRecipe createRecipe(ItemStack item, String key);

	void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut);

}
