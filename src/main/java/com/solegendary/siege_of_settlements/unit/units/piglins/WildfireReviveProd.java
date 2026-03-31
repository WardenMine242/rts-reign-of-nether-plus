package com.solegendary.siege_of_settlements.unit.units.piglins;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.production.ReviveHeroProductionItem;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.unit.interfaces.HeroUnit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class WildfireReviveProd extends ReviveHeroProductionItem {

    public WildfireReviveProd() {
        super(
            "Revive Wildfire",
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/wildfire.png"),
            "units.piglins.siege_of_settlements.wildfire.revive"
        );
    }

    @Override
    protected EntityType<? extends HeroUnit> getHeroEntityType() {
        return EntityRegistrar.WILDFIRE_UNIT.get();
    }
}
