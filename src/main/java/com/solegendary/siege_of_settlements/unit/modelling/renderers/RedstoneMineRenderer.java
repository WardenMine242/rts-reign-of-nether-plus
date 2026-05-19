package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.RedstoneMineUnit;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.unit.modelling.models.RedstoneMineModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RedstoneMineRenderer extends MobRenderer<RedstoneMineUnit, RedstoneMineModel<RedstoneMineUnit>> {

    public static final float SCALE_MULT = 0.5f;
    private static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/entities/redstone_mine.png");

    public RedstoneMineRenderer(EntityRendererProvider.Context context) {
        super(context, new RedstoneMineModel<>(context.bakeLayer(RedstoneMineModel.LAYER_LOCATION)), 0.25f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RedstoneMineUnit redstoneMineUnit) {
        return TEXTURE_LOCATION;
    }

    @Override
    protected void scale(@NotNull RedstoneMineUnit redstoneMineUnit, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(SCALE_MULT, SCALE_MULT, SCALE_MULT);
    }
}
