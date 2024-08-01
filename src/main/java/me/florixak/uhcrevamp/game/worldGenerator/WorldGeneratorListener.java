package me.florixak.uhcrevamp.game.worldGenerator;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.XSeries.XBiome;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldGeneratorListener implements Listener {

	private GameManager gameManager;

	public WorldGeneratorListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		World world = event.getWorld();
		if (!world.getName().equalsIgnoreCase(GameValues.WORLD_NAME)) return;
		try {
			if (isOceanBiomeFound(world)) {
				Bukkit.getLogger().info("Ocean biome found. Creating new world...");
				Bukkit.unloadWorld(world, false);
				gameManager.getWorldManager().createNewUHCWorld();
			}
		} catch (Exception ignore) {
			Bukkit.getLogger().info("Failed to create new world.");
		}

	}

	private boolean isOceanBiomeFound(World world) {
		for (Chunk chunk : world.getLoadedChunks()) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					XBiome xBiome = XBiome.matchXBiome(chunk.getBlock(x, world.getHighestBlockYAt(x, z), z).getBiome());
					if (xBiome.isSupported() && GameValues.GAME.DISABLED_BIOMES.contains(xBiome.getBiome().name())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}