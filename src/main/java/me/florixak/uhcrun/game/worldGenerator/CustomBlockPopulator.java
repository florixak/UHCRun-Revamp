package me.florixak.uhcrun.game.worldGenerator;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.List;
import java.util.Random;

public class CustomBlockPopulator extends BlockPopulator {

    private final static BlockFace[] OFFSETS = {
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

    private final static List<OreGenerator> ORE_GEN_LIST = GameManager.getGameManager().getOreGenManager().getOreGeneratorList();
    private static int populationCount = 0; // Counter to track the number of times populate has been called
    private final static int BORDER_SIZE = 450; // Size of the border within which to populate

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        World world = Bukkit.getWorld(GameValues.WORLD_NAME);

        if (world == null) {
            UHCRun.getInstance().getLogger().info("World does not exist!");
            return;
        }

        int startX = random.nextInt(16) + (chunkX * 16);
        int startZ = random.nextInt(16) + (chunkZ * 16);
        int startY = random.nextInt(55);

        Location location = new Location(world, startX, startY, startZ);

        if (!limitedRegion.isInRegion(location)) return;

        if (isOutsideBorder(location, BORDER_SIZE)) {
            UHCRun.getInstance().getLogger().info("Location is outside of border: " + location.getX() + ", " + location.getY() + ", " + location.getZ());
            return;
        }

        Material startMaterial = limitedRegion.getType(location);

        if (startMaterial != Material.STONE && startMaterial != Material.DEEPSLATE) {
            return;
        }

        OreGenerator oreGen = ORE_GEN_LIST.get(random.nextInt(ORE_GEN_LIST.size()));
        Material oreType = oreGen.getMaterial();
        int maxSize = RandomUtils.randomInteger(oreGen.getMinVein(), oreGen.getMaxVein()); // Reduce the maximum size of the ore vein
        for (int size = 0; size < maxSize; size++) {
            if (limitedRegion.isInRegion(location)) {
                limitedRegion.setType(location, oreType);
            }

            BlockFace blockFace = OFFSETS[random.nextInt(OFFSETS.length)];
            location.add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());

            if (!limitedRegion.isInRegion(location) ||
                    (limitedRegion.getType(location) != Material.STONE && limitedRegion.getType(location) != Material.DEEPSLATE)) {
                break;
            }
        }

        populationCount++; // Increment the population count
        UHCRun.getInstance().getLogger().info(populationCount + ". Setting " + oreType + " at: " + location.getX() + ", " + location.getY() + ", " + location.getZ());

    }

    public boolean isOutsideBorder(Location location, int borderSize) {
        return Math.abs(location.getX()) > borderSize / 2 || Math.abs(location.getZ()) > borderSize / 2;
    }
}