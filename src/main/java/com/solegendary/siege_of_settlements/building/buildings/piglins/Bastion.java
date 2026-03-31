package com.solegendary.siege_of_settlements.building.buildings.piglins;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.placements.BastionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;

public class Bastion extends ProductionBuilding {

    public final static String buildingName = "Bastion";
    public final static String structureName = "bastion";
    public final static ResourceCost cost = ResourceCosts.BASTION;

    public Bastion() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.CHISELED_POLISHED_BLACKSTONE;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/chiseled_polished_blackstone.png");

        this.canSetRallyPoint = false;

        this.buildTimeModifier = 0.72f;

        this.startingBlockTypes.add(Blocks.POLISHED_BLACKSTONE);
        this.startingBlockTypes.add(Blocks.POLISHED_BLACKSTONE_BRICKS);
        this.startingBlockTypes.add(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);

        this.explodeChance = 0.2f;

        this.productions.add(ProductionItems.RESEARCH_BRUTE_SHIELDS, Keybindings.keyQ);
        this.productions.add(ProductionItems.RESEARCH_HEAVY_TRIDENTS, Keybindings.keyW);
        this.productions.add(ProductionItems.RESEARCH_CLEAVING_FLAILS, Keybindings.keyE);
    }

    public Faction getFaction() {return Faction.PIGLINS;}

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new BastionPlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), false);
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/chiseled_polished_blackstone.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.BASTION,
                () -> false,
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.PORTAL_BASIC) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.bastion"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.bastion.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.bastion.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.bastion.tooltip3", BastionPlacement.MAX_OCCUPANTS), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.bastion.tooltip4"), Style.EMPTY)
                ),
                this
        );
    }
}
