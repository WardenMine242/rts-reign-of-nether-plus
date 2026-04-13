package com.solegendary.siege_of_settlements.building.production;

import com.solegendary.siege_of_settlements.building.BuildingServerboundPacket;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.function.Supplier;

public class StopProductionButton extends Button {
    public final ProductionItem productionItem;

   public StopProductionButton(String name, ResourceLocation rl, ProductionPlacement placement, ProductionItem production, boolean first) {

        // generate x/y based on given position (starting at 0 which is bottom left 1 row above generic action buttons)
        super(name, Button.itemIconSize, rl, (Keybinding) null, () -> false, () -> false, () -> true, () -> BuildingServerboundPacket.cancelProduction(placement.minCorner, production, first), () -> {}, null);
        this.productionItem = production;
    }

    public StopProductionButton(String name, ResourceLocation iconRl, ResourceLocation frameRl, ProductionPlacement placement, ProductionItem production, boolean first) {

        // generate x/y based on given position (starting at 0 which is bottom left 1 row above generic action buttons)
        super(name, Button.itemIconSize, iconRl, frameRl, null, () -> false, () -> false, () -> true, () -> BuildingServerboundPacket.cancelProduction(placement.originPos, production, first), () -> {}, null);
        this.productionItem = production;
    }
}
