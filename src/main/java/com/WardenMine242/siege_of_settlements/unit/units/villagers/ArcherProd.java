package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.StartProductionButton;
import com.solegendary.siege_of_settlements.building.production.StopProductionButton;
import com.solegendary.siege_of_settlements.hud.buttons.UnitSpawnButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;

import java.util.List;

public class ArcherProd extends ProductionItem {

    public static final String itemName = "Archer";
    public static final ResourceCost cost = ResourceCosts.ARCHER;

    public ArcherProd() {
        super(cost);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (!level.isClientSide()) {
                placement.produceUnit((ServerLevel) level, EntityRegistrar.ARCHER_UNIT.get(), placement.ownerName, true);
            }
        };
    }

    public String getItemName() {
        return itemName;
    }

    public UnitSpawnButton getPlaceButton() {
        return new UnitSpawnButton(
                itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/archer.png"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.archer"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.archer.tooltip1"), Style.EMPTY)
                )
        );
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(
                itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/archer.png"),
                hotkey,
                () -> false,
                () -> true,
                List.of(
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.archer"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        ResourceCosts.getFormattedPopAndTime(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.archer.tooltip1"), Style.EMPTY)
                ),
                this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(
                itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/archer.png"),
                prodBuilding,
                this,
                first
        );
    }
}
