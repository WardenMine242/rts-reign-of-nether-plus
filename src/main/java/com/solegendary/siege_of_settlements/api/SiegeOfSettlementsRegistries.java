package com.solegendary.siege_of_settlements.api;

import com.mojang.serialization.Lifecycle;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class SiegeOfSettlementsRegistries {
    public static final MappedRegistry<Building> BUILDING = createMappedRegistry(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "building"));
    public static final MappedRegistry<ProductionItem> PRODUCTION_ITEM = createMappedRegistry(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "production_item"));

    private static <T> MappedRegistry<T> createMappedRegistry(ResourceLocation id) {
        ResourceKey<Registry<T>> key = ResourceKey.createRegistryKey(id);
        return new MappedRegistry<T>(key, Lifecycle.experimental(), false);
    }
}
