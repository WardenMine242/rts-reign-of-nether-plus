package com.solegendary.siege_of_settlements.research.researchItems;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.BuildingServerboundPacket;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProdDupeRule;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.building.production.StopProductionButton;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.research.ResearchServerEvents;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.building.production.StartProductionButton;
import com.solegendary.siege_of_settlements.unit.UnitServerEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.interfaces.WorkerUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class ResearchResourceCapacity extends ProductionItem {

    public final static String itemName = "Worker Carry Bags";
    public final static ResourceCost cost = ResourceCosts.RESEARCH_RESOURCE_CAPACITY;

    public ResearchResourceCapacity() {
        super(cost, ProdDupeRule.DISALLOW);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (level.isClientSide()) {
                ResearchClient.addResearch(placement.ownerName, ProductionItems.RESEARCH_RESOURCE_CAPACITY);
                for (LivingEntity unit : UnitClientEvents.getAllUnits())
                    if (unit instanceof WorkerUnit) {
                        ((Unit) unit).setupEquipmentAndUpgradesClient();
                    }
            } else {
                ResearchServerEvents.addResearch(placement.ownerName, ProductionItems.RESEARCH_RESOURCE_CAPACITY);
                for (LivingEntity unit : UnitServerEvents.getAllUnits())
                    if (unit instanceof WorkerUnit) {
                        ((Unit) unit).setupEquipmentAndUpgradesServer();
                    }
            }
        };
    }

    public String getItemName() {
        return ResearchResourceCapacity.itemName;
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(ResearchResourceCapacity.itemName,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/research/research_resource_capacity.png"),
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
            hotkey,
            () -> ProductionItems.RESEARCH_RESOURCE_CAPACITY.itemIsBeingProduced(prodBuilding.ownerName)
                || ResearchClient.hasResearch(ProductionItems.RESEARCH_RESOURCE_CAPACITY),
            () -> true,
            List.of(FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.resource_capacity"),
                    Style.EMPTY.withBold(true)
                ),
                ResourceCosts.getFormattedCost(cost),
                ResourceCosts.getFormattedTime(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.resource_capacity.tooltip1"),
                    Style.EMPTY
                ),
                FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.resource_capacity.tooltip2"),
                    Style.EMPTY
                )
            ),
            this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(ResearchResourceCapacity.itemName,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/items/chest.png"),
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
            prodBuilding,
            this,
            first
        );
    }
}
