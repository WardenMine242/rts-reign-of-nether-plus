package com.WardenMine242.siege_of_settlements.unit.modelling.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.IllusionerUnit;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IllusionerUnitRenderer extends MobRenderer<IllusionerUnit, IllagerModel<IllusionerUnit>> {
    private static final ResourceLocation ILLUSIONER_ILLAGER = ResourceLocation.fromNamespaceAndPath("siege_of_settlements", "textures/entities/illusioner_unit.png");

    public IllusionerUnitRenderer(EntityRendererProvider.Context p_174108_) {
        super(p_174108_, new IllagerModel<>(p_174108_.bakeLayer(ModelLayers.ILLUSIONER)), 0.5F);
        this.addLayer(new ItemInHandLayer<IllusionerUnit, IllagerModel<IllusionerUnit>>(this, p_174108_.getItemInHandRenderer()) {
            @Override
            public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, IllusionerUnit illusionerUnit,
                               float pLimbSwing, float pLimbSwingAmount, float pPartialTicks,
                               float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
                // Keep the bow hidden unless the unit is actively drawing/using it.
                if (illusionerUnit.isUsingItem() || illusionerUnit.hasLivingTarget()) {
                    super.render(pPoseStack, pBuffer, pPackedLight, illusionerUnit,
                            pLimbSwing, pLimbSwingAmount, pPartialTicks, pAgeInTicks, pNetHeadYaw, pHeadPitch);
                }
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(IllusionerUnit pEntity) {
        return ILLUSIONER_ILLAGER;
    }

    @Override
    protected void scale(IllusionerUnit pEntity, PoseStack pPoseStack, float pPartialTickTime) {
        float scale = 0.9375F;
        pPoseStack.scale(scale, scale, scale);
    }

    @Override
    public void render(IllusionerUnit pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        // Ensure headwear (second texture layer) is always rendered.
        this.getModel().getHat().visible = true;
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}
