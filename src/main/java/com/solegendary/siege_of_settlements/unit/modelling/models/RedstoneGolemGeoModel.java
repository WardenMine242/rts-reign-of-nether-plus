package com.solegendary.siege_of_settlements.unit.modelling.models;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.RedstoneGolemUnit;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RedstoneGolemGeoModel extends GeoModel<RedstoneGolemUnit> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "geo/redstone_golem.geo.json"
    );
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "textures/entities/redstone_golem.png"
    );
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "animations/redstone_golem.animation.json"
    );

    @Override
    public ResourceLocation getModelResource(RedstoneGolemUnit animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RedstoneGolemUnit animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RedstoneGolemUnit animatable) {
        return ANIMATION;
    }
}
