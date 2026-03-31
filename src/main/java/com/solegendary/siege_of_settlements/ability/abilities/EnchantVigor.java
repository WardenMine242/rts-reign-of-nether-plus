package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.ability.EnchantAbility;
import com.solegendary.siege_of_settlements.ability.EnchantEquipAbilityServerboundPacket;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.LibraryPlacement;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.registrars.EnchantmentRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.units.villagers.EvokerUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public class EnchantVigor extends EnchantAbility {

    private static final UnitAction ENCHANT_ACTION = UnitAction.ENCHANT_VIGOR;

    public EnchantVigor() {
        super(ENCHANT_ACTION, ResourceCosts.ENCHANT_VIGOR, 1, EquipmentSlot.MAINHAND);
        this.defaultHotkey = Keybindings.keyT;
    }

    @Override
    public Enchantment getEnchantment() {
        return EnchantmentRegistrar.VIGOR.get();
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof LibraryPlacement)) return null;
        LibraryPlacement library = (LibraryPlacement) placement;
        return new AbilityButton(
                "Vigor Enchantment",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/stick.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == ENCHANT_ACTION || library.autoCastEnchant == this,
                () -> false,
                () -> library.getUpgradeLevel() > 0,
                () -> CursorClientEvents.setLeftClickAction(ENCHANT_ACTION),
                () -> {
                    EnchantEquipAbilityServerboundPacket.setAutocastEnchantOrEquip(ENCHANT_ACTION, library.originPos);
                    if (library.autoCastEnchant == this)
                        library.autoCastEnchant = null;
                    else
                        library.autoCastEnchant = this;
                },
                List.of(
                        FormattedCharSequence.forward(I18n.get("ability.siege_of_settlements.enchant.vigor"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("ability.siege_of_settlements.enchant.vigor.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("ability.siege_of_settlements.enchant.vigor.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("ability.siege_of_settlements.enchant.vigor.tooltip3"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.autocast"), Style.EMPTY)
                ),
                this,
                placement
        );
    }

    @Override
    public boolean isCorrectUnitAndEquipment(LivingEntity entity) {
        return entity instanceof EvokerUnit && entity.getItemBySlot(equipmentSlot).getItem() instanceof BowItem;
    }
}
