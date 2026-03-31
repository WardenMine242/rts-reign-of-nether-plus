package com.solegendary.siege_of_settlements.registrars;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.guiscreen.TopdownGuiContainer;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ContainerRegistrar {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
            SiegeOfSettlements.MOD_ID);

    public static final RegistryObject<MenuType<TopdownGuiContainer>> TOPDOWNGUI_CONTAINER = CONTAINERS
            .register("topdowngui_container", () -> new MenuType<>(TopdownGuiContainer::new, FeatureFlagSet.of()));

    public static void init(FMLJavaModLoadingContext context) {
        CONTAINERS.register(context.getModEventBus());
    }
}
