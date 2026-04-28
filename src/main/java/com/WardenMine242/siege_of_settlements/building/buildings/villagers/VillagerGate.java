package com.WardenMine242.siege_of_settlements.building.buildings.villagers;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.*;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialStage;
import com.WardenMine242.siege_of_settlements.building.buildings.placements.VillagerGatePlacement;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

public class VillagerGate extends ProductionBuilding {

    public final static String buildingName = "VillagerGate";
    public final static String structureName = "villager_gate";
    public final static ResourceCost cost = ResourceCosts.GATE;

    public VillagerGate() {
        this(structureName);
    }

    public VillagerGate(String structureName) {
        super(structureName, cost, false);

        this.name = buildingName;
        this.portraitBlock = Blocks.CHISELED_STONE_BRICKS;
        this.icon = ResourceLocation.fromNamespaceAndPath(
                "minecraft",
                "textures/item/oak_door.png"
        );

        this.buildTimeModifier = 0.3f;
        this.startingBlockTypes.add(Blocks.OAK_LOG);

        this.productions.add(
                ProductionItems.TOGGLE_GATE_PRODUCTION,
                Keybindings.keyQ
        );
    }




    public Faction getFaction() {
        return Faction.VILLAGERS;
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/oak_door.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.VILLAGER_GATE,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.EXPLAIN_BUILDINGS),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.TOWN_CENTRE) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.villager_gate"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.villager_gate.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.villager_gate.tooltip2"), Style.EMPTY)
                ),
                this
        );
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level,
                                                     BlockPos pos,
                                                     Rotation rotation,
                                                     String ownerName) {

        return new VillagerGatePlacement(
                this,
                level,
                pos,
                rotation,
                ownerName,
                BuildingUtils.getAbsoluteBlockData(
                        this.getRelativeBlockData(level),
                        level,
                        pos,
                        rotation
                ),
                this.isCapitol
        );
    }
}
