package com.solegendary.siege_of_settlements.resources;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.config.SiegeOfSettlementsCommonConfigs;
import com.solegendary.siege_of_settlements.util.MyRenderer;
import net.minecraft.util.FormattedCharSequence;


// defined here because we need to be able to access in both
// static (for ProductionItems) and nonstatic (for getCurrentPopulation) contexts
// and we can't declare static getters in the Unit interface
public class ResourceCosts {
    private static final String ID = SiegeOfSettlements.MOD_ID;

    public static final ResourceCost ZOMBIE_VILLAGER = new ResourceCost(ID, "ZOMBIE_VILLAGER");
    public static final ResourceCost CREEPER = new ResourceCost(ID, "CREEPER");
    public static final ResourceCost ZOMBIE = new ResourceCost(ID, "ZOMBIE");
    public static final ResourceCost SKELETON = new ResourceCost(ID, "SKELETON");
    public static final ResourceCost STRAY = new ResourceCost(ID, "STRAY");
    public static final ResourceCost HUSK = new ResourceCost(ID, "HUSK");
    public static final ResourceCost DROWNED = new ResourceCost(ID, "DROWNED");
    public static final ResourceCost SPIDER = new ResourceCost(ID, "SPIDER");
    public static final ResourceCost POISON_SPIDER = new ResourceCost(ID, "POISON_SPIDER");
    public static final ResourceCost SLIME = new ResourceCost(ID, "SLIME");
    public static final ResourceCost WARDEN = new ResourceCost(ID, "WARDEN");
    public static final ResourceCost ZOMBIE_PIGLIN = new ResourceCost(ID, "ZOMBIE_PIGLIN");
    public static final ResourceCost ZOGLIN = new ResourceCost(ID, "ZOGLIN");
    public static final ResourceCost VILLAGER = new ResourceCost(ID, "VILLAGER");
    public static final ResourceCost MILITIA = new ResourceCost(ID, "MILITIA");
    public static final ResourceCost IRON_GOLEM = new ResourceCost(ID, "IRON_GOLEM");
    public static final ResourceCost REDSTONE_GOLEM = new ResourceCost(ID, "REDSTONE_GOLEM");
    public static final ResourceCost REDSTONE_MINE = new ResourceCost(ID, "REDSTONE_MINE");
    public static final ResourceCost PILLAGER = new ResourceCost(ID, "PILLAGER");
    public static final ResourceCost ARCHER = new ResourceCost(ID, "ARCHER");
    public static final ResourceCost ELITE_ARMORED_PILLAGER = new ResourceCost(ID, "elite_armored_pillager");
    public static final ResourceCost ARMORED_PILLAGER = new ResourceCost(ID, "armored_pillager");
    public static final ResourceCost VINDICATOR = new ResourceCost(ID, "VINDICATOR");
    public static final ResourceCost ELITE_VINDICATOR = new ResourceCost(ID, "ELITE_VINDICATOR");
    public static final ResourceCost ELITE_ARMORED_VINDICATOR = new ResourceCost(ID, "elite_armored_vindicator");
    public static final ResourceCost ARMORED_VINDICATOR = new ResourceCost(ID, "armored_vindicator");
    public static final ResourceCost ELITE_ARCHER = new ResourceCost(ID, "ELITE_ARCHER");
    public static final ResourceCost WITCH = new ResourceCost(ID, "WITCH");
    public static final ResourceCost EVOKER = new ResourceCost(ID, "EVOKER");
    public static final ResourceCost ILLUSIONER = new ResourceCost(ID, "ILLUSIONER");
    public static final ResourceCost RAVAGER = new ResourceCost(ID, "RAVAGER");
    public static final ResourceCost GRUNT = new ResourceCost(ID, "GRUNT");
    public static final ResourceCost BRUTE = new ResourceCost(ID, "BRUTE");
    public static final ResourceCost HEADHUNTER = new ResourceCost(ID, "HEADHUNTER");
    public static final ResourceCost MARAUDER = new ResourceCost(ID, "MARAUDER");
    public static final ResourceCost HOGLIN = new ResourceCost(ID, "HOGLIN");
    public static final ResourceCost BLAZE = new ResourceCost(ID, "BLAZE");
    public static final ResourceCost WITHER_SKELETON = new ResourceCost(ID, "WITHER_SKELETON");
    public static final ResourceCost GHAST = new ResourceCost(ID, "GHAST");
    public static final ResourceCost MAGMA_CUBE = new ResourceCost(ID, "MAGMA_CUBE");
    public static final ResourceCost AUROMANCER = new ResourceCost(ID, "AUROMANCER");
    public static final ResourceCost HORSE = new ResourceCost(ID, "HORSE");


    public static final ResourceCost NECROMANCER = new ResourceCost(ID, "NECROMANCER");
    public static final ResourceCost ROYAL_GUARD = new ResourceCost(ID, "ROYAL_GUARD");
    public static final ResourceCost PIGLIN_MERCHANT = new ResourceCost(ID, "PIGLIN_MERCHANT");
    public static final ResourceCost WRETCHED_WRAITH = new ResourceCost(ID, "WRETCHED_WRAITH");
    public static final ResourceCost ENCHANTER = new ResourceCost(ID, "ENCHANTER");
    public static final ResourceCost WILDFIRE = new ResourceCost(ID, "WILDFIRE");
    public static final ResourceCost HERO_BASE_REVIVE_COST = new ResourceCost(ID, "HERO_BASE_REVIVE_COST");
    public static final ResourceCost HERO_EXTRA_REVIVE_COST_PER_LEVEL = new ResourceCost(ID, "HERO_REVIVE_COST_PER_LEVEL");

