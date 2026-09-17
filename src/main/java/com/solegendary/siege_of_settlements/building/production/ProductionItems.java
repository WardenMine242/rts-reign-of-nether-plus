package com.solegendary.siege_of_settlements.building.production;

import com.WardenMine242.siege_of_settlements.building.buildings.villagers.VillagerDrill;
import com.WardenMine242.siege_of_settlements.building.buildings.placements.ArcaneTowerPlacement;
import com.WardenMine242.siege_of_settlements.upgrades.BarracksTierUpgrade;
import com.WardenMine242.siege_of_settlements.upgrades.BlacksmithTierUpgrade;
import com.WardenMine242.siege_of_settlements.upgrades.CastleT2;
import com.WardenMine242.siege_of_settlements.upgrades.DrillTierUpgrade;
import com.WardenMine242.siege_of_settlements.upgrades.DrillTierUpgrade.DrillResearchRequirement;
import com.WardenMine242.siege_of_settlements.upgrades.ArcaneTowerTierUpgrade;
import com.WardenMine242.siege_of_settlements.upgrades.VillagerHouseT2;
import com.WardenMine242.siege_of_settlements.upgrades.TownCentreT2;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.buildings.placements.BarracksPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.research.researchItems.*;
import com.solegendary.siege_of_settlements.research.researchItems.ResearchProfessions;
import com.solegendary.siege_of_settlements.unit.units.monsters.*;
import com.solegendary.siege_of_settlements.unit.units.neutral.*;
import com.solegendary.siege_of_settlements.unit.units.piglins.*;
import com.solegendary.siege_of_settlements.unit.units.villagers.*;
import com.WardenMine242.siege_of_settlements.building.production.ToggleGateProduction;
import com.WardenMine242.siege_of_settlements.research.*;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.EliteArmoredPillager;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.ArmoredPillagerProd;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.EliteArmoredVindicator;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.ArmoredVindicatorProd;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.ArcherProd;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.EliteArcherProd;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.EliteVindicatorProd;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.HorseProd;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.IllusionerProd;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.RedstoneGolemProd;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class ProductionItems {
    public static final CreeperProd CREEPER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "creeper"), new CreeperProd());
    public static final SkeletonProd SKELETON = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "skeleton"), new SkeletonProd());
    public static final ZombieProd ZOMBIE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "zombie"), new ZombieProd());
    public static final ZombieVillagerProd ZOMBIE_VILLAGER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "zombie_villager"), new ZombieVillagerProd());
    public static final StrayProd STRAY = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "stray"), new StrayProd());
    public static final HuskProd HUSK = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "husk"), new HuskProd());
    public static final DrownedProd DROWNED = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "drowned"), new DrownedProd());
    public static final SpiderProd SPIDER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "spider"), new SpiderProd());
    public static final PoisonSpiderProd POISON_SPIDER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "poison_spider"), new PoisonSpiderProd());
    public static final VillagerProd VILLAGER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "villager"), new VillagerProd());
    public static final com.WardenMine242.siege_of_settlements.unit.units.villagers.AuromancerProd AUROMANCER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "auromancer"), new com.WardenMine242.siege_of_settlements.unit.units.villagers.AuromancerProd());
    public static final EliteVindicatorProd ELITE_VINDICATOR = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "elite_vindicator"), new EliteVindicatorProd());
    public static final EliteArmoredVindicator ELITE_ARMORED_VINDICATOR = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "elite_armored_vindicator"), new EliteArmoredVindicator());
    public static final ArmoredVindicatorProd ARMORED_VINDICATOR = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "armored_vindicator"), new ArmoredVindicatorProd(false));
    public static final EliteArmoredPillager ELITE_ARMORED_PILLAGER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "elite_armored_pillager"), new EliteArmoredPillager());
    public static final EliteArcherProd ELITE_ARCHER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "elite_archer"), new EliteArcherProd());
    public static final ArcherProd ARCHER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "archer"), new ArcherProd());
    public static final HorseProd HORSE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "horse"), new HorseProd());
    public static final IllusionerProd ILLUSIONER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "illusioner"), new IllusionerProd());
    public static final ArmoredPillagerProd ARMORED_PILLAGER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "armored_pillager"), new ArmoredPillagerProd());
    public static final VindicatorProd VINDICATOR = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "vindicator"), new VindicatorProd());
    public static final PillagerProd PILLAGER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "pillager"), new PillagerProd());
    public static final IronGolemProd IRON_GOLEM = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "iron_golem"), new IronGolemProd());
    public static final RedstoneGolemProd REDSTONE_GOLEM = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "redstone_golem"), new RedstoneGolemProd());
    public static final WitchProd WITCH = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "witch"), new WitchProd());
    public static final EvokerProd EVOKER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "evoker"), new EvokerProd());
    public static final SlimeProd SLIME = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "slime"), new SlimeProd());
    public static final WardenProd WARDEN = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "warden"), new WardenProd());
    public static final RavagerProd RAVAGER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "ravager"), new RavagerProd());
    public static final DragonProd DRAGON = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "dragon"), new DragonProd());

    public static final GruntProd GRUNT = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "grunt"), new GruntProd());
    public static final BruteProd BRUTE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "brute"), new BruteProd());
    public static final HeadhunterProd HEADHUNTER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "headhunter"), new HeadhunterProd());
    public static final MarauderProd MARAUDER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "marauder"), new MarauderProd());
    public static final HoglinProd HOGLIN = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "hoglin"), new HoglinProd());
    public static final BlazeProd BLAZE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "blaze"), new BlazeProd());
    public static final WitherSkeletonProd WITHER_SKELETON = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "wither_skeleton"), new WitherSkeletonProd());
    public static final MagmaCubeProd MAGMA_CUBE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "magma_cube"), new MagmaCubeProd());
    public static final GhastProd GHAST = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "ghast"), new GhastProd());
    public static final RoyalGuardProd ROYAL_GUARD = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "royal_guard"), new RoyalGuardProd());
    public static final EnchanterProd ENCHANTER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "enchanter"), new EnchanterProd());
    public static final EnchanterReviveProd ENCHANTER_REVIVE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "enchanter_revive"), new EnchanterReviveProd());
    public static final NecromancerProd NECROMANCER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "necromancer"), new NecromancerProd());
    public static final NecromancerReviveProd NECROMANCER_REVIVE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "necromancer_revive"), new NecromancerReviveProd());
    public static final WretchedWraithProd WRETCHED_WRAITH = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "wretched_wraith"), new WretchedWraithProd());
    public static final WretchedWraithReviveProd WRETCHED_WRAITH_REVIVE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "wretched_wraith_revive"), new WretchedWraithReviveProd());
    public static final PiglinMerchantProd PIGLIN_MERCHANT = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "piglin_merchant"), new PiglinMerchantProd());
    public static final PiglinMerchantReviveProd PIGLIN_MERCHANT_REVIVE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "piglin_merchant_revive"), new PiglinMerchantReviveProd());
    public static final WildfireProd WILDFIRE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "wildfire"), new WildfireProd());
    public static final WildfireReviveProd WILDFIRE_REVIVE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "wildfire_revive"), new WildfireReviveProd());
    public static final ZombiePiglinProd ZOMBIE_PIGLIN = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "zombie_piglin"), new ZombiePiglinProd());
    public static final ZoglinProd ZOGLIN = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "zoglin"), new ZoglinProd());
    public static final EndermanProd ENDERMAN = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "enderman"), new EndermanProd());
    public static final PolarBearProd POLAR_BEAR = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "polar_bear"), new PolarBearProd());
    public static final GrizzlyBearProd GRIZZLY_BEAR = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "grizzly_bear"), new GrizzlyBearProd());
    public static final PandaProd PANDA = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "panda"), new PandaProd());
    public static final WolfProd WOLF = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "wolf"), new WolfProd());
    public static final LlamaProd LLAMA = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "llama"), new LlamaProd());

    public static final ResearchVindicatorAxes RESEARCH_VINDICATOR_AXES = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "vindicator_axes"), new ResearchVindicatorAxes());
    public static final ResearchPillagerCrossbows RESEARCH_PILLAGER_CROSSBOWS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "pillager_crossbows"), new ResearchPillagerCrossbows());
    public static final ResearchMilitiaBows RESEARCH_MILITIA_BOWS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "militia_bows"), new ResearchMilitiaBows());
    public static final ResearchLabLightningRod RESEARCH_LAB_LIGHTNING_ROD = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "lab_lightning_rod"), new ResearchLabLightningRod());
    public static final ResearchResourceCapacity RESEARCH_RESOURCE_CAPACITY = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "resource_capacity"), new ResearchResourceCapacity());
    public static final ResearchSpiderJockeys RESEARCH_SPIDER_JOCKEYS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "spider_jockeys"), new ResearchSpiderJockeys());
    public static final ResearchPoisonSpiders RESEARCH_POISON_SPIDERS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_poison_spiders"), new ResearchPoisonSpiders());
    public static final ResearchHusks RESEARCH_HUSKS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_husks"), new ResearchHusks());
    public static final ResearchDrowned RESEARCH_DROWNED = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_drowned"), new ResearchDrowned());
    public static final ResearchStrays RESEARCH_STRAYS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_strays"), new ResearchStrays());
    public static final ResearchSlimeConversion RESEARCH_SLIME_CONVERSION = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "slime_conversion"), new ResearchSlimeConversion());
    public static final ResearchLingeringPotions RESEARCH_LINGERING_POTIONS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "lingering_potions"), new ResearchLingeringPotions());
    public static final ResearchWaterPotions RESEARCH_WATER_POTIONS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "water_potions"), new ResearchWaterPotions());
    public static final ResearchHealingPotions RESEARCH_HEALING_POTIONS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "healing_potions"), new ResearchHealingPotions());
    public static final ResearchEvokerVexes RESEARCH_EVOKER_VEXES = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "evoker_vexes"), new ResearchEvokerVexes());
    public static final ResearchGolemSmithing RESEARCH_GOLEM_SMITHING = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "golem_smithing"), new ResearchGolemSmithing());
    public static final ResearchSilverfish RESEARCH_SILVERFISH = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "silverfish"), new ResearchSilverfish());
    public static final ResearchSculkAmplifiers RESEARCH_SCULK_AMPLIFIERS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "sculk_amplifiers"), new ResearchSculkAmplifiers());
    public static final CastleT2 RESEARCH_CASTLE_FLAG = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "castle_flag"), new CastleT2());
    public static final ResearchRavagerCavalry RESEARCH_RAVAGER_CAVALRY = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "ravager_cavalry"), new ResearchRavagerCavalry());
    public static final ResearchBruteShields RESEARCH_BRUTE_SHIELDS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "brute_shields"), new ResearchBruteShields());
    public static final ResearchHoglinCavalry RESEARCH_HOGLIN_CAVALRY = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "hoglin_cavalry"), new ResearchHoglinCavalry());
    public static final ResearchHeavyTridents RESEARCH_HEAVY_TRIDENTS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "heavy_tridents"), new ResearchHeavyTridents());
    public static final ResearchCleavingFlails RESEARCH_CLEAVING_FLAILS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "cleaving_flails"), new ResearchCleavingFlails());
    public static final ResearchBlazeFirewall RESEARCH_BLAZE_FIREWALL = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "blaze_firewall"), new ResearchBlazeFirewall());
    public static final ResearchWitherClouds RESEARCH_WITHER_CLOUDS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "wither_clouds"), new ResearchWitherClouds());
    public static final ResearchAdvancedPortals RESEARCH_ADVANCED_PORTALS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "advanced_portals"), new ResearchAdvancedPortals());
    public static final ResearchFireResistance RESEARCH_FIRE_RESISTANCE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "fire_resistance"), new ResearchFireResistance());
    public static final ResearchGrandLibrary RESEARCH_GRAND_LIBRARY = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "grand_library"), new ResearchGrandLibrary());
    public static final VillagerHouseT2 RESEARCH_VILLAGER_HOUSE_T2 = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "villager_house_t2"), new VillagerHouseT2());
    public static final TownCentreT2 RESEARCH_TOWN_CENTRE_T2 = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "town_centre_t2"), new TownCentreT2());
    public static final ResearchEliteTrader RESEARCH_ELITE_TRADER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "elite_trader"), new ResearchEliteTrader());
    public static final ResearchEliteTraining RESEARCH_ELITE_TRAINING = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "elite_training"), new ResearchEliteTraining());
    public static final ResearchSuperiorBlacksmith RESEARCH_SUPERIOR_BLACKSMITH = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "superior_blacksmith"), new ResearchSuperiorBlacksmith());
    public static final ResearchSpiderWebs RESEARCH_SPIDER_WEBS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "spider_webs"), new ResearchSpiderWebs());
    public static final ResearchBloodlust RESEARCH_BLOODLUST = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "bloodlust"), new ResearchBloodlust());
    public static final ResearchCubeMagma RESEARCH_CUBE_MAGMA = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "cube_magma"), new ResearchCubeMagma());
    public static final ResearchSoulFireballs RESEARCH_SOUL_FIREBALLS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "soul_fireballs"), new ResearchSoulFireballs());

    public static final ProductionItem TOGGLE_GATE_PRODUCTION = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "toggle_gate_production"), new ToggleGateProduction());

    public static final DrillTierUpgrade DRILL_T2;
    public static final DrillTierUpgrade DRILL_T3;
    public static final DrillTierUpgrade DRILL_T4;
    public static final DrillTierUpgrade DRILL_T5;
    public static final DrillTierUpgrade DRILL_T6;
    public static final BarracksTierUpgrade BARRACKS_T2;
    public static final BarracksTierUpgrade BARRACKS_T3;
    public static final BlacksmithTierUpgrade BLACKSMITH_T2;
    public static final BlacksmithTierUpgrade BLACKSMITH_T3;
    public static final ArcaneTowerTierUpgrade ARCANE_TOWER_T2;
    public static final ArcaneTowerTierUpgrade ARCANE_TOWER_T3;

    static {
        DrillTierUpgrade[] t2 = new DrillTierUpgrade[1];
        t2[0] = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "drill_tier_2"),
                new DrillTierUpgrade(
                        "Drill Tier 2",
                        ResourceCosts.DRILL_T2,
                        0,
                        DrillResearchRequirement.UPGRADED_DRILL,
                        p -> t2[0].itemIsBeingProducedAt(p)
                                || !(p.getBuilding() instanceof VillagerDrill)
                                || p.getUpgradeLevel() != 0,
                        "research.siege_of_settlements.upgrade.t2",
                        "research.siege_of_settlements.upgrade.drill_tier_2.tooltip1",
                        "research.siege_of_settlements.upgrade.drill_tier_2.tooltip2"
                ));
        DRILL_T2 = t2[0];

        DrillTierUpgrade[] t3 = new DrillTierUpgrade[1];
        t3[0] = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "drill_tier_3"),
                new DrillTierUpgrade(
                        "Drill Tier 3",
                        ResourceCosts.DRILL_T3,
                        1,
                        DrillResearchRequirement.UPGRADED_DRILL,
                        p -> t3[0].itemIsBeingProducedAt(p)
                                || !(p.getBuilding() instanceof VillagerDrill)
                                || p.getUpgradeLevel() != 1,
                        "research.siege_of_settlements.upgrade.t3",
                        "research.siege_of_settlements.upgrade.drill_tier_3.tooltip1",
                        "research.siege_of_settlements.upgrade.drill_tier_3.tooltip2"
                ));
        DRILL_T3 = t3[0];

        DrillTierUpgrade[] t4 = new DrillTierUpgrade[1];
        t4[0] = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "drill_tier_4"),
                new DrillTierUpgrade(
                        "Drill Tier 4",
                        ResourceCosts.DRILL_T4,
                        2,
                        DrillResearchRequirement.ADVANCED_DRILL,
                        p -> t4[0].itemIsBeingProducedAt(p)
                                || !(p.getBuilding() instanceof VillagerDrill)
                                || p.getUpgradeLevel() != 2,
                        "research.siege_of_settlements.upgrade.t4",
                        "research.siege_of_settlements.upgrade.drill_tier_4.tooltip1",
                        "research.siege_of_settlements.upgrade.drill_tier_4.tooltip2"
                ));
        DRILL_T4 = t4[0];

        DrillTierUpgrade[] t5 = new DrillTierUpgrade[1];
        t5[0] = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "drill_tier_5"),
                new DrillTierUpgrade(
                        "Drill Tier 5",
                        ResourceCosts.DRILL_T5,
                        3,
                        DrillResearchRequirement.ADVANCED_DRILL,
                        p -> t5[0].itemIsBeingProducedAt(p)
                                || !(p.getBuilding() instanceof VillagerDrill)
                                || p.getUpgradeLevel() != 3,
                        "research.siege_of_settlements.upgrade.t5",
                        "research.siege_of_settlements.upgrade.drill_tier_5.tooltip1",
                        "research.siege_of_settlements.upgrade.drill_tier_5.tooltip2"
                ));
        DRILL_T5 = t5[0];

        DrillTierUpgrade[] t6 = new DrillTierUpgrade[1];
        t6[0] = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "drill_tier_6"),
                new DrillTierUpgrade(
                        "Drill Tier 6",
                        ResourceCosts.DRILL_T6,
                        4,
                        DrillResearchRequirement.ADVANCED_DRILL,
                        p -> t6[0].itemIsBeingProducedAt(p)
                                || !(p.getBuilding() instanceof VillagerDrill)
                                || p.getUpgradeLevel() != 4,
                        "research.siege_of_settlements.upgrade.t6",
                        "research.siege_of_settlements.upgrade.drill_tier_6.tooltip1",
                        "research.siege_of_settlements.upgrade.drill_tier_6.tooltip2"
                ));
        DRILL_T6 = t6[0];

        BarracksTierUpgrade[] barracksT2 = new BarracksTierUpgrade[1];
        barracksT2[0] = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "barracks_tier_2"),
                new BarracksTierUpgrade(
                        "Barracks Tier 2",
                        ResourceCosts.BARRACKS_T2,
                        0,
                        p -> barracksT2[0].itemIsBeingProducedAt(p)
                                || !(p instanceof BarracksPlacement)
                                || p.getUpgradeLevel() != 0,
                        "research.siege_of_settlements.upgrade.t2",
                        "research.siege_of_settlements.upgrade.barracks_tier_2.tooltip1",
                        "research.siege_of_settlements.upgrade.barracks_tier_2.tooltip2"
                ));
        BARRACKS_T2 = barracksT2[0];

        BarracksTierUpgrade[] barracksT3 = new BarracksTierUpgrade[1];
        barracksT3[0] = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "barracks_tier_3"),
                new BarracksTierUpgrade(
                        "Barracks Tier 3",
                        ResourceCosts.BARRACKS_T3,
                        1,
                        p -> barracksT3[0].itemIsBeingProducedAt(p)
                                || !(p instanceof BarracksPlacement)
                                || p.getUpgradeLevel() != 1,
                        "research.siege_of_settlements.upgrade.t3",
                        "research.siege_of_settlements.upgrade.barracks_tier_3.tooltip1",
                        "research.siege_of_settlements.upgrade.barracks_tier_3.tooltip2"
                ));
        BARRACKS_T3 = barracksT3[0];

        BlacksmithTierUpgrade[] blacksmithT2 = new BlacksmithTierUpgrade[1];
        blacksmithT2[0] = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "blacksmith_t2"),
                new BlacksmithTierUpgrade(
                        "Blacksmith Tier 2",
                        ResourceCosts.BLACKSMITH_T2,
                        0,
                        ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_advanced_blacksmith"),
                        p -> blacksmithT2[0].itemIsBeingProducedAt(p)
                                || !(p instanceof BlacksmithPlacement)
                                || p.getUpgradeLevel() != 0,
                        "research.siege_of_settlements.upgrade.t2",
                        "research.siege_of_settlements.upgrade.blacksmith_tier_2.tooltip1",
                        "research.siege_of_settlements.upgrade.blacksmith_tier_2.tooltip2"
                ));
        BLACKSMITH_T2 = blacksmithT2[0];

        BlacksmithTierUpgrade[] blacksmithT3 = new BlacksmithTierUpgrade[1];
        blacksmithT3[0] = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "blacksmith_t3"),
                new BlacksmithTierUpgrade(
                        "Blacksmith Tier 3",
                        ResourceCosts.BLACKSMITH_T3,
                        1,
                        ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_advanced_blacksmith"),
                        p -> blacksmithT3[0].itemIsBeingProducedAt(p)
                                || !(p instanceof BlacksmithPlacement)
                                || p.getUpgradeLevel() != 1,
                        "research.siege_of_settlements.upgrade.t3",
                        "research.siege_of_settlements.upgrade.blacksmith_tier_3.tooltip1",
                        "research.siege_of_settlements.upgrade.blacksmith_tier_3.tooltip2"
                ));
        BLACKSMITH_T3 = blacksmithT3[0];

        ArcaneTowerTierUpgrade[] arcaneTowerT2 = new ArcaneTowerTierUpgrade[1];
        arcaneTowerT2[0] = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "arcane_tower_tier_2"),
                new ArcaneTowerTierUpgrade(
                        "Arcane Tower Tier 2",
                        ResourceCosts.ARCANE_TOWER_T2,
                        0,
                        ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_powerful_arcane_tower"),
                        p -> arcaneTowerT2[0].itemIsBeingProducedAt(p)
                                || !(p instanceof ArcaneTowerPlacement)
                                || p.getUpgradeLevel() != 0,
                        "research.siege_of_settlements.upgrade.t2",
                        "research.siege_of_settlements.upgrade.arcane_tower_tier_2.tooltip1",
                        "research.siege_of_settlements.upgrade.arcane_tower_tier_2.tooltip2"
                ));
        ARCANE_TOWER_T2 = arcaneTowerT2[0];

        ArcaneTowerTierUpgrade[] arcaneTowerT3 = new ArcaneTowerTierUpgrade[1];
        arcaneTowerT3[0] = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "arcane_tower_tier_3"),
                new ArcaneTowerTierUpgrade(
                        "Arcane Tower Tier 3",
                        ResourceCosts.ARCANE_TOWER_T3,
                        1,
                        ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_powerful_arcane_tower"),
                        p -> arcaneTowerT3[0].itemIsBeingProducedAt(p)
                                || !(p instanceof ArcaneTowerPlacement)
                                || p.getUpgradeLevel() != 1,
                        "research.siege_of_settlements.upgrade.t3",
                        "research.siege_of_settlements.upgrade.arcane_tower_tier_3.tooltip1",
                        "research.siege_of_settlements.upgrade.arcane_tower_tier_3.tooltip2"
                ));
        ARCANE_TOWER_T3 = arcaneTowerT3[0];
    }
    public static final ResearchCastle RESEARCH_CASTLE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_castle"), new ResearchCastle());
    public static final ResearchBlacksmith RESEARCH_BLACKSMITH = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_blacksmith"), new ResearchBlacksmith());
    public static final ResearchFireWalls RESEARCH_FIRE_WALLS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_fire_walls"), new ResearchFireWalls());
    public static final ResearchFireWalls2 RESEARCH_FIRE_WALLS2 = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_fire_walls2"), new ResearchFireWalls2());
    public static final ResearchTowerDamage1 RESEARCH_TOWER_DAMAGE_1 = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_tower_damage1"), new ResearchTowerDamage1());
    public static final ResearchTowerDamage2 RESEARCH_TOWER_DAMAGE_2 = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_tower_damage2"), new ResearchTowerDamage2());
    public static final ResearchBeacon RESEARCH_BEACON = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_beacon"), new ResearchBeacon());
    public static final ResearchTrader RESEARCH_TRADER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_trader"), new ResearchTrader());
    public static final ResearchMarketplace RESEARCH_MARKETPLACE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_marketplace"), new ResearchMarketplace());
    public static final ResearchProfessions RESEARCH_PROFESSIONS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_professions"), new ResearchProfessions());
    public static final ResearchUpgradedBarracks RESEARCH_UPGRADED_BARRACKS = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_upgraded_barracks"), new ResearchUpgradedBarracks());
    public static final ResearchUpgradedDrill RESEARCH_UPGRADED_DRILL = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_upgraded_drill"), new ResearchUpgradedDrill());
    public static final ResearchAdvancedDrill RESEARCH_ADVANCED_DRILL = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_advanced_drill"), new ResearchAdvancedDrill());
    public static final ResearchAdvancedBlacksmith RESEARCH_ADVANCED_BLACKSMITH = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_advanced_blacksmith"), new ResearchAdvancedBlacksmith());
    public static final ResearchBigHouse RESEARCH_BIG_HOUSE = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_big_house"), new ResearchBigHouse());
    public static final ResearchPowerfulArcaneTower RESEARCH_POWERFUL_ARCANE_TOWER = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "research_powerful_arcane_tower"), new ResearchPowerfulArcaneTower());


    public static final ResearchPortalForCivilian RESEARCH_PORTAL_FOR_CIVILIAN = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "portal_for_civilian"), new ResearchPortalForCivilian());
    public static final ResearchPortalForMilitary RESEARCH_PORTAL_FOR_MILITARY = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "portal_for_military"), new ResearchPortalForMilitary());
    public static final ResearchPortalForTransport RESEARCH_PORTAL_FOR_TRANSPORT = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "portal_for_transport"), new ResearchPortalForTransport());
    public static final ResearchBeaconLevel1 RESEARCH_BEACON_LEVEL_1 = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "beacon_level_1"), new ResearchBeaconLevel1());
    public static final ResearchBeaconLevel2 RESEARCH_BEACON_LEVEL_2 = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "beacon_level_2"), new ResearchBeaconLevel2());
    public static final ResearchBeaconLevel3 RESEARCH_BEACON_LEVEL_3 = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "beacon_level_3"), new ResearchBeaconLevel3());
    public static final ResearchBeaconLevel4 RESEARCH_BEACON_LEVEL_4 = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "beacon_level_4"), new ResearchBeaconLevel4());
    public static final ResearchBeaconLevel5 RESEARCH_BEACON_LEVEL_5 = register(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "beacon_level_5"), new ResearchBeaconLevel5());



    private static <T extends ProductionItem> T register(ResourceLocation id, T building) {
        return Registry.register(SiegeOfSettlementsRegistries.PRODUCTION_ITEM, id, building);
    }

    public static void init() {}
}
