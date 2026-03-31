package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.enchantments.VigorEnchantment;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.piglins.BruteUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.EvokerUnit;
import com.solegendary.siege_of_settlements.util.MyRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class CastSummonVexes extends Ability {

    public static final int CD_MAX_SECONDS = 60;
    public static final int VEX_DURATION_SECONDS = 30;

    public CastSummonVexes() {
        super(
                UnitAction.CAST_SUMMON_VEXES,
                CD_MAX_SECONDS * ResourceCost.TICKS_PER_SECOND,
                0,
                0,
                true,
                false
        );
        this.autocastEnableAction = UnitAction.CAST_SUMMON_VEXES_AUTOCAST_ENABLE;
        this.autocastDisableAction = UnitAction.CAST_SUMMON_VEXES_AUTOCAST_DISABLE;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof EvokerUnit evokerUnit))
            return null;
        return new AbilityButton(
                "Summon Vexes",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/vex.png"),
                hotkey,
                () -> {
                    if (evokerUnit.getCastSummonVexesGoal() != null)
                        return evokerUnit.getCastSummonVexesGoal().isCasting()|| isAutocasting(unit);
                    return false;
                },
                () -> !ResearchClient.hasResearch(ProductionItems.RESEARCH_EVOKER_VEXES),
                () -> true,
                () -> UnitClientEvents.sendUnitCommand(UnitAction.CAST_SUMMON_VEXES),
                () -> toggleAutocast(unit),
                List.of(
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.summon_vexes"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.summon_vexes.tooltip1", CD_MAX_SECONDS), MyRenderer.iconStyle),
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.summon_vexes.tooltip2", EvokerUnit.SUMMON_VEXES_AMOUNT), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.summon_vexes.tooltip3", VEX_DURATION_SECONDS), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.autocast"),Style.EMPTY)
                ),
                this,
                unit
        );
    }

    @Override
    public void setCooldown(float cooldown, Unit unit) {
        EvokerUnit evokerUnit = (EvokerUnit) unit;
        int vigorLevel = evokerUnit.getVigorLevel();
        if (vigorLevel > 0)
            cooldown *= Math.pow(VigorEnchantment.CD_MULTIPLIER, vigorLevel);
        super.setCooldown(cooldown, unit);
    }


    @Override
    public void setToMaxCooldown(Unit unit) {
        EvokerUnit evokerUnit = (EvokerUnit) unit;

        float cd = cooldownMax;
        int vigorLevel = evokerUnit.getVigorLevel();
        if (vigorLevel > 0)
            cd *= Math.pow(VigorEnchantment.CD_MULTIPLIER, vigorLevel);

        setCooldown(cd, unit);
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        ((EvokerUnit) unitUsing).getCastSummonVexesGoal().setAbility(this);
        ((EvokerUnit) unitUsing).getCastSummonVexesGoal().startCasting();
    }

    @Override
    public void use(Level level, Unit unitUsing, LivingEntity targetEntity) {
        ((EvokerUnit) unitUsing).getCastSummonVexesGoal().setAbility(this);
        ((EvokerUnit) unitUsing).getCastSummonVexesGoal().startCasting();
    }
}