    public static final ResourceCost ENDERMAN = new ResourceCost(ID, "ENDERMAN");
    public static final ResourceCost POLAR_BEAR = new ResourceCost(ID, "POLAR_BEAR");
    public static final ResourceCost GRIZZLY_BEAR = new ResourceCost(ID, "GRIZZLY_BEAR");
    public static final ResourceCost PANDA = new ResourceCost(ID, "PANDA");
    public static final ResourceCost WOLF = new ResourceCost(ID, "WOLF");
    public static final ResourceCost LLAMA = new ResourceCost(ID, "LLAMA");

    //BUILDINGS
    public static final ResourceCost BEACON = new ResourceCost(ID, "BEACON");

    public static final ResourceCost STOCKPILE = new ResourceCost(ID, "STOCKPILE");
    public static final ResourceCost OAK_BRIDGE = new ResourceCost(ID, "OAK_BRIDGE");
    public static final ResourceCost SPRUCE_BRIDGE = new ResourceCost(ID, "SPRUCE_BRIDGE");
    public static final ResourceCost BLACKSTONE_BRIDGE = new ResourceCost(ID, "BLACKSTONE_BRIDGE");
    //Monster
    public static final ResourceCost MAUSOLEUM = new ResourceCost(ID, "MAUSOLEUM");
    public static final ResourceCost HAUNTED_HOUSE = new ResourceCost(ID, "HAUNTED_HOUSE");
    public static final ResourceCost PUMPKIN_FARM = new ResourceCost(ID, "PUMPKIN_FARM");
    public static final ResourceCost SCULK_CATALYST = new ResourceCost(ID, "SKULK_CATALYST");
    public static final ResourceCost GRAVEYARD = new ResourceCost(ID, "GRAVEYARD");
    public static final ResourceCost SPIDER_LAIR = new ResourceCost(ID, "SPIDER_LAIR");
    public static final ResourceCost DUNGEON = new ResourceCost(ID, "DUNGEON");
    public static final ResourceCost LABORATORY = new ResourceCost(ID, "LABORATORY");
    public static final ResourceCost DARK_WATCHTOWER = new ResourceCost(ID, "DARK_WATCHTOWER");
    public static final ResourceCost SLIME_PIT = new ResourceCost(ID, "SLIME_PIT");
    public static final ResourceCost STRONGHOLD = new ResourceCost(ID, "STRONGHOLD");
    public static final ResourceCost ALTAR_OF_DARKNESS = new ResourceCost(ID, "ALTAR_OF_DARKNESS");
    //Villagers
    public static final ResourceCost TOWN_CENTRE = new ResourceCost(ID, "TOWN_CENTRE");
    public static final ResourceCost VILLAGER_HOUSE = new ResourceCost(ID, "VILLAGER_HOUSE");
    public static final ResourceCost WHEAT_FARM = new ResourceCost(ID, "WHEAT_FARM");
    public static final ResourceCost BARRACKS = new ResourceCost(ID, "BARRACKS");
    public static final ResourceCost BLACKSMITH = new ResourceCost(ID, "BLACKSMITH");
    public static final ResourceCost ARCANE_TOWER = new ResourceCost(ID, "ARCANE_TOWER");
    public static final ResourceCost LIBRARY = new ResourceCost(ID, "LIBRARY");
    public static final ResourceCost WATCHTOWER = new ResourceCost(ID, "WATCHTOWER");
    public static final ResourceCost CASTLE = new ResourceCost(ID, "CASTLE");
    public static final ResourceCost IRON_GOLEM_BUILDING = new ResourceCost(ID, "IRON_GOLEM_BUILDING");
    public static final ResourceCost SHRINE_OF_PROSPERITY = new ResourceCost(ID, "SHRINE_OF_PROSPERITY");
    public static final ResourceCost DRILL = new ResourceCost(ID, "DRILL");
    public static final ResourceCost WALL = new ResourceCost(ID, "WALL");
    public static final ResourceCost GATE = new ResourceCost(ID, "GATE");
    public static final ResourceCost CORNER_WALL = new ResourceCost(ID, "CORNER_WALL");
    public static final ResourceCost RESEARCH_CENTRE = new ResourceCost(ID, "RESEARCH_CENTRE");
    public static final ResourceCost MARKETPLACE = new ResourceCost(ID, "MARKETPLACE");
    public static final ResourceCost STABLE = new ResourceCost(ID, "STABLE");

    //Piglins
    public static final ResourceCost CENTRAL_PORTAL = new ResourceCost(ID, "CENTRAL_PORTAL");
    public static final ResourceCost BASIC_PORTAL = new ResourceCost(ID, "BASIC_PORTAL");
    public static final ResourceCost CIVILIAN_PORTAL = new ResourceCost(ID, "CIVILIAN_PORTAL");
    public static final ResourceCost NETHERWART_FARM = new ResourceCost(ID, "NETHERWART_FARM");
    public static final ResourceCost BASTION = new ResourceCost(ID, "BASTION");
    public static final ResourceCost HOGLIN_STABLES = new ResourceCost(ID, "HOGLIN_STABLES");
    public static final ResourceCost FLAME_SANCTUARY = new ResourceCost(ID, "FLAME_SANCTUARY");
    public static final ResourceCost BASALT_SPRINGS = new ResourceCost(ID, "BASALT_SPRINGS");
    public static final ResourceCost WITHER_SHRINE = new ResourceCost(ID, "WITHER_SHRINE");
    public static final ResourceCost FORTRESS = new ResourceCost(ID, "FORTRESS");
    public static final ResourceCost INFERNAL_PORTAL = new ResourceCost(ID, "INFERNAL_PORTAL");

