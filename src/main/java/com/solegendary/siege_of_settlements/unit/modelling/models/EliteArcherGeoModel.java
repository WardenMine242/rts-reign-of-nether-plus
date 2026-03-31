package com.solegendary.siege_of_settlements.unit.modelling.models;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import net.WardenMine242.siege_of_settlements.unit.units.villagers.EliteArcherUnit;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EliteArcherGeoModel extends GeoModel<EliteArcherUnit> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "geo/elite_archer.geo.json"
    );
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "textures/entities/elite_archer_unit.png"
    );
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "animations/elite_archer.animation.json"
    );

    @Override
    public ResourceLocation getModelResource(EliteArcherUnit animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EliteArcherUnit animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EliteArcherUnit animatable) {
        return ANIMATION;
    }
}

