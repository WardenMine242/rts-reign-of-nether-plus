package com.solegendary.siege_of_settlements.unit.modelling.models;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.EliteTraderUnit;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EliteTraderGeoModel extends GeoModel<EliteTraderUnit> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "geo/elite_trader.geo.json"
    );
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "textures/entities/elite_trader.png"
    );
    // Reuse villager animation or common villager animations if available, 
    // but the user mentioned "same as ordinary trader". Ordinary trader is not GeckoLib.
    // However, the provided .geo.json likely expects specific animations.
    // If elite_trader.animation.json doesn't exist, we might need one or the .geo.json might have them.
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "animations/elite_trader.animation.json"
    );

    @Override
    public ResourceLocation getModelResource(EliteTraderUnit animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EliteTraderUnit animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EliteTraderUnit animatable) {
        return ANIMATION;
    }
}
