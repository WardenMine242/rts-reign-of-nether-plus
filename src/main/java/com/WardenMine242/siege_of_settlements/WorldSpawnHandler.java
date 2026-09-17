package com.WardenMine242.siege_of_settlements;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class WorldSpawnHandler {

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {

        if (!(event.getLevel() instanceof ServerLevel level)) return;

        if (!level.dimension().equals(ServerLevel.OVERWORLD)) return;

        if (isValidSpawn(level, level.getSharedSpawnPos())) return;

        BlockPos spawn = findSpawn(level);

        if (spawn != null) {
            level.setDefaultSpawnPos(spawn, 0);
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
        if (level == null) return;

        if (isValidSpawn(level, level.getSharedSpawnPos())) return;

        BlockPos spawn = findSpawn(level);
        if (spawn != null) {
            level.setDefaultSpawnPos(spawn, 0);
        }
    }

    private static boolean isValidSpawn(ServerLevel level, BlockPos pos) {
        if (!level.getBiome(pos).is(Biomes.PLAINS)) return false;

        for (int dx = -50; dx <= 50; dx += 10) {
            for (int dz = -50; dz <= 50; dz += 10) {

                BlockPos check = pos.offset(dx, 0, dz);
                Holder<Biome> biome = level.getBiome(check);

                if (biome.is(Biomes.FOREST)
                        || biome.is(Biomes.BIRCH_FOREST)
                        || biome.is(Biomes.DARK_FOREST)) {

                    return true;
                }
            }
        }
        return false;
    }

    private static BlockPos findSpawn(ServerLevel level) {
        Random seededRandom = new Random(level.getSeed());

        for (int i = 0; i < 5000; i++) {

            int x = seededRandom.nextInt(8001) - 4000;
            int z = seededRandom.nextInt(8001) - 4000;

            int y = level.getHeight(
                    Heightmap.Types.WORLD_SURFACE,
                    x,
                    z
            );

            BlockPos pos = new BlockPos(x, y, z);

            if (isValidSpawn(level, pos)) {
                return pos;
            }
        }

        return null;
    }
}
