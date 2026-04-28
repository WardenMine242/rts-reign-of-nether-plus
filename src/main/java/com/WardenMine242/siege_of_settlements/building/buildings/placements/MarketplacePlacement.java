package com.WardenMine242.siege_of_settlements.building.buildings.placements;

import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingBlock;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MarketplacePlacement extends ProductionPlacement {
    public static final int MAX_TRADERS = 6;

    private record TraderStallSlot(BlockPos stallPos, BlockPos frontPos, Direction facing) { }

    // Slot coordinates are relative to Marketplace originPos (22x23 footprint).
    // Order in this list is the fill order.
    private static final BlockPos[] TRADER_SLOT_OFFSETS = new BlockPos[] {
            new BlockPos(2, 1, 11),
            new BlockPos(11, 1, 20),
            new BlockPos(9, 1, 11),
            new BlockPos(19, 1, 14),
            new BlockPos(11, 1, 3),
            new BlockPos(19, 1, 2)
    };

    private static final Direction[] TRADER_SLOT_FACING = new Direction[] {
            Direction.SOUTH,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST,
            Direction.WEST
    };

    private final Map<BlockPos, BlockState> restoreStates = new HashMap<>();
    private final Set<BlockPos> protectedLowerLayerPositions = new HashSet<>();
    private final Map<BlockPos, BlockState> foundationTemplateStates = new HashMap<>();
    private final ArrayList<TraderStallSlot> traderSlots = new ArrayList<>();
    private int protectedRelativeY = 0;

    public MarketplacePlacement(Building building,
                                Level level,
                                BlockPos originPos,
                                Rotation rotation,
                                String ownerName,
                                ArrayList<BuildingBlock> blocks,
                                boolean isCapitol) {
        super(building, level, originPos, rotation, ownerName, blocks, isCapitol);
        initTraderSlots(blocks);
        captureRestoreStates(level, blocks);
    }

    public static BlockPos getTraderSlotWorldPos(MarketplacePlacement marketplace, int slotIndex) {
        TraderStallSlot slot = marketplace.getTraderSlot(slotIndex);
        return slot != null ? slot.stallPos : marketplace.originPos;
    }

    public static Direction getTraderSlotFacing(MarketplacePlacement marketplace, int slotIndex) {
        TraderStallSlot slot = marketplace.getTraderSlot(slotIndex);
        return slot != null ? slot.facing : Direction.SOUTH;
    }

    public static float getTraderSlotYaw(MarketplacePlacement marketplace, int slotIndex) {
        return getTraderSlotFacing(marketplace, slotIndex).toYRot();
    }

    public static BlockPos getTraderSlotFrontWorldPos(MarketplacePlacement marketplace, int slotIndex) {
        TraderStallSlot slot = marketplace.getTraderSlot(slotIndex);
        return slot != null ? slot.frontPos : marketplace.originPos;
    }

    public static BlockPos getTraderSlotExitWorldPos(MarketplacePlacement marketplace, int slotIndex) {
        TraderStallSlot slot = marketplace.getTraderSlot(slotIndex);
        return slot != null ? slot.stallPos.relative(slot.facing, 2) : marketplace.originPos;
    }

    public int getTraderSlotCount() {
        return Math.min(MAX_TRADERS, traderSlots.size());
    }

    private TraderStallSlot getTraderSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= traderSlots.size()) {
            return null;
        }
        return traderSlots.get(slotIndex);
    }

    private void initTraderSlots(ArrayList<BuildingBlock> blocks) {
        traderSlots.clear();

        for (int i = 0; i < TRADER_SLOT_OFFSETS.length && i < MAX_TRADERS; i++) {
            BlockPos stallPos = this.originPos.offset(TRADER_SLOT_OFFSETS[i].rotate(this.rotation));
            Direction facing = this.rotation.rotate(TRADER_SLOT_FACING[i]);

            BlockPos frontPos = stallPos.relative(facing);
            traderSlots.add(new TraderStallSlot(stallPos.immutable(), frontPos.immutable(), facing));
        }
    }

    private void captureRestoreStates(Level level, ArrayList<BuildingBlock> blocks) {
        int minY = Integer.MAX_VALUE;
        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (BuildingBlock block : blocks) {
            if (!block.getBlockState().isAir()) {
                minY = Math.min(minY, block.getBlockPos().getY());
                minX = Math.min(minX, block.getBlockPos().getX());
                minZ = Math.min(minZ, block.getBlockPos().getZ());
                maxX = Math.max(maxX, block.getBlockPos().getX());
                maxZ = Math.max(maxZ, block.getBlockPos().getZ());
            }
        }
        if (minY == Integer.MAX_VALUE) {
            return;
        }

        // Keep exact replaced first layer, so it returns to the previous terrain after destroy.
        for (BuildingBlock block : blocks) {
            if (block.getBlockState().isAir() || block.getBlockPos().getY() != minY) {
                continue;
            }
            BlockPos pos = block.getBlockPos();
            restoreStates.put(pos.immutable(), level.getBlockState(pos));
            protectedLowerLayerPositions.add(pos.immutable());
            foundationTemplateStates.put(pos.immutable(), block.getBlockState());
        }
        protectedRelativeY = minY - this.originPos.getY();

        // Also snapshot near-ground area to recover any small craters from destroy side effects.
        for (int x = minX - 1; x <= maxX + 1; x++) {
            for (int z = minZ - 1; z <= maxZ + 1; z++) {
                for (int y = minY - 2; y <= minY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    restoreStates.putIfAbsent(pos.immutable(), level.getBlockState(pos));
                }
            }
        }
    }

    @Override
    public void destroy(ServerLevel serverLevel) {
        // Pre-clear placed blocks so base destroy() does not create explosion craters for Marketplace.
        for (BuildingBlock block : getBlocks()) {
            if (!block.getBlockState().isAir()) {
                BlockPos pos = block.getBlockPos();
                if (serverLevel.isLoaded(pos)) {
                    serverLevel.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }

        super.destroy(serverLevel);

        for (Map.Entry<BlockPos, BlockState> entry : restoreStates.entrySet()) {
            BlockPos pos = entry.getKey();
            if (serverLevel.isLoaded(pos)) {
                serverLevel.setBlockAndUpdate(pos, entry.getValue());
            }
        }

        // After relog, exact pre-build states are not always available.
        // Ensure the embedded lower layer never leaves holes or market blocks behind.
        for (BlockPos pos : protectedLowerLayerPositions) {
            if (!serverLevel.isLoaded(pos)) {
                continue;
            }
            BlockState current = serverLevel.getBlockState(pos);
            BlockState template = foundationTemplateStates.get(pos);
            boolean isTemplateBlock = template != null && current.equals(template);

            if (current.isAir() || isTemplateBlock) {
                BlockState restore = restoreStates.get(pos);
                if (restore != null && !restore.isAir() && !restore.equals(template)) {
                    serverLevel.setBlockAndUpdate(pos, restore);
                    continue;
                }

                BlockState below = serverLevel.getBlockState(pos.below());
                BlockState fillState;
                if (below.is(Blocks.SAND) || below.is(Blocks.RED_SAND) || below.is(Blocks.GRAVEL)
                        || below.is(Blocks.STONE) || below.is(Blocks.DEEPSLATE)) {
                    fillState = below;
                } else {
                    fillState = Blocks.GRASS_BLOCK.defaultBlockState();
                }
                serverLevel.setBlockAndUpdate(pos, fillState);
            }
        }
    }

    @Override
    public boolean canDestroyBlock(BlockPos relativeBp) {
        // Units should not destroy the embedded lower foundation layer.
        return relativeBp.getY() != protectedRelativeY;
    }

    @Override
    public float getBlocksPlacedPercent() {
        int breakableTotal = 0;
        int breakablePlaced = 0;

        for (BuildingBlock block : getBlocks()) {
            if (block.getBlockState().isAir()) {
                continue;
            }
            if (protectedLowerLayerPositions.contains(block.getBlockPos())) {
                continue;
            }
            breakableTotal++;
            if (block.isPlaced(getLevel())) {
                breakablePlaced++;
            }
        }

        if (breakableTotal <= 0) {
            return 1.0f;
        }
        return (float) breakablePlaced / (float) breakableTotal;
    }
}
