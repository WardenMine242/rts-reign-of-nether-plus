package com.solegendary.siege_of_settlements.unit.modelling.renderers;

import com.solegendary.siege_of_settlements.unit.modelling.models.RedstoneGolemGeoModel;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.RedstoneGolemUnit;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class RedstoneGolemGeoRenderer extends GeoEntityRenderer<RedstoneGolemUnit> {

    public RedstoneGolemGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RedstoneGolemGeoModel());
        this.shadowRadius = 0.7F;
    }
}
