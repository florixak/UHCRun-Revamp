package me.florixak.uhcrun.game.worldGenerator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class CustomWorldGenerator extends ChunkGenerator {


    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);

        // Decide on the number of veins and their sizes for this chunk
        int numberOfVeins = 1 + random.nextInt(3); // For example, 1 to 3 veins per chunk

        for (int vein = 0; vein < numberOfVeins; vein++) {
            int veinSize = 4 + random.nextInt(4); // Vein size between 4 and 7
            int startY = 5; // Minimum Y level for ores
            int endY = 40; // Maximum Y level for ores

            // Calculate the starting position of the vein
            int y = startY + random.nextInt(endY - startY);
            int rx = random.nextInt(16);
            int rz = random.nextInt(16);

            for (int i = 0; i < veinSize; i++) {
                // Place the ore blocks relative to the starting position
                // Check if the target position is valid for ore placement
                if (chunk.getType(rx, y, rz) == Material.STONE) {
                    chunk.setBlock(rx, y, rz, Material.DIAMOND_ORE);
                }

                // Adjust rx, rz, and y for the next block in the vein
                rx += random.nextInt(3) - 1; // Move -1 to 1 blocks on the x-axis
                rz += random.nextInt(3) - 1; // Move -1 to 1 blocks on the z-axis
                y += random.nextInt(3) - 1; // Move -1 to 1 blocks on the y-axis

                // Ensure the coordinates are within the chunk and valid Y level
                rx = Math.floorMod(rx, 16);
                rz = Math.floorMod(rz, 16);
                y = Math.max(startY, Math.min(y, endY));

                System.out.println("Generated ore at: " + rx + ", " + y + ", " + rz);
            }
        }

        return chunk;
    }
}
