package me.florixak.uhcrun.utils;

import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OreGeneratorUtils {

    // https://www.spigotmc.org/threads/how-do-i-artificially-generate-ores-around-the-world.405941/

    public void generateOre(Material material, World world, int amount, int spawnAmount, int border) {
        Location loc;
        for (int i = 0; i < spawnAmount; i++) {
            loc = generateOreLocation(world, border);
            world.getBlockAt(loc).setType(material);
            generateVein(material, world.getBlockAt(loc), amount);
        }
    }

    private Location generateOreLocation(World world, int border_size) {
        Location loc;
        Random random = new Random();
        do {
            loc = new Location(world, random.nextInt(border_size), random.nextInt(55), random.nextInt(border_size));
        } while (loc.getBlock().getType().equals(XMaterial.WATER.parseMaterial()));
        return loc;
    }

    private void generateVein(final Material material, final Block startBlock, final int nbrBlocks) {
        final List<Block> blocks = this.getAdjacentsBlocks(startBlock, nbrBlocks);

        for (final Block block : blocks) {
            block.setType(material);
        }
    }

    private List<Block> getAdjacentsBlocks(final Block startBlock, final int nbrBlocks) {
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
