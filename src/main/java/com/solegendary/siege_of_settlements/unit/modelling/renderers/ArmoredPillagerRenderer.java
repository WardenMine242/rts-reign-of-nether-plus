package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.WardenMine242.siege_of_settlements.unit.units.villagers.ArmoredPillagerUnit;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.unit.modelling.models.ArmoredPillagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ArmoredPillagerRenderer extends AbstractVillagerUnitRenderer<ArmoredPillagerUnit> {
    private static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/entities/armored_pillager.png");

    public ArmoredPillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new ArmoredPillagerModel<>(context.bakeLayer(ArmoredPillagerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ArmoredPillagerUnit entity) {
        return TEXTURE_LOCATION;
    }
}
