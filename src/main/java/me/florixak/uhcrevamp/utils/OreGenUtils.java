package me.florixak.uhcrevamp.utils;

import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class OreGenUtils {

    // https://www.spigotmc.org/threads/how-do-i-artificially-generate-ores-around-the-world.405941/

    private static HashSet<Material> badBlocks = new HashSet<>();

    static {
        badBlocks.add(XMaterial.WATER.parseMaterial());
        badBlocks.add(XMaterial.LAVA.parseMaterial());
        badBlocks.add(XMaterial.AIR.parseMaterial());
        badBlocks.add(XMaterial.BEDROCK.parseMaterial());
        badBlocks.add(XMaterial.GRASS_BLOCK.parseMaterial());
        badBlocks.add(XMaterial.DIRT.parseMaterial());
    }

    public static void generateOre(Material material, World world, int minVein, int maxVein, int spawnAmount, int borderSize) {
        Location loc;
        for (int i = 0; i < spawnAmount; i++) {
            loc = getOreLocation(world, borderSize);
            world.getBlockAt(loc).setType(material);
            generateVein(material, world.getBlockAt(loc), RandomUtils.randomInteger(minVein, maxVein));
            //UHCRun.getInstance().getLogger().info("OreGenUtils - " + material.name() + " at: X: " + loc.getX() + ", Y: " + loc.getY() + ", Z: " + loc.getZ());
        }
    }

    private static Location getOreLocation(World world, int borderSize) {
        Location loc;
        do {
            loc = new Location(world, RandomUtils.randomInteger(0, borderSize), RandomUtils.randomInteger(0, 60), RandomUtils.randomInteger(0, borderSize));
        } while (!isLocationSafe(loc));
        return loc;
    }

    private static boolean isLocationSafe(Location location) {

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Block block = location.getWorld().getBlockAt(x, y, z);

        return !badBlocks.contains(block.getType()) && block.getType().isSolid();
    }

    public static void generateVein(final Material material, final Block startBlock, final int nbrBlocks) {
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
            if ((blockLocation.getBlockY() <= 1 && face.equals((Object) BlockFace.DOWN)) || (blockLocation.getBlockY() >= 255 && face.equals((Object) BlockFace.UP))) {
                ++failedAttempts;
            } else {
                final Block adjacent = block.getRelative(face);
                if (adjacentBlocks.contains(adjacent) /*|| !adjacent.getType().equals((Object)Material.STONE)*/) {
                    ++failedAttempts;
                } else {
                    adjacentBlocks.add(adjacent);
                }
            }
        }
        return adjacentBlocks;
    }
}
