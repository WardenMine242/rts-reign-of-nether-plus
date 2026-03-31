package com.solegendary.siege_of_settlements.player;

import com.solegendary.siege_of_settlements.alliance.AlliancesClient;
import com.solegendary.siege_of_settlements.blocks.BlockServerEvents;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.custombuilding.CustomBuildingClientEvents;
import com.solegendary.siege_of_settlements.fogofwar.FogOfWarClientEvents;
import com.solegendary.siege_of_settlements.gamemode.ClientGameModeHelper;
import com.solegendary.siege_of_settlements.gamerules.GameruleClient;
import com.solegendary.siege_of_settlements.hero.HeroClientEvents;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.hud.playerdisplay.PlayerDisplayClientEvents;
import com.solegendary.siege_of_settlements.hud.buttons.HelperButtons;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.orthoview.OrthoviewClientEvents;
import com.solegendary.siege_of_settlements.registrars.ItemRegistrar;
import com.solegendary.siege_of_settlements.registrars.SoundRegistrar;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourcesClientEvents;
import com.solegendary.siege_of_settlements.sandbox.SandboxClientEvents;
import com.solegendary.siege_of_settlements.sounds.SoundClientEvents;
import com.solegendary.siege_of_settlements.startpos.StartPosClientEvents;
import com.solegendary.siege_of_settlements.survival.SurvivalClientEvents;
import com.solegendary.siege_of_settlements.time.TimeClientEvents;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.*;

public class PlayerClientEvents {

    private static final double SURVIVAL_REACH = 4.5D;
    private static final int GRASS_SKIP_MAX = 8;

    public static long rtsGameTicks = 0;
    private static final Minecraft MC = Minecraft.getInstance();
    public static boolean rtsLocked = false;
    public static boolean canStartRTS = true;
    public static final List<RTSPlayer> rtsPlayers = Collections.synchronizedList(new ArrayList<>());
    public static BlockState lastUsedBlockState = null;

    public static boolean isRTSPlayer() {
        for (RTSPlayer rtsPlayer : rtsPlayers)
            if (MC.player != null && rtsPlayer.name.equals(MC.player.getName().getString()))
                return true;
        return false;
    }

    public static boolean isRTSPlayer(String playerName) {
        for (RTSPlayer rtsPlayer : rtsPlayers)
            if (rtsPlayer.name.equals(playerName))
                return true;
        return false;
    }

    @Nullable
    public static RTSPlayer getRTSPlayer(String playerName) {
        for (RTSPlayer rtsPlayer : rtsPlayers)
            if (rtsPlayer.name.equals(playerName))
                return rtsPlayer;
        return null;
    }

    public static Faction getFaction() {
        for (RTSPlayer rtsPlayer : rtsPlayers)
            if (MC.player != null && rtsPlayer.name.equals(MC.player.getName().getString()))
                return rtsPlayer.faction;
        return Faction.NONE;
    }

    public static RTSPlayer getPlayer(String playerName) {
        for (RTSPlayer rtsPlayer : rtsPlayers)
            if (rtsPlayer.name.equals(playerName))
                return rtsPlayer;
        return null;
    }

    public static Integer getPlayerId(String playerName) {
        var player = getPlayer(playerName);
        if (player == null) {
            return null;
        }

        return player.id;
    }

    public static Integer getPlayerIndex(String playerName) {
        var player = getPlayer(playerName);
        if (player == null) {
            return null;
        }

        return rtsPlayers.indexOf(player);
    }

    public static int getPlayerMapColorId(String playerName) {
        var player = getPlayer(playerName);
        if (player == null) {
            return 0;
        }

        return player.startPosColorId; // corresponds to a MapColor.id
    }

    public static Faction getPlayerFaction(String playerName) {
        var player = getPlayer(playerName);
        if (player == null) {
            return Faction.NONE;
        }

        return player.faction;
    }

    public static String getPlayerName() {
        return MC.player != null ? MC.player.getName().getString() : "";
    }

