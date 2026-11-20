package com.solegendary.siege_of_settlements.building.buildings.placements;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.EquipAbility;
import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingBlock;
import com.solegendary.siege_of_settlements.building.BuildingBlockData;
import com.solegendary.siege_of_settlements.building.RangeIndicator;
import com.solegendary.siege_of_settlements.building.buildings.villagers.Blacksmith;
import com.solegendary.siege_of_settlements.building.production.ActiveProduction;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.config.SiegeOfSettlementsCommonConfigs;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.resources.ResourcesClientboundPacket;
import com.solegendary.siege_of_settlements.time.TimeClientEvents;
import com.solegendary.siege_of_settlements.unit.units.villagers.VillagerUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.VillagerUnitProfession;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
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

    private record BlacksmithSlot(BlockPos slotPos, BlockPos frontPos, Direction facing) { }

    // Tier 1: 1 slot
    private static final BlockPos[] TIER1_SLOT_OFFSETS = new BlockPos[] {
            new BlockPos(5, 2, 5)
    };
    private static final Direction[] TIER1_SLOT_FACING = new Direction[] {
            Direction.NORTH
    };

    // Tier 2: 2 slots
    private static final BlockPos[] TIER2_SLOT_OFFSETS = new BlockPos[] {
            new BlockPos(5, 2, 7),
            new BlockPos(5, 2, 4)
    };
    private static final Direction[] TIER2_SLOT_FACING = new Direction[] {
            Direction.SOUTH,
            Direction.NORTH
    };

    // Tier 3: 3 slots
    private static final BlockPos[] TIER3_SLOT_OFFSETS = new BlockPos[] {
            new BlockPos(4, 2, 4),
            new BlockPos(10, 2, 7),
            new BlockPos(2, 2, 6)
    };
    private static final Direction[] TIER3_SLOT_FACING = new Direction[] {
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST
    };

    private final ArrayList<BlacksmithSlot> blacksmithSlots = new ArrayList<>();
    private int reservedChestSlots = 0; // Number of chest slots reserved for ongoing production

    public EquipAbility autoCastEquip = null;
    private int blacksmithTier = MIN_BLACKSMITH_TIER;

    public BlacksmithPlacement(Building building, Level level, BlockPos originPos, Rotation rotation, String ownerName, ArrayList<BuildingBlock> blocks, boolean isCapitol) {
        super(building, level, originPos, rotation, ownerName, blocks, isCapitol);
        initBlacksmithSlots();
    }

    public int getBlacksmithSlotCount() {
        return blacksmithSlots.size();
    }

    public int getBlacksmithVillagerCount() {
        int count = 0;
        for (Mob entity : level.getEntitiesOfClass(VillagerUnit.class, 
                new AABB(this.centrePos).inflate(10))) {
            if (entity instanceof VillagerUnit villager &&
                villager.isAlive() &&
                villager.getUnitProfession() == VillagerUnitProfession.BLACKSMITH &&
                villager.isOccupyingBlacksmith() &&
                this.originPos.equals(villager.getBlacksmithOrigin())) {
                count++;
            }
        }
        return count;
    }

    private BlacksmithSlot getBlacksmithSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= blacksmithSlots.size()) {
            return null;
        }
        return blacksmithSlots.get(slotIndex);
    }

    private void initBlacksmithSlots() {
        blacksmithSlots.clear();

        BlockPos[] slotOffsets;
        Direction[] slotFacings;

        switch (blacksmithTier) {
            case 1:
                slotOffsets = TIER1_SLOT_OFFSETS;
                slotFacings = TIER1_SLOT_FACING;
                break;
            case 2:
                slotOffsets = TIER2_SLOT_OFFSETS;
                slotFacings = TIER2_SLOT_FACING;
                break;
            case 3:
                slotOffsets = TIER3_SLOT_OFFSETS;
                slotFacings = TIER3_SLOT_FACING;
                break;
            default:
                slotOffsets = TIER1_SLOT_OFFSETS;
                slotFacings = TIER1_SLOT_FACING;
                break;
        }

        for (int i = 0; i < slotOffsets.length; i++) {
            BlockPos slotPos = this.originPos.offset(slotOffsets[i].rotate(this.rotation));
            Direction facing = this.rotation.rotate(slotFacings[i]);

            BlockPos frontPos = slotPos.relative(facing);
            blacksmithSlots.add(new BlacksmithSlot(slotPos.immutable(), frontPos.immutable(), facing));
        }

        // Force villagers to leave when tier changes (slots may have moved)
        if (!level.isClientSide()) {
            com.solegendary.siege_of_settlements.unit.units.villagers.VillagerUnit.forceLeaveBlacksmith(this);
        }
    }

    public static BlockPos getBlacksmithSlotWorldPos(BlacksmithPlacement blacksmith, int slotIndex) {
        BlacksmithSlot slot = blacksmith.getBlacksmithSlot(slotIndex);
        return slot != null ? slot.slotPos : blacksmith.originPos;
    }

    public static Direction getBlacksmithSlotFacing(BlacksmithPlacement blacksmith, int slotIndex) {
        BlacksmithSlot slot = blacksmith.getBlacksmithSlot(slotIndex);
        return slot != null ? slot.facing : Direction.SOUTH;
    }

    public static BlockPos getBlacksmithSlotFrontWorldPos(BlacksmithPlacement blacksmith, int slotIndex) {
        BlacksmithSlot slot = blacksmith.getBlacksmithSlot(slotIndex);
        return slot != null ? slot.frontPos : blacksmith.originPos;
    }

    public static BlockPos getBlacksmithSlotExitWorldPos(BlacksmithPlacement blacksmith, int slotIndex) {
        BlacksmithSlot slot = blacksmith.getBlacksmithSlot(slotIndex);
        return slot != null ? slot.slotPos.relative(slot.facing, 2) : blacksmith.originPos;
    }

    public int getBlacksmithTier() {
        return blacksmithTier;
    }

    public void syncTierFromNetwork(int upgradeLevelIndex) {
        blacksmithTier = Mth.clamp(upgradeLevelIndex + 1, MIN_BLACKSMITH_TIER, MAX_BLACKSMITH_TIER);
        initBlacksmithSlots();
    }

    public void incrementBlacksmithTierAfterProduction() {
        if (blacksmithTier >= MAX_BLACKSMITH_TIER) {
            return;
        }
        blacksmithTier++;
        initBlacksmithSlots();
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
        initBlacksmithSlots();

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

        // Dynamically adjust production speed based on blacksmith count
        if (!productionQueue.isEmpty() && !tickLevel.isClientSide()) {
            int blacksmithCount = getBlacksmithVillagerCount();
            if (blacksmithCount > 0) {
                ActiveProduction active = productionQueue.get(0);
                // Check if this is a blacksmith equipment production item
                if (active.item == ProductionItems.IRON_CHESTPLATE_PROD ||
                    active.item == ProductionItems.DIAMOND_CHESTPLATE_PROD ||
                    active.item == ProductionItems.IRON_SWORD_PROD ||
                    active.item == ProductionItems.DIAMOND_SWORD_PROD ||
                    active.item == ProductionItems.IRON_PICKAXE_PROD ||
                    active.item == ProductionItems.DIAMOND_PICKAXE_PROD ||
                    active.item == ProductionItems.IRON_AXE_PROD ||
                    active.item == ProductionItems.DIAMOND_AXE_PROD ||
                    active.item == ProductionItems.IRON_HOE_PROD ||
                    active.item == ProductionItems.DIAMOND_HOE_PROD ||
                    active.item == ProductionItems.LEATHER_CHESTPLATE_PROD ||
                    active.item == ProductionItems.CHAINMAIL_CHESTPLATE_PROD ||
                    active.item == ProductionItems.RAVAGER_ARMOR_PROD ||
                    active.item == ProductionItems.BOW_PROD ||
                    active.item == ProductionItems.ARROWS_PROD) {
                    
                    if (!active.completed) {
                        int baseTime = SiegeOfSettlementsCommonConfigs.BlacksmithProductionTimes.TIME_1_BLACKSMITH.get();
                        int currentTime = getProductionTimeSeconds();
                        // Calculate how many extra ticks to subtract based on speed multiplier
                        // Speed multiplier = baseTime / currentTime
                        // If baseTime=120, currentTime=90, multiplier=1.33, so we subtract 0.33 extra ticks per tick
                        float speedMultiplier = (float) baseTime / currentTime;
                        active.ticksLeft -= (speedMultiplier - 1.0f);
                    }
                }
            }
        }

        // Update buttons on client every tick to ensure tooltip shows current production time
        if (tickLevel.isClientSide()) {
            updateButtons();
        }

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

    @Override
    protected ActiveProduction createActiveProduction(ProductionItem prodItem, boolean isClientside, String ownerName) {
        // Check if this is a blacksmith equipment production item
        if (prodItem == ProductionItems.IRON_CHESTPLATE_PROD ||
            prodItem == ProductionItems.DIAMOND_CHESTPLATE_PROD ||
            prodItem == ProductionItems.IRON_SWORD_PROD ||
            prodItem == ProductionItems.DIAMOND_SWORD_PROD ||
            prodItem == ProductionItems.IRON_PICKAXE_PROD ||
            prodItem == ProductionItems.DIAMOND_PICKAXE_PROD ||
            prodItem == ProductionItems.IRON_AXE_PROD ||
            prodItem == ProductionItems.DIAMOND_AXE_PROD ||
            prodItem == ProductionItems.IRON_HOE_PROD ||
            prodItem == ProductionItems.DIAMOND_HOE_PROD ||
            prodItem == ProductionItems.LEATHER_CHESTPLATE_PROD ||
            prodItem == ProductionItems.CHAINMAIL_CHESTPLATE_PROD ||
            prodItem == ProductionItems.RAVAGER_ARMOR_PROD ||
            prodItem == ProductionItems.BOW_PROD ||
            prodItem == ProductionItems.ARROWS_PROD) {
            
            // Always use base time (TIME_1_BLACKSMITH) for ticksLeft and totalTicks
            // Speed is adjusted in tick() method by subtracting extra ticks
            int baseTime = SiegeOfSettlementsCommonConfigs.BlacksmithProductionTimes.TIME_1_BLACKSMITH.get();
            float baseTicks = baseTime * 20;
            return new ActiveProduction(prodItem, isClientside, ownerName, baseTicks);
        }
        return super.createActiveProduction(prodItem, isClientside, ownerName);
    }

    @Override
    public float getProductionProgress(ActiveProduction active) {
        // Check if this is a blacksmith equipment production item
        if (active.item == ProductionItems.IRON_CHESTPLATE_PROD ||
            active.item == ProductionItems.DIAMOND_CHESTPLATE_PROD ||
            active.item == ProductionItems.IRON_SWORD_PROD ||
            active.item == ProductionItems.DIAMOND_SWORD_PROD ||
            active.item == ProductionItems.IRON_PICKAXE_PROD ||
            active.item == ProductionItems.DIAMOND_PICKAXE_PROD ||
            active.item == ProductionItems.IRON_AXE_PROD ||
            active.item == ProductionItems.DIAMOND_AXE_PROD ||
            active.item == ProductionItems.IRON_HOE_PROD ||
            active.item == ProductionItems.DIAMOND_HOE_PROD ||
            active.item == ProductionItems.LEATHER_CHESTPLATE_PROD ||
            active.item == ProductionItems.CHAINMAIL_CHESTPLATE_PROD ||
            active.item == ProductionItems.RAVAGER_ARMOR_PROD ||
            active.item == ProductionItems.BOW_PROD ||
            active.item == ProductionItems.ARROWS_PROD) {
            
            // Use base time for progress calculation so progress bar starts at 0%
            int baseTime = SiegeOfSettlementsCommonConfigs.BlacksmithProductionTimes.TIME_1_BLACKSMITH.get();
            float totalTicks = baseTime * 20;
            if (totalTicks <= 0) return 0f;
            // Progress should go from 0 to 1 as ticksLeft goes from totalTicks to 0
            return 1.0f - (active.ticksLeft / totalTicks);
        }
        return super.getProductionProgress(active);
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

    public boolean hasChestSpace() {
        int freeSlots = 0;
        for (BuildingBlock block : getBlocks()) {
            if (block.getBlockState().getBlock() == Blocks.CHEST) {
                BlockEntity blockEntity = getLevel().getBlockEntity(block.getBlockPos());
                if (blockEntity instanceof ChestBlockEntity chest) {
                    for (int i = 0; i < chest.getContainerSize(); i++) {
                        ItemStack stack = chest.getItem(i);
                        if (stack.isEmpty()) {
                            freeSlots++;
                        }
                    }
                }
            }
        }
        // Only return true if there are more free slots than reserved slots
        return freeSlots > reservedChestSlots;
    }

    public void addItemToChest(ItemStack item) {
        for (BuildingBlock block : getBlocks()) {
            if (block.getBlockState().getBlock() == Blocks.CHEST) {
                BlockEntity blockEntity = getLevel().getBlockEntity(block.getBlockPos());
                if (blockEntity instanceof ChestBlockEntity chest) {
                    for (int i = 0; i < chest.getContainerSize(); i++) {
                        ItemStack stack = chest.getItem(i);
                        if (stack.isEmpty()) {
                            chest.setItem(i, item.copy());
                            // Release the reserved slot after successfully adding the item
                            reservedChestSlots = Math.max(0, reservedChestSlots - 1);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean startProductionItem(ProductionItem prodItem) {
        // Check if this is an equipment production item
        if (prodItem == ProductionItems.IRON_CHESTPLATE_PROD ||
            prodItem == ProductionItems.DIAMOND_CHESTPLATE_PROD ||
            prodItem == ProductionItems.IRON_SWORD_PROD ||
            prodItem == ProductionItems.DIAMOND_SWORD_PROD ||
            prodItem == ProductionItems.IRON_PICKAXE_PROD ||
            prodItem == ProductionItems.DIAMOND_PICKAXE_PROD ||
            prodItem == ProductionItems.IRON_AXE_PROD ||
            prodItem == ProductionItems.DIAMOND_AXE_PROD ||
            prodItem == ProductionItems.IRON_HOE_PROD ||
            prodItem == ProductionItems.DIAMOND_HOE_PROD ||
            prodItem == ProductionItems.LEATHER_CHESTPLATE_PROD ||
            prodItem == ProductionItems.CHAINMAIL_CHESTPLATE_PROD ||
            prodItem == ProductionItems.RAVAGER_ARMOR_PROD ||
            prodItem == ProductionItems.BOW_PROD ||
            prodItem == ProductionItems.ARROWS_PROD) {
            
            if (!hasChestSpace()) {
                HudClientEvents.showTemporaryMessage(I18n.get("building.siege_of_settlements.blacksmith.no_chest_space"));
                return false;
            }
            
            // Check for blacksmith villagers
            int blacksmithCount = getBlacksmithVillagerCount();
            if (blacksmithCount == 0) {
                HudClientEvents.showTemporaryMessage(I18n.get("building.siege_of_settlements.blacksmith.no_blacksmiths"));
                return false;
            }
            
        }
        boolean started = super.startProductionItem(prodItem);
        if (started && !level.isClientSide() && reservesChestSlot(prodItem)) {
            reservedChestSlots++;
        }
        return started;
    }

    private boolean reservesChestSlot(ProductionItem item) {
        return item == ProductionItems.IRON_CHESTPLATE_PROD ||
            item == ProductionItems.DIAMOND_CHESTPLATE_PROD ||
            item == ProductionItems.IRON_SWORD_PROD ||
            item == ProductionItems.DIAMOND_SWORD_PROD ||
            item == ProductionItems.IRON_PICKAXE_PROD ||
            item == ProductionItems.DIAMOND_PICKAXE_PROD ||
            item == ProductionItems.IRON_AXE_PROD ||
            item == ProductionItems.DIAMOND_AXE_PROD ||
            item == ProductionItems.IRON_HOE_PROD ||
            item == ProductionItems.DIAMOND_HOE_PROD ||
            item == ProductionItems.LEATHER_CHESTPLATE_PROD ||
            item == ProductionItems.CHAINMAIL_CHESTPLATE_PROD ||
            item == ProductionItems.RAVAGER_ARMOR_PROD ||
            item == ProductionItems.BOW_PROD ||
            item == ProductionItems.ARROWS_PROD;
    }

    @Override
    public boolean cancelProductionItem(ProductionItem item, boolean frontItem) {
        // Check if this is an equipment production item
        boolean reservesChestSlot = reservesChestSlot(item);

        boolean cancelled = super.cancelProductionItem(item, frontItem);
        if (cancelled && reservesChestSlot) {
            // Release the reserved slot only when an item was actually removed.
            reservedChestSlots = Math.max(0, reservedChestSlots - 1);
        }
        return cancelled;
    }

    public int getProductionTimeSeconds() {
        int blacksmithCount = getBlacksmithVillagerCount();
        return switch (blacksmithCount) {
            case 1 -> SiegeOfSettlementsCommonConfigs.BlacksmithProductionTimes.TIME_1_BLACKSMITH.get();
            case 2 -> SiegeOfSettlementsCommonConfigs.BlacksmithProductionTimes.TIME_2_BLACKSMITHS.get();
            case 3 -> SiegeOfSettlementsCommonConfigs.BlacksmithProductionTimes.TIME_3_BLACKSMITHS.get();
            default -> SiegeOfSettlementsCommonConfigs.BlacksmithProductionTimes.TIME_1_BLACKSMITH.get();
        };
    }
}
