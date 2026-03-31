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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class EquipArrows extends GiveItemToPlayerAbility {

    private static final UnitAction EQUIP_ACTION = UnitAction.EQUIP_ARROWS;
    private static final int ARROW_COUNT = 4;

    public EquipArrows() {
        super(EQUIP_ACTION, ResourceCosts.EQUIP_ARROWS, () -> Items.ARROW);
        this.defaultHotkey = Keybindings.keyK;
    }

    @Override
    protected void giveItem(ServerPlayer player) {
        giveItemStack(player, new ItemStack(Items.ARROW, ARROW_COUNT));
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof BlacksmithPlacement)) return null;
        return new AbilityButton(
                "4 Arrows",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/arrow.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == EQUIP_ACTION,
                () -> false,
                () -> ResearchClient.hasResearch(ProductionItems.RESEARCH_MILITIA_BOWS),
                () -> CursorClientEvents.setLeftClickAction(EQUIP_ACTION),
                null,
                List.of(
                        fcs(I18n.get("ability.siege_of_settlements.equip.arrows"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get("ability.siege_of_settlements.equip.arrows.tooltip1"), Style.EMPTY),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get("ability.siege_of_settlements.equip.arrows.tooltip2"), Style.EMPTY)
                ),
                this,
                placement
        );
    }
}