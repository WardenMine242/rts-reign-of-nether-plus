package com.solegendary.siege_of_settlements.building;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.buildings.monsters.*;
import com.solegendary.siege_of_settlements.building.buildings.neutral.*;
import com.solegendary.siege_of_settlements.building.buildings.piglins.*;
import com.solegendary.siege_of_settlements.building.buildings.villagers.*;
import net.WardenMine242.siege_of_settlements.building.buildings.villagers.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class Buildings {
    public static final Mausoleum MAUSOLEUM = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "mausoleum"), new Mausoleum());
    public static final SpruceStockpile SPRUCE_STOCKPILE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "spruce_stockpile"), new SpruceStockpile());
    public static final HauntedHouse HAUNTED_HOUSE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "haunted_house"), new HauntedHouse());
    public static final PumpkinFarm PUMPKIN_FARM = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "pumpkin_farm"), new PumpkinFarm());
    public static final DarkWatchtower DARK_WATCHTOWER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "dark_watchtower"), new DarkWatchtower());
    public static final Graveyard GRAVEYARD = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "graveyard"), new Graveyard());
    public static final Dungeon DUNGEON = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "dungeon"), new Dungeon());
    public static final SpiderLair SPIDER_LAIR = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "spider_lair"), new SpiderLair());
    public static final SlimePit SLIME_PIT = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "slime_pit"), new SlimePit());
    public static final Laboratory LABORATORY = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "laboratory"), new Laboratory());
    public static final Stronghold STRONGHOLD = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "stronghold"), new Stronghold());
    public static final SpruceBridge SPRUCE_BRIDGE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "spruce_bridge"), new SpruceBridge());
    public static final SculkCatalyst SCULK_CATALYST = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "sculk_catalyst"), new SculkCatalyst());
    public static final CentralPortal CENTRAL_PORTAL = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "central_portal"), new CentralPortal());
    public static final PortalBasic PORTAL_BASIC = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "portal_basic"), new PortalBasic());
    public static final PortalCivilian PORTAL_CIVILIAN = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "portal_civilian"), new PortalCivilian());
    public static final PortalMilitary PORTAL_MILITARY = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "portal_military"), new PortalMilitary());
    public static final PortalTransport PORTAL_TRANSPORT = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "portal_transport"), new PortalTransport());
    public static final NetherwartFarm NETHERWART_FARM = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "netherwart_farm"), new NetherwartFarm());
    public static final Bastion BASTION = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "bastion"), new Bastion());
    public static final HoglinStables HOGLIN_STABLES = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "hoglin_stables"), new HoglinStables());
    public static final FlameSanctuary FLAME_SANCTUARY = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "flame_sanctuary"), new FlameSanctuary());
    public static final WitherShrine WITHER_SHRINE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "wither_shrine"), new WitherShrine());
    public static final BasaltSprings BASALT_SPRINGS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "basalt_springs"), new BasaltSprings());
    public static final Fortress FORTRESS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "fortress"), new Fortress());
    public static final BlackstoneBridge BLACKSTONE_BRIDGE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "blackstone_bridge"), new BlackstoneBridge());
    public static final TownCentre TOWN_CENTRE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "town_centre"), new TownCentre());
    public static final OakStockpile OAK_STOCKPILE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "oak_stockpile"), new OakStockpile());
    public static final VillagerHouse VILLAGER_HOUSE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "villager_house"), new VillagerHouse());
    public static final WheatFarm WHEAT_FARM = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "wheat_farm"), new WheatFarm());
    public static final Watchtower WATCHTOWER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "watchtower"), new Watchtower());
    public static final Barracks BARRACKS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "barracks"), new Barracks());
    public static final Blacksmith BLACKSMITH = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "blacksmith"), new Blacksmith());
    public static final ArcaneTower ARCANE_TOWER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "arcane_tower"), new ArcaneTower());
    public static final Library LIBRARY = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "library"), new Library());
    public static final Castle CASTLE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "castle"), new Castle());
    public static final IronGolemBuilding IRON_GOLEM_BUILDING = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "iron_golem_building"), new IronGolemBuilding());
    public static final OakBridge OAK_BRIDGE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "oak_bridge"), new OakBridge());
    public static final Beacon BEACON = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "beacon"), new Beacon());
    public static final CapturableBeacon CAPTURABLE_BEACON = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "capturable_beacon"), new CapturableBeacon());
    public static final EndPortal END_PORTAL = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "end_portal"), new EndPortal());
    public static final HealingFountain HEALING_FOUNTAIN = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "healing_fountain"), new HealingFountain());
    public static final NeutralTransportPortal NEUTRAL_TRANSPORT_PORTAL = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "neutral_transport_portal"), new NeutralTransportPortal());
    public static final AltarOfDarkness ALTAR_OF_DARKNESS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "altar_of_darkness"), new AltarOfDarkness());
    public static final ShrineOfProsperity SHRINE_OF_PROSPERITY = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "shrine_of_prosperity"), new ShrineOfProsperity());
    public static final InfernalPortal INFERNAL_PORTAL = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "infernal_portal"), new InfernalPortal());
    public static final VillagerDrill VILLAGER_DRILL = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "villager_drill"), new VillagerDrill());
    public static final VillagerWall VILLAGER_WALL = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "villager_wall"), new VillagerWall());
    public static final VillagerGate VILLAGER_GATE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "villager_gate_closed"), new VillagerGate());
    public static final VillagerCornerWall VILLAGER_CORNER_WALL = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "villager_corner_wall"), new VillagerCornerWall());
    public static final ResearchCentre RESEARCH_CENTRE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_centre"), new ResearchCentre());
    public static final Marketplace MARKETPLACE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "marketplace"), new Marketplace());

    private static <T extends Building> T register(ResourceLocation id, T building) {
        return Registry.register(SiegeOfSettlementsRegistries.BUILDING, id, building);
    }

    public static void init() {}
}