    @SubscribeEvent
    public static void onRegisterCommand(RegisterClientCommandsEvent evt) {
        evt.getDispatcher().register(Commands.literal("rts-camera").executes((command) -> {
            OrthoviewClientEvents.tryToToggleEnable();
            return 1;
        }));
        evt.getDispatcher().register(Commands.literal("rts-surrender").executes((command) -> {
            PlayerServerboundPacket.surrender();
            return 1;
        }));
        evt.getDispatcher().register(Commands.literal("rts-reset").executes((command) -> {
            if (MC.player != null && MC.player.hasPermissions(4)) {
                PlayerServerboundPacket.resetRTS();
                return 1;
            }
            return 0;
        }));
        evt.getDispatcher().register(Commands.literal("rts-hard-reset").executes((command) -> {
            if (MC.player != null && MC.player.hasPermissions(4)) {
                PlayerServerboundPacket.resetRTSHard();
                return 1;
            }
            return 0;
        }));
        evt.getDispatcher()
            .register(Commands.literal("rts-syncing").then(Commands.literal("enable").executes((command) -> {
                if (MC.player != null && MC.player.hasPermissions(4)) {
                    PlayerServerboundPacket.enableRTSSyncing();
                    return 1;
                }
                return 0;
            })));
        evt.getDispatcher()
            .register(Commands.literal("rts-syncing").then(Commands.literal("disable").executes((command) -> {
                if (MC.player != null && MC.player.hasPermissions(4)) {
                    PlayerServerboundPacket.disableRTSSyncing();
                    return 1;
                }
                return 0;
            })));
        evt.getDispatcher().register(Commands.literal("rts-help").executes((command) -> {
            if (MC.player != null) {
                MC.player.sendSystemMessage(Component.literal(""));
                MC.player.sendSystemMessage(Component.translatable("commands.siege_of_settlements.toggle_fow","/rts-fog enable/disable"));
                MC.player.sendSystemMessage(Component.translatable("commands.siege_of_settlements.surrender","/rts-surrender"));
                MC.player.sendSystemMessage(Component.translatable("commands.siege_of_settlements.reset", "/rts-reset"));
                MC.player.sendSystemMessage(Component.translatable("commands.siege_of_settlements.hard_reset", "/rts-hard-reset"));
                MC.player.sendSystemMessage(Component.translatable("commands.siege_of_settlements.lock", "/rts-lock enable/disable"));
                MC.player.sendSystemMessage(Component.translatable("commands.siege_of_settlements.ally", "/ally"));
                MC.player.sendSystemMessage(Component.translatable("commands.siege_of_settlements.disband", "/disband"));
                MC.player.sendSystemMessage(Component.translatable("commands.siege_of_settlements.send_resources", "/sendfood | wood | ore | emeralds <playername> <amount>"));
                MC.player.sendSystemMessage(Component.translatable("commands.siege_of_settlements.camera", "/rts-camera"));
            }
            return 1;
        }));
        evt.getDispatcher().register(Commands.literal("rts-controls").executes((command) -> {
            if (MC.player != null) {
                MC.player.sendSystemMessage(Component.literal(""));
                MC.player.sendSystemMessage(Component.translatable("controls.siege_of_settlements.toggle_cam"));
                MC.player.sendSystemMessage(Component.translatable("controls.siege_of_settlements.refresh_chunks"));
                MC.player.sendSystemMessage(Component.translatable("controls.siege_of_settlements.toggle_fps_tps"));
                MC.player.sendSystemMessage(Component.translatable("controls.siege_of_settlements.deselect"));
                MC.player.sendSystemMessage(Component.translatable("controls.siege_of_settlements.command"));
                MC.player.sendSystemMessage(Component.translatable("controls.siege_of_settlements.create_group"));
                MC.player.sendSystemMessage(Component.translatable("controls.siege_of_settlements.recenter_map"));
                MC.player.sendSystemMessage(Component.translatable("controls.siege_of_settlements.select_same"));
                MC.player.sendSystemMessage(Component.translatable("controls.siege_of_settlements.destroy"));
                MC.player.sendSystemMessage(Component.translatable("controls.siege_of_settlements.rotate_cam"));
                MC.player.sendSystemMessage(Component.translatable("controls.siege_of_settlements.zoom"));
                MC.player.sendSystemMessage(Component.translatable("controls.siege_of_settlements.rotate"));
            }
            return 1;
        }));
    }

