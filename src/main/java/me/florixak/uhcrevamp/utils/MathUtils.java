package me.florixak.uhcrevamp.utils;

import org.bukkit.block.BlockFace;

import java.util.Random;

public class MathUtils {
	private static Random r;

	static {
		MathUtils.r = new Random();
	}

	public static Random getRandom() {
		return r;
	}

	public static int randomInteger(final int min, final int max) {
		final int realMin = Math.min(min, max);
		final int realMax = Math.max(min, max);
		final int exclusiveSize = realMax - realMin;
		return MathUtils.r.nextInt(exclusiveSize + 1) + min;
	}

	public static double randomDouble(final double min, final double max) {
		final double realMin = Math.min(min, max);
		final double realMax = Math.max(min, max);
		final double exclusiveSize = realMax - realMin;
		return MathUtils.r.nextDouble(exclusiveSize + 1) + min;
	}

	public static double randomFloat(final float min, final float max) {
		final float realMin = Math.min(min, max);
		final float realMax = Math.max(min, max);
		final float exclusiveSize = realMax - realMin;
		return MathUtils.r.nextFloat(exclusiveSize + 1) + min;
	}

	public static BlockFace randomAdjacentFace() {
		final BlockFace[] faces = {BlockFace.DOWN, BlockFace.DOWN, BlockFace.DOWN, BlockFace.UP, BlockFace.UP, BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
		return faces[randomInteger(0, faces.length - 1)];
	}


}
