package me.florixak.uhcrevamp.game.worldGenerator;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.MathUtils;
import me.florixak.uhcrevamp.utils.XSeries.XBiome;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.ArrayList;
import java.util.List;

public class OreGeneratorListener implements Listener {

	private final String targetWorldName;
	private final int maxOresPerChunk;
	private final List<GeneratedOre> oresList;
	private final List<String> notSpawnIn;

	public OreGeneratorListener(GameManager gameManager) {
		FileConfiguration oreGenConfig = gameManager.getConfigManager().getFile(ConfigType.ORE_GENERATION).getConfig();
		this.targetWorldName = GameValues.WORLD_NAME;
		this.maxOresPerChunk = GameValues.GAME.MAX_ORE_PER_CHUNK;
		this.oresList = gameManager.getOreGenManager().getOreGeneratorList();
		this.notSpawnIn = oreGenConfig.getStringList("not-generate-in");
	}

	private void generateVein(final Material material, final Block startBlock, final int nbrBlocks) {
		if (notSpawnIn.contains(material.name())) return;
		final List<Block> blocks = getAdjacentsBlocks(startBlock, nbrBlocks);

		for (final Block block : blocks) {
			block.setType(material);
		}
	}

	private List<Block> getAdjacentsBlocks(final Block startBlock, final int nbrBlocks) {
		int failedAttempts = 0;
		final List<Block> adjacentBlocks = new ArrayList<>();
		adjacentBlocks.add(startBlock);
		while (adjacentBlocks.size() < nbrBlocks && failedAttempts < 25) {
			final Block block = adjacentBlocks.get(MathUtils.randomInteger(0, adjacentBlocks.size() - 1));
			final BlockFace face = MathUtils.randomAdjacentFace();
			final Location blockLocation = block.getLocation();
			if ((blockLocation.getBlockY() <= 1 && face.equals(BlockFace.DOWN)) || (blockLocation.getBlockY() >= 255 && face.equals(BlockFace.UP))) {
				++failedAttempts;
			} else {
				final Block adjacent = block.getRelative(face);
				if (adjacentBlocks.contains(adjacent)) {
					++failedAttempts;
				} else {
					adjacentBlocks.add(adjacent);
				}
			}
		}
		return adjacentBlocks;
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		World world = event.getWorld();
		if (!world.getName().equalsIgnoreCase(targetWorldName)) {
			return;
		}
//        Bukkit.getLogger().info("Not Generate In: " + notSpawnIn.toString());
		Bukkit.getLogger().info("World " + targetWorldName + " loaded. Generating ores...");
		for (Chunk chunk : world.getLoadedChunks()) {
			for (int i = 0; i < maxOresPerChunk; i++) {
				int x = MathUtils.getRandom().nextInt(16);
				int z = MathUtils.getRandom().nextInt(16);
				int highestY = chunk.getBlock(x, 0, z).getWorld().getHighestBlockYAt(chunk.getBlock(x, 0, z).getLocation());
				int y = MathUtils.getRandom().nextInt(highestY - 10); // Ensure ores are generated below the highest block
				Block startBlock = chunk.getBlock(x, y, z);
				if (!notSpawnIn.contains(startBlock.getType().name())
						&& !startBlock.getBiome().equals(XBiome.OCEAN.getBiome())
						&& !startBlock.getBiome().equals(XBiome.DEEP_OCEAN.getBiome())) {
					GeneratedOre generatedOre = oresList.get(MathUtils.getRandom().nextInt(oresList.size()));
					generateVein(generatedOre.getMaterial(), startBlock, MathUtils.randomInteger(generatedOre.getMinVein(), generatedOre.getMaxVein())); // Generate a vein of ores
				}
			}
		}
		Bukkit.getLogger().info("Ores generated in world " + targetWorldName);
	}
}
