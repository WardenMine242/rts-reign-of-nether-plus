package com.solegendary.siege_of_settlements.config;

import com.WardenMine242.siege_of_settlements.unit.units.villagers.ArmoredRavagerUnit;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import net.minecraftforge.common.ForgeConfigSpec;

/*
    Class responsible for defining all configurable ResourceCosts; this occurs during commonsetup
 */
public class SiegeOfSettlementsCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("Configuration File");
        BUILDER.pop();

        BUILDER.comment("Unit cost configurations");
        //*Units*
        //Monsters
        BUILDER.comment("Monsters");
        UnitCosts.CREEPER.define(BUILDER);
        UnitCosts.ZOMBIE.define(BUILDER);
        UnitCosts.ZOMBIE_VILLAGER.define(BUILDER);
        UnitCosts.SKELETON.define(BUILDER);
        UnitCosts.STRAY.define(BUILDER);
        UnitCosts.HUSK.define(BUILDER);
        UnitCosts.DROWNED.define(BUILDER);
        UnitCosts.SPIDER.define(BUILDER);
        UnitCosts.POISON_SPIDER.define(BUILDER);
        UnitCosts.SLIME.define(BUILDER);
        UnitCosts.WARDEN.define(BUILDER);
        UnitCosts.ZOMBIE_PIGLIN.define(BUILDER);
        UnitCosts.ZOGLIN.define(BUILDER);
        UnitCosts.NECROMANCER.define(BUILDER);
        UnitCosts.WRETCHED_WRAITH.define(BUILDER);
        //Villagers
        BUILDER.comment("Villagers");
        UnitCosts.VILLAGER.define(BUILDER);
        UnitCosts.MILITIA.define(BUILDER);
        UnitCosts.IRON_GOLEM.define(BUILDER);
        UnitCosts.REDSTONE_GOLEM.define(BUILDER);
        UnitCosts.REDSTONE_MINE.define(BUILDER);
        UnitCosts.PILLAGER.define(BUILDER);
        UnitCosts.ARCHER.define(BUILDER);
        UnitCosts.ELITE_ARMORED_PILLAGER.define(BUILDER);
        UnitCosts.ARMORED_PILLAGER.define(BUILDER);
        UnitCosts.VINDICATOR.define(BUILDER);
        UnitCosts.ELITE_VINDICATOR.define(BUILDER);
        UnitCosts.ELITE_ARMORED_VINDICATOR.define(BUILDER);
        UnitCosts.ARMORED_VINDICATOR.define(BUILDER);
        UnitCosts.ELITE_ARCHER.define(BUILDER);
        UnitCosts.WITCH.define(BUILDER);
        UnitCosts.EVOKER.define(BUILDER);
        UnitCosts.ILLUSIONER.define(BUILDER);
        UnitCosts.RAVAGER.define(BUILDER);
        UnitCosts.ROYAL_GUARD.define(BUILDER);
        UnitCosts.ENCHANTER.define(BUILDER);
        UnitCosts.AUROMANCER.define(BUILDER);
        UnitCosts.HORSE.define(BUILDER);
        //Piglins
        BUILDER.comment("Piglins");
        UnitCosts.GRUNT.define(BUILDER);
        UnitCosts.BRUTE.define(BUILDER);
        UnitCosts.HEADHUNTER.define(BUILDER);
        UnitCosts.MARAUDER.define(BUILDER);
        UnitCosts.HOGLIN.define(BUILDER);
        UnitCosts.BLAZE.define(BUILDER);
        UnitCosts.WITHER_SKELETON.define(BUILDER);
        UnitCosts.GHAST.define(BUILDER);
        UnitCosts.MAGMA_CUBE.define(BUILDER);
        UnitCosts.PIGLIN_MERCHANT.define(BUILDER);
        UnitCosts.WILDFIRE.define(BUILDER);
        //Neutral
        UnitCosts.ENDERMAN.define(BUILDER);
        UnitCosts.POLAR_BEAR.define(BUILDER);
        UnitCosts.GRIZZLY_BEAR.define(BUILDER);
        UnitCosts.PANDA.define(BUILDER);
        UnitCosts.WOLF.define(BUILDER);
        UnitCosts.LLAMA.define(BUILDER);

        UnitCosts.HERO_BASE_REVIVE_COST.define(BUILDER);
        UnitCosts.HERO_EXTRA_REVIVE_COST_PER_LEVEL.define(BUILDER);

        BUILDER.comment("Armored Ravager stat configuration");
        ArmoredRavagerStats.define(BUILDER);

        BUILDER.comment("Blacksmith production time configuration");
        BlacksmithProductionTimes.define(BUILDER);

        //*Buildings*
        BUILDER.comment("Building Cost Configurations");
        BuildingCosts.BEACON.define(BUILDER);
        BuildingCosts.STOCKPILE.define(BUILDER);
        BuildingCosts.OAK_BRIDGE.define(BUILDER);
        BuildingCosts.SPRUCE_BRIDGE.define(BUILDER);
        BuildingCosts.BLACKSTONE_BRIDGE.define(BUILDER);
        //Monsters
        BUILDER.comment("Monsters");
        BuildingCosts.MAUSOLEUM.define(BUILDER);
        BuildingCosts.HAUNTED_HOUSE.define(BUILDER);
        BuildingCosts.PUMPKIN_FARM.define(BUILDER);
        BuildingCosts.SCULK_CATALYST.define(BUILDER);
        BuildingCosts.GRAVEYARD.define(BUILDER);
        BuildingCosts.SPIDER_LAIR.define(BUILDER);
        BuildingCosts.DUNGEON.define(BUILDER);
        BuildingCosts.LABORATORY.define(BUILDER);
        BuildingCosts.DARK_WATCHTOWER.define(BUILDER);
        BuildingCosts.SLIME_PIT.define(BUILDER);
        BuildingCosts.STRONGHOLD.define(BUILDER);
        BuildingCosts.ALTAR_OF_DARKNESS.define(BUILDER);
        //Villagers
        BUILDER.comment("Villagers");
        BuildingCosts.TOWN_CENTRE.define(BUILDER);
        BuildingCosts.VILLAGER_HOUSE.define(BUILDER);
        BuildingCosts.WHEAT_FARM.define(BUILDER);
        BuildingCosts.BARRACKS.define(BUILDER);
        BuildingCosts.BLACKSMITH.define(BUILDER);
        BuildingCosts.ARCANE_TOWER.define(BUILDER);
        BuildingCosts.LIBRARY.define(BUILDER);
        BuildingCosts.WATCHTOWER.define(BUILDER);
        BuildingCosts.CASTLE.define(BUILDER);
        BuildingCosts.IRON_GOLEM_BUILDING.define(BUILDER);
        BuildingCosts.SHRINE_OF_PROSPERITY.define(BUILDER);
        BuildingCosts.DRILL.define(BUILDER);
        BuildingCosts.WALL.define(BUILDER);
        BuildingCosts.GATE.define(BUILDER);
        BuildingCosts.CORNER_WALL.define(BUILDER);
        BuildingCosts.RESEARCH_CENTRE.define(BUILDER);
        BuildingCosts.MARKETPLACE.define(BUILDER);
        BuildingCosts.STABLE.define(BUILDER);
        //Piglins
        BUILDER.comment("Piglins");
        BuildingCosts.CENTRAL_PORTAL.define(BUILDER);
        BuildingCosts.BASIC_PORTAL.define(BUILDER);
        BuildingCosts.CIVILIAN_PORTAL.define(BUILDER);
        BuildingCosts.NETHERWART_FARM.define(BUILDER);
        BuildingCosts.BASTION.define(BUILDER);
        BuildingCosts.HOGLIN_STABLES.define(BUILDER);
        BuildingCosts.FLAME_SANCTUARY.define(BUILDER);
        BuildingCosts.WITHER_SHRINE.define(BUILDER);
        BuildingCosts.BASALT_SPRINGS.define(BUILDER);
        BuildingCosts.FORTRESS.define(BUILDER);
        BuildingCosts.INFERNAL_PORTAL.define(BUILDER);

        //*Research*
        BUILDER.comment("Research Cost Configurations");
        ResearchCosts.RESEARCH_GOLEM_SMITHING.define(BUILDER);
        ResearchCosts.RESEARCH_SUPERIOR_BLACKSMITH.define(BUILDER);
        ResearchCosts.RESEARCH_MILITIA_BOWS.define(BUILDER);
        ResearchCosts.RESEARCH_LAB_LIGHTNING_ROD.define(BUILDER);
        ResearchCosts.RESEARCH_RESOURCE_CAPACITY.define(BUILDER);
        ResearchCosts.RESEARCH_SPIDER_JOCKEYS.define(BUILDER);
        ResearchCosts.RESEARCH_SPIDER_WEBS.define(BUILDER);
        ResearchCosts.RESEARCH_POISON_SPIDERS.define(BUILDER);
        ResearchCosts.RESEARCH_HUSKS.define(BUILDER);
        ResearchCosts.RESEARCH_DROWNED.define(BUILDER);
        ResearchCosts.RESEARCH_STRAYS.define(BUILDER);
        ResearchCosts.RESEARCH_SLIME_CONVERSION.define(BUILDER);
        ResearchCosts.RESEARCH_LINGERING_POTIONS.define(BUILDER);
        ResearchCosts.RESEARCH_HEALING_POTIONS.define(BUILDER);
        ResearchCosts.RESEARCH_WATER_POTIONS.define(BUILDER);
        ResearchCosts.RESEARCH_EVOKER_VEXES.define(BUILDER);
        ResearchCosts.RESEARCH_CASTLE_FLAG.define(BUILDER);
        ResearchCosts.RESEARCH_GRAND_LIBRARY.define(BUILDER);
        ResearchCosts.RESEARCH_VILLAGER_HOUSE_T2.define(BUILDER);
        ResearchCosts.ARCANE_TOWER_T2.define(BUILDER);
        ResearchCosts.ARCANE_TOWER_T3.define(BUILDER);
        ResearchCosts.BLACKSMITH_T2.define(BUILDER);
        ResearchCosts.BLACKSMITH_T3.define(BUILDER);
        ResearchCosts.RESEARCH_SILVERFISH.define(BUILDER);
        ResearchCosts.RESEARCH_SCULK_AMPLIFIERS.define(BUILDER);
        ResearchCosts.RESEARCH_RAVAGER_ARTILLERY.define(BUILDER);
        ResearchCosts.RESEARCH_BRUTE_SHIELDS.define(BUILDER);
        ResearchCosts.RESEARCH_HOGLIN_CAVALRY.define(BUILDER);
        ResearchCosts.RESEARCH_HEAVY_TRIDENTS.define(BUILDER);
        ResearchCosts.RESEARCH_CLEAVING_FLAILS.define(BUILDER);
        ResearchCosts.RESEARCH_BLAZE_FIRE_WALL.define(BUILDER);
        ResearchCosts.RESEARCH_FIRE_RESISTANCE.define(BUILDER);
        ResearchCosts.RESEARCH_WITHER_CLOUDS.define(BUILDER);
        ResearchCosts.RESEARCH_BLOODLUST.define(BUILDER);
        ResearchCosts.RESEARCH_ADVANCED_PORTALS.define(BUILDER);
        ResearchCosts.RESEARCH_CIVILIAN_PORTAL.define(BUILDER);
        ResearchCosts.RESEARCH_MILITARY_PORTAL.define(BUILDER);
        ResearchCosts.RESEARCH_TRANSPORT_PORTAL.define(BUILDER);
        ResearchCosts.RESEARCH_CUBE_MAGMA.define(BUILDER);
        ResearchCosts.RESEARCH_SOUL_FIREBALLS.define(BUILDER);
        ResearchCosts.RESEARCH_BEACON_LEVEL1.define(BUILDER);
        ResearchCosts.RESEARCH_BEACON_LEVEL2.define(BUILDER);
        ResearchCosts.RESEARCH_BEACON_LEVEL3.define(BUILDER);
        ResearchCosts.RESEARCH_BEACON_LEVEL4.define(BUILDER);
        ResearchCosts.RESEARCH_BEACON_LEVEL5.define(BUILDER);
        ResearchCosts.TOGGLE_VILLAGER_GATE.define(BUILDER);
        ResearchCosts.DRILL_T2.define(BUILDER);
        ResearchCosts.DRILL_T3.define(BUILDER);
        ResearchCosts.DRILL_T4.define(BUILDER);
        ResearchCosts.DRILL_T5.define(BUILDER);
        ResearchCosts.DRILL_T6.define(BUILDER);
        ResearchCosts.BARRACKS_T2.define(BUILDER);
        ResearchCosts.BARRACKS_T3.define(BUILDER);
        ResearchCosts.RESEARCH_CASTLE.define(BUILDER);
        ResearchCosts.RESEARCH_BLACKSMITH.define(BUILDER);
        ResearchCosts.RESEARCH_FIRE_WALLS.define(BUILDER);
        ResearchCosts.RESEARCH_FIRE_WALLS2.define(BUILDER);
        ResearchCosts.RESEARCH_TOWER_DAMAGE1.define(BUILDER);
        ResearchCosts.RESEARCH_TOWER_DAMAGE2.define(BUILDER);
        ResearchCosts.RESEARCH_BEACON.define(BUILDER);
        ResearchCosts.RESEARCH_TRADER.define(BUILDER);
        ResearchCosts.RESEARCH_MARKETPLACE.define(BUILDER);
        ResearchCosts.RESEARCH_PROFESSIONS.define(BUILDER);
        ResearchCosts.RESEARCH_BLACKSMITHS.define(BUILDER);
        ResearchCosts.RESEARCH_ELITE_TRADER.define(BUILDER);
        ResearchCosts.RESEARCH_ELITE_TRAINING.define(BUILDER);
        ResearchCosts.RESEARCH_UPGRADED_BARRACKS.define(BUILDER);
        ResearchCosts.RESEARCH_UPGRADED_DRILL.define(BUILDER);
        ResearchCosts.RESEARCH_ADVANCED_DRILL.define(BUILDER);
        ResearchCosts.RESEARCH_ADVANCED_BLACKSMITH.define(BUILDER);
        ResearchCosts.RESEARCH_BIG_HOUSE.define(BUILDER);
        ResearchCosts.RESEARCH_POWERFUL_ARCANE_TOWER.define(BUILDER);
        ResearchCosts.RESEARCH_TOWN_CENTRE_T2.define(BUILDER);


        //*Abilities*
        BUILDER.comment("Ability Cost Configurations");
        AbilityCosts.ENCHANT_MAIMING.define(BUILDER);
        AbilityCosts.ENCHANT_QUICK_CHARGE.define(BUILDER);
        AbilityCosts.ENCHANT_SHARPNESS.define(BUILDER);
        AbilityCosts.ENCHANT_MULTISHOT.define(BUILDER);
        AbilityCosts.ENCHANT_VIGOR.define(BUILDER);
        AbilityCosts.EQUIP_LEATHER_ARMOR.define(BUILDER);
        AbilityCosts.EQUIP_CHAINMAIL_ARMOR.define(BUILDER);
        AbilityCosts.EQUIP_IRON_ARMOR.define(BUILDER);
        AbilityCosts.EQUIP_IRON_SWORD.define(BUILDER);
        AbilityCosts.EQUIP_IRON_PICKAXE.define(BUILDER);
        AbilityCosts.EQUIP_IRON_AXE.define(BUILDER);
        AbilityCosts.EQUIP_IRON_HOE.define(BUILDER);
        AbilityCosts.EQUIP_DIAMOND_ARMOR.define(BUILDER);
        AbilityCosts.EQUIP_DIAMOND_SWORD.define(BUILDER);
        AbilityCosts.EQUIP_DIAMOND_PICKAXE.define(BUILDER);
        AbilityCosts.EQUIP_DIAMOND_AXE.define(BUILDER);
        AbilityCosts.EQUIP_DIAMOND_HOE.define(BUILDER);
        AbilityCosts.STEAK.define(BUILDER);
        AbilityCosts.EQUIP_BOW.define(BUILDER);
        AbilityCosts.EQUIP_ARROWS.define(BUILDER);
        AbilityCosts.EQUIP_RAVAGER_ARMOR.define(BUILDER);
        AbilityCosts.BECOME_TRADER.define(BUILDER);
        AbilityCosts.BERSERK.define(BUILDER);
        AbilityCosts.UPGRADE_TRADER_TO_ELITE.define(BUILDER);
        AbilityCosts.UPGRADE_ARCHER_TO_ELITE.define(BUILDER);
        AbilityCosts.UPGRADE_ARMORED_VINDICATOR_TO_ELITE.define(BUILDER);
        AbilityCosts.UPGRADE_ARMORED_PILLAGER_TO_ELITE.define(BUILDER);

        SPEC = BUILDER.build();
    }

    public static class UnitCosts implements Costs {
        //Monsters
        public static final ResourceCostConfigEntry CREEPER = ResourceCostConfigEntry.Unit(50, 0, 100,0, 35, 2, ResourceCosts.CREEPER, "Creeper Config");
        public static final ResourceCostConfigEntry ZOMBIE = ResourceCostConfigEntry.Unit(75, 0, 0,0, 18, 1, ResourceCosts.ZOMBIE, "Zombie Config");
        public static final ResourceCostConfigEntry ZOMBIE_VILLAGER = ResourceCostConfigEntry.Unit(50,0,0,0,15,1, ResourceCosts.ZOMBIE_VILLAGER, "Zombie Villager Config");
        public static final ResourceCostConfigEntry SKELETON = ResourceCostConfigEntry.Unit(50,45,0,0,18,1, ResourceCosts.SKELETON, "Skeleton Config");
        public static final ResourceCostConfigEntry STRAY = ResourceCostConfigEntry.Unit(50,45,0,0,18,1, ResourceCosts.STRAY, "Stray Config");
        public static final ResourceCostConfigEntry HUSK = ResourceCostConfigEntry.Unit(75,0,0,0,18,1, ResourceCosts.HUSK, "Husk Config");
        public static final ResourceCostConfigEntry DROWNED = ResourceCostConfigEntry.Unit(75,0,0,0,18,1, ResourceCosts.DROWNED, "Drowned Config");
        public static final ResourceCostConfigEntry SPIDER = ResourceCostConfigEntry.Unit(90,25,25,0,25,2, ResourceCosts.SPIDER, "Spider Config");
        public static final ResourceCostConfigEntry POISON_SPIDER = ResourceCostConfigEntry.Unit(90,25,25,0,25,2, ResourceCosts.POISON_SPIDER, "Poison Spider Config");
        public static final ResourceCostConfigEntry SLIME = ResourceCostConfigEntry.Unit(40,40,40,0,25,2, ResourceCosts.SLIME, "Slime Config");
        public static final ResourceCostConfigEntry WARDEN = ResourceCostConfigEntry.Unit(275,0,125,0,50,5, ResourceCosts.WARDEN, "Warden Config");
        public static final ResourceCostConfigEntry NECROMANCER = ResourceCostConfigEntry.Unit(0,0,0,0,30, 5, ResourceCosts.NECROMANCER, "Necromancer Config");
        public static final ResourceCostConfigEntry WRETCHED_WRAITH = ResourceCostConfigEntry.Unit(0,0,0,0,30, 5, ResourceCosts.WRETCHED_WRAITH, "Wretched Wraith Config");

        public static final ResourceCostConfigEntry ZOMBIE_PIGLIN = ResourceCostConfigEntry.Unit(0,0,0,0,10,1, ResourceCosts.ZOMBIE_PIGLIN, "Zombie Piglin Config");
        public static final ResourceCostConfigEntry ZOGLIN = ResourceCostConfigEntry.Unit(0,0,0,0,10,2, ResourceCosts.ZOGLIN, "Zoglin Config");

        // Villagers
        public static final ResourceCostConfigEntry VILLAGER = ResourceCostConfigEntry.Unit(75,0,0,0,15,1, ResourceCosts.VILLAGER, "Villager Config");
        public static final ResourceCostConfigEntry MILITIA = ResourceCostConfigEntry.Unit(75,0,0,0,15,1, ResourceCosts.MILITIA, "Militia Config");
        public static final ResourceCostConfigEntry IRON_GOLEM = ResourceCostConfigEntry.Unit(0,50,350,0,45,4, ResourceCosts.IRON_GOLEM, "Iron Golem Config");
        public static final ResourceCostConfigEntry REDSTONE_GOLEM = ResourceCostConfigEntry.Unit(700,50,1500,300,45,8, ResourceCosts.REDSTONE_GOLEM, "Redstone Golem Config");
        public static final ResourceCostConfigEntry REDSTONE_MINE = ResourceCostConfigEntry.Unit(0,0,25,0,8,1, ResourceCosts.REDSTONE_MINE, "Redstone Mine Config");
        public static final ResourceCostConfigEntry PILLAGER = ResourceCostConfigEntry.Unit(120,80,0,0,32,3, ResourceCosts.PILLAGER, "Pillager Config");
        public static final ResourceCostConfigEntry ARCHER = ResourceCostConfigEntry.Unit(120,80,0,0,32,3, ResourceCosts.ARCHER, "Archer Config");
        public static final ResourceCostConfigEntry ELITE_ARMORED_PILLAGER = ResourceCostConfigEntry.Unit(400,120,700,300,36,3, ResourceCosts.ELITE_ARMORED_PILLAGER, "Elite Armored Pillager Config");
        public static final ResourceCostConfigEntry ARMORED_PILLAGER = ResourceCostConfigEntry.Unit(400,150,400,200,40,4, ResourceCosts.ARMORED_PILLAGER, "Armored Pillager Config");
        public static final ResourceCostConfigEntry VINDICATOR = ResourceCostConfigEntry.Unit(170,0,0,0,32,3, ResourceCosts.VINDICATOR, "Vindicator Config");
        public static final ResourceCostConfigEntry ELITE_VINDICATOR = ResourceCostConfigEntry.Unit(200, 0, 0, 300, 100, 3, ResourceCosts.ELITE_VINDICATOR, "Elite Vindicator Config");
        public static final ResourceCostConfigEntry ELITE_ARMORED_VINDICATOR = ResourceCostConfigEntry.Unit(300, 0, 0, 0, 1, 0, ResourceCosts.ELITE_ARMORED_VINDICATOR, "Elite Armored Vindicator Config");
        public static final ResourceCostConfigEntry ARMORED_VINDICATOR = ResourceCostConfigEntry.Unit(300, 0, 450, 150, 45, 4, ResourceCosts.ARMORED_VINDICATOR, "Armored Vindicator Config");

        public static final ResourceCostConfigEntry ELITE_ARCHER = ResourceCostConfigEntry.Unit(0,0,0,1000,120,1, ResourceCosts.ELITE_ARCHER, "Elite Archer Config");
        public static final ResourceCostConfigEntry WITCH = ResourceCostConfigEntry.Unit(100,90,90,0,35,3, ResourceCosts.WITCH, "Witch Config");
        public static final ResourceCostConfigEntry EVOKER = ResourceCostConfigEntry.Unit(300,0,400,0,35,3, ResourceCosts.EVOKER, "Evoker Config");
        public static final ResourceCostConfigEntry ILLUSIONER = ResourceCostConfigEntry.Unit(1000,1000,300,0,30,3, ResourceCosts.ILLUSIONER, "Illusioner Config");
        public static final ResourceCostConfigEntry RAVAGER = ResourceCostConfigEntry.Unit(600,200,250,0,60,7, ResourceCosts.RAVAGER, "Ravager Config");
        public static final ResourceCostConfigEntry ROYAL_GUARD = ResourceCostConfigEntry.Unit(200,500,600,0,30, 5, ResourceCosts.ROYAL_GUARD, "Royal Guard Config");
        public static final ResourceCostConfigEntry ENCHANTER = ResourceCostConfigEntry.Unit(0,0,0,0,30, 5, ResourceCosts.ENCHANTER, "Enchanter Config");
        public static final ResourceCostConfigEntry AUROMANCER = ResourceCostConfigEntry.Unit(400,600,400,80,120, 1, ResourceCosts.AUROMANCER, "Auromancer Config");
        public static final ResourceCostConfigEntry HORSE = ResourceCostConfigEntry.Unit(300,100,50,0,60, 1, ResourceCosts.HORSE, "Horse Config");

        // Piglins
        public static final ResourceCostConfigEntry GRUNT = ResourceCostConfigEntry.Unit(50,0,0,0,15,1, ResourceCosts.GRUNT, "Grunt Config");
        public static final ResourceCostConfigEntry BRUTE = ResourceCostConfigEntry.Unit(120,0,0,0,25,2, ResourceCosts.BRUTE, "Brute Config");
        public static final ResourceCostConfigEntry HEADHUNTER = ResourceCostConfigEntry.Unit(90,60,0,0,25,2, ResourceCosts.HEADHUNTER, "Headhunter Config");
        public static final ResourceCostConfigEntry MARAUDER = ResourceCostConfigEntry.Unit(175,50,50,0,40,4, ResourceCosts.MARAUDER, "Marauder Config");
        public static final ResourceCostConfigEntry HOGLIN = ResourceCostConfigEntry.Unit(140,0,70,0,35,3, ResourceCosts.HOGLIN, "Hoglin Config");
        public static final ResourceCostConfigEntry BLAZE = ResourceCostConfigEntry.Unit(40,40,100,0,30,2, ResourceCosts.BLAZE, "Blaze Config");
        public static final ResourceCostConfigEntry WITHER_SKELETON = ResourceCostConfigEntry.Unit(200,0,125,0,40,4, ResourceCosts.WITHER_SKELETON, "Wither Skeleton Config");
        public static final ResourceCostConfigEntry GHAST = ResourceCostConfigEntry.Unit(100,100,250,0,60,6, ResourceCosts.GHAST, "Ghast Config");
        public static final ResourceCostConfigEntry MAGMA_CUBE = ResourceCostConfigEntry.Unit(40,40,40,0,25,2, ResourceCosts.MAGMA_CUBE, "Magma Cube Config");
        public static final ResourceCostConfigEntry PIGLIN_MERCHANT = ResourceCostConfigEntry.Unit(0,0,0,0,30, 5, ResourceCosts.PIGLIN_MERCHANT, "Piglin Merchant Config");
        public static final ResourceCostConfigEntry WILDFIRE = ResourceCostConfigEntry.Unit(0,0,0,0,30, 5, ResourceCosts.WILDFIRE, "Wildfire Config");

        public static final ResourceCostConfigEntry ENDERMAN = ResourceCostConfigEntry.Unit(75,75,75,0,35,3, ResourceCosts.ENDERMAN, "Enderman Config");
        public static final ResourceCostConfigEntry POLAR_BEAR = ResourceCostConfigEntry.Unit(250,0,0,0,40,4, ResourceCosts.POLAR_BEAR, "Polar Bear Config");
        public static final ResourceCostConfigEntry GRIZZLY_BEAR = ResourceCostConfigEntry.Unit(250,0,0,0,40,4, ResourceCosts.GRIZZLY_BEAR, "Grizzly Bear Config");
        public static final ResourceCostConfigEntry PANDA = ResourceCostConfigEntry.Unit(250,0,0,0,40,4, ResourceCosts.PANDA, "Panda Config");
        public static final ResourceCostConfigEntry WOLF = ResourceCostConfigEntry.Unit(120,0,0,0,25,2, ResourceCosts.WOLF, "Wolf Config");
        public static final ResourceCostConfigEntry LLAMA = ResourceCostConfigEntry.Unit(180,0,0,0,25,2, ResourceCosts.LLAMA, "Llama Config");

        public static final ResourceCostConfigEntry HERO_BASE_REVIVE_COST = ResourceCostConfigEntry.Unit(400,300,200,0,30, 5, ResourceCosts.HERO_BASE_REVIVE_COST, "Hero Base Revive Cost Config");
        public static final ResourceCostConfigEntry HERO_EXTRA_REVIVE_COST_PER_LEVEL = ResourceCostConfigEntry.Unit(50,30,50,0,5, 0, ResourceCosts.HERO_EXTRA_REVIVE_COST_PER_LEVEL, "Hero Extra Revive Cost Per Level Config");
    }

    public static class ArmoredRavagerStats {
        public static ForgeConfigSpec.DoubleValue ATTACK_DAMAGE;
        public static ForgeConfigSpec.DoubleValue MAX_HEALTH;
        public static ForgeConfigSpec.DoubleValue ARMOR;
        public static ForgeConfigSpec.DoubleValue MOVEMENT_SPEED;
        public static ForgeConfigSpec.DoubleValue ATTACKS_PER_SECOND;
        public static ForgeConfigSpec.DoubleValue ATTACK_RANGE;
        public static ForgeConfigSpec.DoubleValue AGGRO_RANGE;
        public static ForgeConfigSpec.DoubleValue ATTACK_KNOCKBACK;
        public static ForgeConfigSpec.DoubleValue KNOCKBACK_RESISTANCE;

        public static void define(ForgeConfigSpec.Builder builder) {
            builder.push("ArmoredRavagerStats");
            ATTACK_DAMAGE = builder
                    .comment("Base melee damage of the Armored Ravager.")
                    .defineInRange("attack_damage", (double) ArmoredRavagerUnit.DEFAULT_ATTACK_DAMAGE, 0.0D, 2048.0D);
            MAX_HEALTH = builder
                    .comment("Base maximum health of the Armored Ravager.")
                    .defineInRange("max_health", (double) ArmoredRavagerUnit.DEFAULT_MAX_HEALTH, 1.0D, 4096.0D);
            ARMOR = builder
                    .comment("Base armor value of the Armored Ravager.")
                    .defineInRange("armor", (double) ArmoredRavagerUnit.DEFAULT_ARMOR, 0.0D, 200.0D);
            MOVEMENT_SPEED = builder
                    .comment("Movement speed of the Armored Ravager. HUD speed is this value multiplied by 100.")
                    .defineInRange("movement_speed", (double) ArmoredRavagerUnit.DEFAULT_MOVEMENT_SPEED, 0.0D, 4.0D);
            ATTACKS_PER_SECOND = builder
                    .comment("Attack speed of the Armored Ravager in attacks per second.")
                    .defineInRange("attacks_per_second", (double) ArmoredRavagerUnit.DEFAULT_ATTACKS_PER_SECOND, 0.01D, 20.0D);
            ATTACK_RANGE = builder
                    .comment("Melee attack range used by unit logic.")
                    .defineInRange("attack_range", (double) ArmoredRavagerUnit.DEFAULT_ATTACK_RANGE, 0.0D, 64.0D);
            AGGRO_RANGE = builder
                    .comment("Aggro range used when idle/retaliating.")
                    .defineInRange("aggro_range", (double) ArmoredRavagerUnit.DEFAULT_AGGRO_RANGE, 0.0D, 128.0D);
            ATTACK_KNOCKBACK = builder
                    .comment("Attack knockback of the Armored Ravager.")
                    .defineInRange("attack_knockback", ArmoredRavagerUnit.DEFAULT_ATTACK_KNOCKBACK, 0.0D, 32.0D);
            KNOCKBACK_RESISTANCE = builder
                    .comment("Knockback resistance of the Armored Ravager.")
                    .defineInRange("knockback_resistance", ArmoredRavagerUnit.DEFAULT_KNOCKBACK_RESISTANCE, 0.0D, 1.0D);
            builder.pop();
        }
    }

    public static class BlacksmithProductionTimes {
        public static ForgeConfigSpec.IntValue TIME_1_BLACKSMITH;
        public static ForgeConfigSpec.IntValue TIME_2_BLACKSMITHS;
        public static ForgeConfigSpec.IntValue TIME_3_BLACKSMITHS;

        public static void define(ForgeConfigSpec.Builder builder) {
            builder.push("BlacksmithProductionTimes");
            TIME_1_BLACKSMITH = builder
                    .comment("Production time in seconds with 1 blacksmith.")
                    .defineInRange("time_1_blacksmith", 60, 1, 3600);
            TIME_2_BLACKSMITHS = builder
                    .comment("Production time in seconds with 2 blacksmiths.")
                    .defineInRange("time_2_blacksmiths", 40, 1, 3600);
            TIME_3_BLACKSMITHS = builder
                    .comment("Production time in seconds with 3 blacksmiths.")
                    .defineInRange("time_3_blacksmiths", 20, 1, 3600);
            builder.pop();
        }
    }

    public static class BuildingCosts implements Costs {
        public static final ResourceCostConfigEntry BEACON = ResourceCostConfigEntry.Building(0,4000,5000,0, 0, ResourceCosts.BEACON, "Beacon Config");

        public static final ResourceCostConfigEntry STOCKPILE = ResourceCostConfigEntry.Building(0,75,0,0, 0, ResourceCosts.STOCKPILE, "Stockpile Config");
        public static final ResourceCostConfigEntry OAK_BRIDGE = ResourceCostConfigEntry.Building(0,100,0,0, 0, ResourceCosts.OAK_BRIDGE, "Oak Bridge Config");
        public static final ResourceCostConfigEntry SPRUCE_BRIDGE = ResourceCostConfigEntry.Building(0,100,0,0, 0, ResourceCosts.SPRUCE_BRIDGE, "Spruce Bridge Config");
        public static final ResourceCostConfigEntry BLACKSTONE_BRIDGE = ResourceCostConfigEntry.Building(0,0,100,0, 0, ResourceCosts.BLACKSTONE_BRIDGE, "Blackstone Bridge Config");

        // Monsters
        public static final ResourceCostConfigEntry MAUSOLEUM = ResourceCostConfigEntry.Building(0,350,250,0, 10, ResourceCosts.MAUSOLEUM, "Mausoleum Config");
        public static final ResourceCostConfigEntry HAUNTED_HOUSE = ResourceCostConfigEntry.Building(0,100,0,0, 10, ResourceCosts.HAUNTED_HOUSE, "Haunted House Config");
        public static final ResourceCostConfigEntry PUMPKIN_FARM = ResourceCostConfigEntry.Building(0,200,0,0, 0, ResourceCosts.PUMPKIN_FARM, "Pumpkin Farm Config");
        public static final ResourceCostConfigEntry SCULK_CATALYST = ResourceCostConfigEntry.Building(0,125,0,0, 0, ResourceCosts.SCULK_CATALYST, "Sculk Catalyst Config");
        public static final ResourceCostConfigEntry GRAVEYARD = ResourceCostConfigEntry.Building(0,150,0,0, 0, ResourceCosts.GRAVEYARD, "Graveyard Config");
        public static final ResourceCostConfigEntry SPIDER_LAIR = ResourceCostConfigEntry.Building(0,150,75,0, 0, ResourceCosts.SPIDER_LAIR, "Spider Lair Config");
        public static final ResourceCostConfigEntry DUNGEON = ResourceCostConfigEntry.Building(0,150,75,0, 0, ResourceCosts.DUNGEON, "Dungeon Config");
        public static final ResourceCostConfigEntry LABORATORY = ResourceCostConfigEntry.Building(0,250,150,0, 0, ResourceCosts.LABORATORY, "Laboratory Config");
        public static final ResourceCostConfigEntry DARK_WATCHTOWER = ResourceCostConfigEntry.Building(0,100,75,0, 0, ResourceCosts.DARK_WATCHTOWER, "Dark Watchtower Config");
        public static final ResourceCostConfigEntry SLIME_PIT = ResourceCostConfigEntry.Building(0,175, 125,0, 0, ResourceCosts.SLIME_PIT, "Slime Pit Config");
        public static final ResourceCostConfigEntry STRONGHOLD = ResourceCostConfigEntry.Building(0,400,300,0, 0, ResourceCosts.STRONGHOLD, "Stronghold Config");
        public static final ResourceCostConfigEntry ALTAR_OF_DARKNESS = ResourceCostConfigEntry.Building(0,125,50,0, 0, ResourceCosts.ALTAR_OF_DARKNESS, "Altar of Darkness Config");

        // Villagers
        public static final ResourceCostConfigEntry TOWN_CENTRE = ResourceCostConfigEntry.Building(0,350,250,0, 10, ResourceCosts.TOWN_CENTRE, "Town Centre Config");
        public static final ResourceCostConfigEntry VILLAGER_HOUSE = ResourceCostConfigEntry.Building(0,500,200,0, 10, ResourceCosts.VILLAGER_HOUSE, "Villager House Config");
        public static final ResourceCostConfigEntry WHEAT_FARM = ResourceCostConfigEntry.Building(0,300,50,0, 0, ResourceCosts.WHEAT_FARM, "Wheat Farm Config");
        public static final ResourceCostConfigEntry BARRACKS = ResourceCostConfigEntry.Building(0,800,600,0, 0, ResourceCosts.BARRACKS, "Barracks Config");
        public static final ResourceCostConfigEntry BLACKSMITH = ResourceCostConfigEntry.Building(0,900,800,0, 0, ResourceCosts.BLACKSMITH, "Blacksmith Config");
        public static final ResourceCostConfigEntry ARCANE_TOWER = ResourceCostConfigEntry.Building(0,900,1000,0, 0, ResourceCosts.ARCANE_TOWER, "Arcane Tower Config");
        public static final ResourceCostConfigEntry LIBRARY = ResourceCostConfigEntry.Building(0,500,600,0, 0, ResourceCosts.LIBRARY, "Library Config");
        public static final ResourceCostConfigEntry WATCHTOWER = ResourceCostConfigEntry.Building(0,200,275,0, 0, ResourceCosts.WATCHTOWER, "Watchtower Config");
        public static final ResourceCostConfigEntry CASTLE = ResourceCostConfigEntry.Building(1000,1100,1100,100, 0, ResourceCosts.CASTLE, "Castle Config");
        public static final ResourceCostConfigEntry IRON_GOLEM_BUILDING = ResourceCostConfigEntry.Building(0,50,250,0, 0, ResourceCosts.IRON_GOLEM_BUILDING, "Iron Golem Building Config");
        public static final ResourceCostConfigEntry SHRINE_OF_PROSPERITY = ResourceCostConfigEntry.Building(0,900,450,0, 0, ResourceCosts.SHRINE_OF_PROSPERITY, "Shrine of Prosperity Config");
        public static final ResourceCostConfigEntry DRILL = ResourceCostConfigEntry.Building(0,900,800,0, 0, ResourceCosts.DRILL, "Drill Config");
        public static final ResourceCostConfigEntry WALL = ResourceCostConfigEntry.Building(0,275,300,0, 0, ResourceCosts.WALL, "Wall Config");
        public static final ResourceCostConfigEntry GATE = ResourceCostConfigEntry.Building(0,275,275,0, 0, ResourceCosts.GATE, "Gate Config");
        public static final ResourceCostConfigEntry CORNER_WALL = ResourceCostConfigEntry.Building(0,325,350,0, 0, ResourceCosts.CORNER_WALL, "Corner Wall Config");
        public static final ResourceCostConfigEntry RESEARCH_CENTRE = ResourceCostConfigEntry.Building(0,2000,1000,0, 0, ResourceCosts.RESEARCH_CENTRE, "Research Centre Config");
        public static final ResourceCostConfigEntry MARKETPLACE = ResourceCostConfigEntry.Building(0,2000,2000,0, 0, ResourceCosts.MARKETPLACE, "Marketplace Config");
        public static final ResourceCostConfigEntry STABLE = ResourceCostConfigEntry.Building(0,500,400,0, 0, ResourceCosts.STABLE, "Stable Config");


        // Piglins
        public static final ResourceCostConfigEntry CENTRAL_PORTAL = ResourceCostConfigEntry.Building(0,350,250,0, 10, ResourceCosts.CENTRAL_PORTAL, "Central Portal Config");
        public static final ResourceCostConfigEntry BASIC_PORTAL = ResourceCostConfigEntry.Building(0, 75, 0,0, 0, ResourceCosts.BASIC_PORTAL, "Basic Portal Config");
        public static final ResourceCostConfigEntry CIVILIAN_PORTAL = ResourceCostConfigEntry.Building(0, 75, 0,0, 15, ResourceCosts.CIVILIAN_PORTAL, "Civilian Portal Config");
        public static final ResourceCostConfigEntry NETHERWART_FARM = ResourceCostConfigEntry.Building(0, 150, 0,0, 0, ResourceCosts.NETHERWART_FARM, "Netherwart Farm Config");
        public static final ResourceCostConfigEntry BASTION = ResourceCostConfigEntry.Building(0, 175, 150,0, 0, ResourceCosts.BASTION, "Bastion Config");
        public static final ResourceCostConfigEntry HOGLIN_STABLES = ResourceCostConfigEntry.Building(0, 150, 50,0, 0, ResourceCosts.HOGLIN_STABLES, "Hoglin Stables Config");
        public static final ResourceCostConfigEntry FLAME_SANCTUARY = ResourceCostConfigEntry.Building(0, 150, 200,0, 0, ResourceCosts.FLAME_SANCTUARY, "Flame Sanctuary Config");
        public static final ResourceCostConfigEntry WITHER_SHRINE = ResourceCostConfigEntry.Building(0, 150, 200,0, 0, ResourceCosts.WITHER_SHRINE, "Wither Shrine Config");
        public static final ResourceCostConfigEntry BASALT_SPRINGS = ResourceCostConfigEntry.Building(0, 150, 200,0, 0, ResourceCosts.BASALT_SPRINGS, "Basalt Springs Config");
        public static final ResourceCostConfigEntry FORTRESS = ResourceCostConfigEntry.Building(0, 400, 300,0, 0, ResourceCosts.FORTRESS, "Fortress Config");
        public static final ResourceCostConfigEntry INFERNAL_PORTAL = ResourceCostConfigEntry.Building(0,125,50,0, 0, ResourceCosts.INFERNAL_PORTAL, "Infernal Portal Config");
    }
    public static class ResearchCosts implements Costs {
        public static final ResourceCostConfigEntry RESEARCH_GOLEM_SMITHING = ResourceCostConfigEntry.Research(0, 0,0,200, 0, ResourceCosts.RESEARCH_GOLEM_SMITHING, "Golem Smithing Research Config");
        public static final ResourceCostConfigEntry RESEARCH_SUPERIOR_BLACKSMITH = ResourceCostConfigEntry.Research(0, 0,0,0, 0, ResourceCosts.RESEARCH_SUPERIOR_BLACKSMITH, "Superior Blacksmith Research Config");
        public static final ResourceCostConfigEntry RESEARCH_MILITIA_BOWS = ResourceCostConfigEntry.Research(0,0,0,150, 200, ResourceCosts.RESEARCH_MILITIA_BOWS, "Militia Bows Research Config");
        public static final ResourceCostConfigEntry RESEARCH_LAB_LIGHTNING_ROD = ResourceCostConfigEntry.Research(0,0,0,0, 0, ResourceCosts.RESEARCH_LAB_LIGHTNING_ROD, "Lightning Lab Research Config");
        public static final ResourceCostConfigEntry RESEARCH_RESOURCE_CAPACITY = ResourceCostConfigEntry.Research(0,0,0,200, 0, ResourceCosts.RESEARCH_RESOURCE_CAPACITY, "Stockpile Resource Capacity Research Config");
        public static final ResourceCostConfigEntry RESEARCH_SPIDER_JOCKEYS = ResourceCostConfigEntry.Research(0,0,0,0, 0, ResourceCosts.RESEARCH_SPIDER_JOCKEYS, "Spider Jockey Research Config");
        public static final ResourceCostConfigEntry RESEARCH_SPIDER_WEBS = ResourceCostConfigEntry.Research(0,0,0,0, 0, ResourceCosts.RESEARCH_SPIDER_WEBS, "Spider Webs Research Config");
        public static final ResourceCostConfigEntry RESEARCH_POISON_SPIDERS = ResourceCostConfigEntry.Research(0,0,0,0, 0, ResourceCosts.RESEARCH_POISON_SPIDERS, "Poison Spider Research Config");
        public static final ResourceCostConfigEntry RESEARCH_HUSKS = ResourceCostConfigEntry.Research(0,0,0,0, 0, ResourceCosts.RESEARCH_HUSKS, "Husk Research Config");
        public static final ResourceCostConfigEntry RESEARCH_DROWNED = ResourceCostConfigEntry.Research(0,0,0,0, 0, ResourceCosts.RESEARCH_DROWNED, "Drowned Research Config");
        public static final ResourceCostConfigEntry RESEARCH_STRAYS = ResourceCostConfigEntry.Research(0,0,0,0, 0, ResourceCosts.RESEARCH_STRAYS, "Stray Research Config");
        public static final ResourceCostConfigEntry RESEARCH_SLIME_CONVERSION = ResourceCostConfigEntry.Research(0,0,0,0, 0, ResourceCosts.RESEARCH_SLIME_CONVERSION, "Slime Conversion Research Config");
        public static final ResourceCostConfigEntry RESEARCH_LINGERING_POTIONS = ResourceCostConfigEntry.Research(0,0,0,150, 0, ResourceCosts.RESEARCH_LINGERING_POTIONS, "Extended Lingering Potion Research Config");
        public static final ResourceCostConfigEntry RESEARCH_HEALING_POTIONS = ResourceCostConfigEntry.Research(0,0,0,200, 0, ResourceCosts.RESEARCH_HEALING_POTIONS, "Healing Potion Research Config");
        public static final ResourceCostConfigEntry RESEARCH_WATER_POTIONS = ResourceCostConfigEntry.Research(0,0,0,100, 0, ResourceCosts.RESEARCH_WATER_POTIONS, "Water Potion Research Config");
        public static final ResourceCostConfigEntry RESEARCH_EVOKER_VEXES = ResourceCostConfigEntry.Research(0,0,0,200, 0, ResourceCosts.RESEARCH_EVOKER_VEXES, "Evoker Vex Research Config");
        public static final ResourceCostConfigEntry RESEARCH_CASTLE_FLAG = ResourceCostConfigEntry.Research(0,2000,1500,200, 120, ResourceCosts.RESEARCH_CASTLE_FLAG, "Captain Banner Research Config");
        public static final ResourceCostConfigEntry RESEARCH_GRAND_LIBRARY = ResourceCostConfigEntry.Research(0,0,300,0, 120, ResourceCosts.RESEARCH_GRAND_LIBRARY, "Grand Library Research Config");
        public static final ResourceCostConfigEntry RESEARCH_VILLAGER_HOUSE_T2 = ResourceCostConfigEntry.Research(0,500,200,0, 50, ResourceCosts.RESEARCH_VILLAGER_HOUSE_T2, "Villager House Tier 2 Research Config");
        public static final ResourceCostConfigEntry ARCANE_TOWER_T2 = ResourceCostConfigEntry.Research(0,1200,900,0, 120, ResourceCosts.ARCANE_TOWER_T2, "Arcane Tower Tier 2 Config");
        public static final ResourceCostConfigEntry ARCANE_TOWER_T3 = ResourceCostConfigEntry.Research(0,2200,1800,0, 120, ResourceCosts.ARCANE_TOWER_T3, "Arcane Tower Tier 3 Config");
        public static final ResourceCostConfigEntry BLACKSMITH_T2 = ResourceCostConfigEntry.Research(0,1200,900,0, 120, ResourceCosts.BLACKSMITH_T2, "Blacksmith Tier 2 Config");
        public static final ResourceCostConfigEntry BLACKSMITH_T3 = ResourceCostConfigEntry.Research(0,2200,1800,0, 120, ResourceCosts.BLACKSMITH_T3, "Blacksmith Tier 3 Config");
        public static final ResourceCostConfigEntry RESEARCH_SILVERFISH = ResourceCostConfigEntry.Research(0,0,0,0, 0, ResourceCosts.RESEARCH_SILVERFISH, "Silverfish Research Config");
        public static final ResourceCostConfigEntry RESEARCH_SCULK_AMPLIFIERS = ResourceCostConfigEntry.Research(0,0,0,0, 0, ResourceCosts.RESEARCH_SCULK_AMPLIFIERS, "Sculk Amplifier Research Config");
        public static final ResourceCostConfigEntry RESEARCH_RAVAGER_ARTILLERY = ResourceCostConfigEntry.Research(0,0,0,350, 0, ResourceCosts.RESEARCH_RAVAGER_ARTILLERY, "Ravager Artillery Research Config");
        public static final ResourceCostConfigEntry RESEARCH_BRUTE_SHIELDS = ResourceCostConfigEntry.Research(0,0,0,0, 0, ResourceCosts.RESEARCH_BRUTE_SHIELDS, "Brute Shields Research Config");
        public static final ResourceCostConfigEntry RESEARCH_HOGLIN_CAVALRY = ResourceCostConfigEntry.Research(0,0,0,0, 0, ResourceCosts.RESEARCH_HOGLIN_CAVALRY, "Hoglin Cavalry Research Config");
        public static final ResourceCostConfigEntry RESEARCH_HEAVY_TRIDENTS = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_HEAVY_TRIDENTS, "Heavy Tridents Research Config");
        public static final ResourceCostConfigEntry RESEARCH_CLEAVING_FLAILS = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_CLEAVING_FLAILS, "Cleaving Flails Research Config");
        public static final ResourceCostConfigEntry RESEARCH_BLAZE_FIRE_WALL = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_BLAZE_FIRE_WALL, "Blaze Fire Wall Research Config");
        public static final ResourceCostConfigEntry RESEARCH_FIRE_RESISTANCE = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_FIRE_RESISTANCE, "Fire Resistance Research Config");
        public static final ResourceCostConfigEntry RESEARCH_WITHER_CLOUDS = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_WITHER_CLOUDS, "Wither Clouds Research Config");
        public static final ResourceCostConfigEntry RESEARCH_BLOODLUST = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_BLOODLUST, "Bloodlust Research Config");
        public static final ResourceCostConfigEntry RESEARCH_ADVANCED_PORTALS = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_ADVANCED_PORTALS, "Advanced Portals Research Config");
        public static final ResourceCostConfigEntry RESEARCH_CIVILIAN_PORTAL = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_CIVILIAN_PORTAL, "Civilian Portal Research Config");
        public static final ResourceCostConfigEntry RESEARCH_MILITARY_PORTAL = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_MILITARY_PORTAL, "Military Portal Research Config");
        public static final ResourceCostConfigEntry RESEARCH_TRANSPORT_PORTAL = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_TRANSPORT_PORTAL, "Transport Portal Research Config");
        public static final ResourceCostConfigEntry RESEARCH_CUBE_MAGMA = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_CUBE_MAGMA, "Cube Magma Research Config");
        public static final ResourceCostConfigEntry RESEARCH_SOUL_FIREBALLS = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_SOUL_FIREBALLS, "Soul Fireballs Research Config");

        public static final ResourceCostConfigEntry RESEARCH_BEACON_LEVEL1 = ResourceCostConfigEntry.Research(0, 0, 1000,0, 0, ResourceCosts.RESEARCH_BEACON_LEVEL1, "Beacon Level 1 Research Config");
        public static final ResourceCostConfigEntry RESEARCH_BEACON_LEVEL2 = ResourceCostConfigEntry.Research(0, 0, 1000,0, 0, ResourceCosts.RESEARCH_BEACON_LEVEL2, "Beacon Level 2 Research Config");
        public static final ResourceCostConfigEntry RESEARCH_BEACON_LEVEL3 = ResourceCostConfigEntry.Research(0, 0, 1000,0, 0, ResourceCosts.RESEARCH_BEACON_LEVEL3, "Beacon Level 3 Research Config");
        public static final ResourceCostConfigEntry RESEARCH_BEACON_LEVEL4 = ResourceCostConfigEntry.Research(0, 0, 1000,0, 0, ResourceCosts.RESEARCH_BEACON_LEVEL4, "Beacon Level 4 Research Config");
        public static final ResourceCostConfigEntry RESEARCH_BEACON_LEVEL5 = ResourceCostConfigEntry.Research(0, 0, 1500,0, 0, ResourceCosts.RESEARCH_BEACON_LEVEL5, "Beacon Level 5 Research Config");

        public static final ResourceCostConfigEntry TOGGLE_VILLAGER_GATE = ResourceCostConfigEntry.Research(0, 0, 0,0, 1, ResourceCosts.TOGGLE_VILLAGER_GATE, "Gate close/open Config");
        public static final ResourceCostConfigEntry DRILL_T2 = ResourceCostConfigEntry.Research(0, 900, 800,0, 80, ResourceCosts.DRILL_T2, "Drill Tier 2 Config");
        public static final ResourceCostConfigEntry DRILL_T3 = ResourceCostConfigEntry.Research(0, 900, 800,0, 80, ResourceCosts.DRILL_T3, "Drill Tier 3 Config");
        public static final ResourceCostConfigEntry DRILL_T4 = ResourceCostConfigEntry.Research(0, 900, 800,0, 80, ResourceCosts.DRILL_T4, "Drill Tier 4 Config");
        public static final ResourceCostConfigEntry DRILL_T5 = ResourceCostConfigEntry.Research(0, 1200, 1000,0, 120, ResourceCosts.DRILL_T5, "Drill Tier 5 Config");
        public static final ResourceCostConfigEntry DRILL_T6 = ResourceCostConfigEntry.Research(0, 1200, 1000,0, 120, ResourceCosts.DRILL_T6, "Drill Tier 6 Config");
        public static final ResourceCostConfigEntry BARRACKS_T2 = ResourceCostConfigEntry.Research(0, 1200, 900,0, 120, ResourceCosts.BARRACKS_T2, "Barracks Tier 2 Config");
        public static final ResourceCostConfigEntry BARRACKS_T3 = ResourceCostConfigEntry.Research(0, 2200, 1800,0, 120, ResourceCosts.BARRACKS_T3, "Barracks Tier 3 Config");
        public static final ResourceCostConfigEntry RESEARCH_CASTLE = ResourceCostConfigEntry.Research(0, 0, 0,500, 0, ResourceCosts.RESEARCH_CASTLE, "Research Castle Config");
        public static final ResourceCostConfigEntry RESEARCH_BLACKSMITH = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_BLACKSMITH, "Research Blacksmith Config");
        public static final ResourceCostConfigEntry RESEARCH_FIRE_WALLS = ResourceCostConfigEntry.Research(0, 0, 0,450, 0, ResourceCosts.RESEARCH_FIRE_WALLS, "Research Fire Walls Config");
        public static final ResourceCostConfigEntry RESEARCH_FIRE_WALLS2 = ResourceCostConfigEntry.Research(0, 0, 0,550, 0, ResourceCosts.RESEARCH_FIRE_WALLS2, "Research Fire Walls 2 Config");
        public static final ResourceCostConfigEntry RESEARCH_TOWER_DAMAGE1 = ResourceCostConfigEntry.Research(0, 0, 0,300, 0, ResourceCosts.RESEARCH_TOWER_DAMAGE1, "Research Tower Damage 1 Config");
        public static final ResourceCostConfigEntry RESEARCH_TOWER_DAMAGE2 = ResourceCostConfigEntry.Research(0, 0, 0,400, 0, ResourceCosts.RESEARCH_TOWER_DAMAGE2, "Research Tower Damage 2 Config");
        public static final ResourceCostConfigEntry RESEARCH_BEACON = ResourceCostConfigEntry.Research(0, 0, 0,0, 0, ResourceCosts.RESEARCH_BEACON, "Research Beacon Config");
        public static final ResourceCostConfigEntry RESEARCH_TRADER = ResourceCostConfigEntry.Research(0, 0, 0,150, 0, ResourceCosts.RESEARCH_TRADER, "Research Trader Config");
        public static final ResourceCostConfigEntry RESEARCH_MARKETPLACE = ResourceCostConfigEntry.Research(0, 0, 0,200, 0, ResourceCosts.RESEARCH_MARKETPLACE, "Research Marketplace Config");
        public static final ResourceCostConfigEntry RESEARCH_PROFESSIONS = ResourceCostConfigEntry.Research(0, 0, 0,300, 0, ResourceCosts.RESEARCH_PROFESSIONS, "Research Professions Config");
        public static final ResourceCostConfigEntry RESEARCH_BLACKSMITHS = ResourceCostConfigEntry.Research(0, 0, 0,200, 0, ResourceCosts.RESEARCH_BLACKSMITHS, "Research Blacksmiths Config");
        public static final ResourceCostConfigEntry RESEARCH_UPGRADED_BARRACKS = ResourceCostConfigEntry.Research(0, 0, 0,500, 0, ResourceCosts.RESEARCH_UPGRADED_BARRACKS, "Research Upgraded Barracks Config");
        public static final ResourceCostConfigEntry RESEARCH_UPGRADED_DRILL = ResourceCostConfigEntry.Research(0, 0, 0,400, 0, ResourceCosts.RESEARCH_UPGRADED_DRILL, "Research Upgraded Drill Config");
        public static final ResourceCostConfigEntry RESEARCH_ADVANCED_DRILL = ResourceCostConfigEntry.Research(0, 0, 0,400, 0, ResourceCosts.RESEARCH_ADVANCED_DRILL, "Research Advanced Drill Config");
        public static final ResourceCostConfigEntry RESEARCH_ADVANCED_BLACKSMITH = ResourceCostConfigEntry.Research(0, 0, 0,650, 0, ResourceCosts.RESEARCH_ADVANCED_BLACKSMITH, "Research Advanced Blacksmith Config");
        public static final ResourceCostConfigEntry RESEARCH_BIG_HOUSE = ResourceCostConfigEntry.Research(0, 0, 0,450, 0, ResourceCosts.RESEARCH_BIG_HOUSE, "Research Big House Config");
        public static final ResourceCostConfigEntry RESEARCH_POWERFUL_ARCANE_TOWER = ResourceCostConfigEntry.Research(0, 0, 0,500, 0, ResourceCosts.RESEARCH_POWERFUL_ARCANE_TOWER, "Research Powerful Arcane Tower Config");
        public static final ResourceCostConfigEntry RESEARCH_TOWN_CENTRE_T2 = ResourceCostConfigEntry.Research(0, 1000, 2000,0, 120, ResourceCosts.RESEARCH_TOWN_CENTRE_T2, "Research Tower Centre t2 Config");
        public static final ResourceCostConfigEntry RESEARCH_ELITE_TRADER = ResourceCostConfigEntry.Research(0, 0, 0,450, 0, ResourceCosts.RESEARCH_ELITE_TRADER, "Research Elite Trader Config");
        public static final ResourceCostConfigEntry RESEARCH_ELITE_TRAINING = ResourceCostConfigEntry.Research(0, 0, 0,600, 0, ResourceCosts.RESEARCH_ELITE_TRAINING, "Research Elite Training Config");

    }
    public static class AbilityCosts implements Costs {
        public static final ResourceCostConfigEntry ENCHANT_MAIMING = ResourceCostConfigEntry.Ability(0,20, 30,0, ResourceCosts.ENCHANT_MAIMING, "Maiming Enchantment Config");
        public static final ResourceCostConfigEntry ENCHANT_QUICK_CHARGE = ResourceCostConfigEntry.Ability(0,40, 20,0, ResourceCosts.ENCHANT_QUICK_CHARGE, "Quick Charge Enchantment Config");
        public static final ResourceCostConfigEntry ENCHANT_SHARPNESS = ResourceCostConfigEntry.Ability(0,300, 400,0, ResourceCosts.ENCHANT_SHARPNESS, "Sharpness Enchantment Config");
        public static final ResourceCostConfigEntry ENCHANT_MULTISHOT = ResourceCostConfigEntry.Ability(0,375, 100,0, ResourceCosts.ENCHANT_MULTISHOT, "Multishot Enchantment Config");
        public static final ResourceCostConfigEntry ENCHANT_VIGOR = ResourceCostConfigEntry.Ability(0,50, 50,0, ResourceCosts.ENCHANT_VIGOR, "Vigor Enchantment Config");
        public static final ResourceCostConfigEntry EQUIP_LEATHER_ARMOR = ResourceCostConfigEntry.Ability(0,0, 100,0,120, ResourceCosts.EQUIP_LEATHER_ARMOR, "Equip Leather Armor Config");
        public static final ResourceCostConfigEntry EQUIP_CHAINMAIL_ARMOR = ResourceCostConfigEntry.Ability(0,0, 200,0,120, ResourceCosts.EQUIP_CHAINMAIL_ARMOR, "Equip Chainmail Armor Config");
        public static final ResourceCostConfigEntry EQUIP_IRON_ARMOR = ResourceCostConfigEntry.Ability(0,0, 300,0,120, ResourceCosts.EQUIP_IRON_ARMOR, "Equip Iron Armor Config");
        public static final ResourceCostConfigEntry EQUIP_IRON_SWORD = ResourceCostConfigEntry.Ability(0,15, 130,0,120, ResourceCosts.EQUIP_IRON_SWORD, "Equip Iron Sword Config");
        public static final ResourceCostConfigEntry EQUIP_IRON_PICKAXE = ResourceCostConfigEntry.Ability(0,30, 195,0,120, ResourceCosts.EQUIP_IRON_PICKAXE, "Equip Iron Pickaxe Config");
        public static final ResourceCostConfigEntry EQUIP_IRON_AXE = ResourceCostConfigEntry.Ability(0,30, 195,0,120, ResourceCosts.EQUIP_IRON_AXE, "Equip Iron Axe Config");
        public static final ResourceCostConfigEntry EQUIP_IRON_HOE = ResourceCostConfigEntry.Ability(0,30, 130,0,120, ResourceCosts.EQUIP_IRON_HOE, "Equip Iron Hoe Config");
        public static final ResourceCostConfigEntry EQUIP_DIAMOND_ARMOR = ResourceCostConfigEntry.Ability(0,0, 1000,0,120, ResourceCosts.EQUIP_DIAMOND_ARMOR, "Equip Diamond Armor Config");
        public static final ResourceCostConfigEntry EQUIP_DIAMOND_SWORD = ResourceCostConfigEntry.Ability(0,15, 300,0,120, ResourceCosts.EQUIP_DIAMOND_SWORD, "Equip Diamond Sword Config");
        public static final ResourceCostConfigEntry EQUIP_DIAMOND_PICKAXE = ResourceCostConfigEntry.Ability(0,30, 900,0,120, ResourceCosts.EQUIP_DIAMOND_PICKAXE, "Equip Diamond Pickaxe Config");
        public static final ResourceCostConfigEntry EQUIP_DIAMOND_AXE = ResourceCostConfigEntry.Ability(0,30, 400,0,120, ResourceCosts.EQUIP_DIAMOND_AXE, "Equip Diamond Axe Config");
        public static final ResourceCostConfigEntry EQUIP_DIAMOND_HOE = ResourceCostConfigEntry.Ability(0,30, 250,0,120, ResourceCosts.EQUIP_DIAMOND_HOE, "Equip Diamond Hoe Config");
        public static final ResourceCostConfigEntry STEAK = ResourceCostConfigEntry.Ability(200,0, 0,0, ResourceCosts.STEAK, "Steak Config");
        public static final ResourceCostConfigEntry EQUIP_BOW = ResourceCostConfigEntry.Ability(0,300, 200,0,120, ResourceCosts.EQUIP_BOW, "Bow Config");
        public static final ResourceCostConfigEntry EQUIP_ARROWS = ResourceCostConfigEntry.Ability(0,100, 50,0,120, ResourceCosts.EQUIP_ARROWS, "Arrows Config");
        public static final ResourceCostConfigEntry EQUIP_RAVAGER_ARMOR = ResourceCostConfigEntry.Ability(0,0, 3000,0,120, ResourceCosts.EQUIP_RAVAGER_ARMOR, "Equip Ravager Armor Config");
        public static final ResourceCostConfigEntry BECOME_TRADER = ResourceCostConfigEntry.Ability(200,500, 300,0, ResourceCosts.BECOME_TRADER, "Become Trader Config");
        public static final ResourceCostConfigEntry BECOME_FARMER = ResourceCostConfigEntry.Ability(200,50, 0,50, ResourceCosts.BECOME_FARMER, "Become Farmer Config");
        public static final ResourceCostConfigEntry BECOME_LUMBERJACK = ResourceCostConfigEntry.Ability(100,250, 100,50, ResourceCosts.BECOME_LUMBERJACK, "Become Lumberjack Config");
        public static final ResourceCostConfigEntry BECOME_MINER = ResourceCostConfigEntry.Ability(100,100, 250, 50, ResourceCosts.BECOME_MINER, "Become Miner Config");
        public static final ResourceCostConfigEntry BECOME_MASON = ResourceCostConfigEntry.Ability(100,200, 150,50, ResourceCosts.BECOME_MASON, "Become Mason Config");
        public static final ResourceCostConfigEntry BECOME_HUNTER = ResourceCostConfigEntry.Ability(50,0, 0,0, ResourceCosts.BECOME_HUNTER, "Become Hunter Config");
        public static final ResourceCostConfigEntry BECOME_BLACKSMITH = ResourceCostConfigEntry.Ability(250,100, 350,50, ResourceCosts.BECOME_BLACKSMITH, "Become Blacksmith Config");
        public static final ResourceCostConfigEntry BERSERK = ResourceCostConfigEntry.Ability(1000,0, 0,0, ResourceCosts.BERSERK, "Elite Vindicator Berserk Config");
        public static final ResourceCostConfigEntry UPGRADE_TRADER_TO_ELITE = ResourceCostConfigEntry.Ability(250, 400, 300, 50, ResourceCosts.UPGRADE_TRADER_TO_ELITE, "Upgrade Trader to Elite Config");
        public static final ResourceCostConfigEntry UPGRADE_ARCHER_TO_ELITE = ResourceCostConfigEntry.Ability(400, 500, 400, 100, ResourceCosts.UPGRADE_ARCHER_TO_ELITE, "Upgrade Archer to Elite Config");
        public static final ResourceCostConfigEntry UPGRADE_ARMORED_VINDICATOR_TO_ELITE = ResourceCostConfigEntry.Ability(500, 1000, 600, 200, ResourceCosts.UPGRADE_ARMORED_VINDICATOR_TO_ELITE, "Upgrade Armored Vindicator to Elite Config");
        public static final ResourceCostConfigEntry UPGRADE_ARMORED_PILLAGER_TO_ELITE = ResourceCostConfigEntry.Ability(500, 1000, 500, 200, ResourceCosts.UPGRADE_ARMORED_PILLAGER_TO_ELITE, "Upgrade Armored Pillager to Elite Config");

        }
    public interface Costs {}
}
