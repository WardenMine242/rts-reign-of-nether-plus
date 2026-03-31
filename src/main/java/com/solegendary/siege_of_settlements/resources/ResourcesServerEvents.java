package com.solegendary.siege_of_settlements.resources;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.BuildingServerEvents;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ActiveProduction;
import com.solegendary.siege_of_settlements.player.PlayerServerEvents;
import com.solegendary.siege_of_settlements.registrars.BlockRegistrar;
import com.solegendary.siege_of_settlements.registrars.GameRuleRegistrar;
import com.solegendary.siege_of_settlements.sandbox.SandboxServer;
import com.solegendary.siege_of_settlements.sounds.SoundAction;
import com.solegendary.siege_of_settlements.sounds.SoundClientboundPacket;
import com.solegendary.siege_of_settlements.tutorial.TutorialServerEvents;
import com.solegendary.siege_of_settlements.unit.UnitServerEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

import static com.solegendary.siege_of_settlements.resources.BlockUtils.isLogBlock;

public class ResourcesServerEvents {

    // tracks all players' resources
    public static ArrayList<Resources> resourcesList = new ArrayList<>();

    public static final int STARTING_FOOD_TUTORIAL = 750;
    public static final int STARTING_WOOD_TUTORIAL = 850;
    public static final int STARTING_ORE_TUTORIAL = 250;
    public static final int STARTING_EMERALD_TUTORIAL = 0;
    public static final int STARTING_FOOD_SANDBOX = 999999;
    public static final int STARTING_WOOD_SANDBOX = 999999;
    public static final int STARTING_ORE_SANDBOX = 999999;
    public static final int STARTING_EMERALD_SANDBOX = 999999;
    public static final int STARTING_FOOD = 100;
    public static final int STARTING_WOOD = 450;
    public static final int STARTING_ORE = 250;
    public static final int STARTING_EMERALD = 0;

    // Marketplace trade settings (easy to tune)
    public static int MARKET_TRADE_RESOURCE_AMOUNT = 100;
    public static int MARKET_SELL_FOOD_EMERALDS = 30;
    public static int MARKET_SELL_WOOD_EMERALDS = 25;
    public static int MARKET_SELL_ORE_EMERALDS = 35;
    public static int MARKET_BUY_FOOD_EMERALDS = 40;
    public static int MARKET_BUY_WOOD_EMERALDS = 30;
    public static int MARKET_BUY_ORE_EMERALDS = 40;

    public static final float NEUTRAL_UNIT_BOUNTY_PERCENT = 0.25f;
    public static final float NEUTRAL_BUILDING_BOUNTY_PERCENT = 0.25f;

    // to avoid having to save units too often add on all unit resources here too and just add directly on load
    public static void saveResources(ServerLevel serverLevel) {
        if (serverLevel == null) {
            return;
        }
        ResourcesSaveData data = ResourcesSaveData.getInstance(serverLevel);
        data.resources.clear();
        resourcesList.forEach(r -> {

            // add all unit held resources to resources so we don't have to save unit items
            int unitFood = 0;
            int unitWood = 0;
            int unitOre = 0;
            int unitEmerald = 0;
            for (LivingEntity le : UnitServerEvents.getAllUnits()) {
                if (le instanceof Unit u && u.getOwnerName().equals(r.ownerName)) {
                    Resources unitRes = Resources.getTotalResourcesFromItems(u.getItems());
                    unitFood += unitRes.food;
                    unitWood += unitRes.wood;
                    unitOre += unitRes.ore;
                    unitEmerald += unitRes.emerald;
                }
            }
            // add all production item costs since they will be cancelled on server shutdown
            int prodFood = 0;
            int prodWood = 0;
            int prodOre = 0;
            int prodEmerald = 0;
            for (BuildingPlacement building : BuildingServerEvents.getBuildings()) {
                if (building instanceof ProductionPlacement pBuilding) {
                    for (ActiveProduction item : pBuilding.productionQueue) {
                        prodFood += item.item.getCost(false, pBuilding.ownerName).food;
                        prodWood += item.item.getCost(false, pBuilding.ownerName).wood;
                        prodOre += item.item.getCost(false, pBuilding.ownerName).ore;
                        prodEmerald += item.item.getCost(false, pBuilding.ownerName).emerald;
                    }
                }
            }
            data.resources.add(new Resources(r.ownerName,
                r.food + r.foodToAdd + unitFood + prodFood,
                r.wood + r.woodToAdd + unitWood + prodWood,
                r.ore + r.oreToAdd + unitOre + prodOre,
                r.emerald + r.emeraldToAdd + unitEmerald + prodEmerald
            ));
            //SiegeOfSettlements.LOGGER.info("saved resources in serverevents: " + r.ownerName + "|" + r.food + "|" + r.wood + "|" + r.ore);
        });
        data.save();
        serverLevel.getDataStorage().save();
    }

