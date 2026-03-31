package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.piglins.BruteUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class ToggleShield extends Ability {

    private static final int CD_MAX_SECONDS = 0;

    public static final float MOVESPEED_MULTIPLIER = 0.5f;
    public static final float PROJECTILE_DAMAGE_RESIST = 0.75f;

    public ToggleShield() {
        super(
                UnitAction.NONE,
                CD_MAX_SECONDS * ResourceCost.TICKS_PER_SECOND,
                0,
                0,
                false
        );
        this.autocastEnableAction = UnitAction.ENABLE_SHIELD_RAISE;
        this.autocastDisableAction = UnitAction.DISABLE_SHIELD_RAISE;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof BruteUnit bruteUnit))
            return null;
        return new AbilityButton(
                "Shield Stance",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/items/shield.png"),
                hotkey,
                () -> bruteUnit.isHoldingUpShield,
                () -> !ResearchClient.hasResearch(ProductionItems.RESEARCH_BRUTE_SHIELDS) ||
                        bruteUnit.getItemBySlot(EquipmentSlot.OFFHAND).getItem() != Items.SHIELD,
                () -> true,
                () -> toggleAutocast(unit),
                null,
                List.of(
                        fcs(I18n.get("abilities.siege_of_settlements.shield_stance"), true),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.shield_stance.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.shield_stance.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.shield_stance.tooltip3"), Style.EMPTY)
                ),
                this,
                unit
        );
    }

    @Override
    public void setAutocast(boolean value, Unit unit) {
        if (!(unit instanceof BruteUnit bruteUnit))
            return;
        super.setAutocast(value, unit);
        if ((isAutocasting(unit) && !bruteUnit.isHoldingUpShield) ||
            (!isAutocasting(unit) && bruteUnit.isHoldingUpShield))
            bruteUnit.toggleShield();
        bruteUnit.updateAbilityButtons();
    }

    @Override
    public boolean shouldResetBehaviours() { return false; }
}
