package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.WardenMine242.siege_of_settlements.unit.units.villagers.ArmoredVindicatorUnit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.unit.modelling.models.ArmoredVindicatorModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ArmoredVindicatorRenderer extends AbstractVillagerUnitRenderer<ArmoredVindicatorUnit> {
    private static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/entities/armored_vindicator.png");

    public ArmoredVindicatorRenderer(EntityRendererProvider.Context context) {
        super(context, new ArmoredVindicatorModel<>(context.bakeLayer(ArmoredVindicatorModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ArmoredVindicatorUnit entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    protected void scale(@NotNull ArmoredVindicatorUnit entity, @NotNull PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
