package com.solegendary.siege_of_settlements.orthoview;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.RangeIndicator;
import com.solegendary.siege_of_settlements.config.SiegeOfSettlementsClientConfigs;
import com.solegendary.siege_of_settlements.fogofwar.FogOfWarClientEvents;
import com.solegendary.siege_of_settlements.guiscreen.TopdownGui;
import com.solegendary.siege_of_settlements.guiscreen.TopdownGuiServerboundPacket;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.minimap.MinimapClientEvents;
import com.solegendary.siege_of_settlements.player.PlayerServerboundPacket;
import com.solegendary.siege_of_settlements.startpos.StartPosClientEvents;
import com.solegendary.siege_of_settlements.startpos.StartPosServerboundPacket;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialStage;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import com.solegendary.siege_of_settlements.util.MyMath;
import net.minecraft.client.CameraType;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import java.nio.DoubleBuffer;
import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;
import static net.minecraft.util.Mth.sign;

/**
 * Handler that implements and manages hotkeys for the orthographic camera.
 *
 * @author SoLegendary, adapted from Mineshot by Nico Bergemann <barracuda415 at yahoo.de>
 */
public class OrthoviewClientEvents {


    public enum LeafHideMethod {
        NONE, AROUND_UNITS_AND_CURSOR, // requires threaded video option
        ALL
    }

    public static boolean shouldHideLeaves() {
        return hideLeavesMethod != LeafHideMethod.NONE;
    }

    public static LeafHideMethod hideLeavesMethod = LeafHideMethod.NONE;
    public static int enabledCount = 0;
    public static boolean enabled = false;
    private static boolean cameraMovingByMouse = false; // excludes edgepanning

    private static final Minecraft MC = Minecraft.getInstance();
    private static Entity cameraEntity = null;
    private static int nextCameraEntityId = -1;
    private static final float ZOOM_STEP_KEY = 5;
    private static final float ZOOM_STEP_SCROLL = 1;
    private static final float ZOOM_MIN = 10;
    private static final float ZOOM_MAX = 90;
    private static final float CAMROTY_MAX = -20;
    private static final float CAMROTY_MIN = -90;
    private static final float CAMROT_MOUSE_SENSITIVITY = 0.12f;

    private static final float ZOOM_DEFAULT = 30;
    private static final float CAMROTX_DEFAULT = 135;
    private static final float CAMROTY_DEFAULT = -45;
    private static Vec3 frozenPlayerPos = null;
    private static Vec3 frozenVehiclePos = null;
    private static final int CAMERA_SYNC_TICKS_MAX = 1;
    private static final double CAMERA_SYNC_MOVE_SQR = 1.0D;
    private static int cameraSyncTicksLeft = 0;
    private static Vec3 lastSyncedCameraPos = null;

    private static final int FORCE_PAN_TICKS_MAX = 20;
    private static int forcePanTicksLeft = 0;
    private static float forcePanTargetX = 0;
    private static float forcePanTargetZ = 0;
    private static float forcePanOriginalX = 0;
    private static float forcePanOriginalZ = 0;
    private static float forcePanOriginalZoom = 0;

    private static final int FORCE_ROT_FRAMES_MAX = 20;
    private static int forceRotFramesLeft = 0;
    private static float forceRotTargetX = 0;
    private static float forceRotOriginalX = 0;

    private static int cameraLockTicksLeft = 0;
    private static boolean cameraLocked = false;

    private static float zoom = 30; // * 2 = number of blocks in height (higher == zoomed out)
    private static float camRotX = 135; // left/right - should start northeast (towards -Z,+X)
    private static float camRotY = -45; // up/down
    private static float camRotAdjX = 0;
    private static float camRotAdjY = 0;
    private static float mouseRightDownX = 0;
    private static float mouseRightDownY = 0;
    private static float mouseLeftDownX = 0;
    private static float mouseLeftDownY = 0;
    public static final float MAX_PAN_SENSITIVITY = 3.0f;

    // by default orthoview players stay at BASE_Y, but can be raised to as high as MAX_Y if they are clipping terrain
    public static double orthoviewPlayerBaseY = 100;
    public static double orthoviewPlayerMaxY = 160;
    private static double minOrthoviewY = 0;

