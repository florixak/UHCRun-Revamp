package me.florixak.uhcrun.game.worldGenerator;

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

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        int startX = random.nextInt(16) + (chunkX * 16);
        int startY = random.nextInt(30) + worldInfo.getMinHeight();
        int startZ = random.nextInt(16) + (chunkZ * 16);
        Location location = new Location(null, startX, startY, startZ);

        if (!limitedRegion.isInRegion(location)) {
            return;
        }

        Material startMaterial = limitedRegion.getType(location);

        if (startMaterial != Material.STONE && startMaterial != Material.DEEPSLATE) {
            return;
        }

        for (int seize = random.nextInt(15); seize > 0; seize--) {
            if (limitedRegion.isInRegion(location)) {
                limitedRegion.setType(location, Material.ANCIENT_DEBRIS);
            }

            BlockFace blockFace = OFFSETS[random.nextInt(OFFSETS.length)];
            location.add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
        }
    }
}