package com.solegendary.siege_of_settlements.unit.units.piglins;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.production.ReviveHeroProductionItem;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.unit.interfaces.HeroUnit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class PiglinMerchantReviveProd extends ReviveHeroProductionItem {

    public PiglinMerchantReviveProd() {
        super(
            "Revive Piglin Merchant",
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/piglin_merchant.png"),
            "units.piglins.siege_of_settlements.piglin_merchant.revive"
        );
    }

    @Override
    protected EntityType<? extends HeroUnit> getHeroEntityType() {
        return EntityRegistrar.PIGLIN_MERCHANT_UNIT.get();
    }
}
