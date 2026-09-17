package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.WardenMine242.siege_of_settlements.unit.units.villagers.EliteArmoredVindicatorUnit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.unit.goals.MeleeAttackBuildingGoal;
import com.solegendary.siege_of_settlements.unit.modelling.models.EliteArmoredVindicatorModel;
import com.solegendary.siege_of_settlements.unit.modelling.models.VillagerUnitModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class EliteArmoredVindicatorRenderer extends AbstractVillagerUnitRenderer<EliteArmoredVindicatorUnit> {
    private static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/entities/elite_armored_vindicator.png");

    public EliteArmoredVindicatorRenderer(EntityRendererProvider.Context context) {
        super(context, new EliteArmoredVindicatorModel<>(context.bakeLayer(EliteArmoredVindicatorModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new ItemInHandLayer<EliteArmoredVindicatorUnit, VillagerUnitModel<EliteArmoredVindicatorUnit>>(this, context.getItemInHandRenderer()) {
            public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, EliteArmoredVindicatorUnit unit, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
                if (unit.getTarget() != null || (unit.getAttackBuildingGoal() instanceof MeleeAttackBuildingGoal mabg && mabg.getBuildingTarget() != null)) {
                    super.render(poseStack, bufferSource, packedLight, unit, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
                }
            }
        });
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EliteArmoredVindicatorUnit entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    protected void scale(@NotNull EliteArmoredVindicatorUnit entity, @NotNull PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
