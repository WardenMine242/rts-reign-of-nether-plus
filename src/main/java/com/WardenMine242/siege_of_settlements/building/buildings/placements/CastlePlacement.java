package com.WardenMine242.siege_of_settlements.building.buildings.placements;

import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingBlock;
import com.solegendary.siege_of_settlements.building.BuildingBlockData;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.building.GarrisonableBuilding;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.building.production.StartProductionButton;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CastlePlacement extends com.solegendary.siege_of_settlements.building.buildings.placements.CastlePlacement {
    private static final int BASE_SLOT_CAPACITY = MAX_OCCUPANTS;
    private static final int EXTRA_SLOT_CAPACITY = 4;
    private static final BlockPos[] ENTRY_OFFSETS = new BlockPos[] {
            new BlockPos(5, 16, 5),
            new BlockPos(2, 11, 2),
            new BlockPos(8, 11, 8)
    };
    private final Map<UUID, Integer> reservedEntrySlots = new HashMap<>();

    public CastlePlacement(Building building, Level level, BlockPos originPos, Rotation rotation, String ownerName, ArrayList<BuildingBlock> blocks, boolean isCapitol) {
        super(building, level, originPos, rotation, ownerName, sanitizeCastleBlocks(blocks), isCapitol);
        clearVegetationAtAirSlots(this.getBlocks());
        filterTierButtons();
    }

    public static boolean isCastleVegetation(BlockState blockState) {
        return blockState.is(Blocks.GRASS) ||
                blockState.is(Blocks.TALL_GRASS) ||
                blockState.is(Blocks.FERN) ||
                blockState.is(Blocks.LARGE_FERN);
    }

    private static ArrayList<BuildingBlock> sanitizeCastleBlocks(ArrayList<BuildingBlock> blocks) {
        if (blocks == null) {
            return new ArrayList<>();
        }
        for (BuildingBlock block : blocks) {
            if (isCastleVegetation(block.getBlockState())) {
                block.setBlockState(Blocks.AIR.defaultBlockState());
            }
        }
        return blocks;
    }

    private void clearVegetationAtAirSlots(ArrayList<BuildingBlock> structureBlocks) {
        if (this.getLevel().isClientSide()) {
            return;
        }
        for (BuildingBlock block : structureBlocks) {
            if (!block.getBlockState().isAir()) {
                continue;
            }
            BlockPos pos = block.getBlockPos();
            BlockState worldState = this.getLevel().getBlockState(pos);
            if (isCastleVegetation(worldState)) {
                this.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    private void applyAirAndRemovedBlocks(ArrayList<BuildingBlock> oldBlocks, ArrayList<BuildingBlock> newBlocks) {
        if (this.getLevel().isClientSide()) {
            return;
        }

        Map<BlockPos, BlockState> newStatesByPos = new HashMap<>();
        for (BuildingBlock block : newBlocks) {
            newStatesByPos.put(block.getBlockPos(), block.getBlockState());
        }

        for (BuildingBlock oldBlock : oldBlocks) {
            BlockPos pos = oldBlock.getBlockPos();
            BlockState newState = newStatesByPos.get(pos);
            if (newState == null || newState.isAir()) {
                BlockState worldState = this.getLevel().getBlockState(pos);
                if (!worldState.isAir()) {
                    this.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private boolean isTier2() {
        return getUpgradeLevel() > 0;
    }

    private int getEntrySlotIndexForOccupants(int occupants) {
        if (!isTier2()) {
            return 0;
        }
        if (occupants < BASE_SLOT_CAPACITY) {
            return 0;
        }
        if (occupants < BASE_SLOT_CAPACITY + EXTRA_SLOT_CAPACITY) {
            return 1;
        }
        return 2;
    }

    private int getEntrySlotIndex() {
        return getEntrySlotIndexForOccupants(getOccupants().size());
    }

    @Override
    public BlockPos getEntryPosition() {
        BlockPos offset = ENTRY_OFFSETS[getEntrySlotIndex()];
        return originPos.offset(GarrisonableBuilding.rotatePos(offset, this.rotation));
    }

    public BlockPos reserveEntryPositionFor(Entity entity) {
        if (!isTier2()) {
            return getEntryPosition();
        }

        UUID uuid = entity.getUUID();
        Integer reservedSlot = reservedEntrySlots.get(uuid);
        if (reservedSlot == null) {
            int virtualOccupants = getOccupants().size() + reservedEntrySlots.size();
            reservedSlot = getEntrySlotIndexForOccupants(virtualOccupants);
            reservedEntrySlots.put(uuid, reservedSlot);
        }

        BlockPos offset = ENTRY_OFFSETS[Math.max(0, Math.min(ENTRY_OFFSETS.length - 1, reservedSlot))];
        return originPos.offset(GarrisonableBuilding.rotatePos(offset, this.rotation));
    }

    public void releaseEntryReservation(Entity entity) {
        reservedEntrySlots.remove(entity.getUUID());
    }

    @Override
    public int getCapacity() {
        return isTier2()
                ? BASE_SLOT_CAPACITY + (EXTRA_SLOT_CAPACITY * (ENTRY_OFFSETS.length - 1))
                : BASE_SLOT_CAPACITY;
    }

    @Override
    public boolean canDestroyBlock(BlockPos relativeBp) {
        if (relativeBp.getY() == 15 || relativeBp.getY() == 17) {
            return false;
        }
        if (isTier2() && relativeBp.getY() == 11) {
            return false;
        }
        return true;
    }

    @Override
    public boolean startProductionItem(ProductionItem prodItem) {
        if (prodItem == ProductionItems.RAVAGER && !isTier2()) {
            return false;
        }
        return super.startProductionItem(prodItem);
    }

    @Override
    public void updateButtons() {
        super.updateButtons();
        filterTierButtons();
    }

    @Override
    public void changeStructure(String newStructureName) {
        ArrayList<BuildingBlock> oldBlocks = new ArrayList<>(this.getBlocks());
        ArrayList<BuildingBlock> newRelativeBlocks = BuildingBlockData.getBuildingBlocksFromNbt(newStructureName, this.getLevel());
        ArrayList<BuildingBlock> newAbsoluteBlocks = BuildingUtils.getAbsoluteBlockData(
                sanitizeCastleBlocks(newRelativeBlocks),
                this.getLevel(),
                originPos,
                rotation
        );

        applyAirAndRemovedBlocks(oldBlocks, newAbsoluteBlocks);
        clearVegetationAtAirSlots(newAbsoluteBlocks);

        this.blockMap.clear();
        this.placedBlockPosSet.clear();
        setBlocks(newAbsoluteBlocks);
        refreshBlocks();
        updateButtons();
    }

    private void filterTierButtons() {
        if (productionButtons == null || isTier2()) {
            return;
        }
        productionButtons.removeIf(button ->
                button instanceof StartProductionButton startButton &&
                        startButton.productionItem == ProductionItems.RAVAGER
        );
    }
}
