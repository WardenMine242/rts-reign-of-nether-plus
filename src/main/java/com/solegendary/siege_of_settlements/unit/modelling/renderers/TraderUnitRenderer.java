package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.solegendary.siege_of_settlements.unit.modelling.models.VillagerUnitModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TraderUnitRenderer extends AbstractVillagerUnitRenderer<AbstractIllager> {
    private static final ResourceLocation TRADER_UNIT =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/wandering_trader.png");

    public TraderUnitRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerUnitModel<>(context.bakeLayer(VillagerUnitModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractIllager entity) {
        return TRADER_UNIT;
    }
}
