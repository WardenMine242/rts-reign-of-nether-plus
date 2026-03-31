package com.solegendary.siege_of_settlements.building.buildings.monsters;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.shared.AbstractBridge;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class SpruceBridge extends AbstractBridge {

    public final static String buildingName = "Spruce Bridge";
    public final static String structureNameOrthogonal = "bridge_spruce_orthogonal";
    public final static String structureNameDiagonal = "bridge_spruce_diagonal";
    public final static ResourceCost cost = ResourceCosts.SPRUCE_BRIDGE;

    public SpruceBridge() {
        super(cost);

        this.name = buildingName;
        this.portraitBlock = Blocks.DARK_OAK_FENCE;
        this.icon = ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/spruce_fence.png");

        this.buildTimeModifier = 1.0f;

        this.startingBlockTypes.add(Blocks.SPRUCE_LOG);
    }

    @Override
    public String getDiagonalStructureName() {
        return structureNameDiagonal;
    }

    @Override
    public String getOrthogonalStructureName() {
        return structureNameOrthogonal;
    }

    @Override
    public Faction getFaction() {
        return Faction.MONSTERS;
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        Minecraft MC = Minecraft.getInstance();
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/spruce_fence.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.SPRUCE_BRIDGE,
                () -> false,
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.TOWN_CENTRE) ||
                        BuildingClientEvents.hasFinishedBuilding(Buildings.MAUSOLEUM) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.spruce_bridge"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.spruce_bridge.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.spruce_bridge.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.spruce_bridge.tooltip3"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.spruce_bridge.tooltip4"), Style.EMPTY)
                ),
                this
        );
    }
}
