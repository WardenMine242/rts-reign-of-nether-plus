package com.solegendary.siege_of_settlements.unit.modelling.models;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.unit.units.villagers.DragonUnit_TEST;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DragonUnitGeoModel extends GeoModel<DragonUnit_TEST> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "geo/dragon_unit.geo.json"
    );
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "textures/entities/dragon_unit.png"
    );
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "animations/dragon_unit.animation.json"
    );

    @Override
    public ResourceLocation getModelResource(DragonUnit_TEST animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DragonUnit_TEST animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(DragonUnit_TEST animatable) {
        return ANIMATION;
    }
}