    // RESEARCH

    public static final ResourceCost RESEARCH_SUPERIOR_BLACKSMITH = new ResourceCost(ID, "RESEARCH_SUPERIOR_BLACKSMITH");
    public static final ResourceCost RESEARCH_GOLEM_SMITHING = new ResourceCost(ID, "RESEARCH_GOLEM_SMITHING");
    public static final ResourceCost RESEARCH_MILITIA_BOWS = new ResourceCost(ID, "RESEARCH_MILITIA_BOWS");
    public static final ResourceCost RESEARCH_LAB_LIGHTNING_ROD = new ResourceCost(ID, "RESEARCH_LAB_LIGHTNING_ROD");
    public static final ResourceCost RESEARCH_RESOURCE_CAPACITY = new ResourceCost(ID, "RESEARCH_RESOURCE_CAPACITY");
    public static final ResourceCost RESEARCH_SPIDER_JOCKEYS = new ResourceCost(ID, "RESEARCH_SPIDER_JOCKEYS");
    public static final ResourceCost RESEARCH_SPIDER_WEBS = new ResourceCost(ID, "RESEARCH_SPIDER_WEBS");
    public static final ResourceCost RESEARCH_POISON_SPIDERS = new ResourceCost(ID, "RESEARCH_POISON_SPIDERS");
    public static final ResourceCost RESEARCH_HUSKS = new ResourceCost(ID, "RESEARCH_HUSKS");
    public static final ResourceCost RESEARCH_DROWNED = new ResourceCost(ID, "RESEARCH_DROWNED");
    public static final ResourceCost RESEARCH_STRAYS = new ResourceCost(ID, "RESEARCH_STRAYS");
    public static final ResourceCost RESEARCH_SLIME_CONVERSION = new ResourceCost(ID, "RESEARCH_SLIME_CONVERSION");
    public static final ResourceCost RESEARCH_LINGERING_POTIONS = new ResourceCost(ID, "RESEARCH_LINGERING_POTIONS");
    public static final ResourceCost RESEARCH_HEALING_POTIONS = new ResourceCost(ID, "RESEARCH_HEALING_POTIONS");
    public static final ResourceCost RESEARCH_WATER_POTIONS = new ResourceCost(ID, "RESEARCH_WATER_POTIONS");
    public static final ResourceCost RESEARCH_EVOKER_VEXES = new ResourceCost(ID, "RESEARCH_EVOKER_VEXES");
    public static final ResourceCost RESEARCH_CASTLE_FLAG = new ResourceCost(ID, "RESEARCH_CASTLE_FLAG");
    public static final ResourceCost RESEARCH_GRAND_LIBRARY = new ResourceCost(ID, "RESEARCH_GRAND_LIBRARY");
    public static final ResourceCost RESEARCH_VILLAGER_HOUSE_T2 = new ResourceCost(ID, "RESEARCH_VILLAGER_HOUSE_T2");
    public static final ResourceCost RESEARCH_TOWN_CENTRE_T2 = new ResourceCost(ID, "RESEARCH_TOWN_CENTRE_T2");
    public static final ResourceCost ARCANE_TOWER_T2 = new ResourceCost(ID, "ARCANE_TOWER_T2");
    public static final ResourceCost ARCANE_TOWER_T3 = new ResourceCost(ID, "ARCANE_TOWER_T3");
    public static final ResourceCost BLACKSMITH_T2 = new ResourceCost(ID, "BLACKSMITH_T2");
    public static final ResourceCost BLACKSMITH_T3 = new ResourceCost(ID, "BLACKSMITH_T3");
    public static final ResourceCost RESEARCH_SILVERFISH = new ResourceCost(ID, "RESEARCH_SILVERFISH");
    public static final ResourceCost RESEARCH_SCULK_AMPLIFIERS = new ResourceCost(ID, "RESEARCH_SCULK_AMPLIFIERS");
    public static final ResourceCost RESEARCH_RAVAGER_ARTILLERY = new ResourceCost(ID, "RESEARCH_RAVAGER_ARTILLERY");
    public static final ResourceCost RESEARCH_BRUTE_SHIELDS = new ResourceCost(ID, "RESEARCH_BRUTE_SHIELDS");
    public static final ResourceCost RESEARCH_HOGLIN_CAVALRY = new ResourceCost(ID, "RESEARCH_HOGLIN_CAVALRY");
    public static final ResourceCost RESEARCH_HEAVY_TRIDENTS = new ResourceCost(ID, "RESEARCH_HEAVY_TRIDENTS");
    public static final ResourceCost RESEARCH_CLEAVING_FLAILS = new ResourceCost(ID, "RESEARCH_CLEAVING_FLAILS");
    public static final ResourceCost RESEARCH_BLAZE_FIRE_WALL = new ResourceCost(ID, "RESEARCH_BLAZE_FIRE_WALL");
    public static final ResourceCost RESEARCH_FIRE_RESISTANCE = new ResourceCost(ID, "RESEARCH_FIRE_RESISTANCE");
    public static final ResourceCost RESEARCH_WITHER_CLOUDS = new ResourceCost(ID, "RESEARCH_WITHER_CLOUDS");
    public static final ResourceCost RESEARCH_BLOODLUST = new ResourceCost(ID, "RESEARCH_BLOODLUST");
    public static final ResourceCost RESEARCH_ADVANCED_PORTALS = new ResourceCost(ID, "RESEARCH_ADVANCED_PORTALS");
    public static final ResourceCost RESEARCH_CIVILIAN_PORTAL = new ResourceCost(ID, "RESEARCH_CIVILIAN_PORTAL");
    public static final ResourceCost RESEARCH_MILITARY_PORTAL = new ResourceCost(ID, "RESEARCH_MILITARY_PORTAL");
    public static final ResourceCost RESEARCH_TRANSPORT_PORTAL = new ResourceCost(ID, "RESEARCH_TRANSPORT_PORTAL");
    public static final ResourceCost RESEARCH_CUBE_MAGMA = new ResourceCost(ID, "RESEARCH_CUBE_MAGMA");
    public static final ResourceCost RESEARCH_SOUL_FIREBALLS = new ResourceCost(ID, "RESEARCH_SOUL_FIREBALLS");
    public static final ResourceCost RESEARCH_BEACON_LEVEL1 = new ResourceCost(ID, "RESEARCH_BEACON_LEVEL1");
    public static final ResourceCost RESEARCH_BEACON_LEVEL2 = new ResourceCost(ID, "RESEARCH_BEACON_LEVEL2");
    public static final ResourceCost RESEARCH_BEACON_LEVEL3 = new ResourceCost(ID, "RESEARCH_BEACON_LEVEL3");
    public static final ResourceCost RESEARCH_BEACON_LEVEL4 = new ResourceCost(ID, "RESEARCH_BEACON_LEVEL4");
    public static final ResourceCost RESEARCH_BEACON_LEVEL5 = new ResourceCost(ID, "RESEARCH_BEACON_LEVEL5");
    public static final ResourceCost TOGGLE_VILLAGER_GATE = new ResourceCost(ID, "TOGGLE_VILLAGER_GATE");
    public static final ResourceCost DRILL_T2 = new ResourceCost(ID, "DRILL_T2");
    public static final ResourceCost DRILL_T3 = new ResourceCost(ID, "DRILL_T3");
    public static final ResourceCost DRILL_T4 = new ResourceCost(ID, "DRILL_T4");
    public static final ResourceCost DRILL_T5 = new ResourceCost(ID, "DRILL_T5");
    public static final ResourceCost DRILL_T6 = new ResourceCost(ID, "DRILL_T6");
    public static final ResourceCost BARRACKS_T2 = new ResourceCost(ID, "BARRACKS_T2");
    public static final ResourceCost BARRACKS_T3 = new ResourceCost(ID, "BARRACKS_T3");
    public static final ResourceCost RESEARCH_CASTLE = new ResourceCost(ID, "RESEARCH_CASTLE");
    public static final ResourceCost RESEARCH_BLACKSMITH = new ResourceCost(ID, "RESEARCH_BLACKSMITH");
    public static final ResourceCost RESEARCH_FIRE_WALLS = new ResourceCost(ID, "RESEARCH_FIRE_WALLS");
    public static final ResourceCost RESEARCH_FIRE_WALLS2 = new ResourceCost(ID, "RESEARCH_FIRE_WALLS2");
    public static final ResourceCost RESEARCH_TOWER_DAMAGE1 = new ResourceCost(ID, "RESEARCH_TOWER_DAMAGE1");
    public static final ResourceCost RESEARCH_TOWER_DAMAGE2 = new ResourceCost(ID, "RESEARCH_TOWER_DAMAGE2");
    public static final ResourceCost RESEARCH_BEACON = new ResourceCost(ID, "RESEARCH_BEACON");
    public static final ResourceCost RESEARCH_TRADER = new ResourceCost(ID, "RESEARCH_TRADER");
    public static final ResourceCost RESEARCH_MARKETPLACE = new ResourceCost(ID, "RESEARCH_MARKETPLACE");
    public static final ResourceCost RESEARCH_PROFESSIONS = new ResourceCost(ID, "RESEARCH_PROFESSIONS");
    public static final ResourceCost RESEARCH_ELITE_TRADER = new ResourceCost(ID, "RESEARCH_ELITE_TRADER");
    public static final ResourceCost RESEARCH_ELITE_TRAINING = new ResourceCost(ID, "RESEARCH_ELITE_TRAININGТ");
    public static final ResourceCost RESEARCH_UPGRADED_BARRACKS = new ResourceCost(ID, "RESEARCH_UPGRADED_BARRACKS");
    public static final ResourceCost RESEARCH_UPGRADED_DRILL = new ResourceCost(ID, "RESEARCH_UPGRADED_DRILL");
    public static final ResourceCost RESEARCH_ADVANCED_DRILL = new ResourceCost(ID, "RESEARCH_ADVANCED_DRILL");
    public static final ResourceCost RESEARCH_ADVANCED_BLACKSMITH = new ResourceCost(ID, "RESEARCH_ADVANCED_BLACKSMITH");
    public static final ResourceCost RESEARCH_BIG_HOUSE = new ResourceCost(ID, "RESEARCH_BIG_HOUSE");
    public static final ResourceCost RESEARCH_POWERFUL_ARCANE_TOWER = new ResourceCost(ID, "RESEARCH_POWERFUL_ARCANE_TOWER");

