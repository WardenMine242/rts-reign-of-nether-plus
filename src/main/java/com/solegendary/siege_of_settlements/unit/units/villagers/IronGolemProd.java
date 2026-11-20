package com.solegendary.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.BuildingServerboundPacket;
import com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.building.production.StopProductionButton;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.hud.buttons.UnitSpawnButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.sandbox.SandboxAction;
import com.solegendary.siege_of_settlements.sandbox.SandboxClientEvents;
import com.solegendary.siege_of_settlements.building.production.StartProductionButton;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

public class IronGolemProd extends ProductionItem {

    public final static String itemName = "Iron Golem";
    public final static ResourceCost cost = ResourceCosts.IRON_GOLEM;

    public IronGolemProd() {
        super(cost);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (!level.isClientSide())
                placement.produceUnit((ServerLevel) level, EntityRegistrar.IRON_GOLEM_UNIT.get(), placement.ownerName, false);
        };
    }

    public String getItemName() {
        return IronGolemProd.itemName;
    }

    public UnitSpawnButton getPlaceButton() {
        return new UnitSpawnButton(
                itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/iron_golem.png"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.iron_golem"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.iron_golem.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.iron_golem.tooltip2"), Style.EMPTY)
                )
        );
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(
            IronGolemProd.itemName,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/iron_golem.png"),
            hotkey,
            () -> false,
            () -> true,
            null,
            this
        );
    }

    public int getDynamicProductionSeconds(ProductionPlacement placement) {
        if (placement instanceof BlacksmithPlacement blacksmith) {
            return blacksmith.getProductionTimeSeconds();
        }
        return cost.ticks;
    }

    @Override
    public Supplier<List<FormattedCharSequence>> getTooltipSupplier(ProductionPlacement prodBuilding) {
        return () -> {
            int productionSeconds = getDynamicProductionSeconds(prodBuilding);
            ResourceCost dynamicCost = ResourceCost.Unit(cost.food, cost.wood, cost.ore, cost.emerald, productionSeconds, cost.population);
            return List.of(
                FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.iron_golem"), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                ResourceCosts.getFormattedPopAndTime(dynamicCost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.iron_golem.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.iron_golem.tooltip2"), Style.EMPTY)
            );
        };
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(
            IronGolemProd.itemName,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/iron_golem.png"),
            prodBuilding,
            this,
            first
        );
    }
}
