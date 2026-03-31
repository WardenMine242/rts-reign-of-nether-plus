package com.solegendary.siege_of_settlements.building.buildings.piglins;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.placements.FlameSanctuaryPlacement;
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

public class FlameSanctuary extends ProductionBuilding {

    public final static String buildingName = "Flame Sanctuary";
    public final static String structureName = "flame_sanctuary";
    public final static ResourceCost cost = ResourceCosts.FLAME_SANCTUARY;

    public FlameSanctuary() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.MAGMA_BLOCK;
        this.icon = ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/magma.png");

        this.canSetRallyPoint = false;

        this.startingBlockTypes.add(Blocks.RED_NETHER_BRICK_STAIRS);

        this.explodeChance = 0.2f;

        this.productions.add(ProductionItems.RESEARCH_BLAZE_FIREWALL, Keybindings.keyQ);
        this.productions.add(ProductionItems.RESEARCH_FIRE_RESISTANCE, Keybindings.keyW);
    }

    public Faction getFaction() {return Faction.PIGLINS;}

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new FlameSanctuaryPlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), false);
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
            name,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/magma.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Buildings.FLAME_SANCTUARY,
            () -> false,
            () -> BuildingClientEvents.hasFinishedBuilding(Buildings.HOGLIN_STABLES) ||
                    ResearchClient.hasCheat("modifythephasevariance"),
            List.of(
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.flame_sanctuary"), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.flame_sanctuary.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.flame_sanctuary.tooltip2"), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.flame_sanctuary.tooltip3"), Style.EMPTY)
            ),
            this
        );
    }
}
