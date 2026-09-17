package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.WardenMine242.siege_of_settlements.unit.units.villagers.EliteTraderUnit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.solegendary.siege_of_settlements.unit.modelling.models.EliteTraderGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class EliteTraderGeoRenderer extends GeoEntityRenderer<EliteTraderUnit> {

    public EliteTraderGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EliteTraderGeoModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(@NotNull EliteTraderUnit entity, float entityYaw, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