    // ABILITIES

    public static final ResourceCost ENCHANT_MAIMING = new ResourceCost(ID, "ENCHANT_MAIMING");
    public static final ResourceCost ENCHANT_QUICK_CHARGE = new ResourceCost(ID, "ENCHANT_QUICK_CHARGE");
    public static final ResourceCost ENCHANT_SHARPNESS = new ResourceCost(ID, "ENCHANT_SHARPNESS");
    public static final ResourceCost ENCHANT_MULTISHOT = new ResourceCost(ID, "ENCHANT_MULTISHOT");
    public static final ResourceCost ENCHANT_VIGOR = new ResourceCost(ID, "ENCHANT_VIGOR");
    public static final ResourceCost EQUIP_LEATHER_ARMOR = new ResourceCost(ID, "EQUIP_LEATHER_ARMOR");
    public static final ResourceCost EQUIP_CHAINMAIL_ARMOR = new ResourceCost(ID, "EQUIP_CHAINMAIL_ARMOR");
    public static final ResourceCost EQUIP_IRON_ARMOR = new ResourceCost(ID, "EQUIP_IRON_ARMOR");
    public static final ResourceCost EQUIP_IRON_SWORD = new ResourceCost(ID, "EQUIP_IRON_SWORD");
    public static final ResourceCost EQUIP_IRON_PICKAXE = new ResourceCost(ID, "EQUIP_IRON_PICKAXE");
    public static final ResourceCost EQUIP_IRON_AXE = new ResourceCost(ID, "EQUIP_IRON_AXE");
    public static final ResourceCost EQUIP_IRON_HOE = new ResourceCost(ID, "EQUIP_IRON_HOE");
    public static final ResourceCost EQUIP_DIAMOND_ARMOR = new ResourceCost(ID, "EQUIP_DIAMOND_ARMOR");
    public static final ResourceCost EQUIP_DIAMOND_SWORD = new ResourceCost(ID, "EQUIP_DIAMOND_SWORD");
    public static final ResourceCost EQUIP_DIAMOND_PICKAXE = new ResourceCost(ID, "EQUIP_DIAMOND_PICKAXE");
    public static final ResourceCost EQUIP_DIAMOND_AXE = new ResourceCost(ID, "EQUIP_DIAMOND_AXE");
    public static final ResourceCost EQUIP_DIAMOND_HOE = new ResourceCost(ID, "EQUIP_DIAMOND_HOE");
    public static final ResourceCost STEAK = new ResourceCost(ID, "STEAK");
    public static final ResourceCost EQUIP_BOW = new ResourceCost(ID, "EQUIP_BOW");
    public static final ResourceCost EQUIP_ARROWS = new ResourceCost(ID, "EQUIP_ARROWS");
    public static final ResourceCost EQUIP_RAVAGER_ARMOR = new ResourceCost(ID, "EQUIP_RAVAGER_ARMOR");
    public static final ResourceCost BECOME_TRADER = new ResourceCost(ID, "BECOME_TRADER");
    public static final ResourceCost BECOME_FARMER = new ResourceCost(ID, "BECOME_FARMER");
    public static final ResourceCost BECOME_LUMBERJACK = new ResourceCost(ID, "BECOME_LUMBERJACK");
    public static final ResourceCost BECOME_MINER = new ResourceCost(ID, "BECOME_MINER");
    public static final ResourceCost BECOME_MASON = new ResourceCost(ID, "BECOME_MASON");
    public static final ResourceCost BECOME_HUNTER = new ResourceCost(ID, "BECOME_HUNTER");
    public static final ResourceCost BECOME_BLACKSMITH = new ResourceCost(ID, "BECOME_BLACKSMITH");
    public static final ResourceCost RESEARCH_BLACKSMITHS = new ResourceCost(ID, "RESEARCH_BLACKSMITHS");
    public static final ResourceCost BERSERK = new ResourceCost(ID, "BERSERK");
    public static final ResourceCost UPGRADE_TRADER_TO_ELITE = new ResourceCost(ID, "UPGRADE_TRADER_TO_ELITE");
    public static final ResourceCost UPGRADE_ARCHER_TO_ELITE = new ResourceCost(ID, "UPGRADE_ARCHER_TO_ELITE");
    public static final ResourceCost UPGRADE_ARMORED_VINDICATOR_TO_ELITE = new ResourceCost(ID, "UPGRADE_ARMORED_VINDICATOR_TO_ELITE");
    public static final ResourceCost UPGRADE_ARMORED_PILLAGER_TO_ELITE = new ResourceCost(ID, "UPGRADE_ARMORED_PILLAGER_TO_ELITE");



