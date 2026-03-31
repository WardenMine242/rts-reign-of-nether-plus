package com.solegendary.siege_of_settlements.resources;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.orthoview.OrthoviewClientEvents;
import com.solegendary.siege_of_settlements.unit.Relationship;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.interfaces.WorkerUnit;
import com.solegendary.siege_of_settlements.util.MyRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

import static com.solegendary.siege_of_settlements.unit.UnitClientEvents.getSelectedUnits;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class ResourcesClientEvents {

    // tracks all players' resources
    public static ArrayList<Resources> resourcesList = new ArrayList<>();

    private final static Minecraft MC = Minecraft.getInstance();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END) {
            return;
        }

        for (Resources resources : resourcesList)
            resources.tick();

        for (PickupText pickupText : pickupTexts) {
            pickupText.tickAge += 1;
        }
        pickupTexts.removeIf(t -> t.tickAge > PICKUP_TEXT_MAX_AGE);
    }

    @SubscribeEvent
    public static void onClientTick(ScreenEvent.Render evt) {
        if (HudClientEvents.hudSelectedEntity instanceof WorkerUnit workerUnit &&
            UnitClientEvents.getPlayerToEntityRelationship((LivingEntity) workerUnit) == Relationship.OWNED &&
            MC.level != null && !HudClientEvents.isMouseOverAnyButtonOrHud()) {

            BlockPos preSelBp = CursorClientEvents.getPreselectedBlockPos();
            ResourceSource res = ResourceSources.getFromBlockPos(preSelBp, MC.level);
            if (res != null && res.resourceValue > 0 && !BuildingUtils.isPosInsideAnyBuilding(true, preSelBp)) {
                String str = switch (res.resourceName) {
                    case FOOD -> "\uE000  " + res.resourceValue;
                    case WOOD -> "\uE001  " + res.resourceValue;
                    case ORE -> "\uE002  " + res.resourceValue;
                    case EMERALDS -> "\uE00A  " + res.resourceValue;
                    case NONE -> "";
                };
                MyRenderer.renderTooltip(evt.getGuiGraphics(),
                        List.of(FormattedCharSequence.forward(str, MyRenderer.iconStyle)),
                        evt.getMouseX(), evt.getMouseY()
                );
            }
        }
    }

    public static void syncResources(Resources serverResources) {
        resourcesList.removeIf(resources -> resources.ownerName.equals(serverResources.ownerName));
        resourcesList.add(serverResources);
    }

    // should never be run from clientside except via packet
    public static void addSubtractResources(Resources serverResources) {
        for (Resources resources : resourcesList)
            if (resources.ownerName.equals(serverResources.ownerName)) {
                resources.changeOverTime(serverResources.food, serverResources.wood, serverResources.ore, serverResources.emerald);
            }
    }

    public static void addSubtractResourcesInstantly(Resources serverResources) {
        for (Resources resources : resourcesList)
            if (resources.ownerName.equals(serverResources.ownerName)) {
                resources.changeInstantly(serverResources.food, serverResources.wood, serverResources.ore, serverResources.emerald);
            }
    }

    public static Resources getOwnResources() {
        Minecraft MC = Minecraft.getInstance();
        if (MC.player != null) {
            return getResources(MC.player.getName().getString());
        }
        return null;
    }

    public static Resources getResources(String playerName) {
        for (Resources resources : resourcesList)
            if (resources.ownerName.equals(playerName)) {
                return resources;
            }
        return null;
    }

    public static void showWarning(String ownerName, String msg) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getName().getString().equals(ownerName)) {
            String loc = I18n.get(msg);
            HudClientEvents.showTemporaryMessage(loc);

            // remove checkpoints from a failed building placement since the client has no knowledge of resource costs
            if (msg.contains("not_enough")) {
                for (LivingEntity entity : getSelectedUnits())
                    if (entity instanceof Unit unit)
                        if (((Entity) unit).level().isClientSide() && !Keybindings.shiftMod.isDown())
                            unit.getCheckpoints().clear();
            }
        }
    }

    // floating text for resource dropoffs
    public static final List<FloatingText> floatingTexts = new ArrayList<>();
    public static final int FLOATING_TEXT_MAX_AGE = 200;

    private static class FloatingText {
        String text;
        BlockPos pos;
        int tickAge;

        public FloatingText(String text, BlockPos pos, int tickAge) {
            this.text = text;
            this.pos = pos;
            this.tickAge = tickAge;
        }
    }

    // screen-space pickup texts for resource pickups
    public static final List<PickupText> pickupTexts = new ArrayList<>();
    public static final int PICKUP_TEXT_MAX_AGE = 80;
    public static final int PICKUP_TEXT_FADE_START = 50;
    public static final int PICKUP_TEXT_STACK_SPACING = 10;
    public static final int PICKUP_TEXT_X = 12;
    public static final float PICKUP_TEXT_START_Y_FACTOR = 0.35f;
    public static final float PICKUP_TEXT_END_Y_FACTOR = 0.55f;

    private static class PickupText {
        String label;
        String icon;
        int tickAge;
        int yOffset;

        public PickupText(String label, String icon, int yOffset) {
            this.label = label;
            this.icon = icon;
            this.yOffset = yOffset;
            this.tickAge = 0;
        }
    }

    public static void addFloatingTextsFromResources(Resources res, BlockPos pos) {
        if (!OrthoviewClientEvents.isEnabled()) {
            return;
        }
        if (!Minecraft.getInstance().player.getName().getString().equals(res.ownerName)) {
            return;
        }

        int tickAge = 0;
        if (res.food > 0) {
            floatingTexts.add(new FloatingText("+" + res.food + "  \uE000", pos, tickAge));
            tickAge -= 25;
        }
        if (res.wood > 0) {
            floatingTexts.add(new FloatingText("+" + res.wood + "  \uE001", pos, tickAge));
            tickAge -= 25;
        }
        if (res.ore > 0) {
            floatingTexts.add(new FloatingText("+" + res.ore + "  \uE002", pos, tickAge));
            tickAge -= 25;
        }
        if (res.emerald > 0) {
            floatingTexts.add(new FloatingText("+" + res.emerald + "  \uE00A", pos, tickAge));
        }
    }

    public static void addPickupTextsFromResources(Resources res) {
        if (MC.player == null || !MC.player.getName().getString().equals(res.ownerName)) {
            return;
        }

        boolean useRussian = isRussianLanguage();
        String foodLabel = useRussian ? "\u0435\u0434\u044b" : "food";
        String woodLabel = useRussian ? "\u0434\u0435\u0440\u0435\u0432\u0430" : "wood";
        String oreLabel = useRussian ? "\u0440\u0443\u0434\u044b" : "ore";
        String emeraldLabel = useRussian ? "\u0438\u0437\u0443\u043c\u0440\u0443\u0434\u043e\u0432" : "emeralds";

        int offset = -pickupTexts.size() * PICKUP_TEXT_STACK_SPACING;
        if (res.food > 0) {
            pickupTexts.add(new PickupText("+ " + res.food + " " + foodLabel, "\uE000", offset));
            offset -= PICKUP_TEXT_STACK_SPACING;
        }
        if (res.wood > 0) {
            pickupTexts.add(new PickupText("+ " + res.wood + " " + woodLabel, "\uE001", offset));
            offset -= PICKUP_TEXT_STACK_SPACING;
        }
        if (res.ore > 0) {
            pickupTexts.add(new PickupText("+ " + res.ore + " " + oreLabel, "\uE002", offset));
            offset -= PICKUP_TEXT_STACK_SPACING;
        }
        if (res.emerald > 0) {
            pickupTexts.add(new PickupText("+ " + res.emerald + " " + emeraldLabel, "\uE00A", offset));
        }
    }

    public static void addPickupTextMessage(String msgCode) {
        if (MC.player == null) {
            return;
        }

        String label;
        boolean useRussian = isRussianLanguage();
        if ("not_rts_resource".equals(msgCode)) {
            label = useRussian ? "\u042d\u0442\u043e \u043d\u0435 RTS \u0440\u0435\u0441\u0443\u0440\u0441" : "This is not an RTS resource";
        } else {
            label = msgCode;
        }

        int offset = -pickupTexts.size() * PICKUP_TEXT_STACK_SPACING;
        pickupTexts.add(new PickupText(label, "", offset));
    }
    private static boolean isRussianLanguage() {
        String code = MC.getLanguageManager().getSelected();
        return code != null && code.startsWith("ru");
    }

    // Render floating text for dropped-off resources
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent evt) {
        if (evt.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        for (FloatingText floatingText : floatingTexts) {

            if (floatingText.tickAge > 0) {
                Minecraft MC = Minecraft.getInstance();
                MutableComponent component = Component.literal(floatingText.text).withStyle(MyRenderer.iconStyle);

                PoseStack poseStack = evt.getPoseStack();
                Camera camera = MC.getEntityRenderDispatcher().camera;
                Vec3 camPos = camera.getPosition();

                poseStack.pushPose();
                poseStack.translate(floatingText.pos.getX() - camPos.x(),
                    floatingText.pos.getY() - camPos.y() + 2.5 + (floatingText.tickAge / 20f),
                    floatingText.pos.getZ() - camPos.z()
                );
                poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
                poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
                if (OrthoviewClientEvents.isEnabled()) {
                    poseStack.scale(-0.075F, -0.075F, 0.075F);
                } else {
                    poseStack.scale(-0.05F, -0.05F, 0.05F);
                }
                Font font = MC.font;

                float f2 = (float) (-font.width(component) / 2);
                float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
                float alphaPercent =
                    ((FLOATING_TEXT_MAX_AGE - floatingText.tickAge) * 1.5f) / (float) FLOATING_TEXT_MAX_AGE;
                if (alphaPercent < 0.05) {
                    alphaPercent = 0.05f;
                }
                if (alphaPercent > 1) {
                    alphaPercent = 1f;
                }
                int textCol = 0x00FFFFFF + ((int) (0xFF * alphaPercent) << 24);
                int bgCol = (int) (f1 * 255.0F * alphaPercent) << 24;

                font.drawInBatch(component,
                    f2,
                    0,
                    textCol,
                    false,
                    poseStack.last().pose(),
                    MC.renderBuffers().bufferSource(),
                    Font.DisplayMode.NORMAL,
                    bgCol,
                    255
                );

                poseStack.popPose();
            }
            floatingText.tickAge += 1;
        }
        floatingTexts.removeIf(t -> t.tickAge > FLOATING_TEXT_MAX_AGE);
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post evt) {
        if (!evt.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            return;
        }

        if (pickupTexts.isEmpty()) {
            return;
        }

        int screenHeight = MC.getWindow().getGuiScaledHeight();
        float startY = screenHeight * PICKUP_TEXT_START_Y_FACTOR;
        float endY = screenHeight * PICKUP_TEXT_END_Y_FACTOR;

        Font font = MC.font;
        var guiGraphics = evt.getGuiGraphics();

        for (PickupText pickupText : pickupTexts) {
            float progress = pickupText.tickAge / (float) PICKUP_TEXT_MAX_AGE;
            float y = Mth.lerp(progress, startY + pickupText.yOffset, endY + pickupText.yOffset);

            float alpha;
            if (pickupText.tickAge <= PICKUP_TEXT_FADE_START) {
                alpha = 1.0f;
            } else {
                alpha = 1.0f - (pickupText.tickAge - PICKUP_TEXT_FADE_START) /
                    (float) (PICKUP_TEXT_MAX_AGE - PICKUP_TEXT_FADE_START);
            }
            alpha = Mth.clamp(alpha, 0.0f, 1.0f);

            int color = 0x00FFFFFF + ((int) (0xFF * alpha) << 24);

            Component textComp = Component.literal(pickupText.label);
            int textWidth = font.width(textComp);
            guiGraphics.drawString(font, textComp, PICKUP_TEXT_X, Math.round(y), color, false);

            if (pickupText.icon != null && !pickupText.icon.isEmpty()) {
                Component iconComp = Component.literal(pickupText.icon).withStyle(MyRenderer.iconStyle);
                guiGraphics.drawString(font, iconComp, PICKUP_TEXT_X + textWidth + 2, Math.round(y), color, false);
            }
        }
    }
}


