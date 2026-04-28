package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.unit.modelling.models.EliteVindicatorModel;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.EliteVindicatorUnit;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class EliteVindicatorRenderer extends MobRenderer<EliteVindicatorUnit, EliteVindicatorModel<EliteVindicatorUnit>> {
    private static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/entities/elite_vindicator_unit.png");

    public EliteVindicatorRenderer(EntityRendererProvider.Context context) {
        super(context, new EliteVindicatorModel<>(context.bakeLayer(EliteVindicatorModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EliteVindicatorUnit entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    protected void scale(@NotNull EliteVindicatorUnit entity, @NotNull PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
