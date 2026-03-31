package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.solegendary.siege_of_settlements.unit.modelling.models.EliteArcherGeoModel;
import com.solegendary.siege_of_settlements.unit.units.villagers.RavagerUnit;
import net.WardenMine242.siege_of_settlements.unit.units.villagers.EliteArcherUnit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class EliteArcherGeoRenderer extends GeoEntityRenderer<EliteArcherUnit> {

    public EliteArcherGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EliteArcherGeoModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    protected void applyRotations(EliteArcherUnit animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        // Keep the custom geo model at the same visual size as a regular pillager.
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);

        if (animatable.shouldUseAttackFacing()) {
            // During draw/fire animation, visually rotate front by 45deg (red-arrow direction).
            poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        }
    }

    @Override
    public void renderRecursively(PoseStack poseStack,
                                  EliteArcherUnit animatable,
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
        boolean ridingRavager = animatable.isPassenger() && animatable.getVehicle() instanceof RavagerUnit;
        boolean isLegBone = "right_leg".equals(bone.getName()) || "left_leg".equals(bone.getName());

        float oldRotX = bone.getRotX();
        float oldRotY = bone.getRotY();
        float oldRotZ = bone.getRotZ();

        if (ridingRavager && isLegBone) {
            if ("right_leg".equals(bone.getName())) {
                bone.setRotX(1.4137167F);
                // Same seated pose, but mirrored to the opposite side.
                bone.setRotY(-(float) Math.PI / 10F);
                bone.setRotZ(-0.07853982F);
            } else if ("left_leg".equals(bone.getName())) {
                bone.setRotX(1.4137167F);
                bone.setRotY((float) Math.PI / 10F);
                bone.setRotZ(0.07853982F);
            }
        }

        if ("arrow2".equals(bone.getName())) {
            bone.setHidden(!animatable.shouldRenderArrow2());
        }

        super.renderRecursively(
                poseStack, animatable, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha
        );

        // Restore animated values so this override only affects the current mounted frame.
        if (isLegBone) {
            bone.setRotX(oldRotX);
            bone.setRotY(oldRotY);
            bone.setRotZ(oldRotZ);
        }
    }
}
