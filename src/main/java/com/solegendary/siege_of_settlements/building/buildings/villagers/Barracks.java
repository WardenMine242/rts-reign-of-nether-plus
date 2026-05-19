package com.solegendary.siege_of_settlements.building.buildings.villagers;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.placements.BarracksPlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItemList;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialStage;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

public class Barracks extends ProductionBuilding {

    public final static String buildingName = "Barracks";
    public final static String structureName = "barracks";
    public static final String structureNameT2 = "barracks_t2";
    public static final String structureNameT3 = "barracks_t3";
    public final static ResourceCost cost = ResourceCosts.BARRACKS;

    public Barracks() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.FLETCHING_TABLE;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/fletching_table_front.png");

        this.startingBlockTypes.add(Blocks.POLISHED_ANDESITE_STAIRS);

        this.explodeChance = 0.2f;

        this.productions = new ProductionItemList();
        this.productions.add(ProductionItems.VINDICATOR, Keybindings.keyQ);
        this.productions.add(ProductionItems.ARCHER, Keybindings.keyW);
        this.productions.add(ProductionItems.PILLAGER, Keybindings.keyE);
        this.productions.add(ProductionItems.ARMORED_PILLAGER, Keybindings.keyR);
        this.productions.add(ProductionItems.BARRACKS_T2, Keybindings.keyE);
        this.productions.add(ProductionItems.BARRACKS_T3, Keybindings.keyE);
        this.productions.add(ProductionItems.ARMORED_VINDICATOR, Keybindings.keyU);
    }

    public Faction getFaction() {return Faction.VILLAGERS;}

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new BarracksPlacement(
                this,
                level,
                pos,
                rotation,
                ownerName,
                BuildingUtils.getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation),
                false
        );
    }

    public static String getStructureNameForTier(int tier) {
        return switch (Mth.clamp(tier, BarracksPlacement.MIN_BARRACKS_TIER, BarracksPlacement.MAX_BARRACKS_TIER)) {
            case 2 -> structureNameT2;
            case 3 -> structureNameT3;
            default -> structureName;
        };
    }

    public static int getTierForStructureName(String structureName) {
        if (structureNameT3.equals(structureName)) {
            return 3;
        }
        if (structureNameT2.equals(structureName)) {
            return 2;
        }
        return 1;
    }

    @Override
    public int getUpgradeLevel(BuildingPlacement placement) {
        if (placement instanceof BarracksPlacement barracks) {
            return barracks.getUpgradeLevel();
        }
        return 0;
    }

    @Override
    public String getUpgradedStructureName(int upgradeLevel) {
        return getStructureNameForTier(upgradeLevel + 1);
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/fletching_table_front.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.BARRACKS,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.EXPLAIN_BUILDINGS),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.TOWN_CENTRE) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.barracks"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.barracks.tooltip1"), Style.EMPTY)
                ),
                this
        );
    }
}
