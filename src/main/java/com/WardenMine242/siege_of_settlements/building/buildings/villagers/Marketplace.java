package com.WardenMine242.siege_of_settlements.building.buildings.villagers;


import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.WardenMine242.siege_of_settlements.ability.abilities.MarketplaceTradeGUI;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialStage;
import com.WardenMine242.siege_of_settlements.building.buildings.placements.MarketplacePlacement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

public class Marketplace extends ProductionBuilding {
    public final static String buildingName = "Marketplace";
    public final static String structureName = "marketplace";
    public final static ResourceCost cost = ResourceCosts.MARKETPLACE;

    public Marketplace() {
        super(structureName, cost, false);


        this.name = buildingName;
        this.portraitBlock = Blocks.EMERALD_BLOCK;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/emerald_block.png");

        this.buildTimeModifier = 0.5f;

        this.abilities.add(new MarketplaceTradeGUI(), Keybindings.keyQ);


        this.productions.add(ProductionItems.ELITE_VINDICATOR, Keybindings.keyR);



        this.startingBlockTypes.add(Blocks.DIRT_PATH);
        this.startingBlockTypes.add(Blocks.COARSE_DIRT);
    }


    public Faction getFaction() {
        return Faction.VILLAGERS;
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        // Build one block lower than default so the first layer sits inside terrain.
        return new MarketplacePlacement(
                this,
                level,
                pos,
                rotation,
                ownerName,
                BuildingUtils.getAbsoluteBlockData(this.getRelativeBlockData(level), level, pos, rotation, new Vec3i(0, -1, 0)),
                this.isCapitol
        );
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/emerald_block.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.MARKETPLACE,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.EXPLAIN_BUILDINGS),
                () -> {
                    if (Minecraft.getInstance().player == null) {
                        return true;
                    }
                    return (ResearchClient.hasResearch(ProductionItems.RESEARCH_MARKETPLACE) ||
                                    ResearchClient.hasCheat("modifythephasevariance"));
                },
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.marketplace"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.marketplace.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.marketplace.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.requires.research"), Style.EMPTY)
                ),
                this
        );
    }
}
