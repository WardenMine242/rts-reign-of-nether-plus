package com.solegendary.siege_of_settlements.unit.units.villagers;

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

import java.util.ArrayList;
import java.util.List;

public class RoyalGuardProd extends ProductionItem {

    public static final String itemName = "Royal Guard";
    public static final ResourceCost cost = ResourceCosts.ROYAL_GUARD;
    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(
            SiegeOfSettlements.MOD_ID,
            "textures/mobheads/royal_guard.png"
    );

    public RoyalGuardProd() {
        super(cost);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (!level.isClientSide())
                placement.produceUnit((ServerLevel) level, EntityRegistrar.ROYAL_GUARD_UNIT.get(), placement.ownerName, true);
        };
    }

    @Override
    public String getItemName() {
        return itemName;
    }

    public UnitSpawnButton getPlaceButton() {
        return new UnitSpawnButton(
                itemName,
                ICON,
                List.of(
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.royal_guard"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.royal_guard.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.royal_guard.tooltip2"), Style.EMPTY)
                )
        );
    }

    @Override
    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        List<FormattedCharSequence> tooltipLines = new ArrayList<>(List.of(
                FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.royal_guard"), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                ResourceCosts.getFormattedPopAndTime(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.royal_guard.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.royal_guard.tooltip2"), Style.EMPTY)
        ));
        return new StartProductionButton(
                itemName,
                ICON,
                hotkey,
                () -> false,
                () -> true,
                tooltipLines,
                this
        );
    }

    @Override
    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(
                itemName,
                ICON,
                prodBuilding,
                this,
                first
        );
    }
}
