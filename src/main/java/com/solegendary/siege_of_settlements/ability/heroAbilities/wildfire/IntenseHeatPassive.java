package com.solegendary.siege_of_settlements.ability.heroAbilities.wildfire;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.HeroAbility;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.HeroUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class IntenseHeatPassive extends HeroAbility {

    public IntenseHeatPassive() {
        super(3, 0, UnitAction.NONE, 0, 0, 0, false);
    }

    public static final float TICK_MULTIPLIER_RANK_1 = 1.5f;
    public static final float TICK_MULTIPLIER_RANK_2 = 2.0f;
    public static final float TICK_MULTIPLIER_RANK_3 = 2.5f;

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof HeroUnit hero)) return null;
        AbilityButton button = new AbilityButton("Intense Heat",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/intense_heat.png"),
                hotkey,
                () -> false,
                () -> getRank(hero) == 0,
                () -> true,
                null,
                null,
                getTooltipLines(hero),
                this,
                hero
        );
        button.stretchIconToBorders = true;
        return button;
    }

    @Override
    public Button getRankUpButton(HeroUnit hero) {
        return super.getRankUpButtonProtected(
                "Intense Heat",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/intense_heat.png"),
                hero
        );
    }

    public List<FormattedCharSequence> getTooltipLines(HeroUnit hero) {
        return List.of(
                fcs(I18n.get("abilities.siege_of_settlements.intense_heat") + " " + rankString(hero), true),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.intense_heat.tooltip1")),
                fcs(I18n.get("abilities.siege_of_settlements.intense_heat.tooltip2"))
        );
    }

    public List<FormattedCharSequence> getRankUpTooltipLines(HeroUnit hero) {
        return List.of(
                fcs(I18n.get("abilities.siege_of_settlements.intense_heat"), true),
                fcs(I18n.get("abilities.siege_of_settlements.level_req", getLevelRequirement(hero)), getLevelReqStyle(hero)),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.intense_heat.tooltip1")),
                fcs(I18n.get("abilities.siege_of_settlements.intense_heat.tooltip2")),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.intense_heat.rank1", TICK_MULTIPLIER_RANK_1), getRank(hero) == 0),
                fcs(I18n.get("abilities.siege_of_settlements.intense_heat.rank2", TICK_MULTIPLIER_RANK_2), getRank(hero) == 1),
                fcs(I18n.get("abilities.siege_of_settlements.intense_heat.rank3", TICK_MULTIPLIER_RANK_3), getRank(hero) == 2)
        );
    }
}
