package net.WardenMine242.reignofnetherplus.building.production;

import com.solegendary.reignofnether.building.buildings.placements.ProductionPlacement;
import com.solegendary.reignofnether.building.production.ProductionItem;
import com.solegendary.reignofnether.building.production.StopProductionButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import net.WardenMine242.reignofnetherplus.building.buildings.placements.VillagerGatePlacement;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import com.solegendary.reignofnether.building.production.StartProductionButton;

import java.util.List;

public class ToggleGateProduction extends ProductionItem {

    public static final String itemName =
            "production.reignofnether.toggle_gate";
    public static final ResourceCost cost = ResourceCosts.TOGGLE_VILLAGER_GATE;

    public ToggleGateProduction() {
        super(cost);

        this.onComplete = (level, placement) -> {
            if (placement instanceof VillagerGatePlacement gate) {
                gate.toggleGate();
            }
        };
    }
    @Override
    public String getItemName() {
        return itemName;
    }


    @Override
    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {

        List<FormattedCharSequence> tooltipLines = List.of(
                FormattedCharSequence.forward(
                        I18n.get("production.reignofnether.toggle_gate"),
                        Style.EMPTY.withBold(true)
                ),
                ResourceCosts.getFormattedTime(cost),

        FormattedCharSequence.forward("", Style.EMPTY),


                FormattedCharSequence.forward(
                        I18n.get("production.reignofnether.toggle_gate.tooltip1"),
                        Style.EMPTY)
        );

        return new StartProductionButton(
                itemName,
                ResourceLocation.fromNamespaceAndPath(
                        "minecraft",
                        "textures/item/oak_door.png"
                ),
                hotkey,
                () -> false,

                // 🔒 КНОПКА АКТИВНА ТОЛЬКО ЕСЛИ HP >= 183
                () -> {
                    if (prodBuilding instanceof VillagerGatePlacement gate) {
                        return gate.getHealth() >= 182;
                    }
                    return true;
                },

                tooltipLines,
                this
        );
    }


    @Override
    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {

        List<FormattedCharSequence> tooltipLines = List.of(
                FormattedCharSequence.forward(
                        I18n.get("production.reignofnether.toggle_gate.cancel"), Style.EMPTY.withBold(true)
                )
        );

        return new StopProductionButton(
                I18n.get("production.reignofnether.toggle_gate.cancel"),
                ResourceLocation.fromNamespaceAndPath(
                        "minecraft",
                        "textures/item/oak_door.png"
                ),
                prodBuilding,
                this,
                first
        );
    }
}