    // UNUSED

    public static final ResourceCost RESEARCH_VINDICATOR_AXES = ResourceCost.Research(0,200,400, 150);
    public static final ResourceCost RESEARCH_PILLAGER_CROSSBOWS = ResourceCost.Research(0,600,300, 180);

    public static FormattedCharSequence getFormattedCost(ResourceCost resCost) {
        String str = "";
        if (resCost.food > 0)
            str += "\uE000  " + resCost.food + "     ";
        if (resCost.wood > 0)
            str += "\uE001  " + resCost.wood + "     ";
        if (resCost.ore > 0)
            str += "\uE002  " + resCost.ore + "     ";
        if (resCost.emerald > 0)
            str += "\uE00A  " + resCost.emerald + "     ";

        if (str.isEmpty())
            str += "\uE000  0     ";
        str = str.trim();
        return FormattedCharSequence.forward(str, MyRenderer.iconStyle);
    }
    public static FormattedCharSequence getFormattedPopAndTime(ResourceCost resCost) {
        if (resCost.id != null && resCost.id.contains("RESEARCH") && 
            !resCost.id.equals("RESEARCH_TOWN_CENTRE_T2") && 
            !resCost.id.equals("RESEARCH_GRAND_LIBRARY") && 
            !resCost.id.equals("RESEARCH_VILLAGER_HOUSE_T2")) {
            return getFormattedPop(resCost);
        }
        return FormattedCharSequence.forward("\uE003  " + resCost.population + "     \uE004  " + resCost.ticks/ResourceCost.TICKS_PER_SECOND + "s", MyRenderer.iconStyle);
    }
    public static FormattedCharSequence getFormattedPop(ResourceCost resCost) {
        return FormattedCharSequence.forward("\uE003  " + resCost.population, MyRenderer.iconStyle);
    }
    public static FormattedCharSequence getFormattedTime(ResourceCost resCost) {
        if (resCost.id != null && resCost.id.contains("RESEARCH") && 
            !resCost.id.equals("RESEARCH_TOWN_CENTRE_T2") && 
            !resCost.id.equals("RESEARCH_GRAND_LIBRARY") && 
            !resCost.id.equals("RESEARCH_VILLAGER_HOUSE_T2")) {
            return FormattedCharSequence.forward("", MyRenderer.iconStyle);
        }
        return FormattedCharSequence.forward("\uE004  " + resCost.ticks/ResourceCost.TICKS_PER_SECOND + "s", MyRenderer.iconStyle);
    }

