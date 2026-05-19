package com.solegendary.siege_of_settlements.building.buildings.placements;

import com.solegendary.siege_of_settlements.ability.EquipAbility;
import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingBlock;
import com.solegendary.siege_of_settlements.building.BuildingBlockData;
import com.solegendary.siege_of_settlements.building.RangeIndicator;
import com.solegendary.siege_of_settlements.building.buildings.villagers.Blacksmith;
import com.solegendary.siege_of_settlements.time.TimeClientEvents;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;

public class BlacksmithPlacement extends ProductionPlacement implements RangeIndicator {
    public static final int MIN_BLACKSMITH_TIER = 1;
    public static final int MAX_BLACKSMITH_TIER = 3;

    public EquipAbility autoCastEquip = null;
    private int blacksmithTier = MIN_BLACKSMITH_TIER;

    public BlacksmithPlacement(Building building, Level level, BlockPos originPos, Rotation rotation, String ownerName, ArrayList<BuildingBlock> blocks, boolean isCapitol) {
        super(building, level, originPos, rotation, ownerName, blocks, isCapitol);
    }

    public int getBlacksmithTier() {
        return blacksmithTier;
    }

    public void syncTierFromNetwork(int upgradeLevelIndex) {
        blacksmithTier = Mth.clamp(upgradeLevelIndex + 1, MIN_BLACKSMITH_TIER, MAX_BLACKSMITH_TIER);
    }

    public void incrementBlacksmithTierAfterProduction() {
        if (blacksmithTier >= MAX_BLACKSMITH_TIER) {
            return;
        }
        blacksmithTier++;
        applyStructureForCurrentTier();
    }

    public void applyStructureForCurrentTier() {
        changeStructure(Blacksmith.getStructureNameForTier(blacksmithTier));
    }

    @Override
    public void changeStructure(String newStructureName) {
        ArrayList<BuildingBlock> previousBlocks = new ArrayList<>(getBlocks());
        ArrayList<BuildingBlock> newBlocks = BuildingBlockData.getBuildingBlocksFromNbt(newStructureName, this.getLevel());
        setBlocks(getAbsoluteBlockData(newBlocks, this.getLevel(), originPos, rotation));
        blacksmithTier = Blacksmith.getTierForStructureName(newStructureName);

        // During world load tickAge is still 0; avoid queuing "missing" blocks
        // otherwise upgraded damaged buildings auto-repair on relog.
        if (!level.isClientSide) {
            if (tickAge > 0) {
                queueStructureUpgradeOnServer(previousBlocks);
            } else {
                blockPlaceQueue.clear();
                initPlacedBlocks();
            }
        } else {
            initPlacedBlocks();
        }
        updateButtons();
    }

    @Override
    public int getUpgradeLevel() {
        return blacksmithTier - 1;
    }

    @Override
    public String getUpgradedName() {
        String key = blacksmithTier >= 3
                ? "buildings.villagers.siege_of_settlements.blacksmith.tier3"
                : "buildings.villagers.siege_of_settlements.blacksmith.tier2";
        String translated = I18n.get(key);
        if (translated.equals(key)) {
            return I18n.get("buildings.villagers.siege_of_settlements.blacksmith") + " T" + blacksmithTier;
        }
        return translated;
    }

    @Override
    public void onBuilt() {
        super.onBuilt();
        updateBorderBps();
    }

    @Override
    public void tick(Level tickLevel) {
        super.tick(tickLevel);

        if (tickAgeAfterBuilt > 0 && tickAgeAfterBuilt % 15 == 0 && isBuilt && autoCastEquip != null
                && autoCastEquip.isOffCooldown(this)) {

            List<Mob> mobs = new ArrayList<>();
            for (Mob e : MiscUtil.getEntitiesWithinRange(new Vector3d(
                    this.centrePos.getX(),
                    this.centrePos.getY(),
                    this.centrePos.getZ()
                ),
                    autoCastEquip.range - 1,
                Mob.class,
                tickLevel
            )) {
                if ((autoCastEquip.isCorrectUnit(e) && autoCastEquip.canAfford(this)
                    && !autoCastEquip.hasItemInSlot(e)
                )) {
                    mobs.add(e);
                }
            }
            if (!mobs.isEmpty()) {
                autoCastEquip.use(tickLevel, this, mobs.get(0));
            }
        }
        if (tickLevel.isClientSide && tickAgeAfterBuilt > 0 && tickAgeAfterBuilt % 100 == 0)
            updateBorderBps();
    }

    private final Set<BlockPos> borderBps = new HashSet<>();

    @Override
    public void updateBorderBps() {
        if (!level.isClientSide())
            return;
        this.borderBps.clear();
        this.borderBps.addAll(MiscUtil.getRangeIndicatorCircleBlocks(centrePos,
                EquipAbility.RANGE - TimeClientEvents.VISIBLE_BORDER_ADJ, level));
    }

    @Override
    public Set<BlockPos> getBorderBps() {
        return borderBps;
    }

    @Override
    public boolean showOnlyWhenSelected() {
        return true;
    }

    private void queueStructureUpgradeOnServer(ArrayList<BuildingBlock> previousBlocks) {
        Map<BlockPos, BuildingBlock> newBlocksByPos = new HashMap<>();
        for (BuildingBlock block : getBlocks()) {
            newBlocksByPos.put(block.getBlockPos(), block);
        }

        Map<BlockPos, BuildingBlock> oldBlocksByPos = new HashMap<>();
        for (BuildingBlock oldBlock : previousBlocks) {
            oldBlocksByPos.put(oldBlock.getBlockPos(), oldBlock);
        }

        blockPlaceQueue.clear();

        for (BuildingBlock oldBlock : previousBlocks) {
            BlockPos pos = oldBlock.getBlockPos();
            BuildingBlock newBlock = newBlocksByPos.get(pos);
            if (newBlock == null || newBlock.getBlockState().isAir()) {
                if (!level.getBlockState(pos).isAir()) {
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }

        for (BuildingBlock newBlock : getBlocks()) {
            BlockPos pos = newBlock.getBlockPos();
            BlockState newState = newBlock.getBlockState();
            if (newState.isAir()) {
                continue;
            }
            boolean changedFromOldStructure = !oldBlocksByPos.containsKey(pos)
                    || !oldBlocksByPos.get(pos).getBlockState().equals(newState);
            boolean differsFromWorld = !level.getBlockState(pos).equals(newState);
            if (changedFromOldStructure || differsFromWorld) {
                addToBlockPlaceQueue(newBlock);
            }
        }
        initPlacedBlocks();
    }
}
