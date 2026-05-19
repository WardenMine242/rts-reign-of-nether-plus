package com.solegendary.siege_of_settlements.blocks;

import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.registrars.BlockRegistrar;
import com.solegendary.siege_of_settlements.registrars.GameRuleRegistrar;
import com.solegendary.siege_of_settlements.registrars.ItemRegistrar;
import com.solegendary.siege_of_settlements.resources.BlockUtils;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.resources.ResourceName;
import com.solegendary.siege_of_settlements.resources.ResourceSources;
import com.solegendary.siege_of_settlements.resources.Resources;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class BlockServerEvents {

    private static final Random RANDOM = new Random();

    public static ArrayList<TemporaryBlock> tempBlocks = new ArrayList<>();

    private static final ArrayList<PendingOreReplace> pendingOreReplacements = new ArrayList<>();

    private static class PendingOreReplace {
        private final ServerLevel level;
        private final BlockPos pos;

        private PendingOreReplace(ServerLevel level, BlockPos pos) {
            this.level = level;
            this.pos = pos;
        }
    }

    public static void addTempBlock(ServerLevel level, BlockPos bp, BlockState bs, BlockState oldBs, int lifespan) {
        addTempBlock(level, bp, bs, oldBs, lifespan, false);
    }

    public static void addTempBlock(ServerLevel level, BlockPos bp, BlockState bs, BlockState oldBs, int lifespan, boolean allowInsideBuildings) {
        if (BuildingUtils.isPosInsideAnyBuilding(level.isClientSide(), bp))
            return;

        if (level.getBlockState(bp) != bs)
            tempBlocks.add(new TemporaryBlock(level, bp, bs, oldBs, lifespan));
        else {
            for (TemporaryBlock block : tempBlocks)
                if (bp.equals(block.bp))
                    block.tickAge = 0;
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END || evt.level.isClientSide()) {
            return;
        }
        if (evt.level.dimension() == Level.OVERWORLD) {
            tempBlocks.removeIf(tb -> tb.tick((ServerLevel) evt.level));
        }

        if (!pendingOreReplacements.isEmpty()) {
            Iterator<PendingOreReplace> iterator = pendingOreReplacements.iterator();
            while (iterator.hasNext()) {
                PendingOreReplace pending = iterator.next();
                if (pending.level != evt.level) {
                    continue;
                }
                if (!pending.level.isLoaded(pending.pos)) {
                    continue;
                }
                if (pending.level.getBlockState(pending.pos).isAir()) {
                    pending.level.setBlockAndUpdate(pending.pos, Blocks.COBBLESTONE.defaultBlockState());
                }
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerBlockBreak(BlockEvent.BreakEvent evt) {
        if (evt.getLevel().isClientSide() || evt.getLevel().getServer() == null)
            return;

        boolean isResource = ResourceSources.getFromBlockState(evt.getState()) != null;
        boolean isInBuilding = BuildingUtils.isPosInsideAnyBuilding(false, evt.getPos());
        boolean isCrop = evt.getState().is(BlockTags.CROPS) &&
                evt.getLevel().getBlockState(evt.getPos().below()).is(Blocks.FARMLAND);

        if (evt.getPlayer() != null && !evt.getPlayer().isCreative() && isInBuilding && !isCrop) {
            evt.setCanceled(true);
            return;
        }

        if (!evt.getLevel().getServer().getGameRules().getRule(GameRuleRegistrar.DO_PLAYER_GRIEFING).get() &&
            !isResource && !isInBuilding) {
            evt.setCanceled(true);
        }

        if (evt.isCanceled())
            return;

        if (evt.getPlayer() instanceof ServerPlayer player &&
            player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL &&
            !player.isCreative()) {
            if (!isAllowedSurvivalBreak(evt.getState(), evt.getPos(), evt.getLevel())) {
                evt.setCanceled(true);
            }
        }

        if (!evt.isCanceled() && evt.getPlayer() instanceof ServerPlayer player &&
            player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL &&
            !player.isCreative() && evt.getState().is(Tags.Blocks.ORES) &&
            evt.getLevel() instanceof ServerLevel level) {
            pendingOreReplacements.add(new PendingOreReplace(level, evt.getPos().immutable()));
        }
    }

    @SubscribeEvent
    public static void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock evt) {
        Player player = evt.getEntity();
        if (player == null)
            return;
        // Intentionally do not cancel left-clicks on restricted blocks.
        // Survival restrictions are enforced by BreakSpeed (0 speed) and BreakEvent server checks.
    }

    @SubscribeEvent
    public static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed evt) {
        Player player = evt.getEntity();
        if (player == null || player.isCreative() || player.isSpectator())
            return;

        BlockState state = evt.getState();
        BlockPos pos = evt.getPosition().orElse(null);
        if (pos != null) {
            boolean isInBuilding = BuildingUtils.isPosInsideAnyBuilding(player.level().isClientSide(), pos);
            boolean isCrop = state.is(BlockTags.CROPS) &&
                    player.level().getBlockState(pos.below()).is(Blocks.FARMLAND);
            if ((isInBuilding && !isCrop) || !isAllowedSurvivalBreak(state, pos, player.level())) {
                // Make restricted blocks behave like unbreakable blocks in survival.
                evt.setNewSpeed(0.0f);
                return;
            }
        }

        if (player.getMainHandItem().is(ItemRegistrar.IRON_PICKAXE.get()) && state.is(Tags.Blocks.ORES)) {
            // ~20s for regular ores with custom iron pickaxe
            evt.setNewSpeed(Math.max(0.0f, evt.getOriginalSpeed() * 0.1125f));
            return;
        }
        if (player.getMainHandItem().is(ItemRegistrar.DIAMOND_PICKAXE.get()) && state.is(Tags.Blocks.ORES)) {
            // 100% faster than custom iron pickaxe on ores
            evt.setNewSpeed(Math.max(0.0f, evt.getOriginalSpeed() * 0.225f));
            return;
        }

        if (!(state.is(BlockTags.LOGS) || BlockUtils.isFallingLogBlock(state)))
            return;

        if (player.getMainHandItem().is(ItemRegistrar.DIAMOND_AXE.get())) {
            // 100% faster than custom iron axe on logs
            evt.setNewSpeed(0.86f);
            return;
        }

        if (player.getMainHandItem().getItem() instanceof AxeItem) {
            // ~7s per log for players in survival with an axe
            evt.setNewSpeed(0.43f);
            return;
        }

        // ~30s per log for players in survival without an axe
        evt.setNewSpeed(0.1f);
    }

    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock evt) {
        Player player = evt.getEntity();
        if (player == null)
            return;

        if (player.isCreative() || player.isSpectator())
            return;

        ItemStack stack = evt.getItemStack();
        if (!(stack.getItem() instanceof BlockItem blockItem))
            return;

        BlockPos targetPos = evt.getPos().relative(evt.getFace());
        BlockState placeState = blockItem.getBlock().defaultBlockState();

        if (!isAllowedSurvivalPlace(placeState, targetPos, evt.getLevel())) {
            evt.setUseBlock(Event.Result.DENY);
            evt.setUseItem(Event.Result.DENY);
            evt.setCancellationResult(InteractionResult.FAIL);
            evt.setCanceled(true);
        }
    }

    public static boolean isAllowedSurvivalBreak(BlockState state, BlockPos pos, LevelAccessor level) {
        Block block = state.getBlock();
        if (block == Blocks.PUMPKIN || block == Blocks.CARVED_PUMPKIN || block == Blocks.JACK_O_LANTERN)
            return true;
        if (block == Blocks.BEEHIVE || block == Blocks.BEE_NEST)
            return true;
        if (state.is(BlockTags.LOGS) || state.is(Tags.Blocks.ORES))
            return true;
        if (BlockUtils.isFallingLogBlock(state))
            return true;
        if (state.is(BlockTags.LEAVES))
            return true;
        if (state.is(BlockTags.CROPS) && level.getBlockState(pos.below()).is(Blocks.FARMLAND))
            return true;
        return false;
    }

    private static boolean isAllowedSurvivalPlace(BlockState state, BlockPos pos, LevelAccessor level) {
        return state.is(BlockTags.CROPS) && level.getBlockState(pos.below()).is(Blocks.FARMLAND);
    }

    private static boolean isUsingIronHoe(Player player) {
        return player.getMainHandItem().is(ItemRegistrar.IRON_HOE.get()) ||
               player.getOffhandItem().is(ItemRegistrar.IRON_HOE.get()) ||
               player.getMainHandItem().is(ItemRegistrar.DIAMOND_HOE.get()) ||
               player.getOffhandItem().is(ItemRegistrar.DIAMOND_HOE.get());
    }

    @SubscribeEvent
    public static void onPlayerBlockPlace(BlockEvent.EntityPlaceEvent evt) {
        if (evt.getLevel().isClientSide() || !(evt.getEntity() instanceof ServerPlayer player))
            return;

        if (player.isCreative() || player.gameMode.getGameModeForPlayer() != GameType.SURVIVAL)
            return;

        if (!isAllowedSurvivalPlace(evt.getPlacedBlock(), evt.getPos(), evt.getLevel())) {
            evt.setCanceled(true);
            return;
        }

        if (evt.getPlacedBlock().is(BlockTags.CROPS)) {
            if (isUsingIronHoe(player)) {
                return;
            }
            BuildingPlacement building = BuildingUtils.findBuilding(false, evt.getPos());
            if (building != null && building.getBuilding() == Buildings.WHEAT_FARM) {
                String playerName = player.getName().getString();
                if (!ResourcesServerEvents.canAfford(playerName, ResourceName.WOOD, ResourceCosts.REPLANT_WOOD_COST)) {
                    evt.setCanceled(true);
                    return;
                }
                ResourcesServerEvents.addSubtractResources(new Resources(playerName, 0, -ResourceCosts.REPLANT_WOOD_COST, 0));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerBlockMultiPlace(BlockEvent.EntityMultiPlaceEvent evt) {
        if (evt.getLevel().isClientSide() || !(evt.getEntity() instanceof ServerPlayer player))
            return;

        if (player.isCreative() || player.gameMode.getGameModeForPlayer() != GameType.SURVIVAL)
            return;

        int totalReplantCost = 0;
        for (BlockSnapshot snapshot : evt.getReplacedBlockSnapshots()) {
            if (!isAllowedSurvivalPlace(snapshot.getCurrentBlock(), snapshot.getPos(), evt.getLevel())) {
                evt.setCanceled(true);
                return;
            }
            if (snapshot.getCurrentBlock().is(BlockTags.CROPS)) {
                if (isUsingIronHoe(player)) {
                    continue;
                }
                BuildingPlacement building = BuildingUtils.findBuilding(false, snapshot.getPos());
                if (building != null && building.getBuilding() == Buildings.WHEAT_FARM) {
                    totalReplantCost += ResourceCosts.REPLANT_WOOD_COST;
                }
            }
        }

        if (totalReplantCost > 0) {
            String playerName = player.getName().getString();
            if (!ResourcesServerEvents.canAfford(playerName, ResourceName.WOOD, totalReplantCost)) {
                evt.setCanceled(true);
                return;
            }
            ResourcesServerEvents.addSubtractResources(new Resources(playerName, 0, -totalReplantCost, 0));
        }
    }

    private static HashMap<BlockPos, Integer> getPosesAndWeights(ServerLevel level, BlockPos pos, int centrePosLayers) {
        int baseWeight = 4;
        List<BlockPos> adjPoses = List.of(
                pos.north(), pos.south(), pos.east(), pos.west(),
                pos.north().north(), pos.south().south(), pos.east().east(), pos.west().west(),
                pos.north().east(), pos.south().east(), pos.north().west(), pos.south().west()
        );
        HashMap<BlockPos, Integer> posesAndLayers = new HashMap<>();
        for (BlockPos adjPos : adjPoses) {

            List<BlockPos> verticalPosesShort = List.of(adjPos, adjPos.below(), adjPos.above());
            List<BlockPos> verticalPosesLong = List.of(adjPos, adjPos.below(), adjPos.above(), adjPos.below().below(), adjPos.above().above());
            int horizDistance = adjPos.atY(0).distManhattan(new Vec3i(adjPos.getX(), 0, adjPos.getZ()));
            for (BlockPos verticalPos : horizDistance <= 1 ? verticalPosesShort : verticalPosesLong) {
                if (BlockUtils.canPlaceSnow(level, verticalPos)) {
                    int layers = BlockUtils.getWraithSnowLayers(level.getBlockState(verticalPos));
                    int layerDiffWeight = (centrePosLayers - layers) * 3;
                    int distanceWeight = - horizDistance * 6;
                    int weight = baseWeight + layerDiffWeight + distanceWeight;
                    if (layers == 0 && layerDiffWeight > 0) {
                        weight += baseWeight * 2;
                    }
                    posesAndLayers.put(adjPos, Math.max(0, weight));
                    break;
                }
            }
        }
        // extra chance for centre pos:
        if (centrePosLayers < 8 && BlockUtils.canPlaceSnow(level, pos))
            posesAndLayers.put(pos, baseWeight * 2);
        return posesAndLayers;
    }

    public static HashMap<BlockPos, Integer> getSnowPositions(
            Level level, BlockPos center, int maxDistance
    ) {
        HashMap<BlockPos, Integer> positions = new HashMap<>();
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        for (int dx = -maxDistance; dx <= maxDistance; dx++) {
            for (int dz = -maxDistance; dz <= maxDistance; dz++) {
                int horizontalDistance = Math.abs(dx) + Math.abs(dz);
                if (horizontalDistance > maxDistance)
                    continue;

                BlockPos basePos = new BlockPos(cx + dx, cy, cz + dz);
                if (BlockUtils.canPlaceSnow(level, basePos)) {
                    positions.put(basePos, horizontalDistance);
                    continue;
                }
                // Vertical search range grows with horizontal distance
                for (int dy = 1; dy <= horizontalDistance; dy++) {
                    BlockPos below = basePos.below(dy);
                    if (BlockUtils.canPlaceSnow(level, below)) {
                        positions.put(below, horizontalDistance);
                        break;
                    }
                    BlockPos above = basePos.above(dy);
                    if (BlockUtils.canPlaceSnow(level, above)) {
                        positions.put(above, horizontalDistance);
                        break;
                    }
                }
            }
        }
        return positions;
    }

    private static BlockPos pickWeighted(Map<BlockPos, Integer> weights) {
        int totalWeight = 0;

        for (int w : weights.values()) {
            if (w > 0) {
                totalWeight += w;
            }
        }
        if (totalWeight <= 0) {
            return null;
        }
        int roll = RANDOM.nextInt(totalWeight);

        for (Map.Entry<BlockPos, Integer> entry : weights.entrySet()) {
            int w = entry.getValue();
            if (w <= 0) continue;

            roll -= w;
            if (roll < 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static boolean placeWraithSnow(ServerLevel level, BlockPos pos, int ownerId) {
        if (!BlockUtils.canPlaceSnow(level, pos))
            return false;

        BlockState bsExisting = level.getBlockState(pos);

        int layers = BlockUtils.getWraithSnowLayers(bsExisting);

        BlockPos targetPos = pos;

        if (layers > 1) {
            HashMap<BlockPos, Integer> posesAndWeights = getPosesAndWeights(level, pos, layers);
            targetPos = pickWeighted(posesAndWeights);
        }
        BlockState snowBs = BlockRegistrar.WRAITH_SNOW_LAYER.get().defaultBlockState();

        if (targetPos != null) {
            BlockState targetBs = level.getBlockState(targetPos);
            int targetLayers = BlockUtils.getWraithSnowLayers(targetBs);

            if (targetLayers <= 0) {
                if (level.getBlockState(targetPos.below()).getBlock() == Blocks.DIRT_PATH) {
                    level.setBlockAndUpdate(targetPos.below(), Blocks.DIRT.defaultBlockState());
                }
                level.setBlockAndUpdate(targetPos, snowBs);
            } else if (targetLayers < 8) {
                BlockState newLayer = targetBs.setValue(BlockStateProperties.LAYERS, targetLayers + 1);
                level.setBlockAndUpdate(targetPos, newLayer);
            }
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, targetPos, Block.getId(snowBs));
            level.levelEvent(targetBs.getSoundType().getPlaceSound().hashCode(), targetPos, Block.getId(snowBs));
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof WraithSnowBlockEntity snowBe) {
                snowBe.setOwnerId(ownerId);
                snowBe.randomiseLifeTicks();
            }
            return true;
        }
        return false;
    }
}




