    public static void setMinOrthoviewY(double value) {
        minOrthoviewY = value;
        Entity cam = getCameraEntity();
        if (MC.level != null && cam != null && cam.getY() < value + 15) {
            cam.setPos(cam.getX(), minOrthoviewY + 15, cam.getZ());
        }
    }

    private static float getEdgeCamPanSensitivity() {
        return (float) (Math.sqrt(getZoom()) / (Math.sqrt(ZOOM_MAX))) * getPanSensitivityMult();
    }
    public static float getPanSensitivityMult() {
        return (float) SiegeOfSettlementsClientConfigs.CAMERA_SENSITIVITY.get() / 10f;
    }
    public static void adjustPanSensitivityMult(boolean increase) {
        if (increase && Math.round(getPanSensitivityMult() * 10) < (MAX_PAN_SENSITIVITY * 10))
            SiegeOfSettlementsClientConfigs.CAMERA_SENSITIVITY.set(SiegeOfSettlementsClientConfigs.CAMERA_SENSITIVITY.get() + 1);
        else if (!increase && Math.round(getPanSensitivityMult() * 10) > 1)
            SiegeOfSettlementsClientConfigs.CAMERA_SENSITIVITY.set(SiegeOfSettlementsClientConfigs.CAMERA_SENSITIVITY.get() - 1);
    }

    private static int reserveCameraEntityId() {
        int id = nextCameraEntityId;
        nextCameraEntityId -= 1;
        return id;
    }

    public static Entity getCameraEntity() {
        if (cameraEntity != null) {
            return cameraEntity;
        }
        Entity cam = MC.getCameraEntity();
        return cam != null ? cam : MC.player;
    }

    private static void ensureCameraEntity() {
        if (cameraEntity != null || MC.level == null || MC.player == null) {
            return;
        }
        ArmorStand cam = new ArmorStand(MC.level, MC.player.getX(), MC.player.getY(), MC.player.getZ());
        cam.setInvisible(true);
        cam.setNoGravity(true);
        cam.noPhysics = true;
        cam.setSilent(true);
        cam.setInvulnerable(true);
        cam.setCustomNameVisible(false);

        if (MC.level instanceof ClientLevel) {
            ClientLevel clientLevel = (ClientLevel) MC.level;
            clientLevel.addFreshEntity(cam);
        }
        cameraEntity = cam;
        MC.setCameraEntity(cam);
    }

    private static void cleanupCameraEntity() {
        if (MC.player != null) {
            MC.setCameraEntity(MC.player);
        }
        if (cameraEntity != null) {
            cameraEntity.discard();
            cameraEntity = null;
        }
    }

    public static void moveCameraTo(double x, double y, double z) {
        Entity cam = getCameraEntity();
        if (cam == null) {
            return;
        }
        cam.setPos(x, y, z);
        syncCameraOldState(cam);
    }

    private static void syncCameraOldState(Entity cam) {
        cam.xo = cam.getX();
        cam.yo = cam.getY();
        cam.zo = cam.getZ();
        cam.xRotO = cam.getXRot();
        cam.yRotO = cam.getYRot();
        if (cam instanceof LivingEntity living) {
            living.yHeadRotO = living.getYHeadRot();
        }
    }
    private static void applyCameraRotation(Entity cam) {
        float pitch = -camRotY - camRotAdjY;
        float yaw = -camRotX - camRotAdjX;
        cam.setXRot(pitch);
        cam.setYRot(yaw);
        if (cam instanceof LivingEntity living) {
            living.setYHeadRot(yaw);
            living.setYBodyRot(yaw);
        }
    }

    private static void syncCameraToServer(Entity cam) {
        if (!enabled || MC.player == null) {
            return;
        }

        cameraSyncTicksLeft -= 1;
        Vec3 cameraPos = cam.position();
        boolean movedEnough = lastSyncedCameraPos == null
            || lastSyncedCameraPos.distanceToSqr(cameraPos) >= CAMERA_SYNC_MOVE_SQR;

        if (cameraSyncTicksLeft <= 0 || movedEnough) {
            PlayerServerboundPacket.syncOrthoviewCamera(cameraPos.x, cameraPos.y, cameraPos.z);
            lastSyncedCameraPos = cameraPos;
            cameraSyncTicksLeft = CAMERA_SYNC_TICKS_MAX;
        }
    }