    @SubscribeEvent
    public static void loadResources(ServerStartedEvent evt) {
        ServerLevel level = evt.getServer().getLevel(Level.OVERWORLD);

        if (level != null) {
            ResourcesSaveData data = ResourcesSaveData.getInstance(level);
            resourcesList.clear();
            resourcesList.addAll(data.resources);

            //SiegeOfSettlements.LOGGER.info("saved " + data.resources.size() + " resources in serverevents");
        }
    }

    private static final int SAVE_TICKS_MAX = 600;
    private static int saveTicks = 0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END)
            return;
        saveTicks += 1;
        if (saveTicks >= SAVE_TICKS_MAX) {
            ServerLevel level = evt.getServer().getLevel(Level.OVERWORLD);
            if (level != null) {
                saveResources(level);
                saveTicks = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent evt) {
        ServerLevel level = evt.getServer().getLevel(Level.OVERWORLD);
        if (level != null) {
            saveResources(level);
        }
    }

    public static void resetResources(String playerName) {
        for (Resources resources : resourcesList) {
            if (resources.ownerName.equals(playerName)) {
                if (TutorialServerEvents.isEnabled()) {
                    resources.food = STARTING_FOOD_TUTORIAL;
                    resources.wood = STARTING_WOOD_TUTORIAL;
                    resources.ore = STARTING_ORE_TUTORIAL;
                    resources.emerald = STARTING_EMERALD_TUTORIAL;
                } else if (SandboxServer.isSandboxPlayer(playerName)) {
                    resources.food = STARTING_FOOD_SANDBOX;
                    resources.wood = STARTING_WOOD_SANDBOX;
                    resources.ore = STARTING_ORE_SANDBOX;
                    resources.emerald = STARTING_EMERALD_SANDBOX;
                } else {
                    resources.food = STARTING_FOOD;
                    resources.wood = STARTING_WOOD;
                    resources.ore = STARTING_ORE;
                    resources.emerald = STARTING_EMERALD;
                }
                ResourcesClientboundPacket.syncResources(resourcesList);
                break;
            }
        }
    }

    public static void addSubtractResources(Resources resourcesToAdd) {
        for (Resources resources : resourcesList) {
            if (resources.ownerName.equals(resourcesToAdd.ownerName)) {
                // change serverside instantly
                resources.changeInstantly(resourcesToAdd.food, resourcesToAdd.wood, resourcesToAdd.ore, resourcesToAdd.emerald);
                // change clientside over time
                ResourcesClientboundPacket.addSubtractResources(new Resources(resourcesToAdd.ownerName,
                    resourcesToAdd.food,
                    resourcesToAdd.wood,
                    resourcesToAdd.ore,
                    resourcesToAdd.emerald
                ));
            }
        }
    }

    public static boolean canAfford(String ownerName, ResourceName resourceName, int cost) {
        if (cost <= 0) {
            return true;
        }
        for (Resources resources : ResourcesServerEvents.resourcesList)
            if (resources.ownerName.equals(ownerName)) {
                switch (resourceName) {
                    case FOOD -> {
                        return resources.food >= cost;
                    }
                    case WOOD -> {
                        return resources.wood >= cost;
                    }
                    case ORE -> {
                        return resources.ore >= cost;
                    }
                    case EMERALDS -> {
                        return resources.emerald >= cost;
                    }
                }
            }
        return false;
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent evt) {
        String playerName = evt.getEntity().getName().getString();
        ResourcesClientboundPacket.syncResources(resourcesList);
    }

    public static void assignResources(String playerName) {
        resourcesList.removeIf(r -> r.ownerName.equals(playerName));
        Resources resources;
        if (TutorialServerEvents.isEnabled()) {
            resources = new Resources(playerName,
                STARTING_FOOD_TUTORIAL,
                STARTING_WOOD_TUTORIAL,
                STARTING_ORE_TUTORIAL,
                STARTING_EMERALD_TUTORIAL
            );
        } else {
            resources = new Resources(playerName, STARTING_FOOD, STARTING_WOOD, STARTING_ORE, STARTING_EMERALD);
        }
        resourcesList.add(resources);
        ResourcesClientboundPacket.syncResources(resourcesList);
    }

    // prevent vanilla growth mechanics because they're slow and random, see FarmPlacement instead
    @SubscribeEvent
    public static void onCropGrow(BlockEvent.CropGrowEvent.Pre evt) {
        if (BuildingUtils.isPosInsideAnyBuilding(evt.getLevel().isClientSide(), evt.getPos()))
            evt.setResult(Event.Result.DENY);
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent evt) {
        // Keep crop drops (potato/carrot, etc.) visible/pickable for players inside buildings.
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END || evt.player.level().isClientSide()) {
            return;
        }

        if (!(evt.player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (serverPlayer.tickCount % 5 != 0) {
            return;
        }

        String playerName = serverPlayer.getName().getString();
        if (!PlayerServerEvents.isRTSPlayer(playerName)) {
            return;
        }

        convertInventoryResources(serverPlayer);
    }

    private static boolean isNonRtsPickupItem(ItemStack stack) {
        return stack.is(net.minecraftforge.common.Tags.Items.SEEDS) ||
               stack.is(net.minecraft.tags.ItemTags.SAPLINGS) ||
               stack.is(net.minecraft.tags.ItemTags.WOOL) ||
               stack.is(Items.APPLE) ||
               stack.is(Items.STICK);
    }

    private static boolean shouldSkipAutoConvert(ItemStack stack) {
        return stack.is(Items.EMERALD_ORE) || stack.is(Items.DEEPSLATE_EMERALD_ORE);
    }

    private static void convertInventoryResources(ServerPlayer player) {
        var inv = player.getInventory();
        String playerName = player.getName().getString();

        int food = 0;
        int wood = 0;
        int ore = 0;
        int emerald = 0;
        boolean removedNonRts = false;
        boolean changed = false;

        for (int i = 0; i < inv.items.size(); i++) {
            ItemStack stack = inv.items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (isNonRtsPickupItem(stack)) {
                inv.setItem(i, ItemStack.EMPTY);
                removedNonRts = true;
                changed = true;
                continue;
            }
            if (shouldSkipAutoConvert(stack)) {
                continue;
            }
            ResourceSource source = ResourceSources.getFromItem(stack.getItem());
            if (source == null || source.resourceValue <= 0 || source.resourceName == ResourceName.NONE) {
                continue;
            }
            int total = source.resourceValue * stack.getCount();
            switch (source.resourceName) {
                case FOOD -> food += total;
                case WOOD -> wood += total;
                case ORE -> ore += total;
                case EMERALDS -> emerald += total;
            }
            inv.setItem(i, ItemStack.EMPTY);
            changed = true;
        }

        for (int i = 0; i < inv.offhand.size(); i++) {
            ItemStack stack = inv.offhand.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (isNonRtsPickupItem(stack)) {
                inv.offhand.set(i, ItemStack.EMPTY);
                removedNonRts = true;
                changed = true;
                continue;
            }
            if (shouldSkipAutoConvert(stack)) {
                continue;
            }
            ResourceSource source = ResourceSources.getFromItem(stack.getItem());
            if (source == null || source.resourceValue <= 0 || source.resourceName == ResourceName.NONE) {
                continue;
            }
            int total = source.resourceValue * stack.getCount();
            switch (source.resourceName) {
                case FOOD -> food += total;
                case WOOD -> wood += total;
                case ORE -> ore += total;
                case EMERALDS -> emerald += total;
            }
            inv.offhand.set(i, ItemStack.EMPTY);
            changed = true;
        }

        if (changed) {
            inv.setChanged();
        }

        if (removedNonRts) {
            ResourcesClientboundPacket.showPickupText(new Resources(playerName, 0, 0, 0, 0), "not_rts_resource");
        }

        if (food > 0 || wood > 0 || ore > 0 || emerald > 0) {
            Resources delta = new Resources(playerName, food, wood, ore, emerald);
            addSubtractResources(delta);
            ResourcesClientboundPacket.showPickupText(delta);
        }
    }

    @SubscribeEvent
    public static void onPlayerBlockBreak(BlockEvent.BreakEvent evt) {
        if (BuildingUtils.isPosInsideAnyBuilding(false, evt.getPos())) {
            BuildingPlacement building = BuildingUtils.findBuilding(false, evt.getPos());
            if (building != null && building.getBuilding() == Buildings.WHEAT_FARM) {
                if (isPlayerCropDropTarget(evt.getLevel(), evt.getPos(), evt.getState()) &&
                    evt.getLevel() instanceof ServerLevel serverLevel &&
                    evt.getPlayer() != null) {
                    Player player = evt.getPlayer();
                    BlockEntity blockEntity = serverLevel.getBlockEntity(evt.getPos());
                    if (isFullyGrownWheat(evt.getState())) {
                        Block.popResource(serverLevel, evt.getPos(), new ItemStack(Items.WHEAT, 1));
                    } else if (isFullyGrownPotatoes(evt.getState())) {
                        int potatoCount = 1 + serverLevel.random.nextInt(3);
                        Block.popResource(serverLevel, evt.getPos(), new ItemStack(Items.POTATO, potatoCount));
                    } else if (isFullyGrownAge7Crop(evt.getState())) {
                        List<ItemStack> drops = Block.getDrops(evt.getState(), serverLevel, evt.getPos(), blockEntity, player, player.getMainHandItem());
                        for (ItemStack drop : drops) {
                            Block.popResource(serverLevel, evt.getPos(), drop);
                        }
                    }
                    serverLevel.setBlockAndUpdate(evt.getPos(), Blocks.AIR.defaultBlockState());
                    evt.setCanceled(true);
                    return;
                }
            }
            evt.setCanceled(true);
            if (evt.getLevel() instanceof ServerLevel serverLevel) {
                serverLevel.setBlockAndUpdate(evt.getPos(), Blocks.AIR.defaultBlockState());
            }
        }

        if (isLogBlock(evt.getState()) && !BuildingUtils.isPosInsideAnyBuilding(false, evt.getPos())) {
            fellAdjacentLogs(evt.getPos(), new ArrayList<>(), (Level) evt.getLevel());
        }
    }

    private static boolean isPlayerCropDropTarget(LevelAccessor level, BlockPos pos, BlockState state) {
        return state.is(BlockTags.CROPS) && level.getBlockState(pos.below()).is(Blocks.FARMLAND);
    }

    private static boolean isFullyGrownWheat(BlockState state) {
        return state.is(Blocks.WHEAT) && state.getValue(BlockStateProperties.AGE_7) == 7;
    }

    private static boolean isFullyGrownPotatoes(BlockState state) {
        return state.is(Blocks.POTATOES) && state.getValue(BlockStateProperties.AGE_7) == 7;
    }

    private static boolean isFullyGrownAge7Crop(BlockState state) {
        return state.hasProperty(BlockStateProperties.AGE_7) && state.getValue(BlockStateProperties.AGE_7) == 7;
    }

    // if a tree is touched, destroy any adjacent logs that are above the ground after some time to avoid leaving
    // tall trees behind
    public static void fellAdjacentLogs(BlockPos bp, ArrayList<BlockPos> bpsExcluded, Level level) {
        if (SandboxServer.isAnyoneASandboxPlayer())
            return;

        if (!level.getGameRules().getRule(GameRuleRegistrar.LOG_FALLING).get())
            return;

        BlockState bs = level.getBlockState(bp);

        List<BlockPos> bpsAdj = List.of(bp.north(),
            bp.south(),
            bp.east(),
            bp.west(),
            bp.above(),
            bp.above().north(),
            bp.above().south(),
            bp.above().east(),
            bp.above().west(),
            bp.north().east(),
            bp.north().west(),
            bp.south().east(),
            bp.south().west(),
            bp.above().north().east(),
            bp.above().north().west(),
            bp.above().south().east(),
            bp.above().south().west()
        );

        for (BlockPos bpAdj : bpsAdj) {
            BlockState bsAdj = level.getBlockState(bpAdj);
            if (isLogBlock(bsAdj) && !bpsExcluded.contains(bpAdj)) {
                Block fallingLogBlock = FALLING_LOGS.get(bsAdj.getBlock());
                if (fallingLogBlock != null && !BuildingUtils.isPosInsideAnyBuilding(level.isClientSide(), bpAdj)) {
                    if (bsAdj.hasProperty(BlockStateProperties.AXIS)) {
                        level.setBlockAndUpdate(bpAdj, fallingLogBlock.defaultBlockState()
                                .setValue(BlockStateProperties.AXIS, bsAdj.getValue(BlockStateProperties.AXIS)));
                    } else {
                        level.setBlockAndUpdate(bpAdj, fallingLogBlock.defaultBlockState());
                    }
                    bpsExcluded.add(bpAdj);
                    fellAdjacentLogs(bpAdj, bpsExcluded, level);
                }
                bpsExcluded.add(bpAdj);
                fellAdjacentLogs(bpAdj, bpsExcluded, level);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent evt) {

        evt.getDispatcher().register(Commands.literal("sendfood")
            .then(Commands.argument("player", EntityArgument.player())
            .then(Commands.argument("amount", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
            .executes((command) -> trySendingResources(command, ResourceName.FOOD)))));

        evt.getDispatcher().register(Commands.literal("sendwood")
            .then(Commands.argument("player", EntityArgument.player())
            .then(Commands.argument("amount", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
            .executes((command) -> trySendingResources(command, ResourceName.WOOD)))));

        evt.getDispatcher().register(Commands.literal("sendore")
            .then(Commands.argument("player", EntityArgument.player())
            .then(Commands.argument("amount", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
            .executes((command) -> trySendingResources(command, ResourceName.ORE)))));

        evt.getDispatcher().register(Commands.literal("sendemeralds")
            .then(Commands.argument("player", EntityArgument.player())
            .then(Commands.argument("amount", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
            .executes((command) -> trySendingResources(command, ResourceName.EMERALDS)))));
    }

    public static int trySendingResources(CommandContext<CommandSourceStack> context, ResourceName resourceName) throws CommandSyntaxException {
        Player sendingPlayer = context.getSource().getPlayer();
        Player receivingPlayer = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        if (sendingPlayer == null) {
            return 0;
        } else {
            String sendingPlayerName = sendingPlayer.getName().getString();
            String receivingPlayerName = receivingPlayer.getName().getString();
            return trySendingResources(sendingPlayerName, receivingPlayerName, resourceName, amount);
        }
    }

    // send resources including any available when you don't have enough
    public static void trySendingAnyResources(String receivingPlayerName, Resources sentResources) {
        Resources res = null;
        for (Resources resources : resourcesList)
            if (resources.ownerName.equals(sentResources.ownerName))
                res = resources;
        if (res != null) {
            int foodAmount = Math.min(res.food, sentResources.food);
            if (foodAmount > 0)
                trySendingResources(sentResources.ownerName,receivingPlayerName, ResourceName.FOOD, foodAmount);
            int woodAmount = Math.min(res.wood, sentResources.wood);
            if (woodAmount > 0)
                trySendingResources(sentResources.ownerName,receivingPlayerName, ResourceName.WOOD, woodAmount);
            int oreAmount = Math.min(res.ore, sentResources.ore);
            if (oreAmount > 0)
                trySendingResources(sentResources.ownerName,receivingPlayerName, ResourceName.ORE, oreAmount);
            int emeraldAmount = Math.min(res.emerald, sentResources.emerald);
            if (emeraldAmount > 0)
                trySendingResources(sentResources.ownerName,receivingPlayerName, ResourceName.EMERALDS, emeraldAmount);

            if (sentResources.getTotalValue() > 0)
                SoundClientboundPacket.playSoundForPlayer(SoundAction.CHAT, receivingPlayerName);
        }
    }

    public static void tryMarketplaceTrade(String playerName, ResourcesAction action) {
        if (!PlayerServerEvents.isRTSPlayer(playerName)) {
            return;
        }

        int foodDelta = 0;
        int woodDelta = 0;
        int oreDelta = 0;
        int emeraldDelta = 0;

        switch (action) {
            case MARKET_SELL_FOOD -> {
                if (!canAfford(playerName, ResourceName.FOOD, MARKET_TRADE_RESOURCE_AMOUNT)) {
                    ResourcesClientboundPacket.warnInsufficientResources(playerName, false, true, true, true);
                    return;
                }
                foodDelta = -MARKET_TRADE_RESOURCE_AMOUNT;
                emeraldDelta = MARKET_SELL_FOOD_EMERALDS;
            }
            case MARKET_SELL_WOOD -> {
                if (!canAfford(playerName, ResourceName.WOOD, MARKET_TRADE_RESOURCE_AMOUNT)) {
                    ResourcesClientboundPacket.warnInsufficientResources(playerName, true, false, true, true);
                    return;
                }
                woodDelta = -MARKET_TRADE_RESOURCE_AMOUNT;
                emeraldDelta = MARKET_SELL_WOOD_EMERALDS;
            }
            case MARKET_SELL_ORE -> {
                if (!canAfford(playerName, ResourceName.ORE, MARKET_TRADE_RESOURCE_AMOUNT)) {
                    ResourcesClientboundPacket.warnInsufficientResources(playerName, true, true, false, true);
                    return;
                }
                oreDelta = -MARKET_TRADE_RESOURCE_AMOUNT;
                emeraldDelta = MARKET_SELL_ORE_EMERALDS;
            }
            case MARKET_BUY_FOOD -> {
                if (!canAfford(playerName, ResourceName.EMERALDS, MARKET_BUY_FOOD_EMERALDS)) {
                    ResourcesClientboundPacket.warnInsufficientResources(playerName, true, true, true, false);
                    return;
                }
                foodDelta = MARKET_TRADE_RESOURCE_AMOUNT;
                emeraldDelta = -MARKET_BUY_FOOD_EMERALDS;
            }
            case MARKET_BUY_WOOD -> {
                if (!canAfford(playerName, ResourceName.EMERALDS, MARKET_BUY_WOOD_EMERALDS)) {
                    ResourcesClientboundPacket.warnInsufficientResources(playerName, true, true, true, false);
                    return;
                }
                woodDelta = MARKET_TRADE_RESOURCE_AMOUNT;
                emeraldDelta = -MARKET_BUY_WOOD_EMERALDS;
            }
            case MARKET_BUY_ORE -> {
                if (!canAfford(playerName, ResourceName.EMERALDS, MARKET_BUY_ORE_EMERALDS)) {
                    ResourcesClientboundPacket.warnInsufficientResources(playerName, true, true, true, false);
                    return;
                }
                oreDelta = MARKET_TRADE_RESOURCE_AMOUNT;
                emeraldDelta = -MARKET_BUY_ORE_EMERALDS;
            }
            default -> {
                return;
            }
        }

        addSubtractResources(new Resources(playerName, foodDelta, woodDelta, oreDelta, emeraldDelta));
    }

    public static int trySendingResources(String sendingPlayerName, String receivingPlayerName, ResourceName resourceName, int amount) {
        Resources res = null;
        for (Resources resources : resourcesList)
            if (resources.ownerName.equals(sendingPlayerName))
                res = resources;
        if (res == null)
            return 0;

        if (sendingPlayerName.equals(receivingPlayerName)) {
            PlayerServerEvents.sendMessageToPlayer(sendingPlayerName, "server.resources.siege_of_settlements.sending_to_self");
            return 0;
        } else if (!PlayerServerEvents.isRTSPlayer(receivingPlayerName)) {
            PlayerServerEvents.sendMessageToPlayer(sendingPlayerName, "server.resources.siege_of_settlements.not_rts_player");
            return 0;
        } else if (!canAfford(sendingPlayerName, resourceName, amount)) {
            ResourcesClientboundPacket.warnInsufficientResources(sendingPlayerName,
                    resourceName != ResourceName.FOOD || canAfford(sendingPlayerName, ResourceName.FOOD, amount),
                    resourceName != ResourceName.WOOD || canAfford(sendingPlayerName, ResourceName.WOOD, amount),
                    resourceName != ResourceName.ORE || canAfford(sendingPlayerName, ResourceName.ORE, amount),
                    resourceName != ResourceName.EMERALDS || canAfford(sendingPlayerName, ResourceName.EMERALDS, amount)
            );
            return 0;
        } else {
            addSubtractResources(new Resources(sendingPlayerName,
                    resourceName == ResourceName.FOOD ? -amount : 0,
                    resourceName == ResourceName.WOOD ? -amount : 0,
                    resourceName == ResourceName.ORE ? -amount : 0,
                    resourceName == ResourceName.EMERALDS ? -amount : 0
            ));
            addSubtractResources(new Resources(receivingPlayerName,
                    resourceName == ResourceName.FOOD ? amount : 0,
                    resourceName == ResourceName.WOOD ? amount : 0,
                    resourceName == ResourceName.ORE ? amount : 0,
                    resourceName == ResourceName.EMERALDS ? amount : 0
            ));
            switch (resourceName) {
                case FOOD -> {
                    PlayerServerEvents.sendMessageToPlayer(sendingPlayerName, "server.resources.siege_of_settlements.sent_food", false, amount, receivingPlayerName);
                    PlayerServerEvents.sendMessageToPlayer(receivingPlayerName, "server.resources.siege_of_settlements.received_food", false, amount, sendingPlayerName);
                }
                case WOOD -> {
                    PlayerServerEvents.sendMessageToPlayer(sendingPlayerName, "server.resources.siege_of_settlements.sent_wood", false, amount, receivingPlayerName);
                    PlayerServerEvents.sendMessageToPlayer(receivingPlayerName, "server.resources.siege_of_settlements.received_wood", false, amount, sendingPlayerName);
                }
                case ORE -> {
                    PlayerServerEvents.sendMessageToPlayer(sendingPlayerName, "server.resources.siege_of_settlements.sent_ore", false, amount, receivingPlayerName);
                    PlayerServerEvents.sendMessageToPlayer(receivingPlayerName, "server.resources.siege_of_settlements.received_ore", false, amount, sendingPlayerName);
                }
                case EMERALDS -> {
                    PlayerServerEvents.sendMessageToPlayer(sendingPlayerName, "server.resources.siege_of_settlements.sent_emeralds", false, amount, receivingPlayerName);
                    PlayerServerEvents.sendMessageToPlayer(receivingPlayerName, "server.resources.siege_of_settlements.received_emeralds", false, amount, sendingPlayerName);
                }
            }
            return 1;
        }
    }

    private static final Map<Block, Block> FALLING_LOGS = new HashMap<>();
    static {
        FALLING_LOGS.put(Blocks.OAK_LOG, BlockRegistrar.FALLING_OAK_LOG.get());
        FALLING_LOGS.put(Blocks.SPRUCE_LOG, BlockRegistrar.FALLING_SPRUCE_LOG.get());
        FALLING_LOGS.put(Blocks.BIRCH_LOG, BlockRegistrar.FALLING_BIRCH_LOG.get());
        FALLING_LOGS.put(Blocks.JUNGLE_LOG, BlockRegistrar.FALLING_JUNGLE_LOG.get());
        FALLING_LOGS.put(Blocks.ACACIA_LOG, BlockRegistrar.FALLING_ACACIA_LOG.get());
        FALLING_LOGS.put(Blocks.DARK_OAK_LOG, BlockRegistrar.FALLING_DARK_OAK_LOG.get());
        FALLING_LOGS.put(Blocks.MANGROVE_LOG, BlockRegistrar.FALLING_MANGROVE_LOG.get());
        FALLING_LOGS.put(Blocks.OAK_WOOD, BlockRegistrar.FALLING_OAK_LOG.get());
        FALLING_LOGS.put(Blocks.SPRUCE_WOOD, BlockRegistrar.FALLING_SPRUCE_LOG.get());
        FALLING_LOGS.put(Blocks.BIRCH_WOOD, BlockRegistrar.FALLING_BIRCH_LOG.get());
        FALLING_LOGS.put(Blocks.JUNGLE_WOOD, BlockRegistrar.FALLING_JUNGLE_LOG.get());
        FALLING_LOGS.put(Blocks.ACACIA_WOOD, BlockRegistrar.FALLING_ACACIA_LOG.get());
        FALLING_LOGS.put(Blocks.DARK_OAK_WOOD, BlockRegistrar.FALLING_DARK_OAK_LOG.get());
        FALLING_LOGS.put(Blocks.MANGROVE_WOOD, BlockRegistrar.FALLING_MANGROVE_LOG.get());
        FALLING_LOGS.put(Blocks.WARPED_STEM, BlockRegistrar.FALLING_WARPED_STEM.get());
        FALLING_LOGS.put(Blocks.WARPED_HYPHAE, BlockRegistrar.FALLING_WARPED_STEM.get());
        FALLING_LOGS.put(Blocks.CRIMSON_STEM, BlockRegistrar.FALLING_CRIMSON_STEM.get());
        FALLING_LOGS.put(Blocks.CRIMSON_HYPHAE, BlockRegistrar.FALLING_CRIMSON_STEM.get());
    }
}
