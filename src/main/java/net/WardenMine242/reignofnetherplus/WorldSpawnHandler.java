package net.WardenMine242.reignofnetherplus;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldSpawnHandler {

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {

        if (!(event.getLevel() instanceof ServerLevel level)) return;

        if (!level.dimension().equals(ServerLevel.OVERWORLD)) return;

        BlockPos spawn = findSpawn(level);

        if (spawn != null) {
            level.setDefaultSpawnPos(spawn, 0);
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
        if (level == null) return;

        BlockPos spawn = findSpawn(level);
        if (spawn != null) {
            level.setDefaultSpawnPos(spawn, 0);
        }
    }

    private static BlockPos findSpawn(ServerLevel level) {

        for (int i = 0; i < 5000; i++) {

            int x = level.random.nextInt(-2000, 2000);
            int z = level.random.nextInt(-2000, 2000);

            int y = level.getHeight(
                    Heightmap.Types.WORLD_SURFACE,
                    x,
                    z
            );

            BlockPos pos = new BlockPos(x, y, z);

            // ???????? ?? ???????
            if (!level.getBiome(pos).is(Biomes.PLAINS)) continue;

            // ????????? ??? ?????
            for (int dx = -50; dx <= 50; dx += 10) {
                for (int dz = -50; dz <= 50; dz += 10) {

                    BlockPos check = pos.offset(dx, 0, dz);

                    if (level.getBiome(check).is(Biomes.FOREST)
                            || level.getBiome(check).is(Biomes.BIRCH_FOREST)
                            || level.getBiome(check).is(Biomes.DARK_FOREST)) {

                        return pos;
                    }
                }
            }
        }

        return null;
    }
}
