package me.florixak.uhcrun.utils;

import org.bukkit.block.BlockFace;

import java.util.Random;

public class RandomUtils {
    private static Random r;

    static {
        RandomUtils.r = new Random();
    }

    public static int randomInteger(final int min, final int max) {
        final int realMin = Math.min(min, max);
        final int realMax = Math.max(min, max);
        final int exclusiveSize = realMax - realMin;
        return RandomUtils.r.nextInt(exclusiveSize + 1) + min;
    }

    public static BlockFace randomAdjacentFace() {
        final BlockFace[] faces = { BlockFace.DOWN,BlockFace.DOWN,BlockFace.DOWN, BlockFace.UP,BlockFace.UP,BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH };
        return faces[randomInteger(0, faces.length - 1)];
    }
}
