package com.solegendary.siege_of_settlements.unit.modelling.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import net.WardenMine242.siege_of_settlements.unit.units.villagers.EliteVindicatorUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

// Made with Blockbench 5.0.7, then adapted for Siege of Settlements.
public class EliteVindicatorModel<T extends EliteVindicatorUnit> extends EntityModel<T> {
    private static final Minecraft MC = Minecraft.getInstance();
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "elite_vindicator_layer"), "main");

    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private static final Map<Integer, Integer> LAST_ATTACK_SWING_INDEX = new HashMap<>();
    private static final Map<Integer, Boolean> ATTACK_SIDE_LEFT = new HashMap<>();
    private static final Map<Integer, Float> ATTACK_START_AGE = new HashMap<>();
    private static final float ATTACK_ANIM_DURATION_TICKS = 14.0F;

    public EliteVindicatorModel(ModelPart root) {
        this.head = root.getChild("head");
        this.nose = this.head.getChild("nose");
        this.body = root.getChild("body");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("nose",
                CubeListBuilder.create()
                        .texOffs(24, 0)
                        .addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -2.0F, 0.0F));

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(16, 20)
                        .addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 38)
                        .addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition leftArm = root.addOrReplaceChild("left_arm",
                CubeListBuilder.create()
                        .texOffs(40, 46).mirror()
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        leftArm.addOrReplaceChild("iron_sword2",
                CubeListBuilder.create()
                        .texOffs(51, 33).addBox(-2.0F, -2.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(53, 28).addBox(-1.0F, -6.0F, -1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(51, 25).addBox(-2.0F, -8.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(59, 26).addBox(1.0F, -8.0F, -0.5F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(47, 26).addBox(-4.0F, -8.0F, -0.5F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(52, 13).addBox(-2.0F, -20.0F, -0.5F, 3.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(54, 11).addBox(-1.0F, -22.0F, -0.5F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, 8.0F, 4.0F, 1.5708F, 0.0F, -1.5708F));

        PartDefinition rightArm = root.addOrReplaceChild("right_arm",
                CubeListBuilder.create()
                        .texOffs(40, 46)
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        rightArm.addOrReplaceChild("iron_sword",
                CubeListBuilder.create()
                        .texOffs(51, 33).addBox(-2.0F, -2.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(53, 28).addBox(-1.0F, -6.0F, -1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(51, 25).addBox(-2.0F, -8.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(59, 26).addBox(1.0F, -8.0F, -0.5F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(47, 26).addBox(-4.0F, -8.0F, -0.5F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(52, 13).addBox(-2.0F, -20.0F, -0.5F, 3.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(54, 11).addBox(-1.0F, -22.0F, -0.5F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0F, 8.0F, 4.0F, 1.5708F, 0.0F, -1.5708F));

        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(0, 22).mirror()
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(2.0F, 12.0F, 0.0F));

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, 12.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPoses();

        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        float walkSpeed = 0.6662F * entity.getWalkAnimationSpeedMultiplier();
        this.rightLeg.xRot = Mth.cos(limbSwing * walkSpeed) * 1.4F * limbSwingAmount * 0.5F;
        this.leftLeg.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        if (!isAttackActive(entity.getId(), ageInTicks)) {
            this.rightArm.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.leftArm.xRot = Mth.cos(limbSwing * walkSpeed) * 2.0F * limbSwingAmount * 0.5F;
        }

        // Subtle vanilla-style idle breathing, matching the Auromancer feel.
        float breathe = Mth.sin(ageInTicks * 0.09F) * 0.03F;
        this.body.xRot += breathe;
        this.head.y += Mth.sin(ageInTicks * 0.09F) * 0.35F;

        applyAlternatingAttackAnimation(entity, ageInTicks);
    }

    private boolean isAttackActive(int entityId, float ageInTicks) {
        if (!ATTACK_START_AGE.containsKey(entityId)) {
            return false;
        }
        float progress = (ageInTicks - ATTACK_START_AGE.get(entityId)) / getAttackAnimDuration(entityId);
        return progress >= 0.0F && progress < 1.0F;
    }

    private void applyAlternatingAttackAnimation(T entity, float ageInTicks) {
        int entityId = entity.getId();
        int swingIndex = entity.getAttackSwingIndex();
        if (!LAST_ATTACK_SWING_INDEX.containsKey(entityId)) {
            LAST_ATTACK_SWING_INDEX.put(entityId, swingIndex);
        }
        int prevSwingIndex = LAST_ATTACK_SWING_INDEX.get(entityId);
        boolean sideLeft = ATTACK_SIDE_LEFT.getOrDefault(entityId, false);

        if (swingIndex != prevSwingIndex) {
            sideLeft = !sideLeft;
            ATTACK_SIDE_LEFT.put(entityId, sideLeft);
            ATTACK_START_AGE.put(entityId, ageInTicks);
            LAST_ATTACK_SWING_INDEX.put(entityId, swingIndex);
        }

        float startAge = ATTACK_START_AGE.getOrDefault(entityId, -9999.0F);
        float progress = (ageInTicks - startAge) / getAttackAnimDuration(entityId);
        if (progress >= 0.0F && progress < 1.0F) {
            progress = Mth.clamp(progress, 0.0F, 1.0F);
            if (sideLeft) {
                applyDungeonsStyleLeftSlash(progress, ageInTicks);
            } else {
                applyDungeonsStyleRightSlash(progress, ageInTicks);
            }
        } else {
            ATTACK_START_AGE.remove(entityId);
        }
    }

    private float getAttackAnimDuration(int entityId) {
        if (MC.level != null && MC.level.getEntity(entityId) instanceof EliteVindicatorUnit unit) {
            return ATTACK_ANIM_DURATION_TICKS / unit.getAttackAnimationSpeedMultiplier();
        }
        return ATTACK_ANIM_DURATION_TICKS;
    }

    private void applyDungeonsStyleRightSlash(float t, float ageInTicks) {
        float windup = Mth.clamp(t / 0.30F, 0.0F, 1.0F);
        float slash = Mth.clamp((t - 0.30F) / 0.40F, 0.0F, 1.0F);
        float recover = Mth.clamp((t - 0.70F) / 0.30F, 0.0F, 1.0F);
        slash = 1.0F - (1.0F - slash) * (1.0F - slash);

        // Compact one-handed slash, close to the Minecraft Dungeons basic sword timing.
        float x = this.rightArm.xRot;
        float y = this.rightArm.yRot;
        float z = this.rightArm.zRot;

        x = Mth.lerp(windup, x, -1.95F);
        y = Mth.lerp(windup, y, 0.62F);
        z = Mth.lerp(windup, z, 0.95F);

        x = Mth.lerp(slash, x, 1.20F);
        // Keep slash on its own side so arm does not pass through torso.
        y = Mth.lerp(slash, y, 0.18F);
        z = Mth.lerp(slash, z, 0.62F);

        x = Mth.lerp(recover, x, -0.20F);
        y = Mth.lerp(recover, y, 0.14F);
        z = Mth.lerp(recover, z, 0.34F);

        this.rightArm.xRot = x;
        // clamp inward rotation so arm/sword never clips through torso
        this.rightArm.yRot = Mth.clamp(y, 0.10F, 1.15F);
        this.rightArm.zRot = Mth.clamp(z, 0.20F, 1.20F);
        this.rightArm.x = -5.8F;

        // Off-hand guard.
        this.leftArm.xRot = -0.28F + Mth.sin(ageInTicks * 0.20F) * 0.02F;
        this.leftArm.yRot = 0.18F;
        this.leftArm.zRot = -0.10F;

        // Light torso follow-through so it still reads well from RTS camera.
        float bodyYaw = Mth.lerp(windup, 0.0F, 0.10F) + Mth.lerp(slash, 0.0F, -0.28F) + Mth.lerp(recover, 0.0F, 0.18F);
        float bodyPitch = Mth.lerp(windup, 0.0F, -0.16F) + Mth.lerp(slash, 0.0F, 0.24F);
        this.body.yRot += bodyYaw;
        this.body.xRot += bodyPitch;
        this.head.yRot += bodyYaw * 0.45F;
        this.head.xRot += bodyPitch * 0.35F;
    }

    private void applyDungeonsStyleLeftSlash(float t, float ageInTicks) {
        float windup = Mth.clamp(t / 0.30F, 0.0F, 1.0F);
        float slash = Mth.clamp((t - 0.30F) / 0.40F, 0.0F, 1.0F);
        float recover = Mth.clamp((t - 0.70F) / 0.30F, 0.0F, 1.0F);
        slash = 1.0F - (1.0F - slash) * (1.0F - slash);

        float x = this.leftArm.xRot;
        float y = this.leftArm.yRot;
        float z = this.leftArm.zRot;

        x = Mth.lerp(windup, x, -1.95F);
        y = Mth.lerp(windup, y, -0.62F);
        z = Mth.lerp(windup, z, -0.95F);

        x = Mth.lerp(slash, x, 1.20F);
        // Mirrored: keep slash on its own side.
        y = Mth.lerp(slash, y, -0.18F);
        z = Mth.lerp(slash, z, -0.62F);

        x = Mth.lerp(recover, x, -0.20F);
        y = Mth.lerp(recover, y, -0.14F);
        z = Mth.lerp(recover, z, -0.34F);

        this.leftArm.xRot = x;
        // mirrored inward clamp
        this.leftArm.yRot = Mth.clamp(y, -1.15F, -0.10F);
        this.leftArm.zRot = Mth.clamp(z, -1.20F, -0.20F);
        this.leftArm.x = 5.8F;

        // Off-hand guard.
        this.rightArm.xRot = -0.28F + Mth.sin(ageInTicks * 0.2F + (float) Math.PI) * 0.02F;
        this.rightArm.yRot = -0.18F;
        this.rightArm.zRot = 0.10F;

        // Torso/head follow-through mirrored.
        float bodyYaw = Mth.lerp(windup, 0.0F, -0.10F) + Mth.lerp(slash, 0.0F, 0.28F) + Mth.lerp(recover, 0.0F, -0.18F);
        float bodyPitch = Mth.lerp(windup, 0.0F, -0.16F) + Mth.lerp(slash, 0.0F, 0.24F);
        this.body.yRot += bodyYaw;
        this.body.xRot += bodyPitch;
        this.head.yRot += bodyYaw * 0.45F;
        this.head.xRot += bodyPitch * 0.35F;
    }

    private void resetPoses() {
        this.head.resetPose();
        this.nose.resetPose();
        this.body.resetPose();
        this.leftArm.resetPose();
        this.rightArm.resetPose();
        this.leftLeg.resetPose();
        this.rightLeg.resetPose();
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
