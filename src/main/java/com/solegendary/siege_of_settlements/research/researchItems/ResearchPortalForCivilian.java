package com.solegendary.siege_of_settlements.research.researchItems;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.BuildingClientboundPacket;
import com.solegendary.siege_of_settlements.building.buildings.placements.PortalPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProdDupeRule;
import com.solegendary.siege_of_settlements.building.production.*;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;

import java.util.List;

public class ResearchPortalForCivilian extends ProductionItem {

    public final static String itemName = "Civilian Portal";
    public final static ResourceCost cost = ResourceCosts.RESEARCH_CIVILIAN_PORTAL;

    public ResearchPortalForCivilian() {
        super(cost, ProdDupeRule.DISALLOW_FOR_BUILDING);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (placement instanceof PortalPlacement portal) {
                if (!level.isClientSide()) {
                    portal.changePortalStructure(PortalPlacement.PortalType.CIVILIAN);
                    BuildingClientboundPacket.changePortal(placement.originPos, PortalPlacement.PortalType.CIVILIAN);
                }
            }
        };
    }

    public String getItemName() {
        return ResearchPortalForCivilian.itemName;
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartPortalTransformButton(ResearchPortalForCivilian.itemName,
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/cyan_glazed_terracotta.png"),
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
            hotkey,
            () -> prodBuilding.productionQueue.size() > 0 || (
                prodBuilding instanceof PortalPlacement portal && portal.getUpgradeLevel() > 0
            ),
            () -> true,
            List.of(FormattedCharSequence.forward(
                    I18n.get("research.siege_of_settlements.civilian_portal"),
                    Style.EMPTY.withBold(true)
                ),
                ResourceCosts.getFormattedCost(cost),
                ResourceCosts.getFormattedTime(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.civilian_portal.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.civilian_portal.tooltip2"), Style.EMPTY)
            ),
            prodBuilding,
            this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(ResearchPortalForCivilian.itemName,
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/cyan_glazed_terracotta.png"),
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
            prodBuilding,
            this,
            first
        );
    }
}
