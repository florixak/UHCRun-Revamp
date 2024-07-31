package me.florixak.uhcrevamp.game.worldGenerator;

import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.XSeries.XBiome;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldGeneratorListener implements Listener {

	public WorldGeneratorListener() {
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		World world = event.getWorld();
		if (!world.getName().equalsIgnoreCase(GameValues.WORLD_NAME)) return;
		try {
			for (Chunk chunk : world.getLoadedChunks()) {
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						XBiome xBiome = XBiome.matchXBiome(chunk.getBlock(x, world.getHighestBlockYAt(x, z), z).getBiome());
						if (xBiome.isSupported()) {
							Biome biome = xBiome.getBiome();
							if (GameValues.GAME.DISABLED_BIOMES.contains(biome.name())) {
								for (int y = 0; y < world.getMaxHeight(); y++) {
									chunk.getBlock(x, y, z).setBiome(Biome.PLAINS);
								}
							}
						}
					}
				}
			}
		} catch (Exception ignore) {
			Bukkit.getLogger().info("Failed to replace biomes.");
		}

	}
}