package com.WardenMine242.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.registrars.ItemRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.WardenMine242.siege_of_settlements.ability.GiveItemToPlayerAbility;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class EquipIronHoe extends GiveItemToPlayerAbility {

    private static final UnitAction EQUIP_ACTION = UnitAction.EQUIP_IRON_HOE;

    public EquipIronHoe() {
        super(EQUIP_ACTION, ResourceCosts.EQUIP_IRON_HOE, ItemRegistrar.IRON_HOE::get);
        this.defaultHotkey = Keybindings.keyP;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof BlacksmithPlacement)) return null;
        return new AbilityButton(
                "Iron Hoe",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/iron_hoe.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == EQUIP_ACTION,
                () -> false,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(EQUIP_ACTION),
                null,
                List.of(
                        fcs(I18n.get("ability.siege_of_settlements.equip.iron_hoe"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get("ability.siege_of_settlements.equip.iron_hoe.tooltip1"), Style.EMPTY)
                ),
                this,
                placement
        );
    }
}
