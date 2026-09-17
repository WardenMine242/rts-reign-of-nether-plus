package com.solegendary.siege_of_settlements.unit.modelling.models;

import com.WardenMine242.siege_of_settlements.unit.units.villagers.EliteArmoredVindicatorUnit;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EliteArmoredVindicatorGeoModel extends GeoModel<EliteArmoredVindicatorUnit> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "geo/armored_vindicator.geo.json"
    );
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "textures/entities/elite_armored_vindicator.png"
    );
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "animations/armored_vindicator.animation.json"
    );

    @Override
    public ResourceLocation getModelResource(EliteArmoredVindicatorUnit animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EliteArmoredVindicatorUnit animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EliteArmoredVindicatorUnit animatable) {
        return ANIMATION;
    }
}
