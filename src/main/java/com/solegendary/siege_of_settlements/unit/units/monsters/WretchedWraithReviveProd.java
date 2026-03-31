package com.solegendary.siege_of_settlements.unit.units.monsters;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.production.ReviveHeroProductionItem;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.unit.interfaces.HeroUnit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class WretchedWraithReviveProd extends ReviveHeroProductionItem {

    public WretchedWraithReviveProd() {
        super(
            "Revive Wretched Wraith",
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/wretched_wraith.png"),
            "units.monsters.siege_of_settlements.wretched_wraith.revive"
        );
    }

    @Override
    protected EntityType<? extends HeroUnit> getHeroEntityType() {
        return EntityRegistrar.WRETCHED_WRAITH_UNIT.get();
    }
}