    public static void defeat(String playerName) {
        if (MC.player == null) {
            return;
        }

        // remove control of this player's buildings for all players' clients
        for (BuildingPlacement building : BuildingClientEvents.getBuildings())
            if (building.ownerName.equals(playerName))
                building.ownerName = "";

        if (isRTSPlayer())
            PlayerDisplayClientEvents.resetDisplay();

        removeRTSPlayer(playerName);
        ResourcesClientEvents.resourcesList.removeIf(r -> r.ownerName.equals(playerName));

        if (!MC.player.getName().getString().equals(playerName))
            return;

        if (!SandboxClientEvents.isSandboxPlayer(playerName)) {
            MC.gui.setTitle(Component.translatable("titles.siege_of_settlements.defeated"));
            MC.player.playSound(SoundRegistrar.DEFEAT.get(), 0.5f, 1.0f);
        }
        ResearchClient.removeAllResearch();
        ResearchClient.removeAllCheats();
        HudClientEvents.controlGroups.clear();
    }

    public static void victory(String playerName) {
        if (MC.player == null || !MC.player.getName().getString().equals(playerName)) {
            return;
        }
        MC.gui.setTitle(Component.translatable("titles.siege_of_settlements.victorious"));
        MC.player.playSound(SoundRegistrar.VICTORY.get(), 0.5f, 1.0f);
    }

    public static void addRTSPlayer(String playerName, Faction faction, Long id, int startPosColorId) {
        if (!isRTSPlayer(playerName)) {
            rtsPlayers.add(RTSPlayer.getNewPlayer(playerName, faction, id.intValue(), startPosColorId));
            if (MC.player != null && MC.player.getName().getString().equals(playerName)) {
                GameruleClient.gamerulesMenuOpen = false;
                if (!SandboxClientEvents.isSandboxPlayer()) {
                    MC.getMusicManager().stopPlaying();
                    ResearchClient.removeAllCheats();
                }
            }
        }
    }

    public static void removeRTSPlayer(String playerName) {
        boolean removed = rtsPlayers.removeIf(p -> p.name.equals(playerName));
        if (removed && MC.player != null && MC.player.getName().getString().equals(playerName)) {
            SoundClientEvents.stopFadeableMusicInstance();
        }
    }

    @SubscribeEvent
    public static void onPlayerLogoutEvent(PlayerEvent.PlayerLoggedOutEvent evt) {
        // LOG OUT FROM SINGLEPLAYER WORLD ONLY
        if (MC.player != null && evt.getEntity().getId() == MC.player.getId()) {
            resetRTS(true);
            UnitClientEvents.getAllUnits().clear();
            BuildingClientEvents.getBuildings().clear();
            FogOfWarClientEvents.movedToCapitol = false;
            FogOfWarClientEvents.frozenChunks.clear();
            FogOfWarClientEvents.semiFrozenChunks.clear();
            OrthoviewClientEvents.unlockCam();
            HeroClientEvents.fallenHeroes.clear();
            PlayerDisplayClientEvents.resetDisplay();
            PlayerColors.reset();
            CustomBuildingClientEvents.customBuildings.clear();
            CustomBuildingClientEvents.setCustomBuildingToEdit(null);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent evt) {
        // LOG IN TO SINGLEPLAYER WORLD ONLY
        if (MC.player != null && evt.getEntity().getId() == MC.player.getId()) {
            FogOfWarClientEvents.updateFogChunks();
        }
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut evt) {
        // LOG OUT FROM SERVER WORLD ONLY
        if (MC.player != null && evt.getPlayer() != null && evt.getPlayer().getId() == MC.player.getId()) {
            resetRTS(true);
            UnitClientEvents.getAllUnits().clear();
            BuildingClientEvents.getBuildings().clear();
            FogOfWarClientEvents.movedToCapitol = false;
            FogOfWarClientEvents.frozenChunks.clear();
            FogOfWarClientEvents.semiFrozenChunks.clear();
            HeroClientEvents.fallenHeroes.clear();
            PlayerDisplayClientEvents.resetDisplay();
            PlayerColors.reset();
            CustomBuildingClientEvents.customBuildings.clear();
        }
    }

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn evt) {
        // LOG IN TO SERVER WORLD ONLY
        if (MC.player != null && evt.getPlayer().getId() == MC.player.getId()) {
            FogOfWarClientEvents.updateFogChunks();
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent evt) {
        if (evt.phase == TickEvent.Phase.END) {
            rtsGameTicks += 1;
            updateGrassIgnoredHitResult();
        }
    }

    // disallow opening the creative menu while orthoview is enabled
    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening evt) {
        if (OrthoviewClientEvents.isEnabled() &&
            (evt.getScreen() instanceof CreativeModeInventoryScreen ||
            evt.getScreen() instanceof InventoryScreen)) {
            evt.setCanceled(true);
        }
    }

