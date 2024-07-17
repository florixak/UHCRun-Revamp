package me.florixak.uhcrun.game.worldGenerator;

import me.florixak.uhcrun.UHCRun;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class CustomBlockPopulator extends org.bukkit.generator.BlockPopulator {

    private final static BlockFace[] OFFSETS = new BlockFace[]{
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH_EAST,
            BlockFace.NORTH_WEST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_WEST,
    };

    private final static Material[] ORES = new Material[]{
            Material.ANCIENT_DEBRIS,
            Material.DIAMOND_ORE,
            Material.GOLD_ORE,
            Material.IRON_ORE,
            Material.COAL_ORE,
            Material.LAPIS_ORE,
            Material.REDSTONE_ORE,
            Material.EMERALD_ORE
    };

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        int startX = random.nextInt(16) + (chunkX * 16);
        int startY = random.nextInt(30) + worldInfo.getMinHeight();
        int startZ = random.nextInt(16) + (chunkZ * 16);
        Location location = new Location(null, startX, startY, startZ);

        UHCRun.getInstance().getLogger().info("Block populator works!");

        if (!limitedRegion.isInRegion(location)) {
            return;
        }

        Material startMaterial = limitedRegion.getType(location);

        if (startMaterial != Material.STONE && startMaterial != Material.DEEPSLATE) {
            return;
        }

        Material oreType = ORES[random.nextInt(ORES.length)];
        int maxSize = random.nextInt(4) + 1; // Reduce the maximum size of the ore vein

        for (int size = 0; size < maxSize; size++) {
            if (limitedRegion.isInRegion(location)) {
                UHCRun.getInstance().getLogger().info("Setting " + oreType + " at: " + location.getX() + ", " + location.getY() + ", " + location.getZ());
                limitedRegion.setType(location, oreType);
            }

            BlockFace blockFace = OFFSETS[random.nextInt(OFFSETS.length)];
            location.add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());

            // Check if the new location is still within the region and is stone or deepslate
            if (!limitedRegion.isInRegion(location) ||
                    (limitedRegion.getType(location) != Material.STONE && limitedRegion.getType(location) != Material.DEEPSLATE)) {
                break;
            }
        }
    }
}