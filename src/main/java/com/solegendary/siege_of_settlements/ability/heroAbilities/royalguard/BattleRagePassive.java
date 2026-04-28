package com.solegendary.siege_of_settlements.ability.heroAbilities.royalguard;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.util.MyMath;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class BattleRagePassive extends Ability {

    public float maxHpRegen = 1.2f;
    public float manaPerDmgTaken = 0.4f;

    public BattleRagePassive() {
        super(UnitAction.NONE, 0, 0, 0, false);
    }

    public double getHpRegen(Unit unit) {
        if (!(unit instanceof LivingEntity livingEntity)) {
            return 0;
        }
        float healthRatio = 1f - (livingEntity.getHealth() / livingEntity.getMaxHealth());
        return MyMath.round(healthRatio * maxHpRegen, 1);
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        AbilityButton button = new AbilityButton(
                "Battle Rage",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/battle_rage.png"),
                hotkey,
                () -> false,
                () -> false,
                () -> true,
                null,
                null,
                getTooltipLines(unit),
                this,
                unit
        );
        button.stretchIconToBorders = true;
        return button;
    }

    public List<FormattedCharSequence> getTooltipLines(Unit unit) {
        return List.of(
                fcs(I18n.get("abilities.siege_of_settlements.battle_rage"), true),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.battle_rage.tooltip1", maxHpRegen)),
                fcs(I18n.get("abilities.siege_of_settlements.battle_rage.tooltip2", manaPerDmgTaken)),
                fcs(I18n.get("abilities.siege_of_settlements.battle_rage.tooltip3", getHpRegen(unit)))
        );
    }
}
