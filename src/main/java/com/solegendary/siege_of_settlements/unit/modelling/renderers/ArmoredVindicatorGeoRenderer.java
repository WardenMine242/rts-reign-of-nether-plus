package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.WardenMine242.siege_of_settlements.unit.units.villagers.ArmoredVindicatorUnit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.unit.modelling.models.ArmoredVindicatorGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

@OnlyIn(Dist.CLIENT)
public class ArmoredVindicatorGeoRenderer extends GeoEntityRenderer<ArmoredVindicatorUnit> {

    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/entities/armored_vindicator.png");

    public ArmoredVindicatorGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ArmoredVindicatorGeoModel());
        this.shadowRadius = 0.5F;

        this.addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, ArmoredVindicatorUnit animatable) {
                if (bone.getName().equals("right_arm") && animatable.isAggressive()) {
                    return animatable.getMainHandItem();
                }
                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, ArmoredVindicatorUnit animatable) {
                return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, ArmoredVindicatorUnit animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                // Same alignment as regular Elite Armored Vindicator
                poseStack.translate(-0.0625f, 0.85f, -0.125f);
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(ArmoredVindicatorUnit animatable) {
        return TEXTURE_LOCATION;
    }

    @Override
    protected void applyRotations(ArmoredVindicatorUnit animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public void renderRecursively(PoseStack poseStack,
                                  ArmoredVindicatorUnit animatable,
                                  GeoBone bone,
                                  RenderType renderType,
                                  MultiBufferSource bufferSource,
                                  com.mojang.blaze3d.vertex.VertexConsumer buffer,
                                  boolean isReRender,
                                  float partialTick,
                                  int packedLight,
                                  int packedOverlay,
                                  float red,
                                  float green,
                                  float blue,
                                  float alpha) {
        String name = bone.getName();

        if (name.equals("arms")) {
            bone.setHidden(animatable.isAggressive());
        } else if (name.equals("left_arm") || name.equals("right_arm")) {
            bone.setHidden(!animatable.isAggressive());
        }

        float limbSwing = animatable.walkAnimation.position(partialTick);
        float limbSwingAmount = animatable.walkAnimation.speed(partialTick);

        if (name.equals("head")) {
            bone.setRotX(animatable.getXRot() * 0.017453292F);
            bone.setRotY((animatable.getYHeadRot() - animatable.yBodyRot) * 0.017453292F);
        } else if (name.equals("right_leg")) {
            bone.setRotX(Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F);
        } else if (name.equals("left_leg")) {
            bone.setRotX(Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F);
        } else if (name.equals("right_arm") && !bone.isHidden()) {
            if (animatable.isAggressive()) {
                float attackAnim = animatable.getAttackAnim(partialTick);
                if (attackAnim > 0) {
                    float f = Mth.sin(attackAnim * (float)Math.PI);
                    float f1 = Mth.sin((1.0F - (1.0F - attackAnim) * (1.0F - attackAnim)) * (float)Math.PI);
                    bone.setRotX(-1.3962634F + (f * 1.2F - f1 * 0.4F));
                    bone.setRotY(-(0.1F - f * 0.6F));
                } else {
                    bone.setRotX(2.3561945F);
                    bone.setRotY(0.0F);
                }
                bone.setRotZ(0.0F);
            }
        } else if (name.equals("left_arm") && !bone.isHidden()) {
            if (animatable.isAggressive()) {
                float attackAnim = animatable.getAttackAnim(partialTick);
                if (attackAnim > 0) {
                    bone.setRotX(-0.1F);
                } else {
                    bone.setRotX(Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F);
                }
                bone.setRotY(0.0F);
                bone.setRotZ(0.0F);
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