    private static void blockPlayerInput() {
        if (MC.player == null) {
            return;
        }
        if (MC.player.input != null) {
            MC.player.input.leftImpulse = 0;
            MC.player.input.forwardImpulse = 0;
            MC.player.input.jumping = false;
            MC.player.input.shiftKeyDown = false;
        }
        MC.player.setSprinting(false);
    }

    private static void freezeLocalPlayer() {
        if (MC.player == null || !enabled) {
            frozenPlayerPos = null;
            frozenVehiclePos = null;
            return;
        }

        if (frozenPlayerPos == null) {
            frozenPlayerPos = MC.player.position();
        }

        MC.player.setDeltaMovement(Vec3.ZERO);
        MC.player.fallDistance = 0;

        if (MC.player.position().distanceToSqr(frozenPlayerPos) > 1.0e-6) {
            MC.player.setPos(frozenPlayerPos.x, frozenPlayerPos.y, frozenPlayerPos.z);
        }

        MC.player.xo = frozenPlayerPos.x;
        MC.player.yo = frozenPlayerPos.y;
        MC.player.zo = frozenPlayerPos.z;

        Entity vehicle = MC.player.getVehicle();
        if (vehicle != null) {
            if (frozenVehiclePos == null) {
                frozenVehiclePos = vehicle.position();
            }

            vehicle.setDeltaMovement(Vec3.ZERO);
            vehicle.fallDistance = 0;
            if (vehicle.position().distanceToSqr(frozenVehiclePos) > 1.0e-6) {
                vehicle.setPos(frozenVehiclePos.x, frozenVehiclePos.y, frozenVehiclePos.z);
            }
            vehicle.xo = frozenVehiclePos.x;
            vehicle.yo = frozenVehiclePos.y;
            vehicle.zo = frozenVehiclePos.z;
        } else {
            frozenVehiclePos = null;
        }
    }
    public static void updateOrthoviewY() {
        Entity cam = getCameraEntity();
        if (cam != null && MC.level != null) {
            BlockPos cameraPos = cam.blockPosition();
            int radius = 10; // Defines the area around the player to sample heights
            int sumHeights = 0;
            int count = 0;

            // Iterate through a square area around the player
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int blockX = cameraPos.getX() + x;
                    int blockZ = cameraPos.getZ() + z;
                    int height = MC.level.getHeight(Heightmap.Types.MOTION_BLOCKING, blockX, blockZ);
                    sumHeights += height;
                    count++;
                }
            }
            // Calculate the average height
            int avgHeight = count > 0 ? sumHeights / count : cameraPos.getY();

            // Update ORTHOVIEW values based on the average height
            orthoviewPlayerBaseY = Math.max(avgHeight + 30, minOrthoviewY);
            orthoviewPlayerMaxY = avgHeight + 100;
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isCameraMovingByMouse() {
        return cameraMovingByMouse;
    }

    public static float getZoom() {
        return zoom;
    }

    public static float getCamRotX() {
        return -camRotX - camRotAdjX;
    }

    public static float getCamRotY() {
        return -camRotY - camRotAdjY;
    }

    public static boolean isCameraLocked() {
        return cameraLockTicksLeft > 0 || cameraLocked;
    }

    public static void lockCam() {
        cameraLocked = true;
    }

    public static void unlockCam() {
        cameraLocked = false;
    }

    private static void reset() {
        zoom = ZOOM_DEFAULT;
        camRotX = CAMROTX_DEFAULT;
        camRotY = CAMROTY_DEFAULT;
    }

