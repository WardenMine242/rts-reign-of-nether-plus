package com.WardenMine242.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.registrars.ItemRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.WardenMine242.siege_of_settlements.ability.GiveItemToPlayerAbility;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class EquipIronAxe extends GiveItemToPlayerAbility {

    private static final UnitAction EQUIP_ACTION = UnitAction.EQUIP_IRON_AXE;
    private static final int DIAMOND_BLACKSMITH_TIER = 2;

    public EquipIronAxe() {
        super(EQUIP_ACTION, ResourceCosts.EQUIP_IRON_AXE, ItemRegistrar.IRON_AXE::get);
        this.defaultHotkey = Keybindings.keyO;
    }

    private static boolean shouldGiveDiamond(BuildingPlacement placement) {
        return placement instanceof BlacksmithPlacement blacksmith &&
                blacksmith.getBlacksmithTier() >= DIAMOND_BLACKSMITH_TIER;
    }

    @Override
    protected ResourceCost getCost(BuildingPlacement buildingUsing) {
        return shouldGiveDiamond(buildingUsing) ? ResourceCosts.EQUIP_DIAMOND_AXE : ResourceCosts.EQUIP_IRON_AXE;
    }

    @Override
    protected void giveItem(ServerPlayer player, BuildingPlacement buildingUsing) {
        boolean diamond = shouldGiveDiamond(buildingUsing);
        ItemStack stack = new ItemStack(diamond ? ItemRegistrar.DIAMOND_AXE.get() : ItemRegistrar.IRON_AXE.get());
        Item oldItemToReplace = diamond ? ItemRegistrar.IRON_AXE.get() : null;
        giveItemStackReplacingExisting(player, stack, oldItemToReplace);
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof BlacksmithPlacement)) return null;
        boolean diamond = shouldGiveDiamond(placement);
        String titleKey = diamond
                ? "ability.siege_of_settlements.equip.diamond_axe"
                : "ability.siege_of_settlements.equip.iron_axe";
        String tooltipKey = diamond
                ? "ability.siege_of_settlements.equip.diamond_axe.tooltip1"
                : "ability.siege_of_settlements.equip.iron_axe.tooltip1";
        return new AbilityButton(
                diamond ? "Diamond Axe" : "Iron Axe",
                ResourceLocation.fromNamespaceAndPath("minecraft", diamond ? "textures/item/diamond_axe.png" : "textures/item/iron_axe.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == EQUIP_ACTION,
                () -> false,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(EQUIP_ACTION),
                null,
                List.of(
                        fcs(I18n.get(titleKey), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(getCost(placement)),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get(tooltipKey), Style.EMPTY)
                ),
                this,
                placement
        );
    }
}
