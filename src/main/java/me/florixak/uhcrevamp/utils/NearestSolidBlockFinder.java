package me.florixak.uhcrevamp.utils;

import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class NearestSolidBlockFinder {

    private static final Vector[] DIRECTIONS = {
            new Vector(1, 0, 0), new Vector(-1, 0, 0),
            new Vector(0, 1, 0), new Vector(0, -1, 0),
            new Vector(0, 0, 1), new Vector(0, 0, -1)
    };

    public static Block findNearestSolidBlock(Block startBlock) {
        if (startBlock == null) return null;

        Queue<Block> queue = new LinkedList<>();
        Set<Block> visited = new HashSet<>();

        queue.add(startBlock);
        visited.add(startBlock);

        while (!queue.isEmpty()) {
            Block currentBlock = queue.poll();

            if (isSolid(currentBlock)) {
                return currentBlock;
            }

            for (Vector direction : DIRECTIONS) {
                Block adjacentBlock = currentBlock.getRelative(direction.getBlockX(), direction.getBlockY(), direction.getBlockZ());
                if (!visited.contains(adjacentBlock)) {
                    queue.add(adjacentBlock);
                    visited.add(adjacentBlock);
                }
            }
        }

        return null; // No solid block found
    }

    private static boolean isSolid(Block block) {
        Material type = block.getType();
        return type.isSolid() && type != XMaterial.WATER.parseMaterial() && type != XMaterial.AIR.parseMaterial() && type != XMaterial.CAVE_AIR.parseMaterial() && type != XMaterial.VOID_AIR.parseMaterial();
    }
}