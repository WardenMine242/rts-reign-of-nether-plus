package com.solegendary.siege_of_settlements.player;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.HeroAbility;
import com.solegendary.siege_of_settlements.alliance.AlliancesServerEvents;
import com.solegendary.siege_of_settlements.alliance.AllyCommand;
import com.solegendary.siege_of_settlements.building.*;
import com.solegendary.siege_of_settlements.building.buildings.neutral.Beacon;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.gamemode.GameMode;
import com.solegendary.siege_of_settlements.gamemode.GameModeClientboundPacket;
import com.solegendary.siege_of_settlements.guiscreen.TopdownGuiContainer;
import com.solegendary.siege_of_settlements.hero.HeroClientboundPacket;
import com.solegendary.siege_of_settlements.hero.HeroServerEvents;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.registrars.GameRuleRegistrar;
import com.solegendary.siege_of_settlements.research.ResearchClientboundPacket;
import com.solegendary.siege_of_settlements.research.ResearchServerEvents;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.Resources;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import com.solegendary.siege_of_settlements.sandbox.SandboxServer;
import com.solegendary.siege_of_settlements.startpos.StartPosServerEvents;
import com.solegendary.siege_of_settlements.survival.SurvivalServerEvents;
import com.solegendary.siege_of_settlements.time.TimeServerEvents;
import com.solegendary.siege_of_settlements.time.TimeUtils;
import com.solegendary.siege_of_settlements.tutorial.TutorialServerEvents;
import com.solegendary.siege_of_settlements.unit.UnitServerEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.AttackerUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.HeroUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.interfaces.WorkerUnit;
import com.solegendary.siege_of_settlements.unit.packets.UnitSyncClientboundPacket;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.solegendary.siege_of_settlements.building.BuildingServerEvents.saveBuildings;
import static com.solegendary.siege_of_settlements.time.TimeUtils.getWaveSurvivalTimeModifier;
import static net.minecraft.world.level.GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK;

// this class tracks all available players so that any serverside functions that need to affect the player can be
// performed here by sending a client->server packet containing MC.player.getId()

public class PlayerServerEvents {

    // list of what gamemode these players should be in when outside of RTS cam
    private static final Map<String, GameType> playerDefaultGameModes = new HashMap<>();
    private static final Map<String, Boolean> playerGuiOpenStatus = new HashMap<>();

    public static final ArrayList<ServerPlayer> players = new ArrayList<>();
    public static final ArrayList<ServerPlayer> orthoviewPlayers = new ArrayList<>();
    private static final Map<UUID, Vec3> orthoviewFrozenPositions = new HashMap<>();
    private static final Map<UUID, Vec3> orthoviewCameraPositions = new HashMap<>();
    // players that are currently playing a match
    public static final List<RTSPlayer> rtsPlayers = Collections.synchronizedList(new ArrayList<>());
    // list of players after a match ends
    public static final List<RTSPlayer> postGameRtsPlayers = Collections.synchronizedList(new ArrayList<>());
    public static boolean rtsLocked = false; // can players join as RTS players or not?
    public static boolean rtsSyncingEnabled = true; // will logging in players sync units and buildings?

    private static final int MONSTER_START_TIME_OF_DAY = 500; // 500 = dawn, 6500 = noon, 12500 = dusk

    public static final int TICKS_TO_REVEAL = 60 * ResourceCost.TICKS_PER_SECOND;

    public static long rtsGameTicks = 0; // ticks up as long as there is at least 1 rtsPlayer

    public static ServerLevel serverLevel = null;
    private static final String FIRST_SPAWN_STEAK_KEY = "ron_first_spawn_steak_given";

    // warpten - faster building/unit production
    // operationcwal - faster resource gathering
    // modifythephasevariance - ignore building requirements
    // medievalman - get all research (cannot reverse)
    // greedisgood X - gain X of each resource
    // foodforthought - ignore soft population caps
    // thereisnospoon - allow changing survival wave by clicking the wave indicator and using debug commands
    // slipslopslap - monster units are unaffected by sunlight
    // wouldyoukindly - allow control of non-unit mobs in RTS mode
    // thebeastofcaerbannog - spawns the Killer Rabbit
    // elitetaurenchieftain - levels all owned heroes to 10
    public static final List<String> singleWordCheats = List.of(
        "warpten",
        "operationcwal",
        "modifythephasevariance",
        "medievalman",
        "foodforthought",
        "thereisnospoon",
        "slipslopslap",
        "wouldyoukindly"
    );

    public static void saveRTSPlayers() {
        if (serverLevel == null) {
            return;
        }
        RTSPlayerSaveData data = RTSPlayerSaveData.getInstance(serverLevel);
        data.rtsPlayers.clear();
        data.rtsPlayers.addAll(rtsPlayers);
        data.save();
        serverLevel.getDataStorage().save();
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent evt) {
        ServerLevel level = evt.getServer().getLevel(Level.OVERWORLD);

        if (level != null) {
            RTSPlayerSaveData data = RTSPlayerSaveData.getInstance(level);

            rtsPlayers.clear();
            rtsPlayers.addAll(data.rtsPlayers);

            for (RTSPlayer rtsPlayer : rtsPlayers) {
                if (rtsPlayer.faction == Faction.NONE) {
                    GameModeClientboundPacket.setAndLockAllClientGameModes(GameMode.SANDBOX);
                    enableAllCheats(rtsPlayer.name);
                    break;
                }
            }
            UnitServerEvents.maxPopulation = level.getGameRules().getInt(GameRuleRegistrar.MAX_POPULATION);

            level.getGameRules().getRule(RULE_DISABLE_ELYTRA_MOVEMENT_CHECK).set(true, evt.getServer());
        }
    }

