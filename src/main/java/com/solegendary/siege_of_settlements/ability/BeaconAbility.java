package com.solegendary.siege_of_settlements.ability;

import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.BeaconPlacement;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;

import static com.solegendary.siege_of_settlements.resources.ResourceCost.TICKS_PER_SECOND;

public abstract class BeaconAbility extends Ability {
    protected final MobEffect effect;

    public static final int CD_MAX = 5 * TICKS_PER_SECOND;

    public BeaconAbility(UnitAction action, MobEffect effect) {
        super(
                action,
                CD_MAX,
                0,
                0,
                false,
                true
        );
        this.effect = effect;
    }

    private void setToMaxCooldownAllAbiltities(BeaconPlacement beacon) {
        for (Ability ability : beacon.getAbilities())
            ability.setToMaxCooldown(beacon);
    }

    @Override
    public void use(Level level, BuildingPlacement buildingUsing, BlockPos bp) {
        if (!(buildingUsing instanceof BeaconPlacement)) return;
        BeaconPlacement beacon = (BeaconPlacement) buildingUsing;
        beacon.setAuraEffect(effect);
        setToMaxCooldownAllAbiltities(beacon);
    }
}
