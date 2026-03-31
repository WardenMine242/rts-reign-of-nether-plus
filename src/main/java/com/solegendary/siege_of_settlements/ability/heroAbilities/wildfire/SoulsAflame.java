package com.solegendary.siege_of_settlements.ability.heroAbilities.wildfire;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.HeroAbility;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.goals.GenericUntargetedSpellGoal;
import com.solegendary.siege_of_settlements.unit.interfaces.HeroUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.piglins.WildfireUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.RoyalGuardUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcsIcons;

public class SoulsAflame extends HeroAbility {

    private static final int CD_MAX = 360 * ResourceCost.TICKS_PER_SECOND;
    public static final int DURATION = 60 * ResourceCost.TICKS_PER_SECOND;

    public SoulsAflame() {
        super(1, 140, UnitAction.SOULS_AFLAME, CD_MAX, 0, 0, false);
    }

    @Override
    public boolean isCasting(Unit hero) {
        if (hero instanceof WildfireUnit wildfireUnit) {
            GenericUntargetedSpellGoal goal = wildfireUnit.getCastSoulsAflameGoal();
            if (goal != null)
                return goal.isCasting();
        }
        return false;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof HeroUnit hero)) return null;
        AbilityButton button = new AbilityButton("Souls Aflame",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/souls_aflame.png"),
            hotkey,
            () -> false,
            () -> getRank(hero) == 0,
            () -> true,
            () -> UnitClientEvents.sendUnitCommand(UnitAction.SOULS_AFLAME),
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
                "Souls Aflame",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/souls_aflame.png"),
                hero
        );
    }

    public List<FormattedCharSequence> getTooltipLines(HeroUnit hero) {
        return List.of(
                fcs(I18n.get("abilities.siege_of_settlements.souls_aflame"), true),
                fcsIcons(I18n.get("abilities.siege_of_settlements.souls_aflame.stats", CD_MAX / 20, manaCost)),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.souls_aflame.tooltip1")),
                fcs(I18n.get("abilities.siege_of_settlements.souls_aflame.tooltip2", DURATION / 20)),
                fcs(I18n.get("abilities.siege_of_settlements.souls_aflame.tooltip3"))
        );
    }

    public List<FormattedCharSequence> getRankUpTooltipLines(HeroUnit hero) {
        return List.of(
                fcs(I18n.get("abilities.siege_of_settlements.souls_aflame"), true),
                fcs(I18n.get("abilities.siege_of_settlements.level_req", getLevelRequirement(hero)), getLevelReqStyle(hero)),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.souls_aflame.tooltip1")),
                fcs(I18n.get("abilities.siege_of_settlements.souls_aflame.tooltip2", DURATION / 20)),
                fcs(I18n.get("abilities.siege_of_settlements.souls_aflame.tooltip3"))
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        ((WildfireUnit) unitUsing).getCastSoulsAflameGoal().setAbility(this);
        ((WildfireUnit) unitUsing).getCastSoulsAflameGoal().startCasting();
    }

    @Override
    public void use(Level level, Unit unitUsing, LivingEntity targetEntity) {
        ((WildfireUnit) unitUsing).getCastSoulsAflameGoal().setAbility(this);
        ((WildfireUnit) unitUsing).getCastSoulsAflameGoal().startCasting();
    }
}