    // allow tab player list menu on the orthoview screen
    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render evt) {
        if (OrthoviewClientEvents.isEnabled() && Keybindings.tab.isDown() && MC.level != null) {
            if (!MC.isLocalServer()) {
                MC.gui.getTabList().setVisible(true);
                MC.gui.getTabList()
                    .render(evt.getGuiGraphics(), MC.getWindow().getGuiScaledWidth(), MC.level.getScoreboard(), null);
            } else {
                MC.gui.getTabList().setVisible(false);
            }
        }
    }

    @SubscribeEvent
    public static void onAttackInput(InputEvent.InteractionKeyMappingTriggered evt) {
        if (!evt.isAttack())
            return;
        if (MC.player == null || MC.level == null)
            return;
        if (MC.player.isCreative() || MC.player.isSpectator())
            return;
        if (MC.hitResult == null || MC.hitResult.getType() == HitResult.Type.MISS)
            return;
        if (!(MC.hitResult instanceof BlockHitResult blockHit))
            return;

        BlockPos pos = blockHit.getBlockPos();
        BlockState state = MC.level.getBlockState(pos);
        if (!BlockServerEvents.isAllowedSurvivalBreak(state, pos, MC.level)) {
            evt.setSwingHand(false);
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent evt) {
        if (!evt.getItemStack().is(ItemRegistrar.IRON_SWORD.get())) {
            return;
        }

        List<Component> tooltip = evt.getToolTip();

        boolean isRussian = MC.options.languageCode != null && MC.options.languageCode.startsWith("ru");
        String unitsDamageText = isRussian ? " Урон по юнитам: 4" : " Damage vs units: 4";
        String mobsDamageText = isRussian ? " Урон по мобам: 1" : " Damage vs mobs: 1";
        int insertIndex = -1;

        for (int i = 0; i < tooltip.size(); i++) {
            if (isAttackDamageLine(tooltip.get(i))) {
                if (insertIndex < 0) {
                    insertIndex = i;
                }
            }
        }

        tooltip.removeIf(PlayerClientEvents::isAttackDamageLine);

        if (insertIndex < 0 || insertIndex > tooltip.size()) {
            insertIndex = tooltip.size();
        }

        tooltip.add(insertIndex, Component.literal(unitsDamageText).withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(insertIndex + 1, Component.literal(mobsDamageText).withStyle(ChatFormatting.DARK_GREEN));
    }

    private static boolean isAttackDamageLine(Component component) {
        return containsAttackDamageModifier(component);
    }

    private static boolean containsAttackDamageModifier(Component component) {
        if (component.getContents() instanceof TranslatableContents contents) {
            String key = contents.getKey();
            if ("attribute.name.generic.attack_damage".equals(key)) {
                return true;
            }
            if (key != null && key.startsWith("attribute.modifier.")) {
                for (Object arg : contents.getArgs()) {
                    if (arg instanceof Component argComponent && containsAttackDamageModifier(argComponent)) {
                        return true;
                    }
                }
            }
        }

        for (Component sibling : component.getSiblings()) {
            if (containsAttackDamageModifier(sibling)) {
                return true;
            }
        }

        return false;
    }

    public static void syncRtsGameTime(Long gameTicks) {
        rtsGameTicks = gameTicks;
    }

    private static boolean isIgnorableGrass(BlockState state) {
        return state.is(Blocks.GRASS) || state.is(Blocks.TALL_GRASS) || state.is(Blocks.FERN) || state.is(Blocks.LARGE_FERN);
    }

    private static HitResult clipIgnoringGrass(Vec3 start, Vec3 look, double reach) {
        Vec3 from = start;
        double remaining = reach;
        for (int i = 0; i < GRASS_SKIP_MAX; i++) {
            Vec3 to = from.add(look.scale(remaining));
            HitResult hit = MC.level.clip(new ClipContext(from, to, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, MC.player));
            if (hit.getType() != HitResult.Type.BLOCK)
                return hit;

            BlockPos pos = ((BlockHitResult) hit).getBlockPos();
            BlockState state = MC.level.getBlockState(pos);
            if (!isIgnorableGrass(state))
                return hit;

            Vec3 hitLoc = hit.getLocation();
            double traveled = hitLoc.distanceTo(from);
            remaining -= traveled;
            if (remaining <= 0.01D)
                break;

            from = hitLoc.add(look.scale(0.01D));
        }

        Vec3 missTo = start.add(look.scale(reach));
        return BlockHitResult.miss(missTo, Direction.getNearest(look.x, look.y, look.z), BlockPos.containing(missTo));
    }

    private static void updateGrassIgnoredHitResult() {
        if (MC.player == null || MC.level == null)
            return;
        if (MC.player.isCreative() || MC.player.isSpectator())
            return;

        Vec3 eyePos = MC.player.getEyePosition(1.0F);
        Vec3 look = MC.player.getViewVector(1.0F);
        double reach = SURVIVAL_REACH;

        HitResult blockHit = clipIgnoringGrass(eyePos, look, reach);

        Vec3 end = eyePos.add(look.scale(reach));
        AABB aabb = MC.player.getBoundingBox().expandTowards(look.scale(reach)).inflate(1.0D);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(MC.player, eyePos, end, aabb,
            (entity) -> !entity.isSpectator() && entity.isPickable(), reach * reach);

        HitResult finalHit = blockHit;
        if (entityHit != null) {
            double entityDist = eyePos.distanceTo(entityHit.getLocation());
            double blockDist = blockHit.getType() == HitResult.Type.MISS ? Double.MAX_VALUE : eyePos.distanceTo(blockHit.getLocation());
            if (entityDist < blockDist) {
                finalHit = entityHit;
            }
        }

        MC.hitResult = finalHit;
    }

    public static void resetRTS(boolean hardReset) {
        boolean isSandbox = SandboxClientEvents.isSandboxPlayer();
        rtsPlayers.clear();
        HelperButtons.updateButtons();
        SoundClientEvents.stopFadeableMusicInstance();

        HudClientEvents.controlGroups.clear();
        UnitClientEvents.getSelectedUnits().clear();
        UnitClientEvents.getPreselectedUnits().clear();
        if (!isSandbox)
            UnitClientEvents.getAllUnits().removeIf(u -> (hardReset || (u instanceof Unit unit && !Unit.hasAnchor(unit))));
        if (!isSandbox)
            for (LivingEntity entity : UnitClientEvents.getAllUnits())
                if (entity instanceof Unit unit)
                    unit.setOwnerName("");
        UnitClientEvents.idleWorkerIds.clear();
        ResearchClient.removeAllResearch();
        ResearchClient.removeAllCheats();
        BuildingClientEvents.getSelectedBuildings().clear();
        if (!isSandbox)
            BuildingClientEvents.getBuildings().removeIf(b -> b.getBuilding().shouldDestroyOnReset || hardReset);
        if (!isSandbox)
            for (BuildingPlacement building : BuildingClientEvents.getBuildings())
                building.ownerName = "";
        ResourcesClientEvents.resourcesList.clear();
        ClientGameModeHelper.gameMode = ClientGameModeHelper.DEFAULT_GAMEMODE;
        ClientGameModeHelper.gameModeLocked = false;
        SurvivalClientEvents.reset();
        StartPosClientEvents.resetAll();
        HeroClientEvents.fallenHeroes.clear();
        AlliancesClient.playersWithAlliedControl.clear();
        PlayerColors.reset();
        PlayerDisplayClientEvents.resetDisplay();
        TimeClientEvents.resetBloodMoon();
        CustomBuildingClientEvents.setCustomBuildingToEdit(null);
    }

    public static void setRTSLock(boolean lock) {
        rtsLocked = lock;
    }

    public static void setCanStartRTS(boolean canStart) {
        canStartRTS = canStart;
    }

    public static void syncBeaconOwnerTicks(String playerName, long ticks) {
        for (int i = 0; i < rtsPlayers.size(); i++)
            if (rtsPlayers.get(i).name.equals(playerName))
                rtsPlayers.get(i).beaconOwnerTicks = (int) ticks;
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock evt) {
        if (MC.level != null)
            lastUsedBlockState = MC.level.getBlockState(evt.getPos());
    }

    /*
    public static double red = 0.0d;
    public static double green = 0.0d;
    public static double blue = 0.0d;

    @SubscribeEvent
    public static void onButtonPress2(ScreenEvent.KeyPressed.Pre evt) {
        if (Keybindings.shiftMod.isDown()) {
            if (evt.getKeyCode() == GLFW.GLFW_KEY_LEFT) {
                red -= 0.02f;
            }
            else if (evt.getKeyCode() == GLFW.GLFW_KEY_RIGHT) {
                red += 0.02f;
            }
            else if (evt.getKeyCode() == GLFW.GLFW_KEY_UP) {
                green -= 0.02f;
            }
            else if (evt.getKeyCode() == GLFW.GLFW_KEY_DOWN) {
                green += 0.02f;
            }
        } else {
            if (evt.getKeyCode() == GLFW.GLFW_KEY_LEFT) {
                blue -= 0.02f;
            }
            else if (evt.getKeyCode() == GLFW.GLFW_KEY_RIGHT) {
                blue += 0.02f;
            }
        }
    }
     */

    /*
    @SubscribeEvent
    public static void onRenderOverLay(RenderGuiOverlayEvent.Pre evt) {
        MiscUtil.drawDebugStrings(evt.getGuiGraphics(), MC.font, new String[] {
                "red: " + red,
                "green: " + green,
                "blue: " + blue,
        });
    }


    public static int titleX = -56;
    public static int titleY = 5;
    public static int width = 380;
    public static int height = 127;
    public static int editionY = 92;

    @SubscribeEvent
    public static void onButtonPress2(ScreenEvent.KeyPressed.Pre evt) {
        if (Keybindings.shiftMod.isDown()) {
            if (evt.getKeyCode() == GLFW.GLFW_KEY_LEFT) {
                titleX -= 1;
            }
            else if (evt.getKeyCode() == GLFW.GLFW_KEY_RIGHT) {
                titleX += 1;
            }
            else if (evt.getKeyCode() == GLFW.GLFW_KEY_UP) {
                titleY -= 1;
            }
            else if (evt.getKeyCode() == GLFW.GLFW_KEY_DOWN) {
                titleY += 1;
            }
        } else if (Keybindings.ctrlMod.isDown()) {
            if (evt.getKeyCode() == GLFW.GLFW_KEY_UP) {
                editionY -= 1;
            }
            else if (evt.getKeyCode() == GLFW.GLFW_KEY_DOWN) {
                editionY += 1;
            }
        } else {
            if (evt.getKeyCode() == GLFW.GLFW_KEY_LEFT) {
                width -= 1;
            }
            else if (evt.getKeyCode() == GLFW.GLFW_KEY_RIGHT) {
                width += 1;
            }
            else if (evt.getKeyCode() == GLFW.GLFW_KEY_UP) {
                height += 1;
            }
            else if (evt.getKeyCode() == GLFW.GLFW_KEY_DOWN) {
                height -= 1;
            }
        }
    }
     */
}



















