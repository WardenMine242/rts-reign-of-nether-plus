package com.solegendary.siege_of_settlements.building.buildings.villagers;

import com.solegendary.siege_of_settlements.ability.abilities.EquipChainmailChestplate;
import com.solegendary.siege_of_settlements.ability.abilities.EquipLeatherChestplate;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.*;
import com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialStage;
import com.WardenMine242.siege_of_settlements.ability.abilities.EquipIronAxe;
import com.WardenMine242.siege_of_settlements.ability.abilities.EquipArrows;
import com.WardenMine242.siege_of_settlements.ability.abilities.EquipBow;
import com.WardenMine242.siege_of_settlements.ability.abilities.EquipDiamondChestplate;
import com.WardenMine242.siege_of_settlements.ability.abilities.EquipIronChestplate;
import com.WardenMine242.siege_of_settlements.ability.abilities.EquipIronHoe;
import com.WardenMine242.siege_of_settlements.ability.abilities.EquipIronPickaxe;
import com.WardenMine242.siege_of_settlements.ability.abilities.EquipIronSword;
import com.WardenMine242.siege_of_settlements.ability.abilities.EquipRavagerArmor;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;

public class Blacksmith extends ProductionBuilding {

    public final static String buildingName = "Blacksmith";
    public final static String structureName = "blacksmith";
    public static final String structureNameT2 = "blacksmith_t2";
    public static final String structureNameT3 = "blacksmith_t3";
    public static final String legacyUpgradedStructureName = "blacksmith_superior";
    public final static String upgradedStructureName = structureNameT2;
    public final static ResourceCost cost = ResourceCosts.BLACKSMITH;

    public Blacksmith() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.SMITHING_TABLE;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/smithing_table_front.png");

        this.buildTimeModifier = 0.85f;

        this.startingBlockTypes.add(Blocks.OAK_PLANKS);
        this.startingBlockTypes.add(Blocks.COBBLESTONE);

        this.abilities.add(new EquipLeatherChestplate(), Keybindings.keyT);
        this.abilities.add(new EquipChainmailChestplate(), Keybindings.keyY);
        this.abilities.add(new EquipIronChestplate(), Keybindings.keyL);
        this.abilities.add(new EquipDiamondChestplate(), Keybindings.keyG);
        this.abilities.add(new EquipRavagerArmor(), Keybindings.keyU);
        this.abilities.add(new EquipIronSword(), Keybindings.keyI);
        this.abilities.add(new EquipIronPickaxe(), Keybindings.keyO);
        this.abilities.add(new EquipIronAxe(), Keybindings.keyP);
        this.abilities.add(new EquipIronHoe(), Keybindings.keyJ);
        this.abilities.add(new EquipBow(), Keybindings.keyK);
        this.abilities.add(new EquipArrows(), Keybindings.keyV);

        this.productions.add(ProductionItems.IRON_GOLEM, Keybindings.keyQ);
        this.productions.add(ProductionItems.REDSTONE_GOLEM, Keybindings.keyF);
        this.productions.add(ProductionItems.RESEARCH_GOLEM_SMITHING, Keybindings.keyW);
        this.productions.add(ProductionItems.RESEARCH_MILITIA_BOWS, Keybindings.keyE);
        this.productions.add(ProductionItems.BLACKSMITH_T2, Keybindings.keyR);
        this.productions.add(ProductionItems.BLACKSMITH_T3, Keybindings.keyR);
    }

    public Faction getFaction() {return Faction.VILLAGERS;}

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new BlacksmithPlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), false);
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/smithing_table_front.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.BLACKSMITH,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.ATTACK_ENEMY_BASE),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.BARRACKS) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.blacksmith"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.blacksmith.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.blacksmith.tooltip2"), Style.EMPTY)
                ),
                this
        );
    }

    @Override
    public int getUpgradeLevel(BuildingPlacement placement) {
        if (placement instanceof BlacksmithPlacement blacksmith) {
            return blacksmith.getUpgradeLevel();
        }
        return 0;
    }

    public static String getStructureNameForTier(int tier) {
        return switch (Mth.clamp(tier, BlacksmithPlacement.MIN_BLACKSMITH_TIER, BlacksmithPlacement.MAX_BLACKSMITH_TIER)) {
            case 2 -> structureNameT2;
            case 3 -> structureNameT3;
            default -> structureName;
        };
    }

    public static int getTierForStructureName(String structureNameIn) {
        if (structureNameT3.equals(structureNameIn)) {
            return 3;
        }
        if (structureNameT2.equals(structureNameIn) || legacyUpgradedStructureName.equals(structureNameIn)) {
            return 2;
        }
        return 1;
    }

    @Override
    public String getUpgradedStructureName(int upgradeLevel) {
        return getStructureNameForTier(upgradeLevel + 1);
    }
}
