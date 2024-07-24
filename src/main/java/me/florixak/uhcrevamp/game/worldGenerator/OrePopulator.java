package me.florixak.uhcrevamp.game.worldGenerator;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.utils.RandomUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import java.util.List;
import java.util.Random;

public class OrePopulator extends BlockPopulator {

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
    private final LimitedRegion limitedRegion;

    public OrePopulator(LimitedRegion limitedRegion) {
        this.limitedRegion = limitedRegion;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {

        if (world == null) {
            UHCRevamp.getInstance().getLogger().info("World does not exist!");
            return;
        }

        if (RandomUtils.getRandom().nextInt(100) < 70) return;

        int startX = random.nextInt(16) + (chunk.getX() * 16);
        int startZ = random.nextInt(16) + (chunk.getZ() * 16);
        int startY = random.nextInt(55);

        Location location = new Location(world, startX, startY, startZ);

        if (isOutsideBorder(location, BORDER_SIZE)) {
            //UHCRun.getInstance().getLogger().info("Location is outside of border: " + location.getX() + ", " + location.getY() + ", " + location.getZ());
            return;
        }

        if (!limitedRegion.isInRegion(location)) return;

        Material startMaterial = world.getBlockAt(location).getType();

        if (startMaterial != XMaterial.STONE.parseMaterial() && startMaterial != XMaterial.DEEPSLATE.parseMaterial()) {
            return;
        }

        OreGenerator oreGen = ORE_GEN_LIST.get(random.nextInt(ORE_GEN_LIST.size()));
        Material oreType = oreGen.getMaterial();
        int maxSize = RandomUtils.randomInteger(oreGen.getMinVein(), oreGen.getMaxVein()); // Reduce the maximum size of the ore vein
        for (int size = 0; size < maxSize; size++) {
            if (limitedRegion.isInRegion(location)) {
                world.getBlockAt(location).setType(oreType);
            }

            BlockFace blockFace = OFFSETS[random.nextInt(OFFSETS.length)];
            location.add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());

            if (!limitedRegion.isInRegion(location) ||
                    (world.getBlockAt(location).getType() != XMaterial.STONE.parseMaterial() && world.getBlockAt(location).getType() != XMaterial.DEEPSLATE.parseMaterial())) {
                break;
            }
        }

        populationCount++; // Increment the population count
        UHCRevamp.getInstance().getLogger().info(populationCount + ". Setting " + oreType + " at: " + location.getX() + ", " + location.getY() + ", " + location.getZ());

    }

    public boolean isOutsideBorder(Location location, int borderSize) {
        return Math.abs(location.getX()) > borderSize / 2 || Math.abs(location.getZ()) > borderSize / 2;
    }
}