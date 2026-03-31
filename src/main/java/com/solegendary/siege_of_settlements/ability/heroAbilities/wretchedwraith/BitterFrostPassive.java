package com.solegendary.siege_of_settlements.ability.heroAbilities.wretchedwraith;

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

import java.util.ArrayList;
import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class BitterFrostPassive extends HeroAbility {

    public static final int SNOW_NO_MELT_RANGE = 5;

    public BitterFrostPassive() {
        super(3, 35, UnitAction.NONE, 0, 0, 0, false);
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof HeroUnit hero)) return null;
        AbilityButton button = new AbilityButton("Bitter Frost",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/bitter_frost.png"),
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
                "Bitter Frost",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/bitter_frost.png"),
                hero
        );
    }

    public List<FormattedCharSequence> getTooltipLines(HeroUnit hero) {
        ArrayList<FormattedCharSequence> tooltipLines = new ArrayList<>(List.of(
                fcs(I18n.get("abilities.siege_of_settlements.bitter_frost") + " " + rankString(hero), true),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.bitter_frost.tooltip1")),
                fcs(I18n.get("abilities.siege_of_settlements.bitter_frost.tooltip2")),
                fcs("")
        ));
        if (getRank(hero) >= 1) {
            tooltipLines.add(fcs(I18n.get("abilities.siege_of_settlements.bitter_frost.rank1")));
        }
        if (getRank(hero) >= 2) {
            tooltipLines.add(fcs(I18n.get("abilities.siege_of_settlements.bitter_frost.rank2")));
        }
        if (getRank(hero) >= 3) {
            tooltipLines.add(fcs(I18n.get("abilities.siege_of_settlements.bitter_frost.rank3")));
        }
        return tooltipLines;
    }

    public List<FormattedCharSequence> getRankUpTooltipLines(HeroUnit hero) {
        return List.of(
                fcs(I18n.get("abilities.siege_of_settlements.bitter_frost"), true),
                fcs(I18n.get("abilities.siege_of_settlements.level_req", getLevelRequirement(hero)), getLevelReqStyle(hero)),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.bitter_frost.tooltip1")),
                fcs(I18n.get("abilities.siege_of_settlements.bitter_frost.tooltip2")),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.bitter_frost.rank1"), getRank(hero) == 0),
                fcs(I18n.get("abilities.siege_of_settlements.bitter_frost.rank2"), getRank(hero) == 1),
                fcs(I18n.get("abilities.siege_of_settlements.bitter_frost.rank3"), getRank(hero) == 2)
        );
    }
}