    private static void forceDisableClient(boolean notifyServer) {
        if (notifyServer) {
            PlayerServerboundPacket.disableOrthoview();
            if (MC.player != null) {
                TopdownGuiServerboundPacket.closeTopdownGui(MC.player.getId());
            }
        }
        enabled = false;
        cameraMovingByMouse = false;
        cameraLocked = false;
        cameraLockTicksLeft = 0;
        forcePanTicksLeft = 0;
        forceRotFramesLeft = 0;
        camRotAdjX = 0;
        camRotAdjY = 0;
        frozenPlayerPos = null;
        frozenVehiclePos = null;
        cameraSyncTicksLeft = 0;
        lastSyncedCameraPos = null;
        reset();
        cleanupCameraEntity();
    }

    public static void rotateCam(float x, float y) {
        if (isCameraLocked()) {
            return;
        }
        camRotX += x;
        if (camRotX >= 360) {
            camRotX -= 360;
        }
        if (camRotX <= -360) {
            camRotX += 360;
        }

        /*
        camRotY += y;
        if (camRotY > CAMROTY_MAX)
            camRotY = CAMROTY_MAX;
        if (camRotY < CAMROTY_MIN)
            camRotY = CAMROTY_MIN;
         */
    }

    public static void zoomCam(float zoomAdj) {
        if (isCameraLocked()) {
            return;
        }
        zoom += zoomAdj;
        if (zoom < ZOOM_MIN) {
            zoom = ZOOM_MIN;
        }
        if (zoom > ZOOM_MAX) {
            zoom = ZOOM_MAX;
        }
    }

    public static void panCam(float x, float y, float z) { // pan camera relative to rotation
        Entity cam = getCameraEntity();
        if (cam != null) {
            Vec2 XZRotated = MyMath.rotateCoords(x, z, -camRotX - camRotAdjX);
            cam.setPos(cam.getX() + XZRotated.x, cam.getY() + y, cam.getZ() + XZRotated.y);
            syncCameraOldState(cam);
        }
    }

    // lock the camera and move it towards a location, remain locked for cameraLockTicks
    public static void forceMoveCam(int x, int z, int cameraLockTicks) {
        Entity cam = getCameraEntity();
        if (cam != null) {
            forcePanTicksLeft = FORCE_PAN_TICKS_MAX;
            forcePanTargetX = x;
            forcePanTargetZ = z;
            cameraLockTicksLeft = FORCE_PAN_TICKS_MAX + cameraLockTicks;
            forcePanOriginalX = cam.getOnPos().getX();
            forcePanOriginalZ = cam.getOnPos().getZ();
            forcePanOriginalZoom = zoom;
        }
    }

    public static void forceMoveCam(Vec3i pos, int cameraLockTicks) {
        forceMoveCam(pos.getX(), pos.getZ(), cameraLockTicks);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END) {
            return;
        }

        if (cameraLockTicksLeft > 0) {
            cameraLockTicksLeft -= 1;
        }
        if (enabled && MC.player != null && !MC.player.isAlive()) {
            toggleEnable();
            return;
        }

        if (!OrthoviewClientEvents.isEnabled() || MC.level == null) {
            forcePanTicksLeft = 0;
            return;
        }

        ensureCameraEntity();
        Entity cam = getCameraEntity();
        if (cam == null) {
            forcePanTicksLeft = 0;
            return;
        }

        blockPlayerInput();
        freezeLocalPlayer();

        double targetY = Math.max(orthoviewPlayerBaseY, Math.min(orthoviewPlayerMaxY, cam.getY()));
        if (targetY != cam.getY()) {
            cam.setPos(cam.getX(), targetY, cam.getZ());
        }

