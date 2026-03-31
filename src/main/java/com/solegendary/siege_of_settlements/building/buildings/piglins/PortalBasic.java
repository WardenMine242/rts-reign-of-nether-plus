package com.solegendary.siege_of_settlements.building.buildings.piglins;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.placements.PortalPlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
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

public class PortalBasic extends AbstractPortal {

    public final static String buildingName = "Basic Portal";
    public final static String structureName = "portal_basic";

    public final static ResourceCost cost = ResourceCosts.BASIC_PORTAL;

    public PortalBasic() {
        super(structureName, cost);
        this.name = buildingName;
        this.portraitBlock = Blocks.GRAY_GLAZED_TERRACOTTA;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/gray_glazed_terracotta.png");
        this.canSetRallyPoint = false;

        this.productions.add(ProductionItems.RESEARCH_PORTAL_FOR_CIVILIAN, Keybindings.keyQ);
        this.productions.add(ProductionItems.RESEARCH_PORTAL_FOR_MILITARY, Keybindings.keyW);
        this.productions.add(ProductionItems.RESEARCH_PORTAL_FOR_TRANSPORT, Keybindings.keyE);
    }

    @Override
    public PortalPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new PortalPlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), false);
    }

    @Override
    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(name,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/portal.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Buildings.PORTAL_BASIC,
            () -> false,
            () -> BuildingClientEvents.hasFinishedBuilding(Buildings.CENTRAL_PORTAL) || ResearchClient.hasCheat(
                "modifythephasevariance"),
            List.of(FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.portal_basic"),
                    Style.EMPTY.withBold(true)
                ),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.portal_basic.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.portal_basic.tooltip2"), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.portal_basic.tooltip3"), Style.EMPTY)
            ),
            this
        );
    }
}