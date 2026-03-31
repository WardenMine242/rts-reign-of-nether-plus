package com.solegendary.siege_of_settlements.registrars;

import com.solegendary.siege_of_settlements.alliance.AlliancesServerEvents;
import com.solegendary.siege_of_settlements.attackwarnings.AttackWarningServerEvents;
import com.solegendary.siege_of_settlements.blocks.BlockServerEvents;
import com.solegendary.siege_of_settlements.building.BuildingServerEvents;
import com.solegendary.siege_of_settlements.building.custombuilding.CustomBuildingServerEvents;
import com.solegendary.siege_of_settlements.commands.CommandsServerEvents;
import com.solegendary.siege_of_settlements.config.ConfigVanillaServerEvents;
import com.solegendary.siege_of_settlements.fogofwar.FogOfWarServerEvents;
import com.solegendary.siege_of_settlements.gamemode.GameModeServerEvents;
import com.solegendary.siege_of_settlements.gamerules.GameruleServerEvents;
import com.solegendary.siege_of_settlements.hero.HeroServerEvents;
import com.solegendary.siege_of_settlements.player.PlayerServerEvents;
import com.solegendary.siege_of_settlements.research.ResearchServerEvents;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import com.solegendary.siege_of_settlements.startpos.StartPosServerEvents;
import com.solegendary.siege_of_settlements.survival.SurvivalServerEvents;
import com.solegendary.siege_of_settlements.time.TimeServerEvents;
import com.solegendary.siege_of_settlements.tps.TPSServerEvents;
import com.solegendary.siege_of_settlements.unit.NonUnitServerEvents;
import com.solegendary.siege_of_settlements.unit.UnitServerEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

/*
    This class is required to make sure that we don't accidentally try to load any client-side-only classes
      on a dedicated server.
    It is a rather convoluted way of doing it, but I haven't found a simpler way to do it which is robust
 */

public class ServerEventRegistrar {
    private final IEventBus vanillaEventBus = MinecraftForge.EVENT_BUS;

    public ServerEventRegistrar() { }

    /**
     * Register server only events. This method must only be called when it is certain that the mod is
     * is executing code on the server side and not the client.
     */
    public void registerServerEvents() {
        vanillaEventBus.register(GameruleServerEvents.class);
        vanillaEventBus.register(BlockServerEvents.class);
        vanillaEventBus.register(PlayerServerEvents.class);
        vanillaEventBus.register(ConfigVanillaServerEvents.class);
        vanillaEventBus.register(UnitServerEvents.class);
        vanillaEventBus.register(BuildingServerEvents.class);
        vanillaEventBus.register(AttackWarningServerEvents.class);
        vanillaEventBus.register(ResourcesServerEvents.class);
        vanillaEventBus.register(TPSServerEvents.class);
        vanillaEventBus.register(FogOfWarServerEvents.class);
        vanillaEventBus.register(ResearchServerEvents.class);
        vanillaEventBus.register(SurvivalServerEvents.class);
        vanillaEventBus.register(GameModeServerEvents.class);
        vanillaEventBus.register(StartPosServerEvents.class);
        vanillaEventBus.register(AlliancesServerEvents.class);
        vanillaEventBus.register(HeroServerEvents.class);
        vanillaEventBus.register(NonUnitServerEvents.class);
        vanillaEventBus.register(TimeServerEvents.class);
        vanillaEventBus.register(CustomBuildingServerEvents.class);
        vanillaEventBus.register(CommandsServerEvents.class);
    }
}
