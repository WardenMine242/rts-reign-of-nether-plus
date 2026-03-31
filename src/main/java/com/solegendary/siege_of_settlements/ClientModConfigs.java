package com.solegendary.siege_of_settlements;

import com.solegendary.siege_of_settlements.config.SiegeOfSettlementsClientConfigs;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientModConfigs {

    public static void registerClientConfigs(FMLJavaModLoadingContext mlctx) {
        mlctx.registerConfig(ModConfig.Type.CLIENT,
                SiegeOfSettlementsClientConfigs.SPEC,
                "siege_of_settlements-client-" + SiegeOfSettlements.VERSION_STRING + ".toml");

        mlctx.registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                SiegeOfSettlementsClientConfigs::createConfigScreen
        );
    }
}