        if (forcePanTicksLeft > 0) {
            float xDiff = (forcePanTargetX - forcePanOriginalX) / FORCE_PAN_TICKS_MAX;
            float zDiff = (forcePanTargetZ - forcePanOriginalZ) / FORCE_PAN_TICKS_MAX;
            float zoomDiff = (ZOOM_DEFAULT - forcePanOriginalZoom) / FORCE_PAN_TICKS_MAX;
            zoom += zoomDiff;
            cam.setPos(cam.getX() + xDiff, cam.getY(), cam.getZ() + zDiff);
            forcePanTicksLeft -= 1;
        }
        updateOrthoviewY();
        applyCameraRotation(cam);
        syncCameraOldState(cam);
        syncCameraToServer(cam);
    }

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn evt) {
        forceDisableClient(true);
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut evt) {
        forceDisableClient(false);
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent evt) throws NoSuchFieldException {
        if (evt.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS ||
            !OrthoviewClientEvents.isEnabled()) {
            return;
        }
        if (forceRotFramesLeft > 0) {
            float xDiff = (forceRotTargetX - forceRotOriginalX) / FORCE_ROT_FRAMES_MAX;
            rotateCam(-xDiff, 0);
            forceRotFramesLeft -= 1;
            if (forceRotFramesLeft <= 0) {
                camRotX = -forceRotTargetX;
                rotateCam(0,0);
            }
        }
    }

    public static void toggleEnable() {
        if (MC.level == null || MC.player == null) {
            return;
        }

        for (BuildingPlacement building : BuildingClientEvents.getBuildings())
            if (building instanceof RangeIndicator ri)
                ri.updateBorderBps();

        enabled = !enabled;

        if (enabled) {
            MC.options.tutorialStep = TutorialSteps.NONE;
            MC.getTutorial().stop();
            enabledCount += 1;
            PlayerServerboundPacket.enableOrthoview();
            ensureCameraEntity();
            frozenPlayerPos = MC.player.position();
            Entity vehicle = MC.player.getVehicle();
            frozenVehiclePos = vehicle != null ? vehicle.position() : null;
            updateOrthoviewY();
            moveCameraTo(MC.player.getX(), orthoviewPlayerBaseY, MC.player.getZ());
            cameraSyncTicksLeft = 0;
            lastSyncedCameraPos = null;
            if (MC.levelRenderer != null) {
                MC.levelRenderer.allChanged();
            }
            MinimapClientEvents.setMapCentre(MC.player.getX(), MC.player.getZ());
            TopdownGuiServerboundPacket.openTopdownGui(MC.player.getId());
            MC.options.cloudStatus().set(CloudStatus.OFF);
            MC.options.hideGui = false; // for some reason, when gui is hidden, shape rendering goes whack
            MC.options.setCameraType(CameraType.FIRST_PERSON);
            switchToEasyIfPeaceful();
        } else {
            PlayerServerboundPacket.disableOrthoview();
            TopdownGuiServerboundPacket.closeTopdownGui(MC.player.getId());
            frozenPlayerPos = null;
            frozenVehiclePos = null;
            cameraSyncTicksLeft = 0;
            lastSyncedCameraPos = null;
            cleanupCameraEntity();
            if (StartPosClientEvents.hasReservedPos()) {
                StartPosClientEvents.selectedFaction = Faction.NONE;
                StartPosServerboundPacket.unreservePos(StartPosClientEvents.getPos().pos);
            }
        }
        TutorialClientEvents.updateStage();
    }

    public static void centreCameraOnPos(BlockPos bp) {
        centreCameraOnPos(new Vec3(bp.getX(), bp.getY(), bp.getZ()));
    }

    // moves the camera to the position such that x,z is at the centre of the screen
    public static void centreCameraOnPos(Vec3 pos) {
        Entity cam = getCameraEntity();
        if (cam == null) {
            return;
        }
        MinimapClientEvents.setMapCentre(pos.x, pos.z);
        // at 0deg by default camera is facing +Z and we want to move it backwards from this
        Vec2 XZRotated = MyMath.rotateCoords(0, -20, OrthoviewClientEvents.getCamRotX());

        float offset = (float) (Math.sqrt(getZoom()) / (Math.sqrt(ZOOM_MAX)));

        int yDiff = (int) (cam.getY() - pos.y) - 40;

        Vec2 XZRotatedOffset = MyMath.rotateCoords(0, -(offset * 35) - yDiff, -camRotX - camRotAdjX);

        moveCameraTo(
                pos.x + XZRotated.x + XZRotatedOffset.x, cam.getY(),
                pos.z + XZRotated.y + XZRotatedOffset.y
        );
    }

    @SubscribeEvent
    public static void onRenderArm(RenderArmEvent evt) {
        if (isEnabled()) {
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent evt) {
        if (isEnabled()) {
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    // can't use ScreenEvent.KeyboardKeyPressedEvent as that only happens when a screen is up
    public static void onInput(InputEvent.Key evt) {
        // Prevent repeated key actions
        if (evt.getAction() == GLFW.GLFW_PRESS) {

            if (evt.getKey() == Keybindings.getFnum(12).key) {
                tryToToggleEnable();
            }
            else if (evt.getKey() == Keybindings.reset.key) {
                reset();
            }
        }
    }

    // rotates the camera to fixed points at -135, -45, 45 and 135 degrees
    public static void fixedRotateCam(boolean clockwise) {
        if (forceRotFramesLeft <= 0) {
            camRotAdjX = 0;
            int roundedRotX = (Math.round(getCamRotX() / 90.0f) * 90) + 45;

            if (roundedRotX > getCamRotX() && clockwise)
                forceRotTargetX = roundedRotX;
            else if (roundedRotX < getCamRotX() && !clockwise)
                forceRotTargetX = roundedRotX - 90;
            else if (clockwise)
                forceRotTargetX = roundedRotX;
            else
                forceRotTargetX = roundedRotX - 90;

            if (forceRotTargetX == getCamRotX() && clockwise)
                forceRotTargetX += 90;
            else if (forceRotTargetX == getCamRotX())
                forceRotTargetX -= 90;

            forceRotOriginalX = getCamRotX();
            forceRotFramesLeft = FORCE_ROT_FRAMES_MAX;
        }
    }

    public static void tryToToggleEnable() {
        if (!OrthoviewClientEvents.isCameraLocked() && MC.gameMode != null) {
            toggleEnable();
        }
    }

    public static Button getLeavesHidingButton() {
        return new Button("Hide Leaves Method",
                14,
                switch(hideLeavesMethod) {
                    case NONE -> ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/leaves.png");
                    case AROUND_UNITS_AND_CURSOR -> ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/lime_stained_glass.png");
                    case ALL -> ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/glass.png");
                },
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame.png"),
                null,
                () -> false,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.MINIMAP_CLICK) || !MinimapClientEvents.isLargeMap(),
                () -> true,
                () -> {
                    FogOfWarClientEvents.resetFogChunks();
                    UnitClientEvents.windowUpdateTicks = 0;
                    if (hideLeavesMethod == LeafHideMethod.NONE) {
                        hideLeavesMethod = LeafHideMethod.AROUND_UNITS_AND_CURSOR;
                    } else if (hideLeavesMethod == LeafHideMethod.AROUND_UNITS_AND_CURSOR) {
                        hideLeavesMethod = LeafHideMethod.ALL;
                    } else if (hideLeavesMethod == LeafHideMethod.ALL) {
                        hideLeavesMethod = LeafHideMethod.NONE;
                    }
                },
                null,
                List.of(
                        fcs(I18n.get("hud.orthoview.siege_of_settlements.hiding_leaves_around"), hideLeavesMethod == LeafHideMethod.AROUND_UNITS_AND_CURSOR),
                        fcs(I18n.get("hud.orthoview.siege_of_settlements.hiding_leaves_all"), hideLeavesMethod == LeafHideMethod.ALL),
                        fcs(I18n.get("hud.orthoview.siege_of_settlements.disabled_hiding_leaves"), hideLeavesMethod == LeafHideMethod.NONE)
                )
        );
    }

    // Method to switch difficulty to Easy if it is currently set to Peaceful
    private static void switchToEasyIfPeaceful() {
        Minecraft minecraft = Minecraft.getInstance();

        // Ensure this only runs in single-player mode
        if (minecraft.getSingleplayerServer() != null) {
            Difficulty currentDifficulty = minecraft.level.getDifficulty();

            // If the current difficulty is Peaceful, switch to Easy
            if (currentDifficulty == Difficulty.PEACEFUL) {
                minecraft.getSingleplayerServer().setDifficulty(Difficulty.EASY, true);
                HudClientEvents.showTemporaryMessage(
                    "RTS units cannot spawn in Peaceful. Your difficulty has been set to Easy.");
            }
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(ScreenEvent.MouseScrolled evt) {
        if (!enabled || isCameraLocked()) {
            return;
        }
        if (Keybindings.altMod.isDown()) {
            zoomCam((float) sign(evt.getScrollDelta()) * -ZOOM_STEP_SCROLL);
        }
    }

    @SubscribeEvent
    public static void onDrawScreen(ScreenEvent.Render evt) {
        if (!enabled || !(evt.getScreen() instanceof TopdownGui)) {
            return;
        }

        // GLFW coords seem to be 2x vanilla coords, but use only them for consistency
        // since we need to use glfwSetCursorPos
        long glfwWindow = MC.getWindow().getWindow();
        int glfwWinWidth = MC.getWindow().getScreenWidth();
        int glfwWinHeight = MC.getWindow().getScreenHeight();

        DoubleBuffer glfwCursorX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer glfwCursorY = BufferUtils.createDoubleBuffer(1);
        GLFW.glfwGetCursorPos(glfwWindow, glfwCursorX, glfwCursorY);
        double cursorX = glfwCursorX.get();
        double cursorY = glfwCursorY.get();

        // panCam when cursor is at edge of screen
        // remember that mouse (0,0) is top left of screen
        if (!Keybindings.altMod.isDown() && MC.isWindowActive() && !isCameraLocked()) {
            if (cursorX <= 0) {
                panCam(getEdgeCamPanSensitivity(), 0, 0);
                TutorialClientEvents.pannedLeft = true;
            } else if (cursorX >= glfwWinWidth) {
                panCam(-getEdgeCamPanSensitivity(), 0, 0);
                TutorialClientEvents.pannedRight = true;
            }
            if (cursorY <= 0) {
                panCam(0, 0, getEdgeCamPanSensitivity());
                TutorialClientEvents.pannedUp = true;
            } else if (cursorY >= glfwWinHeight) {
                panCam(0, 0, -getEdgeCamPanSensitivity());
                TutorialClientEvents.pannedDown = true;
            }
        }
        // lock mouse inside window
        if (cursorX >= glfwWinWidth) {
            GLFW.glfwSetCursorPos(glfwWindow, glfwWinWidth, cursorY);
        }
        if (cursorY >= glfwWinHeight) {
            GLFW.glfwSetCursorPos(glfwWindow, cursorX, glfwWinHeight);
        }
        if (cursorX <= 0) {
            GLFW.glfwSetCursorPos(glfwWindow, 0, cursorY);
        }
        if (cursorY <= 0) {
            GLFW.glfwSetCursorPos(glfwWindow, cursorX, 0);
        }

        Entity cam = getCameraEntity();

        // zoom in/out with keys
        if (Keybindings.zoomIn.isDown()) {
            zoomCam(-ZOOM_STEP_KEY);
        }
        if (Keybindings.zoomOut.isDown()) {
            zoomCam(ZOOM_STEP_KEY);
        }

        float panKeyStep = 1.5f * (getZoom() / ZOOM_MAX);

        if (!isCameraLocked() && !Keybindings.altMod.isDown()) {
            // pan camera with keys
            if (Keybindings.panPlusX.isDown()) {
                panCam(getEdgeCamPanSensitivity(), 0, 0);
            } else if (Keybindings.panMinusX.isDown()) {
                panCam(-getEdgeCamPanSensitivity(), 0, 0);
            }
            if (Keybindings.panPlusZ.isDown()) {
                panCam(0, 0, getEdgeCamPanSensitivity());
            } else if (Keybindings.panMinusZ.isDown()) {
                panCam(0, 0, -getEdgeCamPanSensitivity());
            }
        }
        // note that we treat x and y rot as horizontal and vertical, but MC treats it the other way around...
        if (cam != null) {
            applyCameraRotation(cam);
            syncCameraOldState(cam);
        }
    }

    // prevents stuff like fire and water effects being shown on your HUD
    @SubscribeEvent
    public static void onRenderBlockOverlay(RenderBlockScreenEffectEvent evt) {
        if (enabled) {
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderOverlayPre(RenderGuiOverlayEvent.Pre evt) {
        if (enabled && MC.player != null && !MC.player.isCreative()
            && evt.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id())) {
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMouseClick(ScreenEvent.MouseButtonPressed.Post evt) {
        if (!enabled || isCameraLocked()) {
            return;
        }

        if (evt.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
            mouseLeftDownX = (float) evt.getMouseX();
            mouseLeftDownY = (float) evt.getMouseY();
        } else if (evt.getButton() == GLFW.GLFW_MOUSE_BUTTON_2) {
            mouseRightDownX = (float) evt.getMouseX();
            mouseRightDownY = (float) evt.getMouseY();
        }
    }

    @SubscribeEvent
    public static void onMouseRelease(ScreenEvent.MouseButtonReleased evt) {
        if (!enabled || isCameraLocked()) {
            return;
        }

        // stop treating the rotation as adjustments and add them to the base amount
        if (evt.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
            cameraMovingByMouse = false;
        }
        if (evt.getButton() == GLFW.GLFW_MOUSE_BUTTON_2) {
            cameraMovingByMouse = false;
            rotateCam(camRotAdjX, camRotAdjY);
            camRotAdjX = 0;
            camRotAdjY = 0;
        }
    }

    @SubscribeEvent
    public static void onMouseDrag(ScreenEvent.MouseDragged evt) {
        if (!enabled || isCameraLocked()) {
            return;
        }

        if ((evt.getMouseButton() == GLFW.GLFW_MOUSE_BUTTON_1 && Keybindings.altMod.isDown())
            || evt.getMouseButton() == GLFW.GLFW_MOUSE_BUTTON_3) {
            cameraMovingByMouse = true;

            float moveX = (float) evt.getDragX() * 0.20f * (zoom / ZOOM_MAX) * getPanSensitivityMult();
            float moveZ = (float) evt.getDragY() * 0.20f * (zoom / ZOOM_MAX) * getPanSensitivityMult();
            panCam(moveX, 0, moveZ);
        } else if (evt.getMouseButton() == GLFW.GLFW_MOUSE_BUTTON_2 && Keybindings.altMod.isDown()) {
            cameraMovingByMouse = true;
            camRotAdjX = (float) -(evt.getMouseX() - mouseRightDownX) * CAMROT_MOUSE_SENSITIVITY;
            //camRotAdjY = (float) -(evt.getMouseY() - mouseRightDownY) * CAMROT_MOUSE_SENSITIVITY;

            /*
            if (camRotY + camRotAdjY > CAMROTY_MAX)
                camRotAdjY = CAMROTY_MAX - camRotY;
            if (camRotY + camRotAdjY < CAMROTY_MIN)
                camRotAdjY = CAMROTY_MIN - camRotY;
             */
        }
    }

    // don't let orthoview players see other orthoview players or themselves
    @SubscribeEvent
    public static void onPlayerRender(RenderPlayerEvent.Pre evt) {
        if (enabled && (evt.getEntity().isSpectator() || evt.getEntity().isCreative())
            && (MC.player == null || evt.getEntity().getId() != MC.player.getId())) {
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onFovModifier(ViewportEvent.ComputeFov evt) {
        if (enabled) {
            evt.setFOV(180);
        }
    }

    /*
    @SubscribeEvent
    public static void onRenderOverLay(RenderGuiOverlayEvent.Pre evt) {
        MiscUtil.drawDebugStrings(evt.getGuiGraphics(), MC.font, new String[] {
                "rotX: " + getCamRotX()
        });
    }
     */

    // OrthoViewMixin uses this to generate a customisation orthographic view to replace the usual view
    // shamelessly copied from ImmersivePortals 1.16
    public static Matrix4f getOrthographicProjection() {
        int width = MC.getWindow().getScreenWidth();
        int height = MC.getWindow().getScreenHeight();

        float near = -3000;
        float far = 3000;

        float zoomFinal = zoom;

        float wView = (zoomFinal / height) * width;
        float left = -wView / 2;
        float rgt = wView / 2;

        float top = zoomFinal / 2;
        float bot = -zoomFinal / 2;

        Matrix4f m1 = new Matrix4f(2.0f / (rgt - left), 0, 0,
                -(rgt + left) / (rgt - left), 0,
                2.0f / (top - bot), 0,
                -(top + bot) / (top - bot), 0, 0, -2.0f / (far - near), -(far + near) / (far - near), 0, 0, 0, 1);

        return m1;
    }
}



