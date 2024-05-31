package me.florixak.uhcrun.utils;

import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class OreGenUtils {

    // https://www.spigotmc.org/threads/how-do-i-artificially-generate-ores-around-the-world.405941/

    private static HashSet<Material> bad_blocks = new HashSet<>();

    static {
        bad_blocks.add(XMaterial.WATER.parseMaterial());
        bad_blocks.add(XMaterial.LAVA.parseMaterial());
        bad_blocks.add(XMaterial.AIR.parseMaterial());
        bad_blocks.add(XMaterial.BEDROCK.parseMaterial());
        bad_blocks.add(XMaterial.GRASS_BLOCK.parseMaterial());
        bad_blocks.add(XMaterial.DIRT.parseMaterial());
    }

    public static void generateOre(Material material, World world, int minVein, int maxVein, int spawnAmount, int borderSize) {
        Location loc;
        for (int i = 0; i < spawnAmount; i++) {
            loc = getOreLocation(world, borderSize);
            world.getBlockAt(loc).setType(material);
            generateVein(material, world.getBlockAt(loc), minVein + (int)(Math.random() * (maxVein-minVein+1)));
        }
    }

    private static Location getOreLocation(World world, int borderSize) {
        Location loc;
        Random ran = RandomUtils.getRandom();

        do {
            loc = new Location(world, ran.nextInt(borderSize), RandomUtils.randomInteger(-64, 60), ran.nextInt(borderSize));
        } while (!isLocationSafe(loc));

        return loc;
    }

    private static boolean isLocationSafe(Location location){

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Block block = location.getWorld().getBlockAt(x, y, z);
        Block below = location.getWorld().getBlockAt(x, y - 1, z);
        Block above = location.getWorld().getBlockAt(x, y + 1, z);

        return !bad_blocks.contains(below.getType()) && block.getType().isSolid() && above.getType().isSolid();
    }

    private static void generateVein(final Material material, final Block startBlock, final int nbrBlocks) {
        final List<Block> blocks = getAdjacentsBlocks(startBlock, nbrBlocks);

        for (final Block block : blocks) {
            block.setType(material);
        }
    }

    private static List<Block> getAdjacentsBlocks(final Block startBlock, final int nbrBlocks) {
        int failedAttempts = 0;
        final List<Block> adjacentBlocks = new ArrayList<>();
        adjacentBlocks.add(startBlock);
        while (adjacentBlocks.size() < nbrBlocks && failedAttempts < 25) {
            final Block block = adjacentBlocks.get(RandomUtils.randomInteger(0, adjacentBlocks.size() - 1));
            final BlockFace face = RandomUtils.randomAdjacentFace();
            final Location blockLocation = block.getLocation();
            if ((blockLocation.getBlockY() <= 1 && face.equals((Object)BlockFace.DOWN)) || (blockLocation.getBlockY() >= 255 && face.equals((Object)BlockFace.UP))) {
                ++failedAttempts;
            }
            else {
                final Block adjacent = block.getRelative(face);
                if (adjacentBlocks.contains(adjacent) /*|| !adjacent.getType().equals((Object)Material.STONE)*/) {
                    ++failedAttempts;
                }
                else {
                    adjacentBlocks.add(adjacent);
                }
            }
        }
        return adjacentBlocks;
    }
}
