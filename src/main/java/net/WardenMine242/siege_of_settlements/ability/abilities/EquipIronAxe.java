package net.WardenMine242.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.registrars.ItemRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import net.WardenMine242.siege_of_settlements.ability.GiveItemToPlayerAbility;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class EquipIronAxe extends GiveItemToPlayerAbility {

    private static final UnitAction EQUIP_ACTION = UnitAction.EQUIP_IRON_AXE;

    public EquipIronAxe() {
        super(EQUIP_ACTION, ResourceCosts.EQUIP_IRON_AXE, ItemRegistrar.IRON_AXE::get);
        this.defaultHotkey = Keybindings.keyO;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof BlacksmithPlacement)) return null;
        return new AbilityButton(
                "Iron Axe",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/iron_axe.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == EQUIP_ACTION,
                () -> false,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(EQUIP_ACTION),
                null,
                List.of(
                        fcs(I18n.get("ability.siege_of_settlements.equip.iron_axe"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get("ability.siege_of_settlements.equip.iron_axe.tooltip1"), Style.EMPTY)
                ),
                this,
                placement
        );
    }
}
