package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.solegendary.siege_of_settlements.unit.modelling.models.DragonUnitGeoModel;
import com.solegendary.siege_of_settlements.unit.units.villagers.DragonUnit_TEST;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class DragonUnitGeoRenderer extends GeoEntityRenderer<DragonUnit_TEST> {
    public DragonUnitGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DragonUnitGeoModel());
        this.shadowRadius = 1.25F;
    }

    @Override
    public void renderRecursively(PoseStack poseStack,
                                  DragonUnit_TEST animatable,
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
        if ("head".equals(bone.getName())) {
            float oldRotX = bone.getRotX();
            float oldRotY = bone.getRotY();

            float bodyYaw = Mth.rotLerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
            float headYaw = Mth.rotLerp(partialTick, animatable.yHeadRotO, animatable.getYHeadRot());
            float yawDelta = Mth.clamp(Mth.wrapDegrees(headYaw - bodyYaw), -55.0F, 55.0F);
            float pitch = Mth.clamp(Mth.rotLerp(partialTick, animatable.xRotO, animatable.getXRot()), -30.0F, 35.0F);

            bone.setRotY(oldRotY + yawDelta * ((float) Math.PI / 180.0F));
            bone.setRotX(oldRotX + pitch * ((float) Math.PI / 180.0F));

            super.renderRecursively(
                    poseStack, animatable, bone, renderType, bufferSource, buffer,
                    isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha
            );

            bone.setRotX(oldRotX);
            bone.setRotY(oldRotY);
            return;
        }

        super.renderRecursively(
                poseStack, animatable, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha
        );
    }
}
