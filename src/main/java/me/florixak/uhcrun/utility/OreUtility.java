package me.florixak.uhcrun.utility;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class OreUtility {

    // https://www.spigotmc.org/threads/how-do-i-artificially-generate-ores-around-the-world.405941/

    public void generateVein(final Material material, final Block startBlock, final int nbrBlocks) {
        final List<Block> blocks = this.getAdjacentsBlocks(startBlock, nbrBlocks);

        for (final Block block : blocks) {
            block.setType(material);
        }
    }

    public List<Block> getAdjacentsBlocks(final Block startBlock, final int nbrBlocks) {
        int failedAttempts = 0;
        final List<Block> adjacentBlocks = new ArrayList<Block>();
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
