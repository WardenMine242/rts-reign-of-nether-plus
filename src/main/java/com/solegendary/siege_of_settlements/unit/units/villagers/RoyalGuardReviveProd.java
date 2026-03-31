package com.solegendary.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.production.ReviveHeroProductionItem;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.unit.interfaces.HeroUnit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class RoyalGuardReviveProd extends ReviveHeroProductionItem {

    public RoyalGuardReviveProd() {
        super(
            "Revive Royal Guard",
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/royal_guard.png"),
            "units.villagers.siege_of_settlements.royal_guard.revive"
        );
    }

    @Override
    protected EntityType<? extends HeroUnit> getHeroEntityType() {
        return EntityRegistrar.ROYAL_GUARD_UNIT.get();
    }
}
