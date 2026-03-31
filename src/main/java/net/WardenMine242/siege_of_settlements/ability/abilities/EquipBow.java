package net.WardenMine242.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import net.WardenMine242.siege_of_settlements.ability.GiveItemToPlayerAbility;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class EquipBow extends GiveItemToPlayerAbility {

    private static final UnitAction EQUIP_ACTION = UnitAction.EQUIP_BOW;

    public EquipBow() {
        super(EQUIP_ACTION, ResourceCosts.EQUIP_BOW, () -> Items.BOW);
        this.defaultHotkey = Keybindings.keyJ;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof BlacksmithPlacement)) return null;
        return new AbilityButton(
                "Bow",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/bow.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == EQUIP_ACTION,
                () -> false,
                () -> ResearchClient.hasResearch(ProductionItems.RESEARCH_MILITIA_BOWS),
                () -> CursorClientEvents.setLeftClickAction(EQUIP_ACTION),
                null,
                List.of(
                        fcs(I18n.get("ability.siege_of_settlements.equip.bow"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get("ability.siege_of_settlements.equip.bow.tooltip1"), Style.EMPTY),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get("ability.siege_of_settlements.equip.bow.tooltip2"), Style.EMPTY)
                ),
                this,
                placement
        );
    }
}
