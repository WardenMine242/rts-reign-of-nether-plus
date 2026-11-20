package com.solegendary.siege_of_settlements.building.production;

import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductionItemList {
    Map<ProductionItem, Keybinding> productions = new LinkedHashMap<>();

    public ProductionItemList() {

    }

    public void add(ProductionItem production, Keybinding keybind) {
        productions.put(production, keybind);
    }

    public List<Button> getButtons(ProductionPlacement placement) {
        List<Button> buttons = new ArrayList<>();
        //TODO Remove need for I18n
        if (FMLEnvironment.dist == Dist.CLIENT) {
            for (Map.Entry<ProductionItem, Keybinding> production : productions.entrySet()) {
                Button button = production.getKey().getStartButton(placement, production.getValue());
                if (button != null) {
                    button.tooltipLinesSupplier = production.getKey().getTooltipSupplier(placement);
                }
                buttons.add(button);
            }
        }
        return buttons;
    }

    public List<ProductionItem> get() {
        return new ArrayList<>(productions.keySet());
    }
}
