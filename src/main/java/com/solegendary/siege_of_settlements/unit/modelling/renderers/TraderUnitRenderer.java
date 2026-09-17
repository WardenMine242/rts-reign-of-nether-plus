package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.WardenMine242.siege_of_settlements.unit.units.villagers.TraderUnit;
import com.solegendary.siege_of_settlements.unit.modelling.models.VillagerUnitModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TraderUnitRenderer extends AbstractVillagerUnitRenderer<TraderUnit> {
    private static final ResourceLocation TRADER_UNIT =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/wandering_trader.png");

    public TraderUnitRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerUnitModel<>(context.bakeLayer(VillagerUnitModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TraderUnit entity) {
        return TRADER_UNIT;
    }
}
