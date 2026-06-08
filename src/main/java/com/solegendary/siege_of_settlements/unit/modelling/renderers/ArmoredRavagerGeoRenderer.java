package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.WardenMine242.siege_of_settlements.unit.units.villagers.ArmoredRavagerUnit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.solegendary.siege_of_settlements.unit.modelling.models.ArmoredRavagerGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class ArmoredRavagerGeoRenderer extends GeoEntityRenderer<ArmoredRavagerUnit> {
    private static final float DEG_TO_RAD = (float) Math.PI / 180.0F;
    private static final float LEG_SWING_SCALE = 0.4F;
    private static final float VANILLA_NECK_Y = -7.0F;
    private static final float VANILLA_NECK_Z = 5.5F;

    public ArmoredRavagerGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ArmoredRavagerGeoModel());
        this.shadowRadius = 1.1F;
    }

    @Override
    public void renderRecursively(PoseStack poseStack,
                                  ArmoredRavagerUnit animatable,
                                  GeoBone bone,
                                  RenderType renderType,
                                  MultiBufferSource bufferSource,
                                  VertexConsumer buffer,
                                  boolean isReRender,
                                  float partialTick,
                                  int packedLight,
                                  int packedOverlay,
                                  float red,
                                  float green,
                                  float blue,
                                  float alpha) {
        float oldRotX = bone.getRotX();
        float oldRotY = bone.getRotY();
        float oldRotZ = bone.getRotZ();
        float oldPosX = bone.getPosX();
        float oldPosY = bone.getPosY();
        float oldPosZ = bone.getPosZ();

        applyRavagerPose(animatable, bone, partialTick);

        super.renderRecursively(
                poseStack, animatable, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha
        );

        bone.setRotX(oldRotX);
        bone.setRotY(oldRotY);
        bone.setRotZ(oldRotZ);
        bone.setPosX(oldPosX);
        bone.setPosY(oldPosY);
        bone.setPosZ(oldPosZ);
    }

    private void applyRavagerPose(ArmoredRavagerUnit ravager, GeoBone bone, float partialTick) {
        String boneName = bone.getName();
        float limbSwing = ravager.walkAnimation.position(partialTick);
        float limbSwingAmount = ravager.walkAnimation.speed(partialTick);
        float legSwing = Mth.cos(limbSwing * 0.6662F) * LEG_SWING_SCALE * limbSwingAmount;

        if ("leg1".equals(boneName) || "leg4".equals(boneName)) {
            bone.setRotX(bone.getRotX() + legSwing);
            return;
        }
        if ("leg2".equals(boneName) || "leg3".equals(boneName)) {
            bone.setRotX(bone.getRotX() - legSwing);
            return;
        }

        RavagerHeadPose headPose = getHeadPose(ravager, partialTick);
        if ("head".equals(boneName)) {
            bone.setRotX(bone.getRotX() + headPose.headRotX);
            bone.setRotY(bone.getRotY() + headPose.headRotY);
        } else if ("jaw".equals(boneName)) {
            bone.setRotX(bone.getRotX() + headPose.jawRotX);
        } else if ("neck".equals(boneName)) {
            bone.setRotX(bone.getRotX() + headPose.neckRotX);
            bone.setPosX(bone.getPosX() + headPose.neckPosX);
            bone.setPosY(bone.getPosY() + headPose.neckPosY);
            bone.setPosZ(bone.getPosZ() + headPose.neckPosZ);
        }
    }

    private RavagerHeadPose getHeadPose(ArmoredRavagerUnit ravager, float partialTick) {
        float bodyYaw = Mth.rotLerp(partialTick, ravager.yBodyRotO, ravager.yBodyRot);
        float headYaw = Mth.rotLerp(partialTick, ravager.yHeadRotO, ravager.getYHeadRot());
        float netHeadYaw = Mth.wrapDegrees(headYaw - bodyYaw);
        float headPitch = Mth.rotLerp(partialTick, ravager.xRotO, ravager.getXRot());

        RavagerHeadPose pose = new RavagerHeadPose();
        pose.headRotY = netHeadYaw * DEG_TO_RAD;
        pose.headRotX = headPitch * DEG_TO_RAD;

        int attackTick = ravager.getAttackTick();
        int stunnedTick = ravager.getStunnedTick();
        int roarTick = ravager.getRoarTick();
        if (attackTick > 0) {
            float attackTicks = (float) attackTick - partialTick;
            float wave = Mth.triangleWave(attackTicks, 10.0F);
            float progress = (1.0F + wave) * 0.5F;
            float neckForward = progress * progress * progress * 12.0F;
            float neckLift = neckForward * Mth.sin(pose.neckRotX);
            pose.neckPosZ = -6.5F + neckForward - VANILLA_NECK_Z;
            pose.neckPosY = -7.0F - neckLift - VANILLA_NECK_Y;
            pose.jawRotX = attackTick > 5
                    ? Mth.sin(((float) (-4 + attackTick) - partialTick) / 4.0F) * (float) Math.PI * 0.4F
                    : (float) Math.PI * 0.05F * Mth.sin((float) Math.PI * attackTicks / 10.0F);
        } else {
            float neckLift = -Mth.sin(pose.neckRotX);
            boolean stunned = stunnedTick > 0;
            pose.neckPosY = -neckLift;
            pose.neckRotX = stunned ? 0.21991149F : 0.0F;
            pose.jawRotX = (float) Math.PI * (stunned ? 0.05F : 0.01F);

            if (stunned) {
                pose.neckPosX = (float) Math.sin((double) stunnedTick / 40.0D * 10.0D) * 3.0F;
            } else if (roarTick > 0) {
                float roarOpen = Mth.sin(((float) (20 - roarTick) - partialTick) / 20.0F * (float) Math.PI * 0.25F);
                pose.jawRotX = ((float) Math.PI / 2.0F) * roarOpen;
            }
        }

        return pose;
    }

    private static class RavagerHeadPose {
        private float headRotX;
        private float headRotY;
        private float jawRotX;
        private float neckRotX;
        private float neckPosX;
        private float neckPosY;
        private float neckPosZ;
    }
}
