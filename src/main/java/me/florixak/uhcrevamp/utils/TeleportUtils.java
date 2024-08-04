package me.florixak.uhcrevamp.utils;

import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Random;

public class TeleportUtils {

	private static final Random r;

	public static HashSet<Material> forbiddenBlocks = new HashSet<>();

	static {
		r = new Random();
		forbiddenBlocks.add(XMaterial.LAVA.parseMaterial());
		forbiddenBlocks.add(XMaterial.FIRE.parseMaterial());
		forbiddenBlocks.add(XMaterial.CACTUS.parseMaterial());
		forbiddenBlocks.add(XMaterial.WATER.parseMaterial());
		forbiddenBlocks.add(XMaterial.DIRT.parseMaterial());
		forbiddenBlocks.add(XMaterial.GRASS_BLOCK.parseMaterial());
		forbiddenBlocks.add(XMaterial.STONE.parseMaterial());
	}

	public static Location generateLocation() {

		final World world = Bukkit.getWorld(GameValues.WORLD_NAME);
		final double x = r.nextDouble() * (GameValues.BORDER.INIT_SIZE / 2 - 10);
		double y = 150.0;
		final double z = r.nextDouble() * (GameValues.BORDER.INIT_SIZE / 2 - 10);

		final Location randomLocation = new Location(world, x, y, z);
		y = randomLocation.getWorld().getHighestBlockYAt(randomLocation);
		randomLocation.setY(y);

		return randomLocation;
	}

	public static Location getSafeLocation() {

		Location randomLocation = generateLocation();

		while (!isLocationSafe(randomLocation)) {
			randomLocation = generateLocation();
		}
		return randomLocation;
	}

	public static boolean isLocationSafe(final Location location) {

		final int x = location.getBlockX();
		final int y = location.getBlockY();
		final int z = location.getBlockZ();

		final Block block = location.getWorld().getBlockAt(x, y, z);
		final Block below = location.getWorld().getBlockAt(x, y - 1, z);
		final Block above = location.getWorld().getBlockAt(x, y + 1, z);

		return !(forbiddenBlocks.contains(below.getType())) || (block.getType().isSolid()) || (above.getType().isSolid());
	}

}
