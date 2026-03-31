package com.solegendary.siege_of_settlements.unit.goals;

import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.function.Consumer;

public class SonicBoomGoal extends GenericTargetedSpellGoal {

    public SonicBoomGoal(Mob mob, int channelTicks, int range,
                         Consumer<LivingEntity> onEntityCast, Consumer<BuildingPlacement> onBuildingCast) {
        super(mob, channelTicks, range, onEntityCast, null, onBuildingCast);
        this.bonusChannelingRange = 10;
    }

    @Override
    public void stopCasting() {
        super.stopCasting();
        if (this.mob.level().isClientSide() && !Keybindings.shiftMod.isDown())
            ((Unit) this.mob).getCheckpoints().clear();
    }

    @Override
    public void stop() {
        // hack fix to stop a weird bug where it gets stopped unexpectedly (serverside)
        // happens when needing to move towards the target first
        if (this.channelTicks <= 5 && isInRange())
            return;
        super.stop();
    }
}
