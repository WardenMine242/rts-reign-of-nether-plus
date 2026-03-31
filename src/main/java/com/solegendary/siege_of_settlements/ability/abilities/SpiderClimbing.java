package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.monsters.SpiderUnit;
import com.solegendary.siege_of_settlements.unit.units.piglins.BruteUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.EvokerUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class SpiderClimbing extends Ability {

    public SpiderClimbing() {
        super(
            UnitAction.NONE,
            0,
            0,
            0,
            false,
            false
        );
        this.autocastEnableAction = UnitAction.ENABLE_SPIDER_CLIMBING;
        this.autocastDisableAction = UnitAction.DISABLE_SPIDER_CLIMBING;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof SpiderUnit spiderUnit))
            return null;
        ResourceLocation rlLadder = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/ladder.png");
        ResourceLocation rlBarrier = ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/items/barrier.png");

        AbilityButton ab = new AbilityButton(
                "Toggle Wall Climbing",
                spiderUnit.isWallClimbing() ? rlLadder : rlBarrier,
                hotkey,
                () -> false,
                () -> false,
                () -> true,
                () -> toggleAutocast(unit),
                null,
                List.of(
                        spiderUnit.isWallClimbing() ?
                        fcs(I18n.get("abilities.siege_of_settlements.spider_climbing_on")) :
                        fcs(I18n.get("abilities.siege_of_settlements.spider_climbing_off"))
                ),
                this,
                unit
        );
        if (!spiderUnit.isWallClimbing())
            ab.bgIconResource = rlLadder;

        return ab;
    }

    @Override
    public void setAutocast(boolean value, Unit unit) {
        if (!(unit instanceof SpiderUnit spiderUnit))
            return;
        super.setAutocast(value, unit);
        if ((isAutocasting(unit) && !spiderUnit.isWallClimbing()) ||
            (!isAutocasting(unit) && spiderUnit.isWallClimbing()))
            spiderUnit.toggleWallClimbing();
        spiderUnit.updateAbilityButtons();
    }

    @Override
    public boolean shouldResetBehaviours() { return false; }
}
