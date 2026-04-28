package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.unit.modelling.models.EliteArcherModel;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.EliteArcherUnit;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class EliteArcherRenderer extends MobRenderer<EliteArcherUnit, EliteArcherModel<EliteArcherUnit>> {

    private static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/entities/elite_archer_unit.png");

    public EliteArcherRenderer(EntityRendererProvider.Context context) {
        super(context, new EliteArcherModel<>(context.bakeLayer(EliteArcherModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EliteArcherUnit entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    protected void scale(@NotNull EliteArcherUnit entity, @NotNull PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}

