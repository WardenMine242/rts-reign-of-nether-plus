package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.buildings.villagers.TownCentre;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.villagers.MilitiaUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.VillagerUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.unit.UnitClientEvents.sendUnitCommand;

public class BackToWorkUnit extends Ability {

    private static final int RANGE = TownCentre.MILITIA_RANGE;

    public BackToWorkUnit() {
        super(
                UnitAction.BACK_TO_WORK_UNIT,
                0,
                RANGE,
                0,
                false,
                false
        );
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof MilitiaUnit militiaUnit)) return null;
        return new AbilityButton(
                "Back to Work (Building)",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/iron_pickaxe.png"),
                hotkey,
                () -> false,
                () -> militiaUnit.isCaptain,
                () -> true,
                () -> sendUnitCommand(UnitAction.BACK_TO_WORK_UNIT),
                null,
                List.of(
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.back_to_work_unit"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.back_to_work_unit.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.back_to_work_unit.tooltip2"), Style.EMPTY)
                ),
                this,
                unit
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        if (unitUsing instanceof MilitiaUnit mUnit && !level.isClientSide())
            mUnit.convertToVillager();
        else if (unitUsing instanceof VillagerUnit vUnit && !level.isClientSide()) {
            vUnit.callToArmsGoal.stop();
            Unit.resetBehaviours(vUnit);
            vUnit.getGatherResourceGoal().saveData = vUnit.getGatherResourceGoal().permSaveData;
            vUnit.getGatherResourceGoal().loadState();
        }
    }
}
