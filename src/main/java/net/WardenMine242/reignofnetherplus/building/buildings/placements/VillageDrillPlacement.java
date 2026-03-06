package net.WardenMine242.reignofnetherplus.building.buildings.placements;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.Building;
import com.solegendary.reignofnether.building.BuildingBlock;
import com.solegendary.reignofnether.building.buildings.placements.ProductionPlacement;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;

public class VillageDrillPlacement extends ProductionPlacement {

    // 0..2 (0 = базовый, 1 = уровень 2 в игре, 2 = уровень 3 в игре)
    private int drillLevel = 0;

    private final int MAX_LEVEL = 2; // максимум = 2 (в игре это третий уровень)

    public VillageDrillPlacement(Building building,
                                 Level level,
                                 BlockPos originPos,
                                 Rotation rotation,
                                 String ownerName,
                                 ArrayList<BuildingBlock> blocks,
                                 boolean isCapitol) {
        super(building, level, originPos, rotation, ownerName, blocks, isCapitol);
    }

    private void syncLevelFromStructure() {
        // ищем признаки апгрейдов в блоках структуры (сначала самые "высокие")
        for (BuildingBlock block : this.getBlocks()) {
            Block b = block.getBlockState().getBlock();
            if (b == Blocks.CHISELED_STONE_BRICKS || b == Blocks.STONE_BRICKS) {
                drillLevel = 2; // третий уровень в игре (индекс 2)
                return;
            }
            if (b == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                drillLevel = 1; // второй уровень в игре (индекс 1)
                return;
            }
            if (b == Blocks.LEVER) {
                drillLevel = 1; // если логика оставляет lever как 1 — можно подкорректировать
                return;
            }
        }
        drillLevel = 0;
    }

    public void upgrade() {
        if (drillLevel < MAX_LEVEL)
            upgradeToLevel(drillLevel + 1); // безопасно: 0->1, 1->2
    }

    public void upgradeToLevel(int level) {
        String structureName = getStructureNameForLevel(level);
        if (structureName != null) {
            changeStructure(structureName);
        }
    }

    private String getStructureNameForLevel(int level) {
        switch (level) {
            case 1: return net.WardenMine242.reignofnetherplus.building.buildings.villagers.VillagerDrill.structureNameT1;
            case 2: return net.WardenMine242.reignofnetherplus.building.buildings.villagers.VillagerDrill.structureNameT2; // новый третий шаблон (индекс 2)
            default: return net.WardenMine242.reignofnetherplus.building.buildings.villagers.VillagerDrill.structureName;
        }
    }

    @Override
    public int getUpgradeLevel() {
        // проверяем блоки структуры
        for (BuildingBlock bb : this.getBlocks()) {
            var ideal = bb.getBlockState().getBlock();
            if (ideal == Blocks.CHISELED_STONE_BRICKS || ideal == Blocks.STONE_BRICKS) return 2;
            if (ideal == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) return 1;
            if (ideal == Blocks.LEVER) return 1;

            BlockPos worldPos = bb.getBlockPos();
            var b = this.level.getBlockState(worldPos).getBlock();
            if (b == Blocks.CHISELED_STONE_BRICKS || b == Blocks.STONE_BRICKS) return 2;
            if (b == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) return 1;
            if (b == Blocks.LEVER) return 1;
        }

        // проверка в радиусе originPos (как раньше)
        int r = 6;
        for (BlockPos pos : BlockPos.betweenClosed(originPos.offset(-r, -1, -r), originPos.offset(r, 1, r))) {
            var b = this.level.getBlockState(pos).getBlock();
            if (b == Blocks.CHISELED_STONE_BRICKS || b == Blocks.STONE_BRICKS) return 2;
            if (b == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) return 1;
            if (b == Blocks.LEVER) return 1;
        }

        return 0;
    }

    @Override
    public String getUpgradedName() {
        switch (getUpgradeLevel()) {
            case 1: return I18n.get("buildings.reignofnetherplus.drill_t2"); // существующее имя (игровой второй)
            case 2: return I18n.get("buildings.reignofnetherplus.drill_t3"); // новый ключ для "третий" уровня (игровой третий)
            default: return super.getUpgradedName();
        }
    }

    @Override
    public boolean canDestroyBlock(BlockPos relativeBp) {
        BlockPos worldBp = relativeBp.offset(this.originPos);
        return this.getLevel().getBlockState(worldBp).getBlock() != Blocks.BEACON
                && this.getLevel().getBlockState(worldBp.above()).getBlock() != Blocks.BEACON;
    }

    public void changeDrillStructure(int structureLevel) {
        String newStructureName = switch (structureLevel) {
            case 1 -> net.WardenMine242.reignofnetherplus.building.buildings.villagers.VillagerDrill.structureNameT1;
            case 2 -> net.WardenMine242.reignofnetherplus.building.buildings.villagers.VillagerDrill.structureNameT2;
            default -> net.WardenMine242.reignofnetherplus.building.buildings.villagers.VillagerDrill.structureName;
        };

        ReignOfNether.LOGGER.info("VillageDrillPlacement.changeDrillStructure: switching to " + newStructureName + " at " + originPos);

        changeStructure(newStructureName);
    }

    @Override
    public void destroy(ServerLevel serverLevel) {
        super.destroy(serverLevel);
        cleanupFloatingBlocks(serverLevel);
    }

    private void cleanupFloatingBlocks(ServerLevel level) {
        BlockPos min = originPos;
        BlockPos max = originPos.offset(8, 9, 8); // 9x9

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            if (block == Blocks.OAK_FENCE
                    || block == Blocks.LANTERN
                    || block == Blocks.IRON_TRAPDOOR) {

                level.destroyBlock(pos, false);
            }
        }
    }
}