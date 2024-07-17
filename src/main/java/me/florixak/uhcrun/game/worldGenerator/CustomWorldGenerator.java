package me.florixak.uhcrun.game.worldGenerator;

import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class CustomWorldGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);

        // Get a random existing chunk.
        int highestBlockY = world.getHighestBlockYAt(x, z);

        for (int i = 0; i < 16; i++) {
            for (int k = 0; k < 16; k++) {
                chunk.setBlock(i, random.nextInt(highestBlockY), k, XMaterial.DIAMOND_BLOCK.parseMaterial());
            }
        }

        return chunk;
    }

    @Override
    public boolean shouldGenerateNoise() {
        return true;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return true;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return true;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return true;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return true;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return true;
    }
}