    private static final int SAVE_TICKS_MAX = 1200;
    private static int saveTicks = 0;
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent evt) {
        saveRTSPlayers();
    }

    public static boolean isRTSPlayer(String playerName) {
        synchronized (rtsPlayers) {
            for (RTSPlayer p : rtsPlayers) {
                if (p.name.equals(playerName)) return true;
            }
            return false;
        }
    }

    @Nullable
    public static RTSPlayer getRTSPlayer(String playerName) {
        synchronized (rtsPlayers) {
            for (RTSPlayer rtsPlayer : rtsPlayers)
                if (rtsPlayer.name.equals(playerName))
                    return rtsPlayer;
        }
        return null;
    }

    public static boolean isRTSPlayer(int id) {
        synchronized (rtsPlayers) {
            for (RTSPlayer p : rtsPlayers) {
                if (p.id == id) return true;
            }
            return false;
        }
    }

    public static boolean isBot(String playerName) {
        synchronized (rtsPlayers) {
            for (RTSPlayer rtsPlayer : rtsPlayers)
                if (rtsPlayer.name.equalsIgnoreCase(playerName)) {
                    return rtsPlayer.isBot();
                }
        }
        return false;
    }

    public static boolean isBot(int id) {
        synchronized (rtsPlayers) {
            for (RTSPlayer rtsPlayer : rtsPlayers)
                if (rtsPlayer.id == id) {
                    return rtsPlayer.isBot();
                }
        }
        return false;
    }

    public static boolean isGameActive() {
        return !rtsPlayers.isEmpty();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent evt) {
        serverLevel = evt.getServer().getLevel(Level.OVERWORLD);

        synchronized (rtsPlayers) {
            if (evt.phase == TickEvent.Phase.END) {
                for (RTSPlayer rtsPlayer : rtsPlayers)
                    rtsPlayer.serverTick();

                for (RTSPlayer rtsPlayer : rtsPlayers) {
                    if (rtsPlayer.beaconOwnerTicks == Beacon.getTicksToWin(serverLevel)) {
                        PlayerServerEvents.beaconVictory(rtsPlayer.name);
                        break;
                    }
                }
                if (rtsPlayers.isEmpty()) {
                    rtsGameTicks = 0;
                } else {
                    rtsGameTicks += 1;
                    if (rtsGameTicks % 200 == 0) {
                        PlayerClientboundPacket.syncRtsGameTime(rtsGameTicks);
                    }
                    if (rtsGameTicks % 20 == 0) {
                        for (RTSPlayer rtsPlayer : rtsPlayers) {
                            PlayerClientboundPacket.syncBeaconOwnerTicks(rtsPlayer.name, rtsPlayer.beaconOwnerTicks);
                        }
                    }
                }
            }
        }
        if (evt.phase == TickEvent.Phase.END) {
            saveTicks += 1;
            if (saveTicks >= SAVE_TICKS_MAX) {
                ServerLevel level = evt.getServer().getLevel(Level.OVERWORLD);
                if (level != null) {
                    saveRTSPlayers();
                    saveTicks = 0;
                }
            }
            freezeOrthoviewPlayers();
        }
    }

    private static void freezeOrthoviewPlayers() {
        if (orthoviewPlayers.isEmpty()) {
            orthoviewFrozenPositions.clear();
            orthoviewCameraPositions.clear();
            return;
        }

        orthoviewFrozenPositions.keySet().removeIf(uuid ->
            orthoviewPlayers.stream().noneMatch(p -> p != null && p.getUUID().equals(uuid)));
        orthoviewCameraPositions.keySet().removeIf(uuid ->
            orthoviewPlayers.stream().noneMatch(p -> p != null && p.getUUID().equals(uuid)));

        for (ServerPlayer player : orthoviewPlayers) {
            if (player == null || !player.isAlive()) {
                continue;
            }

            Vec3 frozen = orthoviewFrozenPositions.computeIfAbsent(player.getUUID(), id -> player.position());

            if (player.position().distanceToSqr(frozen) > 1.0e-6) {
                player.teleportTo(frozen.x, frozen.y, frozen.z);
            }
            player.setDeltaMovement(Vec3.ZERO);
            player.fallDistance = 0;
        }
    }

    @Nullable
    public static Vec3 getOrthoviewCameraPos(ServerPlayer player) {
        if (player == null) {
            return null;
        }
        return orthoviewCameraPositions.get(player.getUUID());
    }

    public static Vec3 getOrthoviewViewPos(ServerPlayer player) {
        Vec3 cameraPos = getOrthoviewCameraPos(player);
        return cameraPos != null ? cameraPos : player.position();
    }

    private static void clearOrthoviewCameraData(ServerPlayer player, boolean unloadSentChunks) {
        if (player == null) {
            return;
        }

        orthoviewCameraPositions.remove(player.getUUID());

        if (unloadSentChunks) {
            restorePlayerChunkCache(player);
        }
    }

    private static void restorePlayerChunkCache(ServerPlayer player) {
        if (player == null || player.server == null) {
            return;
        }
        ChunkPos playerChunk = player.chunkPosition();
        int viewDistance = Math.max(2, player.server.getPlayerList().getViewDistance());
        player.connection.send(new ClientboundSetChunkCacheCenterPacket(playerChunk.x, playerChunk.z));
        player.connection.send(new ClientboundSetChunkCacheRadiusPacket(viewDistance));
    }

    private static void syncUnits() {
        for (LivingEntity entity : UnitServerEvents.getAllUnits()) {
            if (entity instanceof Unit unit) {
                UnitSyncClientboundPacket.sendSyncResourcesPacket(unit);
                UnitSyncClientboundPacket.sendSyncOwnerNamePacket(unit);
                UnitSyncClientboundPacket.sendSyncAnchorPosPacket(entity, unit.getAnchor());
            }
            if (entity instanceof HeroUnit hero) {
                HeroClientboundPacket.setExperience(entity.getId(), hero.getExperience());
                HeroClientboundPacket.setSkillPoints(entity.getId(), hero.getSkillPoints());
                HeroClientboundPacket.setCharges(entity.getId(), hero.getChargesForSaveData());
                List<HeroAbility> abls = hero.getHeroAbilities();
                if (abls.size() > 0)
                    HeroClientboundPacket.setAbilityRank(entity.getId(), abls.get(0).getRank(hero), 0);
                if (abls.size() > 1)
                    HeroClientboundPacket.setAbilityRank(entity.getId(), abls.get(1).getRank(hero), 1);
                if (abls.size() > 2)
                    HeroClientboundPacket.setAbilityRank(entity.getId(), abls.get(2).getRank(hero), 2);
                if (abls.size() > 3)
                    HeroClientboundPacket.setAbilityRank(entity.getId(), abls.get(3).getRank(hero), 3);
            }
        }
    }

    private static void giveFirstSpawnSteak(ServerPlayer player) {
        CompoundTag rootTag = player.getPersistentData();
        CompoundTag persistedTag = rootTag.getCompound(Player.PERSISTED_NBT_TAG);

        if (persistedTag.getBoolean(FIRST_SPAWN_STEAK_KEY)) {
            return;
        }

        ItemStack steaks = new ItemStack(Items.COOKED_BEEF, 4);
        if (!player.getInventory().add(steaks) && !steaks.isEmpty()) {
            player.drop(steaks, false);
        }

        persistedTag.putBoolean(FIRST_SPAWN_STEAK_KEY, true);
        rootTag.put(Player.PERSISTED_NBT_TAG, persistedTag);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent evt) {
        ServerPlayer serverPlayer = (ServerPlayer) evt.getEntity();
        giveFirstSpawnSteak(serverPlayer);

        players.add((ServerPlayer) evt.getEntity());
        String playerName = serverPlayer.getName().getString();
        SiegeOfSettlements.LOGGER.info("Player logged in: " + playerName + ", id: " + serverPlayer.getId());

        // if a player is looking directly at a frozenchunk on login, they may load in the real blocks before
        // they are frozen so move them away then BuildingClientEvents.placeBuilding moves them to their base later
        // don't do this if they don't own any buildings
        /*
        if (isRTSPlayer(playerName) && rtsSyncingEnabled) {
            for (BuildingPlacement building : BuildingServerEvents.getBuildings()) {
                if (building.ownerName.equals(playerName)) {
                    movePlayer(serverPlayer.getId(), 0, ORTHOVIEW_PLAYER_BASE_Y, 0);
                    break;
                }
            }
        }
         */
        if (rtsSyncingEnabled) {
            MinecraftServer server = evt.getEntity().level().getServer();
            if (server == null || !server.isDedicatedServer()) {
                CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS).execute(PlayerServerEvents::syncUnits);
            } else {
                syncUnits();
            }
            ResearchServerEvents.syncResearch(playerName);
            ResearchServerEvents.syncCheats(playerName);
        }
        for (ServerPlayer orthoviewPlayer : orthoviewPlayers) {
            if (orthoviewPlayer.getId() != evt.getEntity().getId()) continue;
            orthoviewPlayers.add((ServerPlayer) evt.getEntity());
        }
        if (!TutorialServerEvents.isEnabled()) {
            if (!isRTSPlayer(serverPlayer.getId())) {
                serverPlayer.sendSystemMessage(Component.translatable("tutorial.siege_of_settlements.welcome")
                    .withStyle(Style.EMPTY.withBold(true)));
                serverPlayer.sendSystemMessage(Component.translatable("tutorial.siege_of_settlements.join"));
                serverPlayer.sendSystemMessage(Component.translatable("tutorial.siege_of_settlements.help"));
                serverPlayer.sendSystemMessage(Component.translatable("tutorial.siege_of_settlements.controls"));
                if (rtsLocked) {
                    serverPlayer.sendSystemMessage(Component.literal(""));
                    serverPlayer.sendSystemMessage(Component.translatable("tutorial.siege_of_settlements.locked"));
                }
            } else {
                serverPlayer.sendSystemMessage(Component.translatable("tutorial.siege_of_settlements.welcome_back")
                    .withStyle(Style.EMPTY.withBold(true)));
            }
            if (serverPlayer.hasPermissions(4)) {
                serverPlayer.sendSystemMessage(Component.literal(""));
                serverPlayer.sendSystemMessage(Component.translatable("tutorial.siege_of_settlements.op_commands"));
                serverPlayer.sendSystemMessage(Component.translatable("tutorial.siege_of_settlements.fog"));
                serverPlayer.sendSystemMessage(Component.translatable("tutorial.siege_of_settlements.lock"));;
                serverPlayer.sendSystemMessage(Component.translatable("tutorial.siege_of_settlements.reset"));
                serverPlayer.sendSystemMessage(Component.literal(""));
            }
            if (!rtsSyncingEnabled) {
                serverPlayer.sendSystemMessage(Component.literal(""));
                serverPlayer.sendSystemMessage(Component.translatable("tutorial.siege_of_settlements.sync_disabled1"));
                serverPlayer.sendSystemMessage(Component.translatable("tutorial.siege_of_settlements.sync_disabled2"));
                serverPlayer.sendSystemMessage(Component.literal(""));
            }
        }
        if (getRTSPlayer(playerName) == null) {
            PlayerClientboundPacket.removeRTSPlayer(playerName);
        }
        for (RTSPlayer rtsPlayer : rtsPlayers) {
            PlayerClientboundPacket.addRTSPlayer(rtsPlayer.name, rtsPlayer.faction, (long) rtsPlayer.id, rtsPlayer.startPosColorId);
        }

        if (rtsLocked) {
            PlayerClientboundPacket.lockRTS(playerName);
        } else {
            PlayerClientboundPacket.unlockRTS(playerName);
        }

        if (rtsSyncingEnabled) {
            PlayerClientboundPacket.enableStartRTS(playerName);
        } else {
            PlayerClientboundPacket.disableStartRTS(playerName);
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent evt) {
        int id = evt.getEntity().getId();
        SiegeOfSettlements.LOGGER.info("Player logged out: " + evt.getEntity().getName().getString() + ", id: " + id);
        players.removeIf(player -> player.getId() == id);
        orthoviewPlayers.removeIf(player -> player.getId() == id);
        orthoviewFrozenPositions.remove(evt.getEntity().getUUID());
        if (evt.getEntity() instanceof ServerPlayer serverPlayer) {
            clearOrthoviewCameraData(serverPlayer, true);
        } else {
            orthoviewCameraPositions.remove(evt.getEntity().getUUID());
        }
    }

    public static void startRTS(int playerId, Vec3 pos, Faction faction) {
        startRTS(playerId, pos, faction, 0);
    }

    // readied start is a simultaneous start from players using RTS start pos blocks, difference being:
    // - places the capitol foundations automatically
    // - spawns workers outside the foundations
    // - no start messages are sent other than the one from the countdown
    public static void startRTS(int playerId, Vec3 pos, Faction faction, int startPosColorId) {
        synchronized (rtsPlayers) {
            boolean readiedStart = startPosColorId != 0;

            ServerPlayer serverPlayer = null;
            for (ServerPlayer player : players)
                if (player.getId() == playerId)
                    serverPlayer = player;

            if (serverPlayer == null) {
                return;
            }
            if (rtsLocked) {
                serverPlayer.sendSystemMessage(Component.literal(""));
                serverPlayer.sendSystemMessage(Component.translatable("server.siege_of_settlements.locked"));
                serverPlayer.sendSystemMessage(Component.literal(""));
                return;
            }
            if (faction == Faction.NONE
                    && serverPlayer.gameMode.getGameModeForPlayer() == GameType.SURVIVAL
                    && !serverPlayer.hasPermissions(2)) {
                return;
            }
            if (isRTSPlayer(serverPlayer.getId())) {
                serverPlayer.sendSystemMessage(Component.literal(""));
                serverPlayer.sendSystemMessage(Component.translatable("server.siege_of_settlements.already_started"));
                serverPlayer.sendSystemMessage(Component.literal(""));
                return;
            }
            if (serverPlayer.level().getWorldBorder().getDistanceToBorder(pos.x, pos.z) < 1 && faction != Faction.NONE) {
                serverPlayer.sendSystemMessage(Component.literal(""));
                serverPlayer.sendSystemMessage(Component.translatable("server.siege_of_settlements.outside_border"));
                serverPlayer.sendSystemMessage(Component.literal(""));
                return;
            }

            EntityType<? extends Unit> entityType = switch (faction) {
                case VILLAGERS -> EntityRegistrar.VILLAGER_UNIT.get();
                case MONSTERS -> EntityRegistrar.ZOMBIE_VILLAGER_UNIT.get();
                case PIGLINS -> EntityRegistrar.GRUNT_UNIT.get();
                case NONE -> null;
            };
            rtsPlayers.add(RTSPlayer.getNewPlayer(
                    serverPlayer.getName().getString(),
                    faction,
                    serverPlayer.getId(),
                    startPosColorId
            ));
            String playerName = serverPlayer.getName().getString();
            ResourcesServerEvents.assignResources(playerName);
            PlayerClientboundPacket.addRTSPlayer(playerName, faction, (long) serverPlayer.getId(), startPosColorId);

            ServerLevel level = (ServerLevel) serverPlayer.level();
            ArrayList<Entity> workers = new ArrayList<>();
            for (int i = -1; i <= 1; i++) {
                Entity entity = entityType != null ? entityType.create(level) : null;
                if (entity != null) {
                    BlockPos bp = MiscUtil.getHighestNonAirBlock(level, new BlockPos((int) (pos.x + i), 0, (int) pos.z))
                        .above()
                        .above();
                    ((Unit) entity).setOwnerName(playerName);
                    entity.moveTo(bp, 0, 0);
                    if (!readiedStart)
                        level.addFreshEntity(entity);
                    workers.add(entity);
                }
            }

            if (faction != Faction.NONE) {
                if (SurvivalServerEvents.isEnabled()) {
                    level.setDayTime(TimeUtils.DAWN + getWaveSurvivalTimeModifier(SurvivalServerEvents.getDifficulty()));
                    for (RTSPlayer rtsPlayer : rtsPlayers)
                        if (!rtsPlayer.name.equals(playerName))
                            AlliancesServerEvents.addAlliance(rtsPlayer.name, playerName);
                } else {
                    level.setDayTime(MONSTER_START_TIME_OF_DAY);
                }
                ResearchServerEvents.removeAllCheatsFor(playerName);
            } else {
                enableAllCheats(playerName);
            }
            ResourcesServerEvents.resetResources(playerName);

            if (readiedStart) {
                Building building = null;
                ArrayList<BuildingBlock> blocks = null;

                switch (faction) {
                    case VILLAGERS -> {
                        building = Buildings.TOWN_CENTRE;
                        blocks = Buildings.TOWN_CENTRE.getRelativeBlockData(level);
                    }
                    case MONSTERS -> {
                        building = Buildings.MAUSOLEUM;
                        blocks = Buildings.MAUSOLEUM.getRelativeBlockData(level);
                    }
                    case PIGLINS -> {
                        building = Buildings.CENTRAL_PORTAL;
                        blocks = Buildings.CENTRAL_PORTAL.getRelativeBlockData(level);
                    }
                };
                if (building != null) {
                    BlockPos bp = getBuildingOriginPos(new BlockPos((int) pos.x, (int) pos.y, (int) pos.z), blocks);
                    for (int i = 0; i < workers.size(); i++) {
                        workers.get(i).moveTo(bp.offset(i, 0, 0), 0, 0);
                        level.addFreshEntity(workers.get(i));
                    }
                    var workerIds = new int[workers.size()];
                    for (int i = 0; i < workers.size(); i++) {
                        workerIds[i] = workers.get(i).getId();
                    }
                    BuildingServerEvents.placeBuilding(building, bp, Rotation.NONE, playerName, workerIds, false, false);
                }
                for (RTSPlayer rtsPlayer : rtsPlayers) {
                    String playerName1 = rtsPlayer.name;
                    String playerName2 = serverPlayer.getName().getString();
                    if (!playerName1.equals(playerName2) && rtsPlayer.startPosColorId == startPosColorId) {
                        AlliancesServerEvents.addAlliance(playerName1, playerName2);
                    }
                }
            }

            if (!TutorialServerEvents.isEnabled() && !readiedStart) {
                serverPlayer.sendSystemMessage(Component.literal(""));
                if (faction == Faction.NONE)
                    sendMessageToAllPlayers("server.siege_of_settlements.started_sandbox", true, playerName);
                else
                    sendMessageToAllPlayers("server.siege_of_settlements.started", true, playerName);
                sendMessageToAllPlayers("server.siege_of_settlements.total_players", false, rtsPlayers.size());
            }
            PlayerClientboundPacket.syncRtsGameTime(rtsGameTicks);
            saveRTSPlayers();
        }
    }

    public static BlockPos getBuildingOriginPos(BlockPos bp, ArrayList<BuildingBlock> blocks) {
        Vec3i buildingDimensions = BuildingUtils.getBuildingSize(blocks);
        int xRadius = buildingDimensions.getX() / 2;
        int zRadius = buildingDimensions.getZ() / 2;
        return bp.offset(-xRadius, 0 , -zRadius);
    }

    public static void startRTSBot(String name, Vec3 pos, Faction faction) {
        synchronized (rtsPlayers) {
            ServerLevel level;
            if (players.isEmpty()) {
                return;
            } else {
                level = (ServerLevel) players.get(0).level();
            }

            EntityType<? extends Unit> entityType = switch (faction) {
                case VILLAGERS -> EntityRegistrar.VILLAGER_UNIT.get();
                case MONSTERS -> EntityRegistrar.ZOMBIE_VILLAGER_UNIT.get();
                case PIGLINS -> EntityRegistrar.GRUNT_UNIT.get();
                case NONE -> null;
            };
            RTSPlayer bot = RTSPlayer.getNewBot(name, faction);
            rtsPlayers.add(bot);
            ResourcesServerEvents.assignResources(bot.name);

            for (int i = -1; i <= 1; i++) {
                Entity entity = entityType != null ? entityType.create(level) : null;
                if (entity != null) {
                    BlockPos bp = MiscUtil.getHighestNonAirBlock(level, new BlockPos((int) (pos.x + i), 0, (int) pos.z))
                        .above()
                        .above();
                    ((Unit) entity).setOwnerName(bot.name);
                    entity.moveTo(bp, 0, 0);
                    level.addFreshEntity(entity);
                }
            }
            if (faction == Faction.MONSTERS) {
                level.setDayTime(MONSTER_START_TIME_OF_DAY);
            }
            ResourcesServerEvents.resetResources(bot.name);

            if (!TutorialServerEvents.isEnabled()) {
                sendMessageToAllPlayers("server.siege_of_settlements.bot_added", true, bot.name);
                sendMessageToAllPlayers("server.siege_of_settlements.total_players", false, rtsPlayers.size());
            }
            saveRTSPlayers();
        }
    }

    @SubscribeEvent
    public static void onPlayerChat(ServerChatEvent evt) {
        if (evt.getPlayer().hasPermissions(4)) {
            String msg = evt.getMessage().getString();
            String[] words = msg.split(" ");
            String playerName = evt.getPlayer().getName().getString();

            if (words.length == 1 && words[0].equalsIgnoreCase("thebeastofcaerbannog")) {
                UnitServerEvents.spawnMob(EntityRegistrar.getEntityType("Killer Rabbit"), serverLevel, evt.getPlayer().getOnPos(), playerName);
                sendMessageToAllPlayers("server.siege_of_settlements.used_cheat",false, playerName, words[0]);
            }

            if (words.length == 1 && words[0].equalsIgnoreCase("elitetaurenchieftain")) {
                for (LivingEntity entity : UnitServerEvents.getAllUnits()) {
                    if (entity instanceof HeroUnit heroUnit && ((Unit) heroUnit).getOwnerName().equals(playerName)) {
                        heroUnit.addExperience(10000);
                        heroUnit.setSkillPoints(10);
                        heroUnit.setMana(heroUnit.getMaxMana());
                        HeroClientboundPacket.setExperience(entity.getId(), heroUnit.getExperience());
                        HeroClientboundPacket.setSkillPoints(entity.getId(), 10);
                    }
                }
                sendMessageToAllPlayers("server.siege_of_settlements.used_cheat",false, playerName, words[0]);
            }

            if (words.length == 2) {
                try {
                    if (words[0].equalsIgnoreCase("greedisgood")) {
                        int amount = Integer.parseInt(words[1]);
                        if (amount > 0) {
                            ResourcesServerEvents.addSubtractResources(new Resources(playerName,
                                    amount,
                                    amount,
                                    amount,
                                    amount
                            ));
                            evt.setCanceled(true);
                            sendMessageToAllPlayers("server.siege_of_settlements.used_cheat_amount",
                                    false,
                                    playerName,
                                    words[0],
                                    Integer.toString(amount)
                            );
                        }
                    }
                } catch (NumberFormatException err) {
                    SiegeOfSettlements.LOGGER.error(err);
                }
            }

            if (words.length == 3) {
                try {
                    if (words[0].equalsIgnoreCase("greedisgood")) {
                        int amount = Integer.parseInt(words[2]);
                        if (amount > 0 && List.of("food", "wood", "ore", "emeralds").contains(words[1].toLowerCase())) {
                            switch (words[1].toLowerCase()) {
                                case "food" -> ResourcesServerEvents.addSubtractResources(new Resources(playerName, amount, 0, 0));
                                case "wood" -> ResourcesServerEvents.addSubtractResources(new Resources(playerName, 0, amount, 0));
                                case "ore" -> ResourcesServerEvents.addSubtractResources(new Resources(playerName, 0, 0, amount));
                                case "emeralds" -> ResourcesServerEvents.addSubtractResources(new Resources(playerName, 0, 0, 0, amount));
                            }
                            evt.setCanceled(true);
                            sendMessageToAllPlayers("server.siege_of_settlements.used_cheat_amount",
                                    false,
                                    playerName,
                                    words[0] + " " + words[1],
                                    Integer.toString(amount)
                            );
                        }
                    }
                } catch (NumberFormatException err) {
                    SiegeOfSettlements.LOGGER.error(err);
                }
            }

            for (String cheatName : singleWordCheats) {
                if (words.length == 1 && words[0].equalsIgnoreCase(cheatName)) {
                    if (ResearchServerEvents.playerHasCheat(playerName, cheatName)) {
                        ResearchServerEvents.removeCheat(playerName, cheatName);
                        ResearchClientboundPacket.removeCheat(playerName, cheatName);
                        evt.setCanceled(true);
                        sendMessageToAllPlayers("server.siege_of_settlements.disabled_cheat", false, playerName, cheatName);
                    } else {
                        ResearchServerEvents.addCheat(playerName, cheatName);
                        ResearchClientboundPacket.addCheat(playerName, cheatName);
                        evt.setCanceled(true);
                        sendMessageToAllPlayers("server.siege_of_settlements.enabled_cheat", false, playerName, cheatName);
                    }
                }
            }

            // apply all cheats - NOTE can cause concurrentModificationException clientside
            if (words.length == 1 && words[0].equalsIgnoreCase("allcheats") && (
                playerName.equalsIgnoreCase("solegendary") ||
                playerName.equalsIgnoreCase("altsolegendary"))
            ) {
                ResourcesServerEvents.addSubtractResources(new Resources(playerName, 99999, 99999, 99999, 99999));
                UnitServerEvents.maxPopulation = 99999;
                enableAllCheats(playerName);
                evt.setCanceled(true);
                sendMessageToAllPlayers("server.siege_of_settlements.all_cheats", false, playerName);
            }
        }
    }

    public static void enableAllCheats(String playerName) {
        for (String cheatName : singleWordCheats) {
            ResearchServerEvents.addCheat(playerName, cheatName);
            ResearchClientboundPacket.addCheat(playerName, cheatName);
        }
    }

    public static void enableOrthoview(int id) {
        ServerPlayer player = getPlayerById(id);
        if (player == null) {
            return;
        }
        player.removeAllEffects();

        orthoviewPlayers.removeIf(p -> p.getId() == id);
        orthoviewPlayers.add(player);
        orthoviewFrozenPositions.put(player.getUUID(), player.position());
        orthoviewCameraPositions.put(player.getUUID(), player.position());
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0;

        // Immediately recenter vanilla chunk streaming/simulation around the initial camera location.
        if (player.level() instanceof ServerLevel level) {
            level.getChunkSource().move(player);
        }
    }

    public static void syncOrthoviewCamera(int id, double x, double y, double z) {
        ServerPlayer player = getPlayerById(id);
        if (player == null) {
            return;
        }
        if (orthoviewPlayers.stream().noneMatch(p -> p != null && p.getId() == id)) {
            return;
        }
        orthoviewCameraPositions.put(player.getUUID(), new Vec3(x, y, z));

        // Drive vanilla chunk/track/simulation updates from camera movement.
        // ChunkMap/ServerChunkCache logic is reused; ChunkMapMixin remaps player coords to camera coords.
        if (player.level() instanceof ServerLevel level) {
            level.getChunkSource().move(player);
        }
    }

    public static void disableOrthoview(int id) {
        orthoviewPlayers.removeIf(p -> p.getId() == id);
        ServerPlayer player = getPlayerById(id);
        if (player != null) {
            orthoviewFrozenPositions.remove(player.getUUID());
            clearOrthoviewCameraData(player, true);
            // After camera mode is disabled, restore vanilla tracking around real player body immediately.
            if (player.level() instanceof ServerLevel level) {
                level.getChunkSource().move(player);
            }
        }
    }

    private static ServerPlayer getPlayerById(int playerId) {
        for (ServerPlayer player : players) {
            if (playerId == player.getId()) {
                return player;
            }
        }
        return null;
    }

    public static void openTopdownGui(int playerId) {
        ServerPlayer serverPlayer = getPlayerById(playerId);

        if (serverPlayer != null) {
            // Open GUI server-side
            MenuConstructor provider = TopdownGuiContainer.getServerContainerProvider();
            MenuProvider namedProvider = new SimpleMenuProvider(provider, TopdownGuiContainer.TITLE);
            NetworkHooks.openScreen(serverPlayer, namedProvider);

            // Save original game mode for this session
            String playerName = serverPlayer.getName().getString();
            playerDefaultGameModes.put(playerName, serverPlayer.gameMode.getGameModeForPlayer());

            // Mark that this player has the GUI open
            playerGuiOpenStatus.put(playerName, true);


        } else {
            SiegeOfSettlements.LOGGER.warn("serverPlayer is null, cannot open topdown GUI");
        }
    }

    public static void closeTopdownGui(int playerId) {
        ServerPlayer serverPlayer = getPlayerById(playerId);

        if (serverPlayer != null) {
            String playerName = serverPlayer.getName().getString();

            // Ensure player had GUI open before attempting to close
            if (Boolean.TRUE.equals(playerGuiOpenStatus.get(playerName))) {
                // Restore the player’s original game mode if saved
                GameType originalGameType = playerDefaultGameModes.remove(playerName);

                if (originalGameType != null) {
                    serverPlayer.setGameMode(originalGameType);
                } else {
                    SiegeOfSettlements.LOGGER.warn("No original game mode found for player {}", playerName);
                }

                // Mark that the GUI is now closed
                playerGuiOpenStatus.remove(playerName);
            } else {
                SiegeOfSettlements.LOGGER.warn("Attempted to close GUI for player {} who didn't have it open", playerName);
            }
        } else {
            SiegeOfSettlements.LOGGER.warn("serverPlayer is null, cannot close topdown GUI");
        }
    }

    public static void movePlayer(int playerId, double x, double y, double z) {
        ServerPlayer serverPlayer = getPlayerById(playerId);
        serverPlayer.teleportTo(x, y, z);
    }

    public static void sendMessageToAllPlayers(String msg) {
        sendMessageToAllPlayers(msg, false);
    }

    public static void sendMessageToAllPlayers(String msg, int color, boolean bold, Object... formatArgs) {
        for (ServerPlayer player : players) {
            player.sendSystemMessage(Component.literal(""));
            if (bold) {
                player.sendSystemMessage(Component.translatable(msg, formatArgs)
                        .withStyle(Style.EMPTY.withBold(true).withColor(color)));
            } else {
                player.sendSystemMessage(Component.translatable(msg, formatArgs)
                        .withStyle(Style.EMPTY.withBold(false).withColor(color)));
            }
            player.sendSystemMessage(Component.literal(""));
        }
    }

    public static void sendMessageToAllPlayers(String msg, boolean bold,  Object... formatArgs) {
        sendMessageToAllPlayers(msg, 0xFFFFFF, bold, formatArgs);
    }

    public static void sendMessageToAllPlayersNoNewlines(String msg) {
        sendMessageToAllPlayersNoNewlines(msg, false);
    }

    public static void sendMessageToAllPlayersNoNewlines(String msg, boolean bold, Object... formatArgs) {
        for (ServerPlayer player : players) {
            if (bold) {
                player.sendSystemMessage(Component.translatable(msg, formatArgs).withStyle(Style.EMPTY.withBold(true)));
            } else {
                player.sendSystemMessage(Component.translatable(msg, formatArgs));
            }
        }
    }

    public static void sendMessageToPlayerNoNewLines(String playerName, String msg) {
        sendMessageToPlayerNoNewLines(playerName, msg, false);
    }

    public static void sendMessageToPlayer(String playerName, String msg) {
        sendMessageToPlayer(playerName, msg, false);
    }

    public static void sendMessageToPlayerNoNewLines(String playerName, String msg, boolean bold, Object... formatArgs) {
        for (ServerPlayer player : players) {
            if (player.getName().getString().equals(playerName)) {
                if (bold) {
                    player.sendSystemMessage(Component.translatable(msg, formatArgs).withStyle(Style.EMPTY.withBold(true)));
                } else {
                    player.sendSystemMessage(Component.translatable(msg, formatArgs));
                }
                return;
            }
        }
    }

    public static void sendMessageToPlayer(String playerName, String msg, boolean bold, Object... formatArgs) {
        for (ServerPlayer player : players) {
            if (player.getName().getString().equals(playerName)) {
                player.sendSystemMessage(Component.literal(""));
                if (bold) {
                    player.sendSystemMessage(Component.translatable(msg, formatArgs).withStyle(Style.EMPTY.withBold(true)));
                } else {
                    player.sendSystemMessage(Component.translatable(msg, formatArgs));
                }
                player.sendSystemMessage(Component.literal(""));
                return;
            }
        }
    }

    // defeat a player, giving them a defeat screen, removing all their unit/building control and removing them from
    // rtsPlayers
    public static void defeat(int playerId, String reason) {
        for (ServerPlayer player : players) {
            if (player.getId() == playerId) {
                defeat(player.getName().getString(), reason);
                return;
            }
        }
    }

    public static void defeat(String playerName, String reason) {
        if (SandboxServer.isSandboxPlayer(playerName))
            return;

        synchronized (rtsPlayers) {
            // Remove the defeated player from the list
            rtsPlayers.removeIf(rtsPlayer -> {
                if (rtsPlayer.name.equals(playerName)) {
                    sendMessageToAllPlayers("server.siege_of_settlements.is_defeated", true);
                    sendMessageToAllPlayers("server.siege_of_settlements.players_remaining", false, (rtsPlayers.size() - 1));

                    postGameRtsPlayers.add(rtsPlayer);

                    PlayerClientboundPacket.defeat(playerName);

                    // Remove ownership from all units and buildings of the defeated player
                    for (LivingEntity entity : UnitServerEvents.getAllUnits()) {
                        if (entity instanceof Unit unit && unit.getOwnerName().equals(playerName)) {
                            unit.resetBehaviours();
                            Unit.resetBehaviours(unit);
                            if (unit instanceof AttackerUnit aUnit)
                                AttackerUnit.resetBehaviours(aUnit);
                            if (unit instanceof WorkerUnit wUnit)
                                WorkerUnit.resetBehaviours(wUnit);
                            unit.setOwnerName("");
                        }
                    }
                    for (BuildingPlacement building : BuildingServerEvents.getBuildings()) {
                        if (building.ownerName.equals(playerName)) {
                            if (building instanceof ProductionPlacement productionBuilding)
                                productionBuilding.productionQueue.clear();
                            building.ownerName = "";
                        }
                    }
                    return true;
                }
                return false;
            });

            // Remove research data and resources associated with the defeated player
            saveRTSPlayers();
            ResearchServerEvents.removeAllResearchFor(playerName);
            ResearchServerEvents.syncResearch(playerName);
            ResearchServerEvents.saveResearch();
            ResearchServerEvents.removeAllCheatsFor(playerName);
            ResourcesServerEvents.resourcesList.removeIf(rl -> rl.ownerName.equals(playerName));

            // Check if only allied players are left or if a single player remains
            if (rtsPlayers.size() > 1) {
                // Get the set of remaining player names
                Set<String> remainingPlayers = new HashSet<>();
                for (RTSPlayer player : rtsPlayers) {
                    String name = player.name;
                    remainingPlayers.add(name);
                }

                // Use the first remaining player as a reference to find all connected allies
                String referencePlayer = remainingPlayers.iterator().next();
                Set<String> factionGroup = AlliancesServerEvents.getAllConnectedAllies(referencePlayer);

                // Check if all remaining players are part of the same alliance group
                if (remainingPlayers.equals(factionGroup)) {
                    // Declare victory for all players in the faction group
                    for (String winner : remainingPlayers) {
                        postGameRtsPlayers.add(getRTSPlayer(winner));
                        sendMessageToAllPlayers("server.siege_of_settlements.victory_alliance", true, winner);
                        PlayerClientboundPacket.victory(winner);
                    }
                }
            } else if (rtsPlayers.size() == 1) {
                // Single remaining player - declare victory
                RTSPlayer winner = rtsPlayers.get(0);
                postGameRtsPlayers.add(winner);
                sendMessageToAllPlayers("server.siege_of_settlements.victorious", true, winner.name);
                PlayerClientboundPacket.victory(winner.name);
            }
        }
    }

    public static void beaconVictory(String playerName) {
        if (SurvivalServerEvents.isEnabled()) {
            if (AlliancesServerEvents.getAllAllies(playerName).isEmpty())
                sendMessageToAllPlayers("server.siege_of_settlements.victorious", true, playerName);
            else
                sendMessageToAllPlayers("server.siege_of_settlements.victory_alliance", true, playerName);
            PlayerClientboundPacket.victory(playerName);
            for (String allyName : AlliancesServerEvents.getAllAllies(playerName))
                PlayerClientboundPacket.victory(allyName);
            SurvivalServerEvents.endCurrentWave();
        } else {
            for (RTSPlayer p : rtsPlayers) {
                String n = p.name;
                if (AlliancesServerEvents.isAllied(playerName, n) || n.equals(playerName)) continue;
                defeat(n, Component.translatable("server.siege_of_settlements.beacon_defeat").getString());
            }
        }
    }

    public static String getBeaconWinTime(String playerName) {
        for (RTSPlayer rtsPlayer : rtsPlayers) {
            if (rtsPlayer.name.equals(playerName)) {
                return TimeUtils.getTimeStrFromTicks(Beacon.getTicksToWin(serverLevel) - rtsPlayer.beaconOwnerTicks);
            }
        }
        return TimeUtils.getTimeStrFromTicks(Beacon.getTicksToWin(serverLevel));
    }

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent evt) {
        AllyCommand.register(evt.getDispatcher());
        RTSPlayerScoresCommand.register(evt.getDispatcher());

        evt.getDispatcher().register(Commands.literal("rts-lock").then(Commands.literal("enable").executes((command) -> {
            if ((command.getSource() != null &&
                command.getSource().getPlayer() != null &&
                command.getSource().getPlayer().hasPermissions(4)) ||
                (command.getSource() != null &&
                !command.getSource().isPlayer())) {
                setRTSLock(true);
                return 1;
            }
            return 0;
        })));

        evt.getDispatcher().register(Commands.literal("rts-lock").then(Commands.literal("disable").executes((command) -> {
            if ((command.getSource() != null &&
                command.getSource().getPlayer() != null &&
                command.getSource().getPlayer().hasPermissions(4)) ||
                (command.getSource() != null &&
                !command.getSource().isPlayer())) {
                setRTSLock(false);
                return 1;
            }
            return 0;
        })));
    }

    public static void resetRTS(boolean hardReset) {
        StartPosServerEvents.cancelStartGameCountdown(true);

        boolean isSandbox = SandboxServer.isAnyoneASandboxPlayer();

        synchronized (rtsPlayers) {
            rtsPlayers.clear();

            for (LivingEntity entity : UnitServerEvents.getAllUnits())
                if (hardReset || (entity instanceof Unit unit && !Unit.hasAnchor(unit) && !isSandbox))
                    entity.kill();

            if (!isSandbox)
                UnitServerEvents.getAllUnits().removeIf(u -> (hardReset || (u instanceof Unit unit && !Unit.hasAnchor(unit))));

            if (!isSandbox)
                for (LivingEntity entity : UnitServerEvents.getAllUnits())
                    if (entity instanceof Unit unit)
                        unit.setOwnerName("");

            for (BuildingPlacement building : BuildingServerEvents.getBuildings()) {
                if (building instanceof ProductionPlacement productionBuilding)
                    productionBuilding.productionQueue.clear();
                if ((building.getBuilding().shouldDestroyOnReset || hardReset) && !isSandbox)
                    building.destroy((ServerLevel) building.getLevel());
            }
            if (!isSandbox)
                BuildingServerEvents.getBuildings().removeIf(b -> b.getBuilding().shouldDestroyOnReset || hardReset);

            if (!isSandbox)
                for (BuildingPlacement building : BuildingServerEvents.getBuildings())
                    building.ownerName = "";

            ResearchServerEvents.removeAllResearch();
            ResearchServerEvents.removeAllCheats();
            PlayerClientboundPacket.resetRTS(hardReset);
            if (!TutorialServerEvents.isEnabled()) {
                if (hardReset)
                    sendMessageToAllPlayers("server.siege_of_settlements.match_reset_hard", true);
                else
                    sendMessageToAllPlayers("server.siege_of_settlements.match_reset", true);
            }
            ResourcesServerEvents.resourcesList.clear();
            BuildingServerEvents.netherZones.forEach(NetherZone::startRestoring);

            // clear all saved data
            saveRTSPlayers();
            postGameRtsPlayers.clear();
            saveBuildings(serverLevel);
            BuildingServerEvents.saveNetherZones(serverLevel);
            UnitServerEvents.saveGatherTargets(serverLevel);
            ResourcesServerEvents.saveResources(serverLevel);
            ResearchServerEvents.saveResearch();

            if (rtsLocked)
                setRTSLock(false);
            AlliancesServerEvents.resetAllAlliances();
            SurvivalServerEvents.reset();
        }
        HeroServerEvents.fallenHeroes.clear();
        if (!isSandbox) {
            for (ServerPlayer player : serverLevel.players())
                player.setGameMode(GameType.SPECTATOR);

            playerDefaultGameModes.replaceAll((key, oldValue) -> GameType.SPECTATOR);
        }
        AlliancesServerEvents.playersWithAlliedControl.clear();
        TimeServerEvents.resetBloodMoon();
    }

    public static void setRTSLock(boolean lock) {
        setRTSLock(lock, false);
    }

    public static void setRTSLock(boolean lock, boolean noMsg) {
        rtsLocked = lock;
        serverLevel.players().forEach(p -> {
            if (rtsLocked) {
                PlayerClientboundPacket.lockRTS(p.getName().getString());
            } else {
                PlayerClientboundPacket.unlockRTS(p.getName().getString());
            }
        });
        if (!noMsg) {
            if (rtsLocked) {
                sendMessageToAllPlayers("server.siege_of_settlements.match_locked");
            } else {
                sendMessageToAllPlayers("server.siege_of_settlements.match_unlocked");
            }
        }
    }

    public static void setRTSSyncingEnabled(boolean enable) {
        rtsSyncingEnabled = enable;
        if (rtsSyncingEnabled) {
            sendMessageToAllPlayers("server.siege_of_settlements.sync_enabled");
        } else {
            sendMessageToAllPlayers("server.siege_of_settlements.sync_disabled");
        }
    }
}









