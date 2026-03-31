package com.solegendary.siege_of_settlements.building.buildings.villagers;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class ArcaneTower extends ProductionBuilding {

    public final static String buildingName = "Arcane Tower";
    public final static String structureName = "arcane_tower";
    public final static ResourceCost cost = ResourceCosts.ARCANE_TOWER;

    public ArcaneTower() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.AMETHYST_BLOCK;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/amethyst_block.png");

        this.startingBlockTypes.add(Blocks.STONE_BRICKS);
        this.startingBlockTypes.add(Blocks.ANDESITE_WALL);
        this.startingBlockTypes.add(Blocks.POLISHED_ANDESITE_STAIRS);
        this.startingBlockTypes.add(Blocks.POLISHED_ANDESITE);

        this.buildTimeModifier = 0.7f;
        this.explodeChance = 0.2f;

        this.productions.add(ProductionItems.WITCH, Keybindings.keyQ);
        this.productions.add(ProductionItems.EVOKER, Keybindings.keyW);
    }

    public Faction getFaction() {return Faction.VILLAGERS;}

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
            name,
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/amethyst_block.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Buildings.ARCANE_TOWER,
            TutorialClientEvents::isEnabled,
            () -> BuildingClientEvents.hasFinishedBuilding(Buildings.BARRACKS) ||
                    ResearchClient.hasCheat("modifythephasevariance"),
            List.of(
                FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.arcane_tower"), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.arcane_tower.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.arcane_tower.tooltip2"), Style.EMPTY)
            ),
            this
        );
    }

    @Override
    public BlockPos getIndoorSpawnPoint(ServerLevel level, BlockPos centrePos) {
        return super.getIndoorSpawnPoint(level, centrePos).offset(0,-10,0);
    }
}
