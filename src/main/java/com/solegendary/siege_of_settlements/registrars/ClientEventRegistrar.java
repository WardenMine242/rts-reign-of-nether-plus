package com.solegendary.siege_of_settlements.registrars;

import com.solegendary.siege_of_settlements.alliance.AlliancesServerEvents;
import com.solegendary.siege_of_settlements.attackwarnings.AttackWarningClientEvents;
import com.solegendary.siege_of_settlements.attackwarnings.AttackWarningServerEvents;
import com.solegendary.siege_of_settlements.blocks.BlockServerEvents;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingServerEvents;
import com.solegendary.siege_of_settlements.building.custombuilding.CustomBuildingClientEvents;
import com.solegendary.siege_of_settlements.building.custombuilding.CustomBuildingServerEvents;
import com.solegendary.siege_of_settlements.commands.CommandsServerEvents;
import com.solegendary.siege_of_settlements.config.ConfigClientEvents;
import com.solegendary.siege_of_settlements.config.ConfigVanillaServerEvents;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.fogofwar.FogOfWarClientEvents;
import com.solegendary.siege_of_settlements.fogofwar.FogOfWarServerEvents;
import com.solegendary.siege_of_settlements.gamemode.GameModeServerEvents;
import com.solegendary.siege_of_settlements.gamerules.GameruleServerEvents;
import com.solegendary.siege_of_settlements.guiscreen.TopdownGuiClientEvents;
import com.solegendary.siege_of_settlements.healthbars.HealthBarClientEvents;
import com.solegendary.siege_of_settlements.hero.HeroClientEvents;
import com.solegendary.siege_of_settlements.hero.HeroServerEvents;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.hud.playerdisplay.PlayerDisplayClientEvents;
import com.solegendary.siege_of_settlements.hud.TitleClientEvents;
import com.solegendary.siege_of_settlements.minimap.MinimapClientEvents;
import com.solegendary.siege_of_settlements.orthoview.OrthoviewClientEvents;
import com.solegendary.siege_of_settlements.player.PlayerClientEvents;
import com.solegendary.siege_of_settlements.player.PlayerServerEvents;
import com.solegendary.siege_of_settlements.research.ResearchServerEvents;
import com.solegendary.siege_of_settlements.resources.ResourcesClientEvents;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import com.solegendary.siege_of_settlements.sandbox.SandboxClientEvents;
import com.solegendary.siege_of_settlements.sounds.SoundClientEvents;
import com.solegendary.siege_of_settlements.startpos.StartPosClientEvents;
import com.solegendary.siege_of_settlements.startpos.StartPosServerEvents;
import com.solegendary.siege_of_settlements.survival.SurvivalClientEvents;
import com.solegendary.siege_of_settlements.survival.SurvivalServerEvents;
import com.solegendary.siege_of_settlements.time.TimeClientEvents;
import com.solegendary.siege_of_settlements.time.TimeServerEvents;
import com.solegendary.siege_of_settlements.tps.TPSClientEvents;
import com.solegendary.siege_of_settlements.tps.TPSServerEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialServerEvents;
import com.solegendary.siege_of_settlements.unit.NonUnitClientEvents;
import com.solegendary.siege_of_settlements.unit.NonUnitServerEvents;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.UnitServerEvents;
import com.solegendary.siege_of_settlements.worldborder.WorldBorderClientEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientEventRegistrar {
    private final IEventBus vanillaEventBus = MinecraftForge.EVENT_BUS;

    public ClientEventRegistrar() { }

    /**
     * Register client only events. This method must only be called when it is certain that the mod is
     * is executing code on the client side and not the dedicated server.
     */

    public void registerClientEvents() {
        vanillaEventBus.register(OrthoviewClientEvents.class);
        vanillaEventBus.register(TopdownGuiClientEvents.class);
        vanillaEventBus.register(ConfigClientEvents.class);
        vanillaEventBus.register(BuildingClientEvents.class); // being first fixes a bug with drawBuildingToPlace()
        vanillaEventBus.register(UnitClientEvents.class);
        vanillaEventBus.register(HealthBarClientEvents.class);
        vanillaEventBus.register(SandboxClientEvents.class);
        vanillaEventBus.register(HudClientEvents.class); // ensure this is first so cursor is rendered above hud
        vanillaEventBus.register(AttackWarningClientEvents.class);
        vanillaEventBus.register(CursorClientEvents.class);
        vanillaEventBus.register(MinimapClientEvents.class);
        vanillaEventBus.register(TimeClientEvents.class);
        vanillaEventBus.register(FogOfWarClientEvents.class);
        vanillaEventBus.register(ResourcesClientEvents.class);
        vanillaEventBus.register(TPSClientEvents.class);
        vanillaEventBus.register(PlayerClientEvents.class);
        vanillaEventBus.register(TutorialClientEvents.class);
        vanillaEventBus.register(TitleClientEvents.class);
        vanillaEventBus.register(WorldBorderClientEvents.class);
        vanillaEventBus.register(SurvivalClientEvents.class);
        vanillaEventBus.register(StartPosClientEvents.class);
        vanillaEventBus.register(NonUnitClientEvents.class);
        vanillaEventBus.register(HeroClientEvents.class);
        vanillaEventBus.register(CustomBuildingClientEvents.class);
        vanillaEventBus.register(SoundClientEvents.class);
        vanillaEventBus.register(PlayerDisplayClientEvents.class);

        // to allow singleplayer integrated server to work
        vanillaEventBus.register(GameruleServerEvents.class);
        vanillaEventBus.register(BlockServerEvents.class);
        vanillaEventBus.register(TutorialServerEvents.class);
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
