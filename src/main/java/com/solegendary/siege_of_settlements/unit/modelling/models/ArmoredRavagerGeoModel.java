package com.solegendary.siege_of_settlements.unit.modelling.models;

import com.WardenMine242.siege_of_settlements.unit.units.villagers.ArmoredRavagerUnit;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ArmoredRavagerGeoModel extends GeoModel<ArmoredRavagerUnit> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "geo/armored_ravager.geo.json"
    );
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "textures/entities/armored_ravager.png"
    );
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "animations/armored_ravager.animation.json"
    );

    @Override
    public ResourceLocation getModelResource(ArmoredRavagerUnit animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ArmoredRavagerUnit animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ArmoredRavagerUnit animatable) {
        return ANIMATION;
    }
}