    public static final int REPLANT_WOOD_COST = 1;
    public static final int REDUCED_REPLANT_WOOD_COST = 0;
    public static final int DEFAULT_MAX_POPULATION = 10000;
    /*
        Unit costs are defined here during CommonSetup
        Do not read values and initialize from config earlier, else will result in IllegalStateException !!!
     */
    public static void deferredLoadResourceCosts() {

        // ******************* UNITS ******************* //
        // Monsters
        CREEPER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.CREEPER);
        ZOMBIE.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ZOMBIE);
        ZOMBIE_VILLAGER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ZOMBIE_VILLAGER);
        SKELETON.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.SKELETON);
        STRAY.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.STRAY);
        HUSK.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.HUSK);
        DROWNED.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.DROWNED);
        SPIDER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.SPIDER);
        POISON_SPIDER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.POISON_SPIDER);
        SLIME.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.SLIME);
        WARDEN.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.WARDEN);
        NECROMANCER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.NECROMANCER);
        WRETCHED_WRAITH.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.WRETCHED_WRAITH);
        ZOMBIE_PIGLIN.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ZOMBIE_PIGLIN);
        ZOGLIN.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ZOGLIN);
        // Villagers
        VILLAGER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.VILLAGER);
        MILITIA.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.MILITIA);
        IRON_GOLEM.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.IRON_GOLEM);
        REDSTONE_GOLEM.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.REDSTONE_GOLEM);
        REDSTONE_MINE.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.REDSTONE_MINE);
        PILLAGER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.PILLAGER);
        ARCHER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ARCHER);
        ELITE_ARMORED_PILLAGER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ELITE_ARMORED_PILLAGER);
        ARMORED_PILLAGER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ARMORED_PILLAGER);
        VINDICATOR.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.VINDICATOR);
        ELITE_VINDICATOR.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ELITE_VINDICATOR);
        ELITE_ARMORED_VINDICATOR.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ELITE_ARMORED_VINDICATOR);
        ARMORED_VINDICATOR.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ARMORED_VINDICATOR);
        ELITE_ARCHER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ELITE_ARCHER);
        WITCH.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.WITCH);
        EVOKER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.EVOKER);
        ILLUSIONER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ILLUSIONER);
        RAVAGER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.RAVAGER);
        ROYAL_GUARD.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ROYAL_GUARD);
        ENCHANTER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ENCHANTER);
        AUROMANCER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.AUROMANCER);
        HORSE.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.HORSE);
        // Piglins
        GRUNT.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.GRUNT);
        BRUTE.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.BRUTE);
        HEADHUNTER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.HEADHUNTER);
        MARAUDER.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.MARAUDER);
        HOGLIN.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.HOGLIN);
        BLAZE.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.BLAZE);
        WITHER_SKELETON.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.WITHER_SKELETON);
        GHAST.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.GHAST);
        MAGMA_CUBE.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.MAGMA_CUBE);
        PIGLIN_MERCHANT.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.PIGLIN_MERCHANT);
        WILDFIRE.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.WILDFIRE);

        ENDERMAN.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.ENDERMAN);
        POLAR_BEAR.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.POLAR_BEAR);
        GRIZZLY_BEAR.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.GRIZZLY_BEAR);
        PANDA.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.PANDA);
        WOLF.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.WOLF);
        LLAMA.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.LLAMA);

        HERO_BASE_REVIVE_COST.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.HERO_BASE_REVIVE_COST);
        HERO_EXTRA_REVIVE_COST_PER_LEVEL.bakeValues(SiegeOfSettlementsCommonConfigs.UnitCosts.HERO_EXTRA_REVIVE_COST_PER_LEVEL);

        // ******************* BUILDINGS ******************* //
        BEACON.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.BEACON);

        STOCKPILE.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.STOCKPILE);
        OAK_BRIDGE.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.OAK_BRIDGE);
        SPRUCE_BRIDGE.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.SPRUCE_BRIDGE);
        BLACKSTONE_BRIDGE.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.BLACKSTONE_BRIDGE);
        // Monsters
        MAUSOLEUM.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.MAUSOLEUM);
        HAUNTED_HOUSE.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.HAUNTED_HOUSE);
        PUMPKIN_FARM.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.PUMPKIN_FARM);
        SCULK_CATALYST.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.SCULK_CATALYST);
        GRAVEYARD.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.GRAVEYARD);
        SPIDER_LAIR.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.SPIDER_LAIR);
        DUNGEON.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.DUNGEON);
        LABORATORY.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.LABORATORY);
        DARK_WATCHTOWER.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.DARK_WATCHTOWER);
        SLIME_PIT.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.SLIME_PIT);
        STRONGHOLD.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.STRONGHOLD);
        ALTAR_OF_DARKNESS.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.ALTAR_OF_DARKNESS);
        // Villagers
        TOWN_CENTRE.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.TOWN_CENTRE);
        VILLAGER_HOUSE.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.VILLAGER_HOUSE);
        WHEAT_FARM.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.WHEAT_FARM);
        BARRACKS.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.BARRACKS);
        BLACKSMITH.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.BLACKSMITH);
        ARCANE_TOWER.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.ARCANE_TOWER);
        LIBRARY.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.LIBRARY);
        WATCHTOWER.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.WATCHTOWER);
        CASTLE.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.CASTLE);
        IRON_GOLEM_BUILDING.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.IRON_GOLEM_BUILDING);
        SHRINE_OF_PROSPERITY.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.SHRINE_OF_PROSPERITY);
        DRILL.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.DRILL);
        WALL.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.WALL);
        GATE.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.GATE);
        CORNER_WALL.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.CORNER_WALL);
        RESEARCH_CENTRE.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.RESEARCH_CENTRE);
        MARKETPLACE.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.MARKETPLACE);
        STABLE.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.STABLE);
        // Piglins
        CENTRAL_PORTAL.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.CENTRAL_PORTAL);
        BASIC_PORTAL.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.BASIC_PORTAL);
        CIVILIAN_PORTAL.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.CIVILIAN_PORTAL);
        NETHERWART_FARM.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.NETHERWART_FARM);
        BASTION.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.BASTION);
        HOGLIN_STABLES.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.HOGLIN_STABLES);
        FLAME_SANCTUARY.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.FLAME_SANCTUARY);
        WITHER_SHRINE.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.WITHER_SHRINE);
        BASALT_SPRINGS.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.BASALT_SPRINGS);
        FORTRESS.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.FORTRESS);
        INFERNAL_PORTAL.bakeValues(SiegeOfSettlementsCommonConfigs.BuildingCosts.INFERNAL_PORTAL);
        // ******************* RESEARCH ******************* //
        RESEARCH_GOLEM_SMITHING.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_GOLEM_SMITHING);
        RESEARCH_SUPERIOR_BLACKSMITH.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_SUPERIOR_BLACKSMITH);
        RESEARCH_MILITIA_BOWS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_MILITIA_BOWS);
        RESEARCH_LAB_LIGHTNING_ROD.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_LAB_LIGHTNING_ROD);
        RESEARCH_RESOURCE_CAPACITY.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_RESOURCE_CAPACITY);
        RESEARCH_SPIDER_JOCKEYS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_SPIDER_JOCKEYS);
        RESEARCH_SPIDER_WEBS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_SPIDER_WEBS);
        RESEARCH_POISON_SPIDERS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_POISON_SPIDERS);
        RESEARCH_HUSKS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_HUSKS);
        RESEARCH_DROWNED.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_DROWNED);
        RESEARCH_STRAYS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_STRAYS);
        RESEARCH_SLIME_CONVERSION.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_SLIME_CONVERSION);
        RESEARCH_LINGERING_POTIONS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_LINGERING_POTIONS);
        RESEARCH_WATER_POTIONS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_WATER_POTIONS);
        RESEARCH_HEALING_POTIONS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_HEALING_POTIONS);
        RESEARCH_EVOKER_VEXES.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_EVOKER_VEXES);
        RESEARCH_CASTLE_FLAG.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_CASTLE_FLAG);
        RESEARCH_GRAND_LIBRARY.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_GRAND_LIBRARY);
        RESEARCH_VILLAGER_HOUSE_T2.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_VILLAGER_HOUSE_T2);
        ARCANE_TOWER_T2.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.ARCANE_TOWER_T2);
        ARCANE_TOWER_T3.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.ARCANE_TOWER_T3);
        RESEARCH_SILVERFISH.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_SILVERFISH);
        RESEARCH_SCULK_AMPLIFIERS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_SCULK_AMPLIFIERS);
        RESEARCH_RAVAGER_ARTILLERY.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_RAVAGER_ARTILLERY);
        RESEARCH_BRUTE_SHIELDS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_BRUTE_SHIELDS);
        RESEARCH_HOGLIN_CAVALRY.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_HOGLIN_CAVALRY);
        RESEARCH_HEAVY_TRIDENTS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_HEAVY_TRIDENTS);
        RESEARCH_CLEAVING_FLAILS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_CLEAVING_FLAILS);
        RESEARCH_BLAZE_FIRE_WALL.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_BLAZE_FIRE_WALL);
        RESEARCH_FIRE_RESISTANCE.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_FIRE_RESISTANCE);
        RESEARCH_WITHER_CLOUDS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_WITHER_CLOUDS);
        RESEARCH_BLOODLUST.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_BLOODLUST);
        RESEARCH_ADVANCED_PORTALS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_ADVANCED_PORTALS);
        RESEARCH_CIVILIAN_PORTAL.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_CIVILIAN_PORTAL);
        RESEARCH_MILITARY_PORTAL.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_MILITARY_PORTAL);
        RESEARCH_TRANSPORT_PORTAL.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_TRANSPORT_PORTAL);
        RESEARCH_CUBE_MAGMA.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_CUBE_MAGMA);
        RESEARCH_SOUL_FIREBALLS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_SOUL_FIREBALLS);
        RESEARCH_BEACON_LEVEL1.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_BEACON_LEVEL1);
        RESEARCH_BEACON_LEVEL2.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_BEACON_LEVEL2);
        RESEARCH_BEACON_LEVEL3.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_BEACON_LEVEL3);
        RESEARCH_BEACON_LEVEL4.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_BEACON_LEVEL4);
        RESEARCH_BEACON_LEVEL5.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_BEACON_LEVEL5);
        TOGGLE_VILLAGER_GATE.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.TOGGLE_VILLAGER_GATE);
        RESEARCH_CASTLE.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_CASTLE);
        RESEARCH_BLACKSMITH.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_BLACKSMITH);
        RESEARCH_FIRE_WALLS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_FIRE_WALLS);
        RESEARCH_FIRE_WALLS2.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_FIRE_WALLS2);
        RESEARCH_TOWER_DAMAGE1.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_TOWER_DAMAGE1);
        RESEARCH_TOWER_DAMAGE2.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_TOWER_DAMAGE2);
        RESEARCH_BEACON.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_BEACON);
        RESEARCH_TRADER.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_TRADER);
        RESEARCH_MARKETPLACE.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_MARKETPLACE);
        RESEARCH_PROFESSIONS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_PROFESSIONS);
        RESEARCH_ELITE_TRADER.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_ELITE_TRADER);
        RESEARCH_ELITE_TRAINING.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_ELITE_TRAINING);
        RESEARCH_UPGRADED_BARRACKS.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_UPGRADED_BARRACKS);
        RESEARCH_UPGRADED_DRILL.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_UPGRADED_DRILL);
        RESEARCH_ADVANCED_DRILL.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_ADVANCED_DRILL);
        RESEARCH_ADVANCED_BLACKSMITH.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_ADVANCED_BLACKSMITH);
        RESEARCH_BIG_HOUSE.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_BIG_HOUSE);
        RESEARCH_POWERFUL_ARCANE_TOWER.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.RESEARCH_POWERFUL_ARCANE_TOWER);
        DRILL_T2.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.DRILL_T2);
        DRILL_T3.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.DRILL_T3);
        DRILL_T4.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.DRILL_T4);
        DRILL_T5.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.DRILL_T5);
        DRILL_T6.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.DRILL_T6);
        BARRACKS_T2.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.BARRACKS_T2);
        BARRACKS_T3.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.BARRACKS_T3);
        BLACKSMITH_T2.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.BLACKSMITH_T2);
        BLACKSMITH_T3.bakeValues(SiegeOfSettlementsCommonConfigs.ResearchCosts.BLACKSMITH_T3);
        // ******************* ABILITIES ******************* //
        ENCHANT_MAIMING.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.ENCHANT_MAIMING);
        ENCHANT_QUICK_CHARGE.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.ENCHANT_QUICK_CHARGE);
        ENCHANT_SHARPNESS.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.ENCHANT_SHARPNESS);
        ENCHANT_MULTISHOT.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.ENCHANT_MULTISHOT);
        ENCHANT_VIGOR.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.ENCHANT_VIGOR);
        EQUIP_LEATHER_ARMOR.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_LEATHER_ARMOR);
        EQUIP_CHAINMAIL_ARMOR.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_CHAINMAIL_ARMOR);
        EQUIP_IRON_ARMOR.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_IRON_ARMOR);
        EQUIP_IRON_SWORD.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_IRON_SWORD);
        EQUIP_IRON_PICKAXE.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_IRON_PICKAXE);
        EQUIP_IRON_AXE.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_IRON_AXE);
        EQUIP_IRON_HOE.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_IRON_HOE);
        EQUIP_DIAMOND_ARMOR.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_DIAMOND_ARMOR);
        EQUIP_DIAMOND_SWORD.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_DIAMOND_SWORD);
        EQUIP_DIAMOND_PICKAXE.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_DIAMOND_PICKAXE);
        EQUIP_DIAMOND_AXE.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_DIAMOND_AXE);
        EQUIP_DIAMOND_HOE.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_DIAMOND_HOE);
        STEAK.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.STEAK);
        EQUIP_BOW.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_BOW);
        EQUIP_ARROWS.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_ARROWS);
        EQUIP_RAVAGER_ARMOR.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.EQUIP_RAVAGER_ARMOR);
        BECOME_TRADER.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.BECOME_TRADER);
        BECOME_FARMER.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.BECOME_FARMER);
        BECOME_LUMBERJACK.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.BECOME_LUMBERJACK);
        BECOME_MINER.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.BECOME_MINER);
        BECOME_MASON.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.BECOME_MASON);
        BECOME_HUNTER.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.BECOME_HUNTER);
        BERSERK.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.BERSERK);
        UPGRADE_TRADER_TO_ELITE.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.UPGRADE_TRADER_TO_ELITE);
        UPGRADE_ARCHER_TO_ELITE.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.UPGRADE_ARCHER_TO_ELITE);
        UPGRADE_ARMORED_VINDICATOR_TO_ELITE.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.UPGRADE_ARMORED_VINDICATOR_TO_ELITE);
        UPGRADE_ARMORED_PILLAGER_TO_ELITE.bakeValues(SiegeOfSettlementsCommonConfigs.AbilityCosts.UPGRADE_ARMORED_PILLAGER_TO_ELITE);
    }
